package com.teephopk.loginpage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ReceiveMessageActivity extends AppCompatActivity {

    TextView mtxtRcvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_message);

        mtxtRcvMsg = (TextView) findViewById(R.id.txtMessageRec);

        mtxtRcvMsg.setText(getIntent().getStringExtra("Message"));

    }
}
