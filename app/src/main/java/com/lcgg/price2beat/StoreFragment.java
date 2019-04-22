package com.lcgg.price2beat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lcgg.price2beat.Config.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.net.wifi.WifiConfiguration.Status.strings;
import static com.facebook.FacebookSdk.getApplicationContext;

public class StoreFragment extends Fragment {

    private FirebaseAuth auth;
    private IntentIntegrator qrScan;

    FirebaseDatabase database;
    DatabaseReference refPoints, refTransfer, refStore, refTransactions;

    ImageView qrImage;
    EditText editPayUser, editPay;
    Button btnPayAmount;

    Store store;
    String walletId, refDatePay, referenceId;

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_store, container, false);

        qrScan = new IntentIntegrator(getActivity());

        editPayUser = (EditText) view.findViewById(R.id.editPayUser);
        editPay = (EditText) view.findViewById(R.id.editPay);

        btnPayAmount = (Button) view.findViewById(R.id.btnPayAmount);

        btnPayAmount.setEnabled(false);
        editPayUser.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length() > 0 && editPay.getText().toString().length() > 0)
                    {
                        btnPayAmount.setEnabled(true);
                    }
                    else {
                        btnPayAmount.setEnabled(false);
                    }

                }
            });
        editPay.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length()> 0 && editPayUser.getText().toString().length() > 0)
                    {
                        btnPayAmount.setEnabled(true);
                    }
                    else {
                        btnPayAmount.setEnabled(false);
                    }

                }
            });

        btnPayAmount.setOnClickListener(payAmountListener);

        qrImage = (ImageView) view.findViewById(R.id.storeQRCode);
        qrImage.setOnTouchListener(qrImageListener);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        //Wallet
        refStore = database.getReference("Store");
        refPoints = database.getReference("Points").child(auth.getUid());
        refTransfer = database.getReference("Wallet");
        refTransactions = database.getReference("Transactions").child(auth.getUid()).child("pay");

        return view;
    }


    private View.OnTouchListener qrImageListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            processUser();
            return true;
        }
    };


    private View.OnClickListener payAmountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            processPay();
        }
    };

    private void processUser() {
        qrScan.initiateScan();

        if(walletId != null){
            refStore.child(walletId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    editPayUser.setText(dataSnapshot.child("name").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void processPay() {
        refTransfer.addListenerForSingleValueEvent(valueEventListenerTransfer);
    }
    private ValueEventListener valueEventListenerTransfer = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            refDatePay = df.format(c);

            referenceId = database.getReference().push().getKey().replace("-","").replace("_","");

            refTransfer.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double payMoney = dataSnapshot.child("amount").getValue(Double.class) - Double.valueOf(editPay.getText().toString());
                    refTransfer.child(auth.getUid()).child("amount").setValue(payMoney);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            refTransfer.child(walletId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double receivedMoney = dataSnapshot.child("amount").getValue(Double.class) + Double.valueOf(editPay.getText().toString());
                    refTransfer.child(walletId).child("amount").setValue(receivedMoney);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            refPoints.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double earnedPoints = Double.valueOf(editPay.getText().toString()) / 25;
                    refPoints.child("earned").setValue(earnedPoints);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            refTransactions.child(referenceId);
            refTransactions.child(referenceId).child("refNumber").setValue(referenceId);
            refTransactions.child(referenceId).child("date").setValue(refDatePay);
            refTransactions.child(referenceId).child("amount").setValue(Double.valueOf(editPay.getText().toString()));
            refTransactions.child(referenceId).child("claimed").setValue(true);

            refTransactions.child(referenceId).child("payFrom").setValue(auth.getUid());
            refTransactions.child(referenceId).child("payTo").setValue(walletId);

            refStore = database.getReference("Store").child(walletId);
            refStore.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    store = dataSnapshot.getValue(Store.class);

                    refTransactions.child(referenceId).child("imageURL").setValue(store.getImageUrl());
                    refTransactions.child(referenceId).child("item").setValue(store.getName());

                    Intent intent = new Intent(getContext(), TransactionActivity.class);
                    intent.putExtra("payRefId", referenceId);
                    intent.putExtra("payName", store.getName());
                    intent.putExtra("payPrice", editPay.getText().toString());
                    intent.putExtra("payDate", refDatePay);
                    intent.putExtra("payTo",store.getName());
                    intent.putExtra("transferTo","Paid To:");
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());
                    walletId = obj.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
