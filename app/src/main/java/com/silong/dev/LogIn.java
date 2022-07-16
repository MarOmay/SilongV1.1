package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private FirebaseAuth auth;

    Button signUp, logIn;
    EditText tfloginEmail, tfloginPassword;
    TextView forgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for hiding status bar
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //for transpa status bar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Toast.makeText(this, "Initialize Firebase", Toast.LENGTH_SHORT).show();

        auth = FirebaseAuth.getInstance();

        Toast.makeText(this, "Firebase Live", Toast.LENGTH_SHORT).show();

        tfloginEmail = findViewById(R.id.tfloginEmail);
        tfloginPassword = findViewById(R.id.tfloginPassword);

        signUp = (Button) findViewById(R.id.btnSignup);
        logIn = (Button) findViewById(R.id.btnLogin);
        forgotPass = (TextView) findViewById(R.id.forgotPassword);


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

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassDia(LogIn.this);
            }
        });
    }

    private void attemptLogin(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LogIn.this, "Login successful!", Toast.LENGTH_SHORT).show();
                Toast.makeText(LogIn.this, "User: " + auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LogIn.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetPassword(String email){

        //Send a password reset link to email
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //code here
                        }
                    }
                });
    }

    //method for forgot password dialog
    public void forgotPassDia(Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(Html.fromHtml("<b>"+"Forgot Password"+"</b>"));
        builder.setIcon(getDrawable(R.drawable.forgotpass_icon));
        builder.setBackground(getDrawable(R.drawable.dialog_bg));
        builder.setMessage("\nEnter registered email address.");

        LinearLayout recov_layout = new LinearLayout(context);
        recov_layout.setOrientation(LinearLayout.VERTICAL);
        recov_layout.setVerticalGravity(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(60,0,60,0);
        EditText et_recovEmail = new EditText(context);
        et_recovEmail.setBackground(getResources().getDrawable(R.drawable.tf_background));
        et_recovEmail.setPadding(30,0,0,0);
        et_recovEmail.setHint("Email Address");
        et_recovEmail.setTextSize(15);
        et_recovEmail.setLayoutParams(params);
        et_recovEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        recov_layout.addView(et_recovEmail);
        builder.setView(recov_layout);

        builder.setPositiveButton(Html.fromHtml("<b>"+"SUBMIT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Codes here
                accountRecovDia(context);
            }
        });
        builder.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
        builder.show();
    }

    //method for Account Recovery Dialog
    public void accountRecovDia(Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(Html.fromHtml("<b>"+"Account Recovery"+"</b>"));
        builder.setIcon(R.drawable.accrecovery_icon);
        builder.setBackground(getDrawable(R.drawable.dialog_bg));
        builder.setMessage(getResources().getString(R.string.accRecovMsg));

        builder.setPositiveButton(Html.fromHtml("<b>"+"OK"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Codes here
            }
        });
        builder.show();
    }

    //for transpa status bar
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}