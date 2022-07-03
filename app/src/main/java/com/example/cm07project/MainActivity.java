package com.example.cm07project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cm07project.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fm = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setVisibility(View.INVISIBLE);
        LoadingFragment loadingFragment = new LoadingFragment();
        fm.beginTransaction().replace(R.id.container, loadingFragment).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            //Intent intent = new Intent(this, LoginActivity.class);
            Intent intent = new Intent(this, AnimatedLoginActivity.class);

            startActivity(intent);
        }
    }

    protected void goToProfile(){
        this.bottomNavigationView.getMenu().getItem(4).setChecked(true);
        ProfileFragment profileFragment = new ProfileFragment();
        fm.beginTransaction().replace(R.id.container, profileFragment).commit();
    }

    protected void goToEvents(){
        this.bottomNavigationView.getMenu().getItem(2).setChecked(true);
        EventsFragment eventsFragment = new EventsFragment();
        fm.beginTransaction().replace(R.id.container, eventsFragment).commit();
    }

    protected void appInit(){
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            for(int i = 0; i < fm.getBackStackEntryCount(); i++) {
                fm.popBackStack();
            }
            int id = item.getItemId();
            switch(id){
                case(R.id.navigation_messages):
//                    MessagesFragment messagesFragment = new MessagesFragment();
//                    fm.beginTransaction().replace(R.id.container, messagesFragment).commit();
                    LastChatsFragment lastChatsFragment = new LastChatsFragment();
                    fm.beginTransaction().replace(R.id.container, lastChatsFragment).commit();
                    break;

                case(R.id.navigation_map):
                    MapsFragment mapsFragment = new MapsFragment();
                    fm.beginTransaction().replace(R.id.container, mapsFragment).commit();
                    break;

                case(R.id.navigation_events):
                    EventsFragment eventsFragment = new EventsFragment();
                    fm.beginTransaction().replace(R.id.container, eventsFragment).commit();
                    break;

                case(R.id.navigation_products):
                    ProductsFragment productsFragment = new ProductsFragment();
                    fm.beginTransaction().replace(R.id.container, productsFragment).commit();
                    break;

                case(R.id.navigation_profile):
                    ProfileFragment profileFragment = new ProfileFragment();
                    fm.beginTransaction().replace(R.id.container, profileFragment).commit();
                    break;
            }
            return true;
        });
        goToEvents();
    }

}