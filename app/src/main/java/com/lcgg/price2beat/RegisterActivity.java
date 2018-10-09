package com.lcgg.price2beat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements OnClickListener {

    private FirebaseAuth auth;
    Button btnLogin;
    Button btnRegister;

    TextView txtForgot;
    EditText inputEmail;
    EditText inputPasword;
    LinearLayout activity_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail = (EditText)findViewById(R.id.email);
        inputPasword = (EditText)findViewById(R.id.password);

        txtForgot = (TextView) findViewById(R.id.email_forgotpassword);
        btnLogin = (Button)findViewById(R.id.email_sign_in_button);
        btnRegister = (Button)findViewById(R.id.email_register_button);

        activity_register = (LinearLayout) findViewById(R.id.activity_register);

        txtForgot.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        //Initiate Firebase
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        //Reset Password
        if(v.getId() == R.id.email_forgotpassword){
            this.startActivity(new Intent (RegisterActivity.this, ForgotPasswordActivity.class));
            finish();
        }
        //Log in
        if(v.getId() == R.id.email_sign_in_button){
            this.startActivity(new Intent (RegisterActivity.this, LoginActivity.class));
            finish();
        }
        else if(v.getId() == R.id.email_register_button){
            signUpUser(inputEmail.getText().toString(), inputPasword.getText().toString());
        }
    }

    private void signUpUser(String email, final String password) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        if(password.length() < 8){
                            Snackbar snackbar = Snackbar.make( activity_register, "Error: " + task.getException(), Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                    else{
                        Snackbar snackbar = Snackbar.make( activity_register, "Register Success: ", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            });
    }
}

