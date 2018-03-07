package com.teephopk.loginpage;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddProduct extends AppCompatActivity implements fbInterface {


    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mProductsRef = mRootRef.child("products");


    EditText mName;
    EditText mPrice;
    EditText mDescription;

    View focusView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Add product");

        mName = (EditText) findViewById(R.id.name);
        mPrice = (EditText) findViewById(R.id.price);
        mDescription = (EditText) findViewById(R.id.description);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mName.getText().toString().equals( "") ){
                    mName.setError("Name can't be empty.");
                    focusView = mName;
                    focusView.requestFocus();
                } else {

                    if (mPrice.getText().toString().equals( "")) {
                        mPrice.setError("Price can't be empty.");
                        focusView = mPrice;
                        focusView.requestFocus();

                    } else {
                        Product a = new Product(mName.getText().toString(), mPrice.getText().toString(), mDescription.getText().toString());

                        DatabaseReference newProductRef = mProductsRef.push();


                        newProductRef.setValue(a, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null)
                                    Toast.makeText(getApplicationContext(), "Error in saving data.", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Data saved successfully.", Toast.LENGTH_LONG).show();
                            }
                        });


                        finish();


                    }
                }

            }
        });
    }

    @Override
    public void onError() {

    }

    @Override
    public void onSuccess() {

    }
}
