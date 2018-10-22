package com.lcgg.price2beat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class StorePageFragment extends Fragment {


    private String name;
    private String logo;

    TextView storeName;

    public StorePageFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public StorePageFragment (String name, String logo) {
        this.name = name;
        this.logo = logo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_store_page, container, false);

        ImageView storeLogo = (ImageView) view.findViewById(R.id.storeListLogo);
        storeName = (TextView) view.findViewById(R.id.storeListName);

        String url = logo;
        Picasso.get().load(logo).into(storeLogo);
        storeName.setText(name);

        return view;
    }

}
