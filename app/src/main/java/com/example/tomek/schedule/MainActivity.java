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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.drawable.RippleDrawable;


public class MainActivity extends AppCompatActivity {

    private List<JsonStruct> myScheduleList = new ArrayList<JsonStruct>();
    private String TAG = MainActivity.class.getSimpleName();
    private static String url = "http://planzajec.eaiib.agh.edu.pl/view/timetable/159/events?start=2016-11-28&end=2016-12-03&_=1479814292235";
    private ProgressDialog pDialog;
    private TableLayout myAwesomeTable;

    private class JsonStruct{
        private String title;
        private String start;
        private String end;
        private String eid;
//        private String atid;
        private String group;
//        private String backgroundColor;

        JsonStruct(String _title, String _start, String _end, String _eid, String _group){
            this.title = _title;
            this.start = _start;
            this.end = _end;
            this.eid = _eid;
//            this.atid = _atid;
            this.group = _group;
//            this.backgroundColor = _backgroundColor;
        }
        public ArrayList<String> getAll(){
            ArrayList<String> result = new ArrayList<String>();
            result.add(title);
            result.add(start);
            result.add(end);
            result.add(eid);
            result.add(group);
            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        loadDataFromUrl("http://planzajec.eaiib.agh.edu.pl/view/timetable/159/events?start=2016-11-28&end=2016-12-03&_=1479814292235");
        myAwesomeTable = (TableLayout)findViewById(R.id.scheduleTable);

        new GetSchedules().execute();

//        final TableLayout myAwesomeTable = (TableLayout)findViewById(R.id.scheduleTable);
//        System.out.println(myScheduleList.toString());
//
//        for(JsonStruct jsonStruct : myScheduleList) {
//            TableRow row = new TableRow(this);
//            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
//            TextView title = new TextView(this);
//            TextView group = new TextView(this);
//            title.setText(jsonStruct.title);
//            group.setText(jsonStruct.group);
//            row.addView(title);
//            row.addView(group);
//            myAwesomeTable.addView(row);
//        }
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


            for(JsonStruct jsonStruct : myScheduleList) {
                TableRow row = new TableRow(MainActivity.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                TextView title = new TextView(MainActivity.this);
                TextView group = new TextView(MainActivity.this);
                title.setText(jsonStruct.title);
                group.setText(jsonStruct.group);
                row.addView(title);
                row.addView(group);
                myAwesomeTable.addView(row);
            };
        }

    }

    public List<JsonStruct> toList(JSONArray array) throws JSONException {
        List<JsonStruct> list = new ArrayList<JsonStruct>();
        String title = "", start = "", end = "", eid = "", group = "";
        JsonStruct new_row;
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);

            if(value instanceof JSONObject) {
                title = ((JSONObject) value).get("title").toString();
                start = ((JSONObject) value).get("start").toString();
                end = ((JSONObject) value).get("end").toString();
                eid = ((JSONObject) value).get("eid").toString();
                group = ((JSONObject) value).get("group").toString();
            }
            new_row = new JsonStruct(title, start, end ,eid, group);
            list.add(new_row);
        }
        return list;
    }

    //    void loadDataFromUrl(final String url){
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        JSONParser jsonparser = new JSONParser();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//
//                    @Override
//                    public void onResponse(String response){
//                        JSONArray jsonArray1 = null;
//                        try {
//                            jsonArray1 = new JSONArray(response);
//                            myScheduleList = toList(jsonArray1);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println(error.toString());
//            }
//        });
//        queue.add(stringRequest);
//    }
}


//    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
//        Map<String, Object> retMap = new HashMap<String, Object>();
//
//        if(json != JSONObject.NULL) {
//            retMap = toMap(json);
//        }
//        return retMap;
//    }
//
//    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
//        Map<String, Object> map = new HashMap<String, Object>();
//
//        Iterator<String> keysItr = object.keys();
//        while(keysItr.hasNext()) {
//            String key = keysItr.next();
//            Object value = object.get(key);
//
//            if(value instanceof JSONArray) {
//                value = toList((JSONArray) value);
//            }
//
//            else if(value instanceof JSONObject) {
//                value = toMap((JSONObject) value);
//            }
//            map.put(key, value);
//        }
//        return map;
//    }
//
//    public static List<Object> toList(JSONArray array) throws JSONException {
//        List<Object> list = new ArrayList<Object>();
//        for(int i = 0; i < array.length(); i++) {
//            Object value = array.get(i);
//            if(value instanceof JSONArray) {
//                value = toList((JSONArray) value);
//            }
//
//            else if(value instanceof JSONObject) {
//                value = toMap((JSONObject) value);
//            }
//            list.add(value);
//        }
//        return list;
//    }