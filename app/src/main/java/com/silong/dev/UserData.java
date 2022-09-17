package com.silong.dev;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.silong.Object.Address;
import com.silong.Object.Adoption;
import com.silong.Object.Chat;
import com.silong.Object.Favorite;
import com.silong.Object.Pet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    public static ArrayList<Adoption> adoptionHistory = new ArrayList<>();
    public static ArrayList<Chat> chatHistory;
    public static ArrayList<Favorite> likedPet;

    public static ArrayList<Pet> pets = new ArrayList<>();

    public UserData(){
        /* This class will contain all static data of the current user */

    }

    public static void logout(Activity activity){
        //Delete user-related local files
        Homepage.USERDATA.delete();
        Homepage.AVATARDATA.delete();
        //Many other to be added later

        for (File file : activity.getFilesDir().listFiles()){
            if (file.getAbsolutePath().contains("adoption-") || file.getAbsolutePath().contains("adoptionpic-"))
                file.delete();
        }

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
            Log.d("UserData", " " + e.getMessage());
        }

        //Populate Bitmap variable
        try {
            photo = BitmapFactory.decodeFile(Homepage.AVATARDATA.getAbsolutePath());
            ImageView iv = activity.findViewById(R.id.avatarImgview);
            iv.setImageBitmap(UserData.photo);
        }catch (Exception e){
            Log.d("UserData", e.getMessage());
        }

    }

    public static void populateRecords(Activity activity){
        //Clear current ArrayList to avoid duplicate entries
        pets.clear();

        /* Fetch info from APP-SPECIFIC file, then populate static variables */

        try{
            ArrayList<File> petRecords = new ArrayList<File>();

            for (File file : activity.getFilesDir().listFiles()){
                if (file.getAbsolutePath().contains("pet-")){
                    petRecords.add(file);
                }
            }

            //read each account info
            for (File record : petRecords){
                Pet pet = new Pet();

                //Read basic info
                BufferedReader bufferedReader = new BufferedReader(new FileReader(record));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    line = line.replace(";","");

                    String [] temp = line.split(":");
                    switch (temp[0]){
                        case "petID": pet.setPetID(temp[1]); break;
                        case "status": pet.setStatus(Integer.parseInt(temp[1])); break;
                        case "type": pet.setType(Integer.parseInt(temp[1])); break;
                        case "gender": pet.setGender(Integer.parseInt(temp[1])); break;
                        case "size": pet.setSize(Integer.parseInt(temp[1])); break;
                        case "age": pet.setAge(Integer.parseInt(temp[1])); break;
                        case "color" : pet.setColor(temp[1]); break;
                        case "liked" : pet.setLiked(temp[1].equals("true") ? true : false); break;
                        case "lastModified" : pet.setLastModified(temp[1]); break;
                    }

                }
                bufferedReader.close();

                //Read avatar
                try{
                    pet.setPhoto(BitmapFactory.decodeFile(activity.getFilesDir() + "/petpic-" + pet.getPetID()));
                }
                catch (Exception e){
                    Log.d("AdminData-pR", e.getMessage());
                }

                pets.add(pet);
            }

        }
        catch (Exception e){
            Log.d("AdminData-pR", e.getMessage());
        }
    }

    public static void populateAdoptions(Activity activity){
        //Clear current ArrayList to avoid duplicate entries
        adoptionHistory.clear();

        /* Fetch info from APP-SPECIFIC file, then populate static variables */

        try{
            ArrayList<File> adoptions = new ArrayList<File>();

            for (File file : activity.getFilesDir().listFiles()){
                if (file.getAbsolutePath().contains("adoption-") && !file.getAbsolutePath().contains("null")){
                    adoptions.add(file);
                }
            }

            //read each account info
            for (File record : adoptions){
                Adoption adoption = new Adoption();

                Log.d("DEBUGGER>>>", "Adoption file: " + record.getAbsolutePath());

                //Read basic info
                BufferedReader bufferedReader = new BufferedReader(new FileReader(record));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    line = line.replace(";","");

                    String [] temp = line.split(":");
                    switch (temp[0]){
                        case "petID": adoption.setPetID(temp[1]); break;
                        case "gender": adoption.setGender(Integer.parseInt(temp[1])); break;
                        case "type": adoption.setType(Integer.parseInt(temp[1])); break;
                        case "age": adoption.setAge(Integer.parseInt(temp[1])); break;
                        case "size": adoption.setSize(Integer.parseInt(temp[1])); break;
                        case "color": adoption.setColor(temp[1]); break;
                        case "status": adoption.setStatus(Integer.parseInt(temp[1])); break;
                        case "dateRequested": adoption.setDateRequested(temp[1]); break;
                        case "appointmentDate": adoption.setAppointmentDate(temp[1]); break;
                        case "dateReleased": adoption.setDateReleased(temp[1]); break;
                    }

                }
                bufferedReader.close();

                //Read avatar
                try{
                    adoption.setPhoto(BitmapFactory.decodeFile(activity.getFilesDir() + "/adoptionpic-" + adoption.getPetID()));
                }
                catch (Exception e){
                    Log.d("AdminData-pA", e.getMessage());
                }

                Log.d("DEBUGGER>>>", "AH " + adoption.getPetID());
                Log.d("DEBUGGER>>>", adoption.getDateRequested());
                Log.d("DEBUGGER>>>", "" + adoption.getStatus());

                //check duplicate
                boolean found = false;
                for (Adoption adpt : adoptionHistory){
                    if (adpt.getPetID().equals(adoption.getPetID()))
                        found = true;
                }

                if (!found)
                    adoptionHistory.add(adoption);
            }

        }
        catch (Exception e){
            Log.d("AdminData-pA", e.getMessage());
        }
    }

    public static void deleteAdoptionByID(Activity activity, String petID){
        for (File file : activity.getFilesDir().listFiles()){
            if (file.getAbsolutePath().contains("adoption-")){
                String id = "";
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = bufferedReader.readLine()) != null){
                        line = line.replace(";","");

                        String [] temp = line.split(":");
                        switch (temp[0]){
                            case "petID": id = temp[1]; break;
                        }
                    }
                }
                catch (Exception e){
                    Log.d("DEBUGGER>>>", "Exception occured reading " + file.getAbsolutePath());
                }

                if (petID.equals(id)){
                    file.delete();
                }
            }
        }
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

    public static void writePetToLocal(Context context, String filename, String desc, String content){
        //Check if file exists
        File file = new File(context.getFilesDir() + "/pet-" + filename);
        if (!file.exists()){
            try{
                FileOutputStream fileOuputStream = context.openFileOutput("pet-" + filename, Context.MODE_PRIVATE);
            }
            catch (Exception e){
                Log.d("UserData-wPTL0", e.getMessage());
            }
        }
        //Create local storage copy of pet profile
        try (FileOutputStream fileOutputStream = context.openFileOutput( "pet-" + filename, Context.MODE_APPEND)) {
            String data = desc + ":" + content + ";\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Log.d("UserData-wPTL1", e.getMessage());
        }
    }

    public static Pet fetchRecordFromLocal(Activity activity, String uid){
        Pet pet = new Pet();

        try{
            File file = new File(activity.getFilesDir(), "pet-" + uid);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                line = line.replace(";","");

                String [] temp = line.split(":");
                switch (temp[0]){
                    case "petID": pet.setPetID(temp[1]); break;
                    case "status": pet.setStatus(Integer.parseInt(temp[1])); break;
                    case "type": pet.setType(Integer.parseInt(temp[1])); break;
                    case "gender": pet.setGender(Integer.parseInt(temp[1])); break;
                    case "size": pet.setSize(Integer.parseInt(temp[1])); break;
                    case "age": pet.setAge(Integer.parseInt(temp[1])); break;
                    case "color" : pet.setColor(temp[1]); break;
                    case "lastModified" : pet.setLastModified(temp[1]); break;
                }

            }
            bufferedReader.close();
        }
        catch (Exception e){
            Log.d("UserData-fRFL", e.getMessage());
        }

        return pet;
    }

    public static Pet getPet(String id) {
        for (Pet p : UserData.pets){
            if (p.getPetID().equals(id))
                return p;
        }
        return null;
    }

    public static Adoption fetchAdoptionFromLocal(Activity activity, String uid){
        Adoption adoption = new Adoption();

        try{
            File file = new File(activity.getFilesDir(), "adoption-" + uid);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                line = line.replace(";","");

                String [] temp = line.split(":");
                switch (temp[0]){
                    case "petID": adoption.setPetID(temp[1]); break;
                    case "status": adoption.setStatus(Integer.parseInt(temp[1])); break;
                    case "dateRequested": adoption.setDateRequested(temp[1]); break;
                    case "appointmentDate": adoption.setAppointmentDate(temp[1]); break;
                    case "dateReleased": adoption.setDateReleased(temp[1]); break;
                }

            }
            bufferedReader.close();
        }
        catch (Exception e){
            Log.d("UserData-fAFL", e.getMessage());
        }

        return adoption;
    }

    public static void writeAdoptionToLocal(Context context, String filename, String desc, String content){
        //Check if file exists
        File file = new File(context.getFilesDir() + "/adoption-" + filename);
        if (!file.exists()){
            try{
                FileOutputStream fileOuputStream = context.openFileOutput("adoption-" + filename, Context.MODE_PRIVATE);
            }
            catch (Exception e){
                Log.d("UserData-wATL0", e.getMessage());
            }
        }
        //Create local storage copy of pet profile
        try (FileOutputStream fileOutputStream = context.openFileOutput( "adoption-" + filename, Context.MODE_APPEND)) {
            String data = desc + ":" + content + ";\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Log.d("UserData-wATL1", e.getMessage());
        }
    }

}
