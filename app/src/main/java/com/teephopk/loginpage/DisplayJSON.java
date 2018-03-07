package com.teephopk.loginpage;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.DSAKey;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


import co.omise.Client;
import co.omise.android.models.Token;
import co.omise.android.ui.CreditCardActivity;
import co.omise.android.ui.Verify3DSActivity;
import co.omise.models.Charge;

public class DisplayJSON extends AppCompatActivity {


    public String json;


    ListView mResults;
    TextView mWelcome;
    TextView mPrice;
    Button mSetPrice;
    EditText mEditPrice;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mPriceRef = mRootRef.child("price");
    DatabaseReference mProductsRef = mRootRef.child("products");
    ArrayList<Product> productList = new ArrayList<Product>();
    CustomListAdapter cla;
    int chargePrice = 0;
    Client client;
    Token token;

    Charge charge;

    private static final String OMISE_PKEY = "pkey_test_58i6gpxzxpuw7vlybb3";
    private static final String OMISE_SKEY = "skey_test_58i6gpy08hzwpdyz684";
    private static final int REQUEST_CC = 100;
    ProgressDialog progressDialog;


    private String AUTHORIZED_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_json);

        progressDialog = ProgressDialog.show(this, "", "Loading the store...");


        Intent intent = getIntent();
        String email = intent.getStringExtra(LoginActivity.EXTRA_EMAIL);

        mWelcome = (TextView) findViewById(R.id.textView7);
        mResults = (ListView) findViewById(R.id.listView);


        cla = new CustomListAdapter(getApplicationContext(), R.layout.listview_layout, productList);
        mResults.setAdapter(cla);






        setTitle("Store");
        mWelcome.setText("Welcome to the Store, " + email);

        registerForContextMenu(mResults);

        client = new Client(OMISE_PKEY, OMISE_SKEY);

        mResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                // Get product "behind" the clicked item
                Product p = (Product) mResults.getItemAtPosition(position);


                chargePrice = Integer.parseInt(p.price) * 100;

                // Do something with the data. For example, passing them to a new Activity

                Intent i = new Intent(DisplayJSON.this, CreditCardActivity.class);
                i.putExtra("product_price", p.price);
                i.putExtra(CreditCardActivity.EXTRA_PKEY, OMISE_PKEY);
                startActivityForResult(i, REQUEST_CC);
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        mProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
                productList.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    productList.add(ds.getValue(Product.class));
                }

                cla.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CC:
                if (resultCode == CreditCardActivity.RESULT_CANCEL) {
                    return;
                }

                token = data.getParcelableExtra(CreditCardActivity.EXTRA_TOKEN_OBJECT);


                AlertDialog.Builder ab = new AlertDialog.Builder(DisplayJSON.this);

                ab.setMessage("Confirm payment of THB" + String.valueOf(chargePrice/100) + " using X-" + token.card.lastDigits).setPositiveButton("Confirm", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).show();




            default:
                super.onActivityResult(requestCode, resultCode, data);
        }



    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:

                         class CompletePayment extends AsyncTask<String, String, String> {
                             ProgressDialog progressDialog;

                             @Override
                             protected String doInBackground(String... params) {

                                 try {

                                     charge = client.charges()
                                             .create(new Charge.Create()
                                                     .amount(chargePrice) // 1,000 THB
                                                     .currency("thb")
                                                     .card(token.id));

                                 } catch (IOException e) {


                                     final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayJSON.this);
                                     alertDialog.setTitle("Payment Failed").setMessage(charge.getFailureMessage()).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialogInterface, int i) {
                                             dialogInterface.dismiss();
                                         }
                                     }).show();


                                 }

                                 return "Success";

                             }

                             protected void onPreExecute() {
                                 progressDialog =  ProgressDialog.show(DisplayJSON.this, "",
                                         "Confirming payment. Please wait...", true);
                             }

                             @Override
                             protected void onPostExecute(String result) {
                                 // TODO Auto-generated method stub




                                 super.onPostExecute(result);
                                 progressDialog.dismiss();

                             }
                         }
                    new CompletePayment().execute("");






                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {

            case R.id.delete:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_displayjson, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add_product:
                Intent add = new Intent(DisplayJSON.this, AddProduct.class);

                startActivity(add);
                break;


        }


        return true;
    }
/*


    @Override
    public void onBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(DisplayJSON.this);
        ab.setMessage("Are you sure you want to log out?").setPositiveButton("Yes, Log out", dialogClickListener)
                .setNegativeButton("No, stay", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent back = new Intent(DisplayJSON.this, LoginActivity.class);


                    startActivity(back);
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };
*/

    /*public class getJSONURL extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response = new StringBuilder();
            try {

                URL url = new URL("https://dl.dropboxusercontent.com/s/z1fs44f02xdm8s1/test.json?dl=0");

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        url.openStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    // get lines
                    response.append(line);

                }
                in.close();
            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            return response.toString();
        }

        protected void onProgressUpdate() {
            // called when the background task makes any progress
        }

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(DisplayJSON.this);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progressDialog.dismiss();

            Log.d("tag", "Response is " + result.toString());
            //set your Textview here
            json = result.toString();
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = new GsonBuilder().create();

            try {


                mainObjm a = gson.fromJson(json, mainObjm.class);



                CustomListAdapter cla = new CustomListAdapter(getApplicationContext(), R.layout.listview_layout, a.product);
                lv.setAdapter(cla);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }*/


}
