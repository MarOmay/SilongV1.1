package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private FirebaseAuth auth;

    Button signUp, logIn;
    EditText tfloginEmail, tfloginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for hiding status bar
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Toast.makeText(this, "Initialize Firebase", Toast.LENGTH_SHORT).show();

        auth = FirebaseAuth.getInstance();

        Toast.makeText(this, "Firebase Live", Toast.LENGTH_SHORT).show();

        tfloginEmail = findViewById(R.id.tfloginEmail);
        tfloginPassword = findViewById(R.id.tfloginPassword);

        signUp = (Button) findViewById(R.id.btnSignup);
        logIn = (Button) findViewById(R.id.btnLogin);


        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tfloginEmail.getText().toString();
                String password = tfloginPassword.getText().toString();

                if (email.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter your email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                attemptLogin(email, password);
            }
        });

        //intent to SignUp Screen
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LogIn.this, "Login successful!", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LogIn.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}