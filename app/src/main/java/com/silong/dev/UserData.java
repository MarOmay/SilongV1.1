package com.silong.dev;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.silong.Object.Adoption;
import com.silong.Object.Chat;
import com.silong.Object.Pet;
import com.silong.Object.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class UserData extends User {

    public ArrayList<Pet> pets;

    public UserData(){
        /* This class will contain all static data of the current user */

    }

    public boolean populate(){

        /* Fetch info from APP-SPECIFIC file, then populate static variables */

        boolean allGood = true;

        //Populate all String and int variables
        String s = "";
        if ((s = readFile(new Homepage().USERDATA)) != null){

            String[] data = s.split(";");

            for (String item : data) {
                String [] temp = item.split(":");
                switch (temp[0]){
                    case "userID": userID = temp[1]; break;
                    case "email": email = temp[1]; break;
                    case "firstName": firstName = temp[1]; break;
                    case "lastName": lastName = temp[1]; break;
                    case "gender": gender = Integer.parseInt(temp[1]); break;
                    case "addressLine": address.setAddressLine(temp[1]); break;
                    case "barangay": address.setBarangay(temp[1]);break;
                    case "municipality": address.setMunicipality(temp[1]); break;
                    case "province": address.setProvince(temp[1]); break;
                    case "zipcode":  address.setZipcode(Integer.parseInt(temp[1])); break;
                }
            }
        }
        else allGood = false;

        //Populate Bitmap variable
        try {
            photo = BitmapFactory.decodeFile(new Homepage().AVATARDATA.getAbsolutePath());
        }catch (Exception e){
            allGood = false;
            //photo = default pic from drawable
            e.printStackTrace();
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
        else allGood = false;

        //Poplate Chats
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

        return allGood;
    }

    private String readFile(File file){

        /* READS THE CONTENTS OF A FILE */

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String s = "", temp = "";
            while ((temp = bufferedReader.readLine()) != null){
                s += temp;
            }
            return s;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


}
