package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Adapter.SwipeAdapter;
import com.silong.CustomView.ApplyDialog;
import com.silong.CustomView.ExitDialog;
import com.silong.CustomView.HomepageExitDialog;
import com.silong.CustomView.PendingApplicationNotice;
import com.silong.CustomView.RelaunchNotifier;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetType;
import com.silong.Object.Adoption;
import com.silong.Object.Pet;

import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;
import com.silong.Task.AccountStatusChecker;
import com.silong.Task.SyncAdoptionHistory;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Homepage extends AppCompatActivity {

    public static boolean DOG = true;
    public static boolean CAT = true;
    public static boolean PUPPY = true;
    public static boolean YOUNG = true;
    public static boolean OLD = true;
    public static boolean MALE = true;
    public static boolean FEMALE = true;
    public static boolean LIKED = false;

    /* APP-SPECIFIC FILES */
    protected static File USERDATA;
    protected static File AVATARDATA;
    protected static File ADOPTIONDATA;
    protected static File CHATDATA;
    protected static File CHATCONFIG;
    protected static File PETDATA;
    protected static File FAVORITECONFIG;

    //Firebase objects
    private FirebaseAnalytics mAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public static String FB_ID = null;

    DrawerLayout drawerLayout;

    TextView headerTitle, editProfileTv, indexTrackerTv;
    ImageView filterImgview, messageImgview, menuImgview, closeDrawerBtn, infoIcon;
    Button applyBtn, aboutOfficeBtn, aboutUsBtn,exitBtn;
    Koloda koloda;

    ImageView avatarImgview;
    TextView usernameTv;

    private ArrayList<Pet> tempPetList = new ArrayList<>();
    private Pet CURRENT_PET = new Pet();

    public static boolean BEGIN_APPLY = false;

    public static boolean RESTART_REQUIRED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        getSupportActionBar().hide();

        BEGIN_APPLY = false;

        //Initialize Files
        USERDATA = new File(getFilesDir(),"user.dat");
        AVATARDATA = new File(getFilesDir(),"avatar.dat");
        ADOPTIONDATA = new File(getFilesDir(),"adoption.dat");
        CHATDATA = new File(getFilesDir(),"chat.dat");
        CHATCONFIG = new File(getFilesDir(),"chat.config");
        PETDATA = new File(getFilesDir(),"pet.dat");
        FAVORITECONFIG = new File(getFilesDir(),"favorite.config");

        //Initialize Firebase Objects
        mAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Receive logout trigger
        LocalBroadcastManager.getInstance(this).registerReceiver(mLogoutReceiver, new IntentFilter("logout-user"));
        //Receive trigger from ASC task initiated by Apply button
        LocalBroadcastManager.getInstance(this).registerReceiver(mBeginApplication, new IntentFilter("account-status-active"));
        //Loading dialog receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mLoadingReceiver, new IntentFilter("toggle-loading-dialog"));

        //Initialize layout views
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        View view = findViewById(R.id.headerLayout);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        filterImgview = (ImageView) findViewById(R.id.filterImgview);
        messageImgview = (ImageView) findViewById(R.id.messageImgview);
        menuImgview = (ImageView) findViewById(R.id.menuImgview);
        koloda = findViewById(R.id.koloda);
        indexTrackerTv = findViewById(R.id.indexTrackerTv);
        applyBtn = (Button) findViewById(R.id.applyBtn);
        closeDrawerBtn = (ImageView) findViewById(R.id.closeDrawerBtn);
        aboutOfficeBtn = (Button) findViewById(R.id.aboutOfficeBtn);
        aboutUsBtn = (Button) findViewById(R.id.aboutUsBtn);
        exitBtn = (Button) findViewById(R.id.exitBtn);
        editProfileTv = (TextView) findViewById(R.id.editProfileTv);
        infoIcon = (ImageView) findViewById(R.id.infoIcon);

        avatarImgview = findViewById(R.id.avatarImgview);
        usernameTv = findViewById(R.id.usernameTv);

        UserData.populate(this);

        avatarImgview.setImageBitmap(UserData.photo);
        usernameTv.setText(UserData.firstName + " " + UserData.lastName);

        //check pending adoption request
        UserData.populateAdoptions(Homepage.this);
        if (UserData.adoptionHistory.size() > 0){
            for (Adoption adoption : UserData.adoptionHistory){
                Utility.log("Homepage: Adoption on Device - PetDI:" + adoption.getPetID() + " Status: " + adoption.getStatus());
                if (adoption.getStatus() == Timeline.DECLINED ||
                        adoption.getStatus() == Timeline.CANCELLED ||
                        adoption.getStatus() == Timeline.FINISHED)
                    continue;
                else {
                    Log.d("DEBUGGER>>>", "Goto Timeline" + adoption.getStatus());
                    Intent intent = new Intent(Homepage.this, Timeline.class);
                    startActivity(intent);
                    finish();
                }
            }
        }

        /*
        //sync adoption record
        SyncAdoptionHistory syncAdoptionHistory = new SyncAdoptionHistory(Homepage.this);
        syncAdoptionHistory.execute();*/

        checkAccountStatus();
        loadKoloda();
        setKolodaListener();

    }

    public void onPressedFilter(View view){
        Utility.animateOnClick(Homepage.this, view);
        Intent i = new Intent(Homepage.this, Filter.class);
        startActivity(i);
        finish();
    }

    public void onPressedMessage(View view){
        Utility.animateOnClick(Homepage.this, view);
        Utility.gotoMessenger(Homepage.this);
    }

    public void onPressedMenu(View view){
        Utility.animateOnClick(Homepage.this, view);

        if (UserData.userID == null){
            RelaunchNotifier relaunchNotifier = new RelaunchNotifier(Homepage.this);
            relaunchNotifier.show();
            return;
        }
        if (UserData.userID.equals("null")){
            RelaunchNotifier relaunchNotifier = new RelaunchNotifier(Homepage.this);
            relaunchNotifier.show();
            return;
        }

        avatarImgview.setImageBitmap(UserData.photo);
        usernameTv.setText(UserData.firstName + " " + UserData.lastName);
        drawerLayout.openDrawer(GravityCompat.END);

        //sync adoption record
        //SyncAdoptionHistory syncAdoptionHistory = new SyncAdoptionHistory(Homepage.this, UserData.userID, false);
        //syncAdoptionHistory.execute();
    }

    public void onPressedAdoptionHistory(View view){
        Utility.animateOnClick(Homepage.this, view);
        Intent i = new Intent(Homepage.this, AdoptionHistory.class);
        startActivity(i);
    }

    public void onPressedHelp(View view){
        Utility.animateOnClick(Homepage.this, view);
        Intent i = new Intent(Homepage.this, Help.class);
        startActivity(i);
    }

    public void onPressedAboutOffice(View view){
        Utility.animateOnClick(Homepage.this, view);
        Intent i = new Intent(Homepage.this, AboutTheOffice.class);
        startActivity(i);
    }

    public void onPressedAboutUs(View view){
        Utility.animateOnClick(Homepage.this, view);
        Intent z = new Intent(Homepage.this, AboutUs.class);
        startActivity(z);
    }

    public void onPressedCloseDrawer(View view){
        Utility.animateOnClick(Homepage.this, view);
        drawerLayout.closeDrawer(GravityCompat.END);
    }

    public void onPressedLogout(View view){
        Utility.animateOnClick(Homepage.this, view);
        ExitDialog exitDialog = new ExitDialog(Homepage.this);
        exitDialog.show();
    }

    public void onEditProfilePressed(View view){
        Utility.animateOnClick(Homepage.this, view);
        Intent i = new Intent(Homepage.this, EditProfile.class);
        startActivity(i);
        EditProfile.FORBID_DEACTIVATION = false;
        AccountSecuritySettings.FORBID_DEACTIVATION = false;
    }

    private void checkAccountStatus(){
        AccountStatusChecker accountStatusChecker = new AccountStatusChecker(Homepage.this);
        accountStatusChecker.execute();
    }

    public void onPressedInfoIcon(View view){
        Utility.animateOnClick(Homepage.this, view);
        gotoMoreInfo();
    }

    private void gotoMoreInfo(){
        if (CURRENT_PET.getPetID() == null){
            Toast.makeText(this, "Can't open information page", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(Homepage.this, MoreInfo.class);
        i.putExtra("petID", CURRENT_PET.getPetID());
        startActivity(i);
    }

    private void loadKoloda(){
        try {
            //making a copy of the filtered list as koloda listener reference
            tempPetList = filterList();

            ArrayList<Pet> copyOfTempPetList = (ArrayList<Pet>) tempPetList.clone();

            if (tempPetList.size() < 3){
                for (Pet pet : tempPetList)
                    copyOfTempPetList.add(pet);
            }

            SwipeAdapter adapter = new SwipeAdapter(this, copyOfTempPetList);
            koloda.setAdapter(adapter);

            if (tempPetList.size() > 0){
                indexTrackerTv.setText("1 / " + tempPetList.size());
            }
            else {
                indexTrackerTv.setText("");
            }

        }
        catch (Exception e){
            Utility.log("Homepage.lK: " + e.getMessage());
        }
    }

    private ArrayList<Pet> filterList(){
        ArrayList<Pet> filteredList = new ArrayList<>();

        for (Pet pet : UserData.pets){

            if (pet.getType() == PetType.DOG && !Homepage.DOG)
                continue;
            else if (pet.getType() == PetType.CAT && !Homepage.CAT)
                continue;

            if (pet.getGender() == Gender.MALE && !Homepage.MALE)
                continue;
            else if (pet.getGender() == Gender.FEMALE && !Homepage.FEMALE)
                continue;

            if (!Homepage.PUPPY && pet.getAge() == PetAge.PUPPY)
                continue;
            if (!Homepage.YOUNG && pet.getAge() == PetAge.YOUNG)
                continue;
            if (!Homepage.OLD && pet.getAge() == PetAge.OLD)
                continue;
            
            filteredList.add(pet);
        }

        return filteredList;
    }

    private void setKolodaListener(){

        try {
            CURRENT_PET = tempPetList.get(0);
        }
        catch (Exception e){
            Utility.log("Homepage.sKL: " + e.getMessage());
        }


        koloda.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {

            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @Override
            public void onCardSwipedLeft(int i) {

                //insert the removed record to the end of arraylist
                i++;
                while (i >= tempPetList.size())
                    i -= tempPetList.size();
                SwipeAdapter swipeAdapter = (SwipeAdapter) koloda.getAdapter();
                //swipeAdapter.insert(UserData.pets.get(i+1));
                swipeAdapter.insert(tempPetList.get(i));

                if (tempPetList.size() == i+1){
                    CURRENT_PET = tempPetList.get(0);
                }
                else {
                    CURRENT_PET = tempPetList.get(i+1);
                }

                updateIndexUI(CURRENT_PET.getPetID());
            }

            @Override
            public void onCardSwipedRight(int i) {

                //insert the removed record to the end of arraylist
                i++;
                while (i >= tempPetList.size())
                    i -= tempPetList.size();
                SwipeAdapter swipeAdapter = (SwipeAdapter) koloda.getAdapter();
                swipeAdapter.insert(tempPetList.get(i));

                if (tempPetList.size() == i+1){
                    CURRENT_PET = tempPetList.get(0);
                }
                else {
                    CURRENT_PET = tempPetList.get(i+1);
                }

                updateIndexUI(CURRENT_PET.getPetID());
            }

            @Override
            public void onClickRight(int i) {
                koloda.onButtonClick(true);
            }

            @Override
            public void onClickLeft(int i) {
                koloda.onButtonClick(false);
            }

            @Override
            public void onCardSingleTap(int i) {

            }

            @Override
            public void onCardDoubleTap(int i) {

            }

            @Override
            public void onCardLongPress(int i) {

            }

            @Override
            public void onEmptyDeck() {

            }
        });
    }

    private void updateIndexUI(String petID){
        indexTrackerTv.setText(((SwipeAdapter) koloda.getAdapter()).getPetIndex(petID) + " / " + tempPetList.size());
    }

    public void onPressedApply(View view){

        Utility.animateOnClick(Homepage.this, view);

        if (koloda.getAdapter() == null){
            Toast.makeText(this, "No pet selected.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (Utility.internetConnection(getApplicationContext())){
            BEGIN_APPLY = true;
            ApplyDialog applyDialog = new ApplyDialog(Homepage.this);
            applyDialog.show();

        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoTimeline(){

        String date = Utility.dateToday();

        Map<String, Object> multiNodeMap = new HashMap<>();
        multiNodeMap.put("adoptionRequest/"+UserData.userID+"/status", String.valueOf(Timeline.AWAITING_APPROVAL));
        multiNodeMap.put("adoptionRequest/"+UserData.userID+"/dateRequested", date);
        multiNodeMap.put("adoptionRequest/"+UserData.userID+"/petID", CURRENT_PET.getPetID());
        multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+CURRENT_PET.getPetID()+"/status", Timeline.AWAITING_APPROVAL);
        multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+CURRENT_PET.getPetID()+"/dateRequested", date);

        DatabaseReference histRef = mDatabase.getReference();
        histRef.updateChildren(multiNodeMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        try {

                            Utility.log("Homepage.gTT: PetID: " + CURRENT_PET.getPetID());

                            UserData.deleteAdoptionByID(Homepage.this, CURRENT_PET.getPetID());

                            FileOutputStream fileOuputStream = openFileOutput("adoption-" + Utility.dateToday(), Context.MODE_PRIVATE);
                            try (FileOutputStream fileOutputStream = openFileOutput( "adoption-" + Utility.dateToday(), Context.MODE_APPEND)) {
                                String data = "status:1;\npetID:" + CURRENT_PET.getPetID() + ";";
                                data += "\ndateRequested:" + Utility.dateToday() + ";\n";
                                data += "gender:" + CURRENT_PET.getGender() + "\n";
                                data += "type:" + CURRENT_PET.getType() + "\n";
                                data += "age:" + CURRENT_PET.getAge() + "\n";
                                data += "size:" + CURRENT_PET.getSize() + "\n";
                                data += "color:" + CURRENT_PET.getColor() + "\n";
                                fileOutputStream.write(data.getBytes());
                                fileOutputStream.flush();

                                new ImageProcessor().saveToLocal(getApplicationContext(), CURRENT_PET.getPhoto(), "adoptionpic-" + CURRENT_PET.getPetID());

                                UserData.populateAdoptions(Homepage.this);

                                //launch timeline
                                Intent gotoTimeline = new Intent(Homepage.this, Timeline.class);
                                startActivity(gotoTimeline);
                                Homepage.this.finish();
                            }
                            catch (Exception e){
                                Utility.log("Homepage.gT.oC: " + e.getMessage());
                                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                            }

                        }
                        catch (Exception e){
                            Utility.log("Homepage.gT: " + e.getMessage());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Homepage.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        Utility.log("Homepage.gT: " + e.getMessage());
                    }
                });

    }

    private LoadingDialog loadingDialog = new LoadingDialog(Homepage.this);
    private BroadcastReceiver mLoadingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                boolean toggle = intent.getBooleanExtra("toggle", false);
                if (toggle)
                    loadingDialog.startLoadingDialog();
                else
                    loadingDialog.dismissLoadingDialog();
            }
            catch (Exception e){
                Utility.log("Homepage.mLR: " + e.getMessage());
            }

        }
    };

    private BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log out user
            avatarImgview.setImageResource(R.drawable.circlelogo_white);
            UserData.logout(Homepage.this);
            mAuth.signOut();
            Toast.makeText(Homepage.this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Homepage.this, Splash.class);
            startActivity(i);
            finish();
        }
    };

    private BroadcastReceiver mBeginApplication = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //confirm if there's pending application
            DatabaseReference tempRef = mDatabase.getReference().child("adoptionRequest");
            tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean processing = false;
                    for (DataSnapshot snap : snapshot.getChildren()){
                        try{

                            if (snap.getKey().equals(UserData.userID)){
                                int status = Integer.parseInt(snap.child("status").getValue().toString());
                                if (status >= 1 && status <= 5){
                                    PendingApplicationNotice pan = new PendingApplicationNotice(Homepage.this);
                                    pan.show();
                                    return;
                                }
                            }

                            if (snap.child("petID").getValue().toString().equals(CURRENT_PET.getPetID())){
                                if (snap.child("status").getValue().toString().equals("7"))
                                    processing = false;
                                else {
                                    Toast.makeText(getApplicationContext(), "Pet currently being processed.", Toast.LENGTH_SHORT).show();
                                    processing = true;
                                }

                            }
                        }
                        catch (Exception e){
                            Utility.log("Homepage.mBA: " + e.getMessage());
                        }

                    }

                    if (!processing){
                        DatabaseReference recordRef = mDatabase.getReference().child("recordSummary");
                        recordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean listed = false;
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    if (ds.getKey().equals(CURRENT_PET.getPetID()))
                                        listed = true;
                                }
                                if (listed)
                                    gotoTimeline();
                                else
                                    Toast.makeText(Homepage.this, "Pet no longer available.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Homepage.this, "Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    };

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        else {
            HomepageExitDialog homepageExitDialog = new HomepageExitDialog(Homepage.this);
            homepageExitDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBeginApplication);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLoadingReceiver);
        super.onDestroy();
    }
}