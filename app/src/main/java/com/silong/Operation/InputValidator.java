package com.silong.Operation;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {

    public static boolean checkName(String s){

        //Check if s only contains allowed characters
        Pattern namePattern = Pattern.compile("^[A-Za-z](?=.{1,29}$)[A-Za-z]*(?:\\h+[A-Za-z][A-Za-z]*)*$");
        try {
            Matcher matcher = namePattern.matcher(s);
            if (!matcher.matches()){
                return false;
            }
        }
        catch (Exception e){
            Log.d("InputValidator", e.getMessage());
            return false;
        }

        return true;
    }

    public static boolean checkEmail(String s){
        //Check if s only contains allowed characters
        Pattern namePattern = Pattern.compile("^(.+)@(.+)$");
        try {
            Matcher matcher = namePattern.matcher(s);
            if (!matcher.matches()){
                return false;
            }
        }
        catch (Exception e){
            Log.d("InputValidator", e.getMessage());
            return false;
        }

        return true;
    }

    public static boolean checkContact(String s){
        //Check if contact contain enough numbers
        if (s.length() < 11 || s.length() > 11){
            return false;
        }
        else if (!s.startsWith("09")){
            return false;
        }

        //Check if contact contains letters or special chars
        for (int i = 0; i < s.length(); i++){
            try {
                Integer.parseInt(String.valueOf(s.charAt(i)));
            }
            catch (Exception e){
                Log.d("SignUp", e.getMessage());
                return false;
            }
        }

        return true;
    }

}
