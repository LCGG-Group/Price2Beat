package com.lcgg.price2beat;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import android.support.v7.recyclerview.extensions.ListAdapter;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class PurchasesFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;

    RecyclerView mRecyclerView;
    PurchasesAdapter adapter;
    Purchases purchase = new Purchases();
    ArrayList<Purchases> purchases;

    public PurchasesFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        purchases = new ArrayList<Purchases>();

        dbRef = database.getReference("Transactions").child(auth.getUid()).child("pay");
        dbRef.addValueEventListener(purchasesValueListener);

        return view;
    }

    private ValueEventListener purchasesValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot ds : dataSnapshot.getChildren()){
                purchase = ds.getValue(Purchases.class);
                purchases.add(purchase);
            }
            adapter = new PurchasesAdapter(getContext(), purchases);
            mRecyclerView.setAdapter(adapter);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    };

}
