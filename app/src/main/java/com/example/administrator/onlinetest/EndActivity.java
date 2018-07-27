package com.example.administrator.onlinetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        int value = intent.getIntExtra("value", 0);

        if (value >= 90) {
            score.setText("恭喜你通过考试，成绩: " + value);
        } else
            score.setText("抱歉没通过考试，成绩: " + value);

        retest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(EndActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }
}
