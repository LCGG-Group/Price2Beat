package com.lcgg.price2beat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.internal.WebDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.security.AuthProvider;
import java.util.List;
import java.util.Map;

public class SettingsFragmentProfile extends Fragment {

    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;

    FirebaseDatabase database;
    DatabaseReference refUser;

    TextView editName, editEmail, editPoints;
    EditText updateFirst, updateMiddle, updateLast;

    LinearLayout displayLinear, updateLinear;

    Button btnUpdate, btnEdit, btnCancel;
    User user;
    AccessToken accessToken;

    public SettingsFragmentProfile() {
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

        final View view = inflater.inflate(R.layout.fragment_settings_profile, container, false);

        updateLinear = (LinearLayout) view.findViewById(R.id.updateLinear);
        displayLinear = (LinearLayout) view.findViewById(R.id.displayLinear);

        updateLinear.setVisibility(View.GONE);
        displayLinear.setVisibility(View.VISIBLE);

        editName = (TextView) view.findViewById(R.id.profileName);
        editEmail = (TextView) view.findViewById(R.id.profileEmail);
        editPoints = (TextView) view.findViewById(R.id.points);

        updateFirst = (EditText) view.findViewById(R.id.editProfileFirst);
        updateMiddle = (EditText) view.findViewById(R.id.editProfileMiddle);
        updateLast = (EditText) view.findViewById(R.id.editProfileLast);

        btnUpdate = (Button) view.findViewById(R.id.updateProfile);
        btnEdit = (Button) view.findViewById(R.id.editProfile);
        btnCancel = (Button) view.findViewById(R.id.cancelUpdate);

        firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        accessToken = AccessToken.getCurrentAccessToken();

        //Users
        refUser = database.getReference("User").child(auth.getUid());
        refUser.addListenerForSingleValueEvent(valueEventListener);

        btnEdit.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        btnUpdate.setOnClickListener(updateListener);
        btnEdit.setOnClickListener(updateListenerEdit);
        btnCancel.setOnClickListener(updateListenerCancel);

        return view;
    }

    private View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refUser.addListenerForSingleValueEvent(valueEventListenerUpdate);
        }
    };

    private View.OnClickListener updateListenerEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateLinear.setVisibility(View.VISIBLE);
            displayLinear.setVisibility(View.GONE);

            btnEdit.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener updateListenerCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateLinear.setVisibility(View.GONE);
            displayLinear.setVisibility(View.VISIBLE);

            btnEdit.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
    };

    private ValueEventListener valueEventListenerUpdate = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);

            String firstName = updateFirst.getText().toString();
            String middleName = updateMiddle.getText().toString();
            String lastName = updateLast.getText().toString();

            String displayName = firstName + " " + lastName;

            //Set
            refUser.child("displayName").setValue(displayName);
            refUser.child("firstName").setValue(firstName);
            refUser.child("middleName").setValue(middleName);
            refUser.child("lastName").setValue(lastName);

            //Display
            editName.setText(displayName);

            //Change Layouts
            updateLinear.setVisibility(View.GONE);
            displayLinear.setVisibility(View.VISIBLE);

            //Change Buttons
            btnEdit.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);

            editName.setText(user.getDisplayName());
            editEmail.setText(user.getEmail());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
