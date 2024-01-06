package com.taas.petsconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.taas.petsconnect.Fragment.FriendFragment;
import com.taas.petsconnect.Fragment.addpost;
import com.taas.petsconnect.Fragment.home;
import com.taas.petsconnect.Fragment.Marketplace;
import com.taas.petsconnect.Fragment.notification;
import com.taas.petsconnect.Fragment.profile;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Add this to your Application class or main Activity
        FirebaseApp.initializeApp(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MainActivity.this.setTitle("My profile");

        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        // Create an instance of the PagerAdapter
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        // Set the adapter to the ViewPager
        viewPager.setAdapter(pagerAdapter);

        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Set custom tab icons
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(pagerAdapter.getTabView(i));
            }
        }

        // Set initial fragment and toolbar visibility
        Fragment currentFragment = pagerAdapter.getItem(0);
        setToolbarVisibility(0); // Pass the position of the initial fragment

        // Add a listener to track fragment changes
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Update the toolbar visibility when a new fragment is selected
                setToolbarVisibility(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setToolbarVisibility(int position) {
        boolean isVisible = position == 3;  // Show toolbar for the "profile" fragment

        if (isVisible) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    // PagerAdapter class for handling fragments
    public class PagerAdapter extends FragmentPagerAdapter {

        private final int[] tabIcons = {
                R.drawable.petshomeeicon,
                R.drawable.petsfriendicon,
                R.drawable.add,
                R.drawable.petsprofile,
                R.drawable.notification,
                R.drawable.market
        };

        @SuppressLint("WrongConstant")
        public PagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            // Return the appropriate fragment based on the position
            switch (position) {
                case 0:
                    return new home();
                case 1:
                    return new FriendFragment();
                case 2:
                    return new addpost();
                case 3:
                    return new profile();
                case 4:
                    return new notification();
                case 5:
                    return new Marketplace();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabIcons.length;
        }

        public View getTabView(int position) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);
            ImageView tabIcon = view.findViewById(R.id.tabIcon);
            tabIcon.setImageResource(tabIcons[position]);
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                Toast.makeText(this, "Log out Successfully", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
