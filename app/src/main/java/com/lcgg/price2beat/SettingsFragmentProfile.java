package com.lcgg.price2beat;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

import java.util.List;
import java.util.Map;

public class SettingsFragmentProfile extends Fragment {

    public final static int QRcodeWidth = 500 ;
    private FirebaseAuth auth;

    FirebaseDatabase database;
    DatabaseReference refUser;

    TextView editName, editEmail, editPoints;
    User user;

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

        editName = (TextView) view.findViewById(R.id.profileName);
        editEmail = (TextView) view.findViewById(R.id.profileEmail);
        editPoints = (TextView) view.findViewById(R.id.points);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        //Users
        refUser = database.getReference("User").child(auth.getUid());
        refUser.addListenerForSingleValueEvent(valueEventListener);

        return view;
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);

            editName.setText(user.getDisplayName());
            editEmail.setText(user.getEmail());
            editPoints.setText(user.getPoints().toString());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
