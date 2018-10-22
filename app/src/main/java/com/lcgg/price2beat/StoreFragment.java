package com.lcgg.price2beat;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.net.wifi.WifiConfiguration.Status.strings;
import static com.facebook.FacebookSdk.getApplicationContext;

public class StoreFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference refStore;
    Query query;

    ArrayList<String> storeListName = new ArrayList<String>();
    ArrayList<String> storeListImage = new ArrayList<String>();
    CustomAdapter customAdapter;

    ListView storeList;
    Store store;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        refStore = database.getReference("Store");
        refStore.addListenerForSingleValueEvent(valueEventListener);

        View view = inflater.inflate(R.layout.fragment_store, container, false);
        storeList = (ListView) view.findViewById(R.id.storeListView);

        customAdapter = new CustomAdapter(getActivity(), storeListName, storeListImage);
        storeList.setAdapter(customAdapter);

        storeList.setClickable(true);
        storeList.setOnItemClickListener(itemSelected );

        return view;
    }

    private AdapterView.OnItemClickListener itemSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String name = storeListName.get(position);
            String logo = storeListImage.get(position);

            ((FragmentActivity) view.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, new StorePageFragment(name, logo))
                    .commit();

        }
    };


    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot ds : dataSnapshot.getChildren()){
                store = ds.getValue(Store.class);
                //Toast.makeText(getActivity(), store.getName(), Toast.LENGTH_SHORT).show();
                storeListName.add(store.getName());
                storeListImage.add(store.getImageUrl());

                customAdapter.notifyDataSetChanged();
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
