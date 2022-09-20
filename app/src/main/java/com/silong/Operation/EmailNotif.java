package com.silong.Operation;

import android.util.Log;

import com.silong.Object.Adoption;
import com.silong.Object.Pet;
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
    private String HOST = "smtp.gmail.com";
    private String PORT = "465";

    private int STATUS;
    private Adoption ADOPTION;

    private String SUBJECT;
    private String BODY;
    private String RECEIVER;

    public EmailNotif (String email, int status, Adoption adoption){
        this.RECEIVER = email;
        this.STATUS = status;
        this.ADOPTION = adoption;
        setContent(this.STATUS);
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

    boolean sent = false;
    public boolean sendNotif(){

        Log.d("DEBUGGER>>>", "Sending email");

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
                        Log.d("DEBUGGER>>>", "EmailNotif - thread: " + ex.getMessage());
                    }
                }
            });

            thread.start();
            return sent;

        }
        catch (Exception e){
            return false;
        }
    }

}
