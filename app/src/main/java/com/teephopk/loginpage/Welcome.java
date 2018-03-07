package com.teephopk.loginpage;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Welcome extends AppCompatActivity {

    ImageView mImgView;
    TextView mtxtName;
    TextView mtxtUsrInfo;
    Button mbtnStore;
    Button mbtnMessaging;

    FirebaseUser user;

    Dialog dialog;

    ProfileTracker profileTracker;

    FirebaseAuth mAuth;
    Profile currentProfile;
    FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference userTokenRef;

    String name;
    String email;

    public static final String EXTRA_NAME = "extra email";
    private static final String OMISE_PKEY = "pkey_test_58i6gpxzxpuw7vlybb3";
    private static final int REQUEST_CC = 100;

    StorageReference imgref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setTitle("Welcome to LogInTest");

        dialog = ProgressDialog.show(Welcome.this, "",
                "Loading. Please wait...", true);

        setContentView(R.layout.activity_welcome);

        FacebookSdk.sdkInitialize(getApplicationContext());


        userTokenRef = FirebaseDatabase.getInstance().getReference();
        userTokenRef = userTokenRef.child("userTokens");
        userTokenRef = userTokenRef.push();
        userTokenRef.setValue(MyFirebaseInstanceIDService.userToken);


        mImgView = (ImageView) findViewById(R.id.imageView);
        mtxtName = (TextView) findViewById(R.id.textView6);
        mtxtUsrInfo = (TextView) findViewById(R.id.txtUserInfo);
        mbtnStore = (Button) findViewById(R.id.btnStore);
        mbtnMessaging = (Button) findViewById(R.id.btnMessaging);


        mbtnMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Welcome.this, SendMessageActivity.class);
                startActivity(i);
            }
        });




        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();


            String UID = user.getUid();


            String welcome = "Welcome, " + name;
            mtxtName.setText(welcome);
            String userinfo = "User info:" + "\nEmail: " + email + "\nUID: " + UID;
            mtxtUsrInfo.setText(userinfo);


            if (Profile.getCurrentProfile() == null) {

                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        currentProfile = profile2;
                        String picURL = currentProfile.getProfilePictureUri(600, 600).toString();
                        Picasso.with(getApplicationContext()).load(picURL).into(mImgView);

                        profileTracker.stopTracking();
                        dialog.dismiss();
                    }
                };

                profileTracker.startTracking();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                imgref = storage.getReference().child("users/" + UID + "/dp.jpg");


                imgref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dialog.dismiss();
                        Picasso.with(getApplicationContext()).load(uri).placeholder(R.drawable.progress_animation).into(mImgView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });



            } else {
                currentProfile = Profile.getCurrentProfile();
                String picURL = currentProfile.getProfilePictureUri(600, 600).toString();
                Picasso.with(getApplicationContext()).load(picURL).into(mImgView);
                dialog.dismiss();
            }

        }

        mbtnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this, DisplayJSON.class);
                String user = name;
                intent.putExtra(EXTRA_NAME, user);
                startActivity(intent);
            }
        });
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:


                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();


                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_welcome, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                AlertDialog.Builder ab = new AlertDialog.Builder(Welcome.this);
                ab.setMessage("Are you sure you want to log out?").setPositiveButton("Yes, Exit", dialogClickListener)
                        .setNegativeButton("No, stay", dialogClickListener).show();
                break;

        }



        return true;
    }

    @Override
    public void onStart(){

        super.onStart();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {



                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {

                    if(dialog != null)
                        dialog.dismiss();


                    finish();


                }
            }
        };

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {


        AlertDialog.Builder ab = new AlertDialog.Builder(Welcome.this);
        ab.setMessage("Are you sure you want to log out?").setPositiveButton("Yes, Exit", dialogClickListener)
                .setNegativeButton("No, stay", dialogClickListener).show();
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();


        super.onDestroy();

    }


}









