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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.net.wifi.WifiConfiguration.Status.strings;
import static com.facebook.FacebookSdk.getApplicationContext;

public class StoreFragment extends Fragment {

    private FirebaseAuth auth;
    private IntentIntegrator qrScan;

    FirebaseDatabase database;
    DatabaseReference refPoints, refTransfer, refStore;

    EditText editPayUser, editPay;
    Button btnPayAmount;

    String walletId = "";

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

        btnPayAmount.setOnClickListener(payAmountListener);
        editPayUser.setOnTouchListener(payUserListener);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        //Wallet
        refStore = database.getReference("Store");
        refPoints = database.getReference("Points").child(auth.getUid());
        refTransfer = database.getReference("Wallet");

        return view;
    }

    private View.OnTouchListener payUserListener = new View.OnTouchListener() {
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
    private void processPay() {
        refTransfer.addListenerForSingleValueEvent(valueEventListenerTransfer);
    }
    private ValueEventListener valueEventListenerTransfer = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    walletId = obj.toString();
                    Toast.makeText(getContext(), walletId, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(getContext(), result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
