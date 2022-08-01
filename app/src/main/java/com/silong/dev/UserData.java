package com.silong.dev;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.silong.Object.Address;
import com.silong.Object.Adoption;
import com.silong.Object.Chat;
import com.silong.Object.Favorite;
import com.silong.Object.Pet;
import com.silong.Object.User;
import com.silong.Operation.ImageProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UserData { //removed: extends User

    public static String userID;
    public static String email;
    public static String firstName;
    public static String lastName;
    public static String birthday;
    public static int gender;
    public static String contact;
    public static Bitmap photo;
    public static boolean accountStatus;
    public static int adoptionCounter;
    public static Address address = new Address();
    public static ArrayList<Adoption> adoptionHistory;
    public static ArrayList<Chat> chatHistory;
    public static ArrayList<Favorite> likedPet;

    public static ArrayList<Pet> pets;

    public UserData(){
        /* This class will contain all static data of the current user */

    }

    public static void logout(){
        //Delete user-related local files
        Homepage.USERDATA.delete();
        Homepage.AVATARDATA.delete();
        //Many other to be added later

        //Empty static variables at runtime
        UserData.userID = "";
        UserData.email = "";
        UserData.firstName = "";
        UserData.lastName = "";
        UserData.birthday = "";
        UserData.contact = "";
        UserData.adoptionCounter = 0;
        UserData.address = new Address();
        UserData.adoptionHistory = new ArrayList<Adoption>();
        UserData.chatHistory = new ArrayList<Chat>();
        UserData.likedPet = new ArrayList<Favorite>();
    }

    public static boolean isLoggedIn(Context context){
        File file = new File(context.getFilesDir(), "user.dat");
        return file.exists();
    }

    public static void populate(Activity activity){

        /* Fetch info from APP-SPECIFIC file, then populate static variables */

        //Populate all String and int variables
        try{
            File userdata = Homepage.USERDATA;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(userdata));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                line = line.replace(";","");

                String [] temp = line.split(":");
                switch (temp[0]){
                    case "userID": userID = temp[1]; break;
                    case "email": email = temp[1]; break;
                    case "firstName": firstName = temp[1]; break;
                    case "lastName": lastName = temp[1]; break;
                    case "birthday": birthday = temp[1]; break;
                    case "gender": gender = Integer.parseInt(temp[1]); break;
                    case "contact": contact = temp[1]; break;
                    case "addressLine": address.setAddressLine(temp[1]); break;
                    case "barangay": address.setBarangay(temp[1]);break;
                    case "municipality": address.setMunicipality(temp[1]); break;
                    case "province": address.setProvince(temp[1]); break;
                    case "zipcode":  address.setZipcode(Integer.parseInt(temp[1])); break;
                }

            }
            TextView tv = activity.findViewById(R.id.usernameTv);
            tv.setText(firstName + " " + lastName);
            bufferedReader.close();
        }
        catch (Exception e){
            Log.d("UserData", e.getMessage());
        }

        //Populate Bitmap variable
        try {
            photo = BitmapFactory.decodeFile(Homepage.AVATARDATA.getAbsolutePath());
            ImageView iv = activity.findViewById(R.id.avatarImgview);
            iv.setImageBitmap(UserData.photo);
        }catch (Exception e){
            Log.d("UserData", e.getMessage());
        }

        //Populate Pets
        String sPets = "";
        if ((sPets = readFile(new Homepage().PETDATA)) != null) {
            String[] aPets = sPets.split("\n");
            for (String temp : aPets){
                String[] items = temp.split(";");
                Pet tempPet = new Pet();
                for (String item : items){
                    String[] arr = item.split(":");
                    switch (arr[0]){
                        case "petID": tempPet.setId(Integer.parseInt(arr[1])); break;
                        case "status": tempPet.setStatus(Integer.parseInt(arr[1]));break;
                        case "type": tempPet.setType(Integer.parseInt(arr[1]));break;
                        case "gender": tempPet.setGender(Integer.parseInt(arr[1]));break;
                        case "color": tempPet.setColor(arr[1]);break;
                        case "age": tempPet.setAge(Integer.parseInt(arr[1]));break;
                        case "size": tempPet.setSize(Integer.parseInt(arr[1]));break;
                        case "likes": tempPet.setLikes(Integer.parseInt(arr[1]));break;
                    }
                }
                pets.add(tempPet);
            }
        }

        //Populate Chats
        String sChats = "";
        if ((sChats = readFile(new Homepage().CHATDATA)) != null) {
            String[] aChats = sChats.split("\n");
            for (String temp : aChats){
                String[] items = temp.split(";");
                Chat tempChat = new Chat();
                for (String item : items){
                    String[] arr = item.split(":");
                    switch (arr[0]){
                        case "id": tempChat.setId(Integer.parseInt(arr[1])); break;
                        case "adminEmail": tempChat.setAdminEmail(arr[1]);break;
                        case "date": tempChat.setDate(arr[1]);break;
                        case "time": tempChat.setTime(arr[1]);break;
                        case "content": tempChat.setContent(arr[1]);break;
                    }
                }
                chatHistory.add(tempChat);
            }
        }//App should be able to run even if chats is empty

        //Poplate Adoptions
        String sAdoption = "";
        if ((sAdoption = readFile(new Homepage().CHATDATA)) != null) {
            String[] aAdoption = sAdoption.split("\n");
            for (String temp : aAdoption){
                String[] items = temp.split(";");
                Adoption tempAdoption = new Adoption();
                for (String item : items){
                    String[] arr = item.split(":");
                    switch (arr[0]){
                        case "petID": tempAdoption.setPetID(Integer.parseInt(arr[1])); break;
                        case "dateRequested": tempAdoption.setDateRequested(arr[1]); break;
                        case "appointmentDate": tempAdoption.setAppointmentDate(arr[1]); break;
                        case "status": tempAdoption.setStatus(Integer.parseInt(arr[1])); break;
                        case "dateReleased": tempAdoption.setDateReleased(arr[1]); break;
                    }
                }
                adoptionHistory.add(tempAdoption);
            }
        }//App should be able to run even if adoptions is empty

    }

    private static String readFile(File file){

        /* READS THE CONTENTS OF A FILE */

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String s = "", temp = "";
            while ((temp = bufferedReader.readLine()) != null){
                s += temp;
            }
            return s;
        }catch (Exception e){
            Log.d("UserData", e.getMessage());
            return null;
        }

    }

    private String readFile(Context context, String dir){
        String s = "";
        try{
            FileInputStream fis = context.openFileInput(dir);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (IOException e) {
                Log.d("UserData", e.getMessage());
            } finally {
                s = stringBuilder.toString();
            }
        }
        catch (Exception e){
            Log.d("UserData", "Reading failed: " + dir);
        }

        return s;
    }


}
