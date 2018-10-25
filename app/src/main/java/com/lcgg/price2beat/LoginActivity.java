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

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private FirebaseAuth auth;
    Button btnLogin;
    Button btnRegister;

    TextView txtForgot;
    EditText inputEmail;
    EditText inputPassword;
    LinearLayout activity_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText)findViewById(R.id.email);
        inputPassword = (EditText)findViewById(R.id.password);

        txtForgot = (TextView) findViewById(R.id.email_forgotpassword);
        btnLogin = (Button)findViewById(R.id.email_sign_in_button);
        btnRegister = (Button)findViewById(R.id.email_register_button);

        activity_main = (LinearLayout) findViewById(R.id.activity_main);

        txtForgot.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null)
            LoginActivity.this.startActivity(new Intent (LoginActivity.this, MainActivity.class));

    }

    @Override
    public void onClick(View v) {

        //Reset Password
        if(v.getId() == R.id.email_forgotpassword){
            LoginActivity.this.startActivity(new Intent (LoginActivity.this, ForgotPasswordActivity.class));
            //finish();
        }
        //Register
        if(v.getId() == R.id.email_register_button){
            LoginActivity.this.startActivity(new Intent (LoginActivity.this, RegisterActivity.class));
            //finish();
        }
        else if(v.getId() == R.id.email_sign_in_button){
            loginUser(inputEmail.getText().toString(), inputPassword.getText().toString());
        }
    }

    private void loginUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        if(password.length() < 8){
                            Snackbar snackbar = Snackbar.make( activity_main, "Password length must be 8", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        else {
                            Snackbar snackbar = Snackbar.make( activity_main, "Wrong Password", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                    else{
                        LoginActivity.this.startActivity(new Intent (LoginActivity.this, MainActivity.class));
                    }
                }
            });
    }
}

