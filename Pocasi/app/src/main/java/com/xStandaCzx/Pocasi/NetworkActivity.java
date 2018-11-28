/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.xStandaCzx.Pocasi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NetworkActivity extends Activity {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    List<Lokace> result=new ArrayList<>();
    private ArrayAdapter mAdapter;

    static SharedPreferences prefs;
    static SharedPreferences.Editor edit ;

    static ArrayList<Lokace> array = new ArrayList<>();

    private String URL;
    
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    public static boolean refreshDisplay = true;
    public static String sPref = null;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
    }

    // Refreshes the display if the network connection and the
    // pref settings allow it.
    @Override
    public void onStart() {
        super.onStart();

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");
        prefs = getSharedPreferences("Storage", Context.MODE_PRIVATE);
        edit= prefs.edit();


        updateConnectedFlags();

        if (refreshDisplay) {
            loadPage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
    // This avoids UI lock up. To prevent network operations from
    // causing a delay that results in a poor user experience, always perform
    // network operations on a separate thread from the UI.
    private void loadPage() {
        retriveArray();
        result.clear();
        for (Lokace l:array) {
//lat=49.7053597 lon=18.6134497
            URL ="http://api.openweathermap.org/data/2.5/forecast?lat="+l.la+"&lon="+l.lo+"&appid=e3d7c39579a17f884c35c02c2af807c8&units=metric&mode=xml&lang=cz";
            if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                    || ((sPref.equals(WIFI)) && (wifiConnected))) {
                // AsyncTask subclass
                new DownloadXmlTask(this).execute(URL);
            } else {
                showErrorPage();
            }


        }



    }

    // Displays an error if the app is unable to load content.
    private void showErrorPage() {
        setContentView(R.layout.main);

    }

    // Populates the activity's options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    //--------------------------------------------MENU------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
                Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
        case R.id.refresh:
                loadPage();
                return true;
        case R.id.add:
            Intent addActivity = new Intent(getBaseContext(), Pridatlokaci.class);
            startActivity(addActivity);
            return true;
        case R.id.Remove_all:
            removeAll();
            return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }
//--------------------------------------------XML------------------------------------------------


    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void,List<Lokace>  > {

    	Context context;
    	
        public DownloadXmlTask(Context context) {
			this.context = context;
		}

		@Override
        protected List<Lokace>  doInBackground(String... urls) {
            try {
                result.add(loadXmlFromNetwork(urls[0]));
                return null;
            } catch (IOException e) {
                return null;
            } catch (XmlPullParserException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<Lokace> r ) {
            setContentView(R.layout.main);
            // Displays the HTML string in the UI via a WebView
            ListView lv = (ListView) findViewById(R.id.listView1);
            reloadLoc();

            mAdapter = new Adapter(context,
                    R.layout.list_entry_layout, result);
            lv.setAdapter(mAdapter);
            array.clear();
            for(Lokace l:result){
                array.add(l);
            }
            lv.setOnItemClickListener(myListener);




        }
    }

    private void Refresh() {
        setContentView(R.layout.main);
        ListView lv = (ListView) findViewById(R.id.listView1);
        Adapter adapter = new Adapter(this,R.layout.list_entry_layout, result);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(myListener);
    }

    ListView.OnItemClickListener myListener=new ListView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> adapterwiev, View view, int i, long l){
            Lokace entry = (Lokace) adapterwiev.getItemAtPosition(i);
            Intent changeActivity=new Intent((getBaseContext()),Info.class);
            changeActivity.putExtra("la",entry.la);
            changeActivity.putExtra("lo",entry.lo);
            startActivity(changeActivity);
        }

    };






    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private Lokace loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        Parser parser = new Parser();
        Lokace entries = null;
  
        try {
            stream = downloadUrl(urlString);
            entries = parser.parse(stream);
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }


        return entries;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
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

//--------------------------------------------Array------------------------------------------------

    static void retriveArray() {
        Gson gson = new Gson();
        int x=0;
        array.clear();
        while(true)
        {
            String json = prefs.getString(""+x, "");
            if(json.isEmpty()){break;}
            Lokace obj = gson.fromJson(json, Lokace.class);
            array.add(obj);
            x++;
        }
    }

    static void addLoc(String la,String lo) {
        SharedPreferences.Editor edit = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(new Lokace("","","","",la,lo));
        edit.putString(""+(array.size()), json);
        edit.commit();

    }
    private void removeAll()
    {
        prefs.edit().clear().commit();
        array.clear();
        result.clear();
        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(null);

    }
    static void removeOne(String la,String lo)
    {
        SharedPreferences.Editor edit = prefs.edit();
        Gson gson = new Gson();
        int x=0;
        int y=0;
        int index=0;
        prefs.edit().clear().commit();
        for (Lokace l:array) {

            if(l.la.equals(la) && lo.equals(lo)) {
                index=y;
            }else {
                String json = gson.toJson(new Lokace("","","","",l.la,l.lo));
                edit.putString(""+(x), json);
                edit.commit();
                x++;
                y++;
            }

        }
        array.remove(index);

        retriveArray();
    }

    private void reloadLoc()
    {

        SharedPreferences.Editor edit = prefs.edit();
        Gson gson = new Gson();
        prefs.edit().clear().commit();
        int x=0;
        for (Lokace l:result) {

        String json = gson.toJson(new Lokace("","","","",l.la,l.lo));
        edit.putString(""+(x), json);
        edit.commit();
        x++;
        }
    }











//--------------------------------------------Broadcast------------------------------------------------
    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (WIFI.equals(sPref) && networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                refreshDisplay = true;
                Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
            } else if (ANY.equals(sPref) && networkInfo != null) {
                refreshDisplay = true;
            } else {
                refreshDisplay = false;
                Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
