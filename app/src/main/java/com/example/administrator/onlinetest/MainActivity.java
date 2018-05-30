package com.example.administrator.onlinetest;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    public  void insertValues(String id,String description,String answer,String choice_1,String choice_2,
                              String choice_3,String choice_4) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",id);
        values.put("description",description);
        values.put("answer",answer);
        values.put("choice_1",choice_1);
        values.put("choice_2",choice_2);
        values.put("choice_3",choice_3);
        values.put("choice_4",choice_4);
        db.insert("Key",null,values);
    }


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

    public void getXMLWithSAX() {

    }

    public void getJSONWithJObj() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://184.170.222.14/test.json").build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithJObj(responseData);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getJSONWithGSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://184.170.222.14/test.xml").build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void parseXMLWithPull(String xmlData) {
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
                            insertValues(id,description,answer,choice_1,choice_2,choice_3,choice_4);
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

    public void parseXMLWithSAX(String xmlData) {

    }

    public void parseJSONWithJObj(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String description = jsonObject.getString("description");
                String answer = jsonObject.getString("answer");
                String choice_1 = jsonObject.getString("choice_1");
                String choice_2 = jsonObject.getString("choice_2");
                String choice_3 = jsonObject.getString("choice_3");
                String choice_4 = jsonObject.getString("choice_4");
                insertValues(id,description,answer,choice_1,choice_2,choice_3,choice_4);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Question> questionList = gson.fromJson(jsonData,new TypeToken<List<Question>>(){}.getType());
        for (Question question:questionList) {
            insertValues( question.getId(),question.getDescription(),question.getAnswer(),
                    question.getChoice_1(),question.getChoice_2(),question.getChoice_3(),question.getChoice_4() );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = (Button) findViewById(R.id.start);
        dbHelper = new DatabaseHelper(this,"Key.db",null,1);

        getJSONWithJObj();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AnswerActivity.class);
                startActivity(intent);
            }
        });
    }
}
