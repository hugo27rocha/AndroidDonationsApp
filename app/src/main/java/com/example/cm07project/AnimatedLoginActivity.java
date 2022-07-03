package com.example.cm07project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm07project.Adapter.LoginAdapter;
import com.example.cm07project.Fragments.LoginTabFragment;
import com.example.cm07project.Fragments.SignUpTabFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AnimatedLoginActivity extends AppCompatActivity {

    TabLayout mTabLayout;
    ViewPager mViewPager;
    FloatingActionButton fb,google,twitter;
    float v = 0;

    LoginTabFragment loginTabFragment;
    SignUpTabFragment signUpTabFragment;

    Button button;
    TextView registerText;

    EditText email;
    EditText password;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.button = findViewById(R.id.btn_login);
        //this.registerText = findViewById(R.id.registerText);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);





        /*registerText.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });*/





        mTabLayout = findViewById(R.id.tab_layout);



        mViewPager = findViewById(R.id.view_pager);

        mTabLayout.addTab(mTabLayout.newTab().setText("Login"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Sign Up"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        loginTabFragment = new LoginTabFragment();
        signUpTabFragment = new SignUpTabFragment();

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(),this,mTabLayout.getTabCount(), signUpTabFragment, loginTabFragment);
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0)
                    loginTabFragment.setAnimation();
                if(position==1)
                    signUpTabFragment.setAnimation();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setTranslationY(300);


        mTabLayout.setAlpha(v);


        mTabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
    }




}