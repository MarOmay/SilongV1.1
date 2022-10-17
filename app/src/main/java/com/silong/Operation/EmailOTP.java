package com.silong.Operation;

import android.app.Activity;
import android.content.Context;



import java.security.SecureRandom;

public class EmailOTP {

    private Activity activity;
    private Context context;

    private String email;

    private String OTP;

    public EmailOTP(Activity activity, String email){
        this.activity = activity;
        this.context = (Context) activity;
        this.email = email;
        this.OTP = generateOTP();
    }

    private String generateOTP(){
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(999999);
        return String.format("%06d", num);
    }

    public boolean sendOTP(){
        return new EmailNotif(email, OTP).sendNotif();
    }

    public String getOTP(){
        return OTP;
    }
}
