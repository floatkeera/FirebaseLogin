package com.teephopk.loginpage;

import android.icu.util.Output;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SendMessageActivity extends AppCompatActivity {

    EditText mtxtMessage;
    Button mbtnSend;
    ProgressBar mprg;
    Spinner mUserSpn;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsers = mRootRef.child("userTokens");

    ArrayList<String> userTokenList = new ArrayList<String>();

    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);


        mtxtMessage = (EditText) findViewById(R.id.txtMsg);
        mbtnSend = (Button) findViewById(R.id.btnSend);
        mprg = (ProgressBar) findViewById(R.id.progressBar2);
        mUserSpn = (Spinner) findViewById(R.id.spinner);

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_simple_layout, userTokenList);

        mUserSpn.setAdapter(arrayAdapter);

        mbtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new PostData().execute(mUserSpn.getSelectedItem().toString(), mtxtMessage.getText().toString());



            }
        });





    }

    class PostData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlString = "https://fcm.googleapis.com/fcm/send";
            URL url;
            try {
                url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Authorization", "key=AAAAifV44Gc:APA91bHBKCrhF-iUHeI3gltHOT7K0SLTPjSj3mK7SC5yCj125QHWrOD7ERDW7351o9NVrGVupJLvTgDrFX365851STF-0rquAsivNan3jU9WMVmRIPvA5sU0LzRyYhyVWogpcdo0xf2P");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestMethod("POST");

                JSONObject msg = new JSONObject();
                JSONObject data = new JSONObject();

                msg.put("to", params[0]);
                data.put("m", params[1]);
                data.put("sound", "default");

                msg.put("data", data);

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(msg.toString());
                wr.flush();

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                }

                Log.d("MessageSend", sb.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }



            return "executed";

        }

        @Override
        protected void onPostExecute(String result) {

            mprg.setVisibility(View.INVISIBLE);
            mprg.setIndeterminate(false);
        }

        @Override
        protected void onPreExecute() {
            mprg.setVisibility(View.VISIBLE);
            mprg.setIndeterminate(true);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        mUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userTokenList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    userTokenList.add(ds.getValue(String.class));
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    }



