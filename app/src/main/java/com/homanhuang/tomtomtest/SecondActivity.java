package com.homanhuang.tomtomtest;

import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class SecondActivity extends AppCompatActivity {

    /* Log tag and shortcut */
    final static String TAG = "MYLOG ACT";
    public static void ltag(String message) { Log.i(TAG, message); }

    TextView secondTv;
    EditText testEt;


    public void sendMsg(View v) {
        String msg = testEt.getText().toString();

        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        intent.putExtra("r1", msg);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_activity);

        ltag("second activity");

        secondTv = (TextView) findViewById(R.id.secondTv);
        testEt = (EditText) findViewById(R.id.testEt);

        Bundle bundle = this.getIntent().getExtras();
        String p1 = bundle.getString("p1");

        secondTv.setText(p1);
/*
        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        intent.putExtra("r1", "something");
        setResult(RESULT_OK, intent);
        finish();
*/
    }
}
