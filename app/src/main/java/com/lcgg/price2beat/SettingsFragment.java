package com.lcgg.price2beat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.graphics.Bitmap;

import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class SettingsFragment extends Fragment {

    private FirebaseAuth auth;

    private TabLayout tabLayout;
    private ViewPager viewPager;


    public SettingsFragment() {
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

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout_id);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager_id);

        ViewPagerAdapter adapter =  new ViewPagerAdapter(getChildFragmentManager());

        adapter.AddFragment(new SettingsFragmentProfile(), "Profile");
        adapter.AddFragment(new SettingsFragmentSettings(), "Wallet");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        FirebaseUser user = auth.getCurrentUser();


        return view;
    }

}
