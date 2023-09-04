package com.example.pwdschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    Home homeFragment = new Home();
    Profile profileFragment = new Profile();
    WorkOrderCheckSheet workOrderCheckSheet = new WorkOrderCheckSheet();

//    NotificationFragment notificationFragment = new NotificationFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView  = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
//
        if(Home.dbCount>0){
            BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.profile);
            badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(Home.dbCount);}

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,profileFragment).commit();
                        return true;
                    case R.id.progress:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,workOrderCheckSheet).commit();
                        return true;
//                    case R.id.settings:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.container,settingsFragment).commit();
//                        return true;
                }

                return false;
            }
        });

    }
}