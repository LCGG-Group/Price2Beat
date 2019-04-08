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

import com.facebook.AccessToken;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity implements OnClickListener {

    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;

    FirebaseDatabase database;
    DatabaseReference refUser, refPoints, refWallet, refTransactions;

    Button btnLogin;
    Button btnRegister;

    EditText inputEmail;
    EditText inputPassword;
    LinearLayout activity_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail = (EditText)findViewById(R.id.register_email);
        inputPassword = (EditText)findViewById(R.id.register_password);

        btnLogin = (Button)findViewById(R.id.register_sign_in_button);
        btnRegister = (Button)findViewById(R.id.register_register_button);

        activity_register = (LinearLayout) findViewById(R.id.activity_register);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        //Initiate Firebase
        auth = FirebaseAuth.getInstance();

        firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onClick(View v) {

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
                        refUser = database.getReference("User").child(auth.getUid());
                        refPoints = database.getReference("Points").child(auth.getUid());
                        refWallet = database.getReference("Wallet").child(auth.getUid());
                        refTransactions = database.getReference("Transactions").child(auth.getUid());

                        refUser.addListenerForSingleValueEvent(valueEventListenerUser);
                        refPoints.addListenerForSingleValueEvent(valueEventListenerPoints);
                        refWallet.addListenerForSingleValueEvent(valueEventListenerWallet);
                        refTransactions.addListenerForSingleValueEvent(valueEventListenerTransactions);

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

    private ValueEventListener valueEventListenerUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            refUser.child("userId").setValue(auth.getUid());
            refUser.child("displayName").setValue(inputEmail.getText().toString());
            refUser.child("email").setValue(inputEmail.getText().toString());
            refUser.child("merchant").setValue(false);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private ValueEventListener valueEventListenerPoints = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            refPoints.child("pointsId").setValue(auth.getUid());
            refPoints.child("earned").setValue(0);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private ValueEventListener valueEventListenerWallet = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            refWallet.child("walletId").setValue(auth.getUid());
            refWallet.child("amount").setValue(0);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private ValueEventListener valueEventListenerTransactions = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            refTransactions.child("transactionsId").setValue(auth.getUid());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}

