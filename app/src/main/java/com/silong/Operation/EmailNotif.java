package com.silong.Operation;

import android.util.Log;

import com.silong.Object.Adoption;

import com.silong.dev.Timeline;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailNotif {

    private String EMAIL = "silong.sjdm@gmail.com";
    private String PASSWORD = "lbbpxdnxbsfjnsim";
    private String SENDER = "Silong Support";
    private String HOST = "smtp.gmail.com";
    private String PORT = "465";

    public static final int REQUEST_MORE_PHOTO = 11;

    private int STATUS;
    private Adoption ADOPTION;

    private String SUBJECT;
    private String BODY;
    private String RECEIVER;

    //for timeline use only
    public EmailNotif (String email, int status, Adoption adoption){
        this.RECEIVER = email;
        this.STATUS = status;
        this.ADOPTION = adoption;
        setContent(this.STATUS);
    }

    //for OTP use only
    public EmailNotif (String email, String otp){
        SUBJECT = "Silong | One-time PIN";
        BODY = "Hi,\nHere is your one-time PIN: " + otp + "\n\n- Your Silong Team";
        RECEIVER = email;
    }

    private void setContent(int status){
        switch (status){
            case Timeline.CANCELLED:
                SUBJECT = "Silong | Request Cancelled";
                BODY = "Your adoption application for PetID#" + ADOPTION.getPetID() + " on " + ADOPTION.getDateRequested() + " has been CANCELLED.";
                BODY += "\n\n- Your Silong Team";
                break;
        }
    }

    //for request more photo use only
    public EmailNotif (String officeEmail, String userEmail, String userFirstName, String petID){
        SUBJECT = "Silong | Request More Photo";
        BODY = "Hi, I am curious to know if I may request more photos of the pet in the Silong App PetID#"+petID+"."+
                "\nMy email is " + userEmail + ". \n\nThank you! \n\n"+userFirstName+", Silong User";
        RECEIVER = officeEmail;
    }

    boolean sent = false;
    public boolean sendNotif(){

        //try sending the email
        try{
            //set properties
            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", HOST);
            properties.put("mail.smtp.port", PORT);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            //compose mime
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(RECEIVER)));

            mimeMessage.setFrom(new InternetAddress(EMAIL, SENDER));

            mimeMessage.setSubject(SUBJECT);

            mimeMessage.setText(BODY);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                        sent = true;
                    }
                    catch (Exception ex){
                        sent = false;
                        Utility.log("EmailNotif.sendNotif: " + ex.getMessage());
                    }
                }
            });

            thread.start();
            return sent;

        }
        catch (Exception e){
            Utility.log("EmailNotif.sendNotif: " + e.getMessage());
            return false;
        }
    }

}
