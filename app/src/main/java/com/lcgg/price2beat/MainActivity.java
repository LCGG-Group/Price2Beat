package com.lcgg.price2beat;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Fragment fragment = null;
    FragmentManager transaction = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCenter.start(getApplication(), "59166ca9-1113-49ca-b46d-65c940fab401",
                Analytics.class, Crashes.class);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("Market");
        loadFragment(new MarketFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_store:
                    getSupportActionBar().setTitle("Store");
                    fragment = new StoreFragment();
                    break;
                case R.id.navigation_notifications:
                    getSupportActionBar().setTitle("Market");
                    fragment = new MarketFragment();
                    break;
                case R.id.navigation_settings:
                    getSupportActionBar().setTitle("Wallet");
                    fragment = new SettingsFragment();
                    break;
            }
            return loadFragment(fragment);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.actionbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
                return true;

            case R.id.action_purchases:
                getSupportActionBar().setTitle("Unclaimed Purchases");
                fragment = new PurchasesFragment();
                loadFragment(fragment);
                return true;

            case R.id.action_profile:
                getSupportActionBar().setTitle("Profile");
                fragment = new SettingsFragmentProfile();
                loadFragment(fragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean loadFragment(Fragment fragment) {
        // load fragment
        if(fragment != null){
            transaction.beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                // do what u hv to do for first start activity
                Toast.makeText(MainActivity.this, "My code works", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                // for second start activity
                break;
        }

    }
}
