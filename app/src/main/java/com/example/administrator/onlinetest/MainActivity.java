package com.example.administrator.onlinetest;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    public void getXMLWithPull() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://184.170.222.14/test.xml").build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseXMLWithPull(responseData);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void parseXMLWithPull(String xmlData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String description = "";
            String answer = "";
            String choice_1 = "";
            String choice_2 = "";
            String choice_3 = "";
            String choice_4 = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if ("id".equals(nodeName)) {
                            id = xmlPullParser.nextText();
                        }
                        else if ("description".equals(nodeName)) {
                            description = xmlPullParser.nextText();
                        }
                        else if ("answer".equals(nodeName)) {
                            answer = xmlPullParser.nextText();
                        }
                        else if ("choice_1".equals(nodeName)) {
                            choice_1 = xmlPullParser.nextText();
                        }
                        else if ("choice_2".equals(nodeName)) {
                            choice_2 = xmlPullParser.nextText();
                        }
                        else if ("choice_3".equals(nodeName)) {
                            choice_3 = xmlPullParser.nextText();
                        }
                        else if ("choice_4".equals(nodeName)) {
                            choice_4 = xmlPullParser.nextText();
                        }
                        break;
                    }
                    case  XmlPullParser.END_TAG: {
                        if ("question".equals(nodeName)) {
                            values.put("id",id);
                            values.put("description",description);
                            values.put("answer",answer);
                            values.put("choice_1",choice_1);
                            values.put("choice_2",choice_2);
                            values.put("choice_3",choice_3);
                            values.put("choice_4",choice_4);
                            db.insert("Key",null,values);
                        }
                        break;
                    }
                    default:break;
                }
                eventType = xmlPullParser.next();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = (Button) findViewById(R.id.start);
        dbHelper = new DatabaseHelper(this,"Key.db",null,1);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AnswerActivity.class);
                getXMLWithPull();
                startActivity(intent);
            }
        });
    }
}