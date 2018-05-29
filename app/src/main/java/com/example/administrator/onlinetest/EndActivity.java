package com.example.administrator.onlinetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {
    TextView score;
    Button retest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        score = (TextView) findViewById(R.id.score);
        retest = (Button) findViewById(R.id.retest);

        Intent intent = getIntent();
        String value = intent.getStringExtra("value")+"";
        score.setText(value);
    }
}
