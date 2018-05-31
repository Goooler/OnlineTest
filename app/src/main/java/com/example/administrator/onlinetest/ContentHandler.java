package com.example.administrator.onlinetest;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandler extends DefaultHandler {
    private DatabaseHelper dbHelper;
    private String nodeName;
    private StringBuilder id;
    private StringBuilder description;
    private StringBuilder answer;
    private StringBuilder choice_1;
    private StringBuilder choice_2;
    private StringBuilder choice_3;
    private StringBuilder choice_4;

    public void clearValues() {
        id.setLength(0);
        description.setLength(0);
        answer.setLength(0);
        choice_1.setLength(0);
        choice_2.setLength(0);
        choice_3.setLength(0);
        choice_4.setLength(0);
    }

    public void insertValues() {
        //dbHelper = new DatabaseHelper(ContentHandler.this,"Key.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",id.toString().trim());
        values.put("description",description.toString().trim());
        values.put("answer",answer.toString().trim());
        values.put("choice_1",choice_1.toString().trim());
        values.put("choice_2",choice_2.toString().trim());
        values.put("choice_3",choice_3.toString().trim());
        values.put("choice_4",choice_4.toString().trim());
        db.insert("Key",null,values);

        clearValues();
    }

    @Override
    public void startDocument() throws SAXException {
        id = new StringBuilder();
        description = new StringBuilder();
        answer = new StringBuilder();
        choice_1 = new StringBuilder();
        choice_2 = new StringBuilder();
        choice_3 = new StringBuilder();
        choice_4 = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        nodeName = localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("id".equals(nodeName)) {
            id.append(ch,start,length);
        }
        else if ("description".equals(nodeName)) {
            description.append(ch,start,length);
        }
        else if ("answer".equals(nodeName)) {
            answer.append(ch,start,length);
        }
        else if ("choice_1".equals(nodeName)) {
            choice_1.append(ch,start,length);
        }
        else if ("choice_2".equals(nodeName)) {
            choice_2.append(ch,start,length);
        }
        else if ("choice_3".equals(nodeName)) {
            choice_3.append(ch,start,length);
        }
        else if ("choice_4".equals(nodeName)) {
            choice_4.append(ch,start,length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("question".equals(localName)) {
            insertValues();
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
