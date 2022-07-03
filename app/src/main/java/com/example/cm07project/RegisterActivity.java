package com.example.cm07project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    Button button;

    FirebaseAuth mAuth;

    EditText memail;
    EditText mpassword;
    EditText mfirstname;
    EditText mlastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.button = findViewById(R.id.signUp);

        memail = findViewById(R.id.email);
        mpassword = findViewById(R.id.password);
        mfirstname = findViewById(R.id.firstname);
        mlastname = findViewById(R.id.Lastname);


        mAuth = FirebaseAuth.getInstance();
        button.setOnClickListener(view -> {
            createUser();
        });


    }

    private void createUser() {
        String mail = memail.getText().toString();
        String pass = mpassword.getText().toString();
        String first = mfirstname.getText().toString();
        String last = mlastname.getText().toString();

         if (TextUtils.isEmpty(first)){
            mfirstname.setError("First Name cannot be empty");
            mfirstname.requestFocus();
         } else if (TextUtils.isEmpty(last)) {
             mlastname.setError("Last Name cannot be empty");
             mlastname.requestFocus();
         }else if (TextUtils.isEmpty(mail)){
            memail.setError("Email cannot be empty");
            memail.requestFocus();
         }else if (TextUtils.isEmpty(pass)){
            mpassword.setError("Password cannot be empty");
            mpassword.requestFocus();
         } else {
            mAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        User user = new User(first,last,mail,false);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(RegisterActivity.this, "Registration Failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, AnimatedLoginActivity.class));
                    }else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}