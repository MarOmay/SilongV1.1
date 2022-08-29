package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.EmailPrompt;
import com.silong.CustomView.ResetLinkNotice;
import com.silong.Operation.InputValidator;
import com.silong.Operation.Utility;

import java.util.Timer;
import java.util.TimerTask;

public class LogIn extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    Button signUp, logIn;
    EditText tfloginEmail, tfloginPassword;
    TextView forgotPass;
    static boolean passwordVisible = false;
    int ctr = 0;
    ImageView showHideIv;

    private int loginAttempts = 0;
    private boolean allowLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for hiding status bar
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //for transpa status bar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            Utility.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            Utility.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //Initialize Firebase objects
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Receive email
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mEmailReceiver, new IntentFilter("reset-password-email"));

        tfloginEmail = findViewById(R.id.tfloginEmail);
        tfloginPassword = findViewById(R.id.tfloginPassword);
        signUp = (Button) findViewById(R.id.btnSignup);
        logIn = (Button) findViewById(R.id.btnLogin);
        forgotPass = (TextView) findViewById(R.id.forgotPassword);
        showHideIv = (ImageView) findViewById(R.id.showHideIv);

        //For auto-fill after registration
        try {
            String email = (String) getIntent().getStringExtra("email");
            String password = (String) getIntent().getStringExtra("password");
            tfloginEmail.setText(email);
            tfloginPassword.setText(password);
        }
        catch (Exception e){
            //ignore, no value passed by previous activity
        }

    }

    public void onPressedLogin(View view){
        String email = tfloginEmail.getText().toString().trim();
        String password = tfloginPassword.getText().toString();

        if (email.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter your email.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!InputValidator.checkEmail(email)){
            Toast.makeText(getApplicationContext(), "Please check the format of your email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (loginAttempts >= 3){
            Toast.makeText(getApplicationContext(), "Too many failed attempts.", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Try again after 60 seconds.", Toast.LENGTH_LONG).show();
            loginAttempts = 0;
            allowLogin = false;
            logIn.setTextColor(Color.LTGRAY);

            try{
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        allowLogin = true;
                        Button btn = findViewById(R.id.btnLogin);
                        btn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whitey));
                    }
                }, 1*(60*1000));
            }
            catch (Exception e){
                Log.d("LogIn", e.getMessage());
            }

            return;
        }

        if (password.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(allowLogin){
            attemptLogin(email, password);
        }
        else {
            Toast.makeText(LogIn.this, "Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void onPressedSignUp(View view){
        Intent intent = new Intent(LogIn.this, SignUp.class);
        startActivity(intent);
        finish();
    }

    public void onPressedForgotPassword(View view){
        //Get email
        EmailPrompt emailPrompt = new EmailPrompt(LogIn.this);
        emailPrompt.show();
    }

    private void attemptLogin(String email, String password){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        //Check internet connection
        if (Utility.internetConnection(getApplicationContext())){
            //attempt sign in
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            loginAttempts = 0;
                            //check if regular access
                            String uid = mAuth.getCurrentUser().getUid();
                            try {
                                mReference = mDatabase.getReference("Users/" + uid + "/accountStatus");
                                mReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean accountStatus = false;

                                        try {
                                            accountStatus = (Boolean) snapshot.getValue();

                                            if (accountStatus){
                                                loadingDialog.dismissLoadingDialog();
                                                Intent intent = new Intent(LogIn.this, LoginLoadingScreen.class);
                                                intent.putExtra("UID", mAuth.getCurrentUser().getUid());
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                loadingDialog.dismissLoadingDialog();
                                                Intent intent = new Intent(LogIn.this, DeactivatedScreen.class);
                                                intent.putExtra("uid", mAuth.getCurrentUser().getUid());
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                        catch (Exception e){
                                            Toast.makeText(LogIn.this, "Unauthorized access.", Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismissLoadingDialog();
                                            Log.d("LogIn", e.getMessage());
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            catch (Exception e){
                                Toast.makeText(LogIn.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                                Log.d("LogIn", e.getMessage());
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loginAttempts++;
                            loadingDialog.dismissLoadingDialog();
                            Toast.makeText(LogIn.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

    }

    private void emailChecker(Context context, String email){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        //Check internet connection
        if(Utility.internetConnection(getApplicationContext())){
            //Check if email is registered
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            loadingDialog.dismissLoadingDialog();
                            if (task.getResult().getSignInMethods().isEmpty()){
                                Toast.makeText(getApplicationContext(), "Email is not registered.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                //Trigger Firebase to send instruction email
                                resetPassword(context, email);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Can't check your email right now.", Toast.LENGTH_SHORT).show();
                            Log.d("LogIn", e.getMessage());
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
        loadingDialog.dismissLoadingDialog();

    }

    private void resetPassword(Context context, String email){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        //Send a password reset link to email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.dismissLoadingDialog();
                        if (task.isSuccessful()) {
                            //Show email instruction dialog
                            //accountRecovDia(context);
                            ResetLinkNotice resetLinkNotice = new ResetLinkNotice(LogIn.this);
                            resetLinkNotice.show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismissLoadingDialog();
                    }
                });
    }

    private BroadcastReceiver mEmailReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String email = intent.getStringExtra("email");
                emailChecker(getApplicationContext(), email);
            }
            catch (Exception e){
                Log.d("LogIn", e.getMessage());
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mEmailReceiver);
        super.onDestroy();
    }

    public void onShowHide(View view){
        if (ctr == 0){
            new Utility().passwordFieldTransformer(tfloginPassword, true);
            showHideIv.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_24));
            tfloginPassword.setSelection(tfloginPassword.getText().length());
            ctr++;
        }
        else {
            new Utility().passwordFieldTransformer(tfloginPassword, false);
            showHideIv.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_off_24));
            tfloginPassword.setSelection(tfloginPassword.getText().length());
            ctr--;
        }
    }
}