package com.xStandaCzx.Pocasi;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static final String ns = null;

    public Lokace parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    private Lokace readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Lokace> entries = new ArrayList<Lokace>();
        String cityname = parser.getName();
        String weather = parser.getName();
        String temp = parser.getName();
        String pic = parser.getName();
        String la= parser.getName();;
        String lo= parser.getName(); ;

        parser.require(XmlPullParser.START_TAG, ns, "weatherdata");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
                if (name.equals("name")) {
                    cityname = readText(parser);
                }
            if (name.equals("location")) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("latitude")) {
                        la=parser.getAttributeValue(i);}
                    if (parser.getAttributeName(i).equals("longitude")) {
                        lo=parser.getAttributeValue(i);}
                }
            }
            if (name.equals("symbol")) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("name")) {
                        weather = parser.getAttributeValue(i);}

                    if (parser.getAttributeName(i).equals("var")) {
                        pic = parser.getAttributeValue(i);}}
            }
            if (name.equals("location")) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("latitude")) {
                        la = parser.getAttributeValue(i);}

                    if (parser.getAttributeName(i).equals("longitude")) {
                        lo = parser.getAttributeValue(i);}}
            }
            if (name.equals("temperature")) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("value")) {
                             temp = parser.getAttributeValue(i);}}
                break;
            }

            if (name.equals("credit") || name.equals("meta")) {
                skip(parser);
            }




            }
        return new Lokace(pic, cityname, temp,weather,la,lo);
        }




    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.next();
    }


}
