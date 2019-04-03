package com.lcgg.price2beat;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lcgg.price2beat.Config.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class SettingsFragmentSettings extends Fragment {

    private FirebaseAuth auth;

    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(Config.PAYMENT_CLIENT_ID_LIVE);

    FirebaseDatabase database;
    DatabaseReference refPoints, refWallet, refTransfer, refUser, refStore, refTransactions;

    private IntentIntegrator qrScan;

    TextView editAmount;
    EditText editAmountPay, editUserId;
    Button btnAmountPay, btnTransfer;
    ImageView qrImage;

    Wallet wallet;
    Store store;
    User user;

    String amount, walletId, refId, refDatePayTransfer, refIdAddMoney, refDateAddMoney;

    public SettingsFragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        auth = FirebaseAuth.getInstance();

        Intent intent = new Intent(getActivity(), PayPalService.class)
                .putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getContext().startService(intent);
    }

    @Override
    public void onDestroy() {
        getContext().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings_settings, container, false);
        editAmount = (TextView) view.findViewById(R.id.amount);

        btnAmountPay = (Button) view.findViewById(R.id.btnAmount);

        btnTransfer = (Button) view.findViewById(R.id.btnTransferAmount);

        btnAmountPay.setOnClickListener(paypalListener);
        btnTransfer.setOnClickListener(transferListener);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        //Wallet
        refUser = database.getReference("User");
        refWallet = database.getReference("Wallet").child(auth.getUid());
        refPoints = database.getReference("Points").child(auth.getUid());
        refTransfer = database.getReference("Wallet");
        refStore = database.getReference("Store");
        refTransactions = database.getReference("Transactions").child(auth.getUid());

        refWallet.addListenerForSingleValueEvent(valueEventListenerWallet);

        return view;
    }



    private void transferAmount(){

        qrScan = new IntentIntegrator(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_transfer_amount, null);
        builder.setView(dialogView);

        qrImage = (ImageView) dialogView.findViewById(R.id.transferQRCode);

        editAmountPay = (EditText) dialogView.findViewById(R.id.editAmount);
        editUserId = (EditText) dialogView.findViewById(R.id.editUserId);

        qrImage.setOnTouchListener(qrImageListener);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                processTransfer();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private View.OnClickListener transferListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            transferAmount();
        }
    };

    private void addAmount(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_add_amount, null);
        builder.setView(dialogView);

        editAmountPay = (EditText) dialogView.findViewById(R.id.editAmount);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                processPayment();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void processPayment() {
        amount = editAmountPay.getText().toString();

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "PHP",
                "Add Amount", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getActivity(), PaymentActivity.class)
                .putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
                .putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }
    private View.OnClickListener paypalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addAmount();
        }
    };


        private View.OnTouchListener qrImageListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                processUser();
                return true;
            }
        };

    private void processUser() {
        qrScan.initiateScan();

        refUser.child(walletId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editUserId.setText(dataSnapshot.child("displayName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void processTransfer() {
        refTransfer.addListenerForSingleValueEvent(valueEventListenerTransfer);
    }
    private ValueEventListener valueEventListenerTransfer = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            wallet = dataSnapshot.getValue(Wallet.class);

            refWallet.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double ownWallet = dataSnapshot.child("amount").getValue(Double.class) - Double.valueOf(editAmountPay.getText().toString());
                    refWallet.child("amount").setValue(ownWallet);

                    editAmount.setText(ownWallet.toString());

                    refTransactions.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            refDatePayTransfer = df.format(c);

                            refId = database.getReference().push().getKey().replace("-","").replace("_","");

                            refTransactions.child("transfer").child(refId);
                            refTransactions.child("transfer").child(refId).child("refNumber").setValue(refId);
                            refTransactions.child("transfer").child(refId).child("amount").setValue(Double.valueOf(editAmountPay.getText().toString()));
                            refTransactions.child("transfer").child(refId).child("date").setValue(refDatePayTransfer);
                            refTransactions.child("transfer").child(refId).child("claimed").setValue(true);

                            refUser.child(walletId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    user = dataSnapshot.getValue(User.class);

                                    refTransactions.child("transfer").child(refId).child("transferTo").setValue(user.getDisplayName());

                                    Intent intent = new Intent(getContext(), TransactionActivity.class);
                                    intent.putExtra("payRefId", refId);
                                    intent.putExtra("payPrice", editAmountPay.getText().toString());
                                    intent.putExtra("payDate", refDatePayTransfer);
                                    intent.putExtra("payTo",user.getDisplayName());
                                    intent.putExtra("transferTo","Transfer To:");
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
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            refTransfer.child(walletId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double sendMoney = dataSnapshot.child("amount").getValue(Double.class)+ Double.valueOf(editAmountPay.getText().toString());
                    refTransfer.child(walletId).child("amount").setValue(sendMoney);
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

    private ValueEventListener valueEventListenerWallet = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            wallet = dataSnapshot.getValue(Wallet.class);

            editAmount.setText(wallet.getAmount().toString());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private ValueEventListener valueEventListenerWalletPaypal = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            wallet = dataSnapshot.getValue(Wallet.class);

            Double pp = wallet.getAmount() + Double.valueOf(amount);
            editAmount.setText(pp.toString());
            editAmountPay.setText("");

            refWallet.child("amount").setValue(pp);

            refPoints.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double earnedPoints = Double.valueOf(editAmount.getText().toString()) / 25;

                    refPoints.child("earned").setValue(earnedPoints);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            refDateAddMoney = df.format(c);

            refIdAddMoney = database.getReference().push().getKey().replace("-","").replace("_","");

            refTransactions.child("reload").child(refIdAddMoney);
            refTransactions.child("reload").child(refIdAddMoney).child("refNumber").setValue(refIdAddMoney);
            refTransactions.child("reload").child(refIdAddMoney).child("amount").setValue(Double.valueOf(amount));
            refTransactions.child("reload").child(refIdAddMoney).child("date").setValue(refDateAddMoney);
            refTransactions.child("reload").child(refIdAddMoney).child("claimed").setValue(true);
            refTransactions.child("reload").child(refIdAddMoney).child("addedFrom").setValue("PayPal");

            Intent intent = new Intent(getContext(), TransactionActivity.class);
            intent.putExtra("payRefId", refIdAddMoney);
            intent.putExtra("payPrice", amount);
            intent.putExtra("payDate", refIdAddMoney);
            intent.putExtra("payTo", "PayPal");
            intent.putExtra("transferTo","Added From:");
            startActivity(intent);


        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void alertBox(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_alert, null);

        TextView alerTitle = (TextView) dialogView.findViewById(R.id.titleAlert);
        alerTitle.setText(title);

        TextView alert = (TextView) dialogView.findViewById(R.id.txtAlert);
        alert.setText(message);

        builder.setView(dialogView);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

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


        if (requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirmation != null){
                    try{
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        JSONObject jsonObject = new JSONObject(paymentDetails);

                        refWallet.addListenerForSingleValueEvent(valueEventListenerWalletPaypal);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED){
                alertBox("Add money", "Transaction canceled");
            }
        }
        else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID)
        {
            alertBox("Add money", "Invalid transaction");
        }
    }
}
