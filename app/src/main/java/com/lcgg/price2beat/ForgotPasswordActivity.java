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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordActivity extends AppCompatActivity implements OnClickListener {

    private FirebaseAuth auth;
    Button btnReset;

    EditText inputEmail;
    LinearLayout activity_forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        activity_forgot = (LinearLayout) findViewById(R.id.activity_forgot);

        inputEmail = (EditText)findViewById(R.id.email);

        btnReset = (Button)findViewById(R.id.reset_button);
        btnReset.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.reset_button){
            resetPassword(inputEmail.getText().toString());
        }
    }

    private void resetPassword(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Snackbar snackbar = Snackbar.make( activity_forgot, "Check your email to reset your password.", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });
    }
}

