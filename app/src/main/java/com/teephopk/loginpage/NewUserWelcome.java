package com.teephopk.loginpage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class NewUserWelcome extends AppCompatActivity {

    EditText mName;
    Button btnContinue;
    ProgressDialog dialog;
    ImageView profilepic;
    Uri imageUri;

    StorageReference storageRef;
    StorageReference userrootRef;
    StorageReference imageRef;

    UserProfileChangeRequest profileUpdates;


    final static int PICK_IMAGE = 100;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_welcome);

        user = FirebaseAuth.getInstance().getCurrentUser();


        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        userrootRef = storageRef.child("users");

        mName = (EditText) findViewById(R.id.editText3);
        btnContinue = (Button) findViewById(R.id.button2);
        profilepic = (ImageView) findViewById(R.id.imageView4);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mName.getText().toString()).build();

                dialog = ProgressDialog.show(NewUserWelcome.this, "",
                        "Loading. Please wait...", true);

                String UID = user.getUid();
                imageRef = userrootRef.child(UID+"/dp.jpg");

                Uri file =  imageUri;

                UploadTask uploadTask = imageRef.putFile(file);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        updateProfile(profileUpdates);
                    }
                });





            }


        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, PICK_IMAGE);

            }
        });


    }


    public void updateProfile(UserProfileChangeRequest profileUpdates) {
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent a = new Intent(NewUserWelcome.this, Welcome.class);
                            startActivity(a);
                            dialog.dismiss();
                            finish();

                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            profilepic.setImageURI(imageUri);
        }
    }

}




