package com.example.tomek.schedule;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tomek.schedule.model.JsonStruct;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<JsonStruct> myScheduleList = new ArrayList<JsonStruct>();
    private String TAG = MainActivity.class.getSimpleName();
    private static String url = "http://planzajec.eaiib.agh.edu.pl/view/timetable/159/events?start=2016-11-28&end=2016-12-03";
    private ProgressDialog pDialog;
    private TableLayout myAwesomeTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAwesomeTable = (TableLayout) findViewById(R.id.scheduleTable);

        new GetSchedules().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetSchedules extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);

                    myScheduleList = toList((JSONArray) jsonArray);
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();


            for (JsonStruct jsonStruct : myScheduleList) {
                TableRow row = new TableRow(MainActivity.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                TextView title = new TextView(MainActivity.this);
                TextView group = new TextView(MainActivity.this);
                title.setText(jsonStruct.getTitle());
                group.setText(jsonStruct.getGroup());
                row.addView(title);
                row.addView(group);
                myAwesomeTable.addView(row);
            }
            ;
        }

    }

    public List<JsonStruct> toList(JSONArray array) throws JSONException {
        List<JsonStruct> list = new ArrayList<JsonStruct>();
        String title = "", start = "", end = "", eid = "", group = "";
        JsonStruct new_row;
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);

            if (value instanceof JSONObject) {
                title = ((JSONObject) value).get("title").toString();
                start = ((JSONObject) value).get("start").toString();
                end = ((JSONObject) value).get("end").toString();
                eid = ((JSONObject) value).get("eid").toString();
                group = ((JSONObject) value).get("group").toString();
            }
            new_row = new JsonStruct(title, start, end, eid, group);
            list.add(new_row);
        }
        return list;
    }
}