package com.example.iqiqiya.read_pm25data;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView tvPmData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPmData = (TextView) findViewById(R.id.PmData);

        findViewById(R.id.btnReload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadData();
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    private void reloadData(){
        tvPmData.setText("Loading...");

        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new URL("http://aqicn.org/publishingdata/json").openStream(),"utf-8"));
                    String line = null;
                    StringBuffer content = new StringBuffer();

                    while((line=reader.readLine())!=null){
                        content.append(line);
                    }
                    reader.close();
                    return content.toString();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s!=null){
                    try {
                        JSONArray jsonArr = new JSONArray(s);
                        JSONObject firstJo = jsonArr.getJSONObject(0);

                        JSONArray pollutants = firstJo.getJSONArray("pollutants");
                        JSONObject firstPollutant = pollutants.getJSONObject(0);

                        System.out.println("cityName="+firstJo.getString("cityName")+",localName="+firstJo.getString("localName"));
                        tvPmData.setText(String.format("%s %s:%f",firstJo.getString("cityName"),firstJo.getString("localName"),firstPollutant.getDouble("value")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.execute();
    }
}
