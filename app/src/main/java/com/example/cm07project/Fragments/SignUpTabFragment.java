package com.example.cm07project.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cm07project.AnimatedLoginActivity;
import com.example.cm07project.R;
import com.example.cm07project.RegisterActivity;
import com.example.cm07project.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpTabFragment extends Fragment {
    EditText email, firstname, lastname, password;//confirm;
    Button sign_up;
    Switch org;
    float v = 0;
    FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);


        email = root.findViewById(R.id.emailreg);
        password = root.findViewById(R.id.passwordSignUp);
        firstname = root.findViewById(R.id.firstname);
        lastname = root.findViewById(R.id.lastname);
        org = root.findViewById(R.id.organization);
//        confirm = root.findViewById(R.id.confirm_password);
        sign_up = root.findViewById(R.id.btn_signup);

        mAuth = FirebaseAuth.getInstance();

        sign_up.setOnClickListener(view -> {
            createUser();
        });

        setAnimation();
        return root;
    }

    private void createUser() {
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        String first = firstname.getText().toString();
        String last = lastname.getText().toString();
        Boolean organizer = org.isChecked();


        if (TextUtils.isEmpty(first)){
            firstname.setError("First Name cannot be empty");
            firstname.requestFocus();
        } else if (TextUtils.isEmpty(last)) {
            lastname.setError("Last Name cannot be empty");
            lastname.requestFocus();
        }else if (TextUtils.isEmpty(mail)){
            email.setError("Email cannot be empty");
            email.requestFocus();
        }else if (TextUtils.isEmpty(pass)){
            password.setError("Password cannot be empty");
            password.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        User user = new User(first,last,mail,organizer);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(), "User registered", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getContext(), "Registration Failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), AnimatedLoginActivity.class));
                    }else {
                        Toast.makeText(getContext(), "Registration Failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    public void setAnimation() {
        email.setTranslationX(800);
        password.setTranslationX(800);
        firstname.setTranslationX(800);
        lastname.setTranslationX(800);
        org.setTranslationX(800);
//        confirm.setTranslationX(800);
        sign_up.setTranslationX(800);

        email.setAlpha(v);
        password.setAlpha(v);
        firstname.setAlpha(v);
        lastname.setAlpha(v);
        org.setAlpha(v);
//        confirm.setAlpha(v);
        sign_up.setAlpha(v);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        firstname.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        lastname.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        org.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
//        confirm.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();
        sign_up.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(800).start();


    }
}
