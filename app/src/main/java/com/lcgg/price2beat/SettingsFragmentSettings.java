package com.lcgg.price2beat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import static android.app.Activity.RESULT_OK;

public class SettingsFragmentSettings extends Fragment {

    private FirebaseAuth auth;

    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYMENT_CLIENT_ID_SANDBOX);

    FirebaseDatabase database;
    DatabaseReference refUser, refWallet;

    TextView editAmount;
    EditText editAmountPay;
    Button btnAmountPay;

    Wallet wallet;

    String amount = "";

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

        editAmountPay = (EditText) view.findViewById(R.id.editAmount);
        btnAmountPay = (Button) view.findViewById(R.id.btnAmount);

        btnAmountPay.setOnClickListener(paypalListener);


        FirebaseUser firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        //Wallet
        refWallet = database.getReference("Wallet").child(auth.getUid());
        refWallet.addListenerForSingleValueEvent(valueEventListenerWallet);

        return view;
    }

    private View.OnClickListener paypalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            processPayment();
        }
    };

    private void processPayment() {
        amount = editAmountPay.getText().toString();

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD",
                "Donate for Price2Beat", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getActivity(), PaymentActivity.class)
                .putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
                .putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

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
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
    }
}
