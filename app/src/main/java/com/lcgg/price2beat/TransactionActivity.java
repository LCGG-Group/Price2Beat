package com.lcgg.price2beat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.widget.Toast;

public class TransactionActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    Button btnHome;

    TextView refMerchantTextView;
    EditText refIdTxt, refPriceTxt, refDateTxt, refMerchantTxt;
    LinearLayout activity_main;

    String refId, refName, refPrice, refDate, refMerchant, refMerchantView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Intent intent = getIntent();

        refId = getIntent().getStringExtra("payRefId");
        refName = getIntent().getStringExtra("payName");
        refPrice = getIntent().getStringExtra("payPrice");
        refDate = getIntent().getStringExtra("payDate");
        refMerchant = getIntent().getStringExtra("payTo");
        refMerchantView = getIntent().getStringExtra("transferTo");

        refMerchantTextView = (TextView) findViewById(R.id.transMerchant);
        refIdTxt = (EditText) findViewById(R.id.transRefTxt);
        refDateTxt = (EditText) findViewById(R.id.transDateTxt);
        refMerchantTxt = (EditText) findViewById(R.id.transMerchantTxt);
        refPriceTxt = (EditText) findViewById(R.id.transAmtTxt);

        refIdTxt.setText(refId);
        refDateTxt.setText(refDate);
        refMerchantTxt.setText(refMerchant);
        refPriceTxt.setText(refPrice);
        refMerchantTextView.setText(refMerchantView);

        btnHome = (Button) findViewById(R.id.transBtnHome);
        btnHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }


}

