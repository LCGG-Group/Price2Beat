package com.lcgg.price2beat;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> storeList;
    ArrayList<String> logos;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, ArrayList<String> storeList, ArrayList<String> logos) {
        this.context = applicationContext;
        this.storeList = storeList;
        this.logos = logos;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return storeList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.fragment_storelist, null);


        TextView store = (TextView) view.findViewById(R.id.storeText);
        ImageView imageUrl = (ImageView) view.findViewById(R.id.icon);

        store.setText(storeList.get(i));
        Picasso.get().load(logos.get(i)).into(imageUrl);

        return view;
    }
}