package com.lcgg.price2beat;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.microsoft.appcenter.ingestion.models.Log;
import com.squareup.picasso.Picasso;


public class FacebookLoginActivity extends AppCompatActivity implements OnClickListener {

    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;

    CallbackManager callbackManager;
    LoginButton btnFacebookLogin;
    LoginButton btnFacebookLogout;

    TextView txtUser;
    TextView txtEmail;

    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebooklogin);

        FacebookSdk.getApplicationContext();

        txtUser = (TextView)findViewById(R.id.txtUser);
        txtEmail = (TextView)findViewById(R.id.txtEmail);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);

        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        btnFacebookLogin = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        btnFacebookLogout = (LoginButton) findViewById(R.id.facebook_log_out_button);
        btnFacebookLogout.setOnClickListener(this);

        btnFacebookLogin.setReadPermissions("email","public_profile");
        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(FacebookLoginActivity.this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FacebookLoginActivity.this, "Something bad happend", Toast.LENGTH_SHORT).show();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if(AccessToken.getCurrentAccessToken() != null){
                    Toast.makeText(FacebookLoginActivity.this, AccessToken.getCurrentAccessToken().getExpires().toString(), Toast.LENGTH_SHORT).show();
                }
                if(user != null){
                    txtEmail.setText(user.getEmail());
                    txtUser.setText(user.getDisplayName());
                    Picasso.with(FacebookLoginActivity.this).load(user.getPhotoUrl()).into(imgProfile);
                    btnFacebookLogin.setVisibility(View.GONE);
                    btnFacebookLogout.setVisibility(View.VISIBLE);
                }
                else{
                    txtEmail.setText("");
                    txtUser.setText("");
                    imgProfile.setImageBitmap(null);
                    btnFacebookLogin.setVisibility(View.VISIBLE);
                    btnFacebookLogout.setVisibility(View.GONE);
                }
            }
        };
    }

    private void handleFacebookAccessToken(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(FacebookLoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onClick(View v) {
        auth.signOut();
        LoginManager.getInstance().logOut();
    }
}

