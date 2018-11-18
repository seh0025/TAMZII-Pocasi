package com.xStandaCzx.Pocasi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Info extends Activity {
    private String URL;
    private static final String ns = null;
    private int[] textw = {
            R.id.textView7,
            R.id.textView8,
            R.id.textView9,
            R.id.textView10,
            R.id.textView11,
            R.id.textView12,
            R.id.textView13,
            R.id.textView14,
            R.id.textView15
    };
    private int[] imagew = {
            R.id.imageView2,
            R.id.imageView3,
            R.id.imageView4,
            R.id.imageView5,
            R.id.imageView6,
            R.id.imageView7
    };
    private int[] textwtime = {
            R.id.textView6,
            R.id.textView16,
            R.id.textView17,
            R.id.textView18,
            R.id.textView19,
            R.id.textView20
    };
    private int[] moreinfo = {
            R.id.textView27,
            R.id.textView28,
            R.id.textView29,
            R.id.textView30,
            R.id.textView31,
            R.id.textView32
    };
    ArrayList<String> images = new ArrayList<>();
    String la;
    String lo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        la = intent.getStringExtra("la");
        lo = intent.getStringExtra("lo");
        URL = "http://api.openweathermap.org/data/2.5/forecast?lat=" + la + "&lon=" + lo + "&appid=e3d7c39579a17f884c35c02c2af807c8&units=metric&mode=xml&lang=cz";
        int x = 0;
        new DownloadXmlTask(this).execute(URL);



    }

    //_---------------------------------------------MENU -------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.infomenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.Remove)
        {
            Log.d("","qqqqqqqqqqqq"+la+" "+lo);
            NetworkActivity.removeOne(la,lo);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //-----------------------------------------------XML DOWNLOAD------------------------------------------------------------------------------------
    private class DownloadXmlTask extends AsyncTask<String, Void,List<Lokace>  > {

        Context context;

        public DownloadXmlTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<Lokace> doInBackground(String... urls) {
            try {
                loadXmlFromNetwork(urls[0]);
                return null;
            } catch (IOException e) {
                return null;
            } catch (XmlPullParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Lokace> r) {
            int x = 0;
            for (String s : images) {
                Picasso.get().load(s).into((ImageView) findViewById(imagew[x]));
                x++;
            }
        }

    }



    private Lokace loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        Parser parser = new Parser();
        Lokace entries = null;
        try {
            stream = downloadUrl(urlString);
            parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return entries;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
//-----------------------------------------------PARSER------------------------------------------------------------------------------------

    public void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);
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
    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {


        int x=3;int y=0;
        boolean first=false;
        parser.require(XmlPullParser.START_TAG, ns, "weatherdata");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("name")) {
               // textView7.setText(readText(parser));
                ((TextView) findViewById(textw[0])).setText(readText(parser));
            }
            if (name.equals("location")) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("latitude")) {
                        la=parser.getAttributeValue(i);}
                    if (parser.getAttributeName(i).equals("longitude")) {
                        lo=parser.getAttributeValue(i);}
                }
            }
            if (name.equals("symbol") && first==false) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("name")) {
                        ((TextView) findViewById(textw[2])).setText(parser.getAttributeValue(i));}}
            }
            if (name.equals("pressure") && first==false) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("value")) {
                        ((TextView) findViewById(moreinfo[2])).setText(parser.getAttributeValue(i)+"hPa");}}
            }
            if (name.equals("humidity") && first==false) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("value")) {
                        ((TextView) findViewById(moreinfo[3])).setText(parser.getAttributeValue(i)+"%");}}first=true;
            }
            if (name.equals("sun") && first==false) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("rise")) {
                        String dat=parser.getAttributeValue(i);
                        dat=dat.substring(11);
                        dat = dat.substring(0,dat.length() - 3);
                        ((TextView) findViewById(moreinfo[0])).setText(dat);}
                    if (parser.getAttributeName(i).equals("set")) {
                        String dat=parser.getAttributeValue(i);
                        dat=dat.substring(11);
                        dat = dat.substring(0,dat.length() - 3);
                        ((TextView) findViewById(moreinfo[1])).setText(dat);} }
            }
            if (name.equals("temperature") && first==false) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("value")) {
                        ((TextView)findViewById(textw[1])).setText(parser.getAttributeValue(i)+"°C");}
                    if (parser.getAttributeName(i).equals("min")) {
                        ((TextView)findViewById(moreinfo[4])).setText(parser.getAttributeValue(i)+"°C");}
                    if (parser.getAttributeName(i).equals("max")) {
                        ((TextView)findViewById(moreinfo[5])).setText(parser.getAttributeValue(i)+"°C");}}
            }
            if (name.equals("symbol") && first==true) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("var")) {
                        String pic = parser.getAttributeValue(i);
                        String iconUrl = "http://openweathermap.org/img/w/" + pic + ".png";
                        images.add(iconUrl);

                    }

                    }
            }
            if (name.equals("time") && first==true) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("from")) {
                        String dat=parser.getAttributeValue(i);
                        dat=dat.substring(11);
                        dat = dat.substring(0,dat.length() - 3);
                        ((TextView)findViewById(textwtime[y])).setText(dat); }}
                        y++;
            }
            if (name.equals("temperature") && first==true) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    if (parser.getAttributeName(i).equals("value")) {

                        ((TextView)findViewById(textw[x])).setText(parser.getAttributeValue(i)+"°C");}}
                x++;
            }
            if(x==9){break;}
            if (name.equals("credit") || name.equals("meta")) {
                skip(parser);
            }




        }
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
