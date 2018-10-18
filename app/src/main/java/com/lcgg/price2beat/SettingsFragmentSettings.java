package com.lcgg.price2beat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragmentSettings extends Fragment {

    private FirebaseAuth auth;

    FirebaseDatabase database;
    DatabaseReference refUser, refWallet;

    TextView editAmount;
    Wallet wallet;


    public SettingsFragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings_settings, container, false);
        editAmount = (TextView) view.findViewById(R.id.amount);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        //Wallet
        refWallet = database.getReference("Wallet").child(auth.getUid());
        refWallet.addListenerForSingleValueEvent(valueEventListenerWallet);

        return view;
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

}
