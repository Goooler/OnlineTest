package com.example.administrator.onlinetest;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private String responseData;
    private List<Question> questionList = new ArrayList<>();
    private final String xmlUrl = "http://67.216.214.244/test.xml";
    private final String jsonUrl = "http://67.216.214.244/test.json";
    private final int REQUESTED = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUESTED) {
                parseJSONWithGSON(responseData);
                insertToDatabase(questionList);
            }
        }
    };

    private void insertToDatabase(String id, String description, String answer, String choice_1, String choice_2,
                                  String choice_3, String choice_4) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //db.execSQL("delete from Key");
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("description", description);
        values.put("answer", answer);
        values.put("choice_1", choice_1);
        values.put("choice_2", choice_2);
        values.put("choice_3", choice_3);
        values.put("choice_4", choice_4);
        db.insert("Key", null, values);
    }

    private void insertToDatabase(List<Question> questionList) {
        for (Question question : questionList) {
            insertToDatabase(question.getId(), question.getDescription(), question.getAnswer(), question.getChoice_1(),
                    question.getChoice_2(), question.getChoice_3(), question.getChoice_4());
        }
    }

    public void getXML() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(xmlUrl).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getJSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(jsonUrl).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();

                    Message message = new Message();
                    message.what = REQUESTED;
                    handler.sendMessage(message);
                } catch (Exception e) {
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
                        } else if ("description".equals(nodeName)) {
                            description = xmlPullParser.nextText();
                        } else if ("answer".equals(nodeName)) {
                            answer = xmlPullParser.nextText();
                        } else if ("choice_1".equals(nodeName)) {
                            choice_1 = xmlPullParser.nextText();
                        } else if ("choice_2".equals(nodeName)) {
                            choice_2 = xmlPullParser.nextText();
                        } else if ("choice_3".equals(nodeName)) {
                            choice_3 = xmlPullParser.nextText();
                        } else if ("choice_4".equals(nodeName)) {
                            choice_4 = xmlPullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        if ("question".equals(nodeName)) {
                            questionList.add(new Question(id, description, answer, choice_1, choice_2, choice_3, choice_4));
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseXMLWithSAX(String xmlData) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ContentHandler handler = new ContentHandler();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseJSONWithJObj(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String description = jsonObject.getString("description");
                String answer = jsonObject.getString("answer");
                String choice_1 = jsonObject.getString("choice_1");
                String choice_2 = jsonObject.getString("choice_2");
                String choice_3 = jsonObject.getString("choice_3");
                String choice_4 = jsonObject.getString("choice_4");
                questionList.add(new Question(id, description, answer, choice_1, choice_2, choice_3, choice_4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        questionList = gson.fromJson(jsonData, new TypeToken<List<Question>>() {
        }.getType());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = (Button) findViewById(R.id.start);
        dbHelper = new DatabaseHelper(this, "Key.db", null, 1);

        //getXML();
        getJSON();

        //parseXMLWithPull(responseData);
        //parseXMLWithSAX(responseData);
        //parseJSONWithJObj(responseData);
        //parseJSONWithGSON(responseData);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
                intent.putExtra("questionList", (Serializable) questionList);
                startActivity(intent);
            }
        });
    }
}
