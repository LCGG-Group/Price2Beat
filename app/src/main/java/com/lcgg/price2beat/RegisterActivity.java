package com.lcgg.price2beat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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
    EditText inputPassword;
    EditText inputPassword2;
    LinearLayout activity_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail = (EditText)findViewById(R.id.register_email);
        inputPassword = (EditText)findViewById(R.id.register_password);
        inputPassword2 = (EditText)findViewById(R.id.register_password2);

        txtForgot = (TextView) findViewById(R.id.register_forgotpassword);
        btnLogin = (Button)findViewById(R.id.register_sign_in_button);
        btnRegister = (Button)findViewById(R.id.register_register_button);

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
        if(v.getId() == R.id.register_forgotpassword){
            startActivity(new Intent (RegisterActivity.this, ForgotPasswordActivity.class));
            finish();
        }
        //Log in
        if(v.getId() == R.id.register_sign_in_button){
            startActivity(new Intent (RegisterActivity.this, LoginActivity.class));
            finish();
        }
        else if(v.getId() == R.id.register_register_button){

            if(inputEmail.getText().toString().isEmpty()){
                Toast.makeText(RegisterActivity.this, "Email address must not be empty", Toast.LENGTH_SHORT).show();
            }
            if(inputPassword.getText().toString().isEmpty()){
                Toast.makeText(RegisterActivity.this, "Password must not be empty", Toast.LENGTH_SHORT).show();
            }
            if(inputPassword2.getText().toString().isEmpty()){
                Toast.makeText(RegisterActivity.this, "Retype password must not be empty", Toast.LENGTH_SHORT).show();
            }
            if(!inputPassword.getText().toString().equals(inputPassword.getText().toString())){

                Toast.makeText(RegisterActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            }
            if(inputPassword.getText().toString().equals(inputPassword.getText().toString())){
                signUpUser(inputEmail.getText().toString(), inputPassword.getText().toString());
            }

        }
    }

    private void signUpUser(String email, final String password) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        if(password.length() < 8){
                            Snackbar snackbar = Snackbar.make( activity_register, "Password length must not less than 8 characters" , Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                    else{
                        alertBox("Register","Register Success");
                    }
                }
            });
    }

    private void alertBox(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_alert, null);

        TextView alertTitle = (TextView) dialogView.findViewById(R.id.titleAlert);
        alertTitle.setText(title);

        TextView alert = (TextView) dialogView.findViewById(R.id.txtAlert);
        alert.setText(message);

        builder.setView(dialogView);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                AuthUI.getInstance()
                        .signOut(RegisterActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

