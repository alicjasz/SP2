package com.example.tomek.schedule;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomek.schedule.model.JsonStruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener {

    private List<JsonStruct> myScheduleList = new ArrayList<JsonStruct>();
    private String TAG = MainActivity.class.getSimpleName();
    private static String url = "http://planzajec.eaiib.agh.edu.pl/view/timetable/159/events?start=2016-11-28&end=2016-12-03";
    private ProgressDialog pDialog;
    private TableLayout myAwesomeTable;
    private static ArrayList<TextView> tv = new ArrayList<>();
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAwesomeTable = (TableLayout) findViewById(R.id.scheduleTable);
        createDayColumn();
        new GetSchedules().execute();
        for (int i = 0; i < tv.size(); i++){
            tv.get(i).setOnTouchListener(this);
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        if (event.getAction()==DragEvent.ACTION_DROP)
        {
            //handle the dragged view being dropped over a target view
            TextView dropped = (TextView)event.getLocalState();
            TextView dropTarget = (TextView) v;
            //stop displaying the view where it was before it was dragged
            dropped.setVisibility(View.INVISIBLE);

            //if an item has already been dropped here, there will be different string
            String text=dropTarget.getText().toString();
            //if there is already an item here, set it back visible in its original place
            //just for check if it works
            if(text.equals(tv.get(0).getText().toString())) tv.get(0).setVisibility(View.VISIBLE);
            else if(text.equals(tv.get(1).getText().toString())) tv.get(1).setVisibility(View.VISIBLE);
            else if(text.equals(tv.get(2).getText().toString())) tv.get(2).setVisibility(View.VISIBLE);

            //update the text and color in the target view to reflect the data being dropped
            dropTarget.setText(dropped.getText());
            dropTarget.setBackgroundColor(Color.BLUE);
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN)
        {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, v, 0);
            v.setVisibility(v.INVISIBLE);
            System.out.println("dupa");
            return true;
        }
        else return false;
    }

    /**
     * Async task class to get json by making HTTP call
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void createDayColumn(){

        LinearLayout cos = new LinearLayout(MainActivity.this);
        cos.setOrientation(LinearLayout.HORIZONTAL);
        cos.setLayoutParams(params);
        for(int i=0 ; i<5 ; i++){
            cos.addView(createTableCells(i+1));
        }
        myAwesomeTable.addView(cos);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private LinearLayout createTableCells(int culumnNumber){
        LinearLayout row = new LinearLayout(MainActivity.this);

        row.setOrientation(LinearLayout.VERTICAL);
        row.setBackgroundColor(Color.GREEN);
        row.setLayoutParams(params);
        row.setId(culumnNumber);

        for(int j=0 ; j<13; j++){
            row.addView(createCell(String.valueOf(culumnNumber)+String.valueOf(j+1)));
            tv.add(createCell(String.valueOf(culumnNumber)+String.valueOf(j+1)));
        }
        getAllTableCells();
        return row;
    }

    private ArrayList<TextView> getAllTableCells(){
        for (int i = 0; i < tv.size(); i++){
            System.out.println(tv.get(i).getText());
        }
        return tv;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private TextView createCell(String text){
        TextView cell = new TextView(MainActivity.this);

        cell.setTextSize(11);
        cell.setId(Integer.parseInt(text));
        cell.setText(text);
        cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cell.setBackgroundColor(Color.BLUE);
        cell.setLayoutParams(params);
        if(text.equals("11")){
            cell.setLayoutParams(params);
        }
        cell.setPadding(1,1,1,1);

        return cell;

    }
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

            TextView textView = (TextView)findViewById(Integer.parseInt("22"));
            textView.setText("ELO");
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextSize(7);

            TextView textView1 = (TextView)findViewById(Integer.parseInt("410"));
            textView1.setText(myScheduleList.get(0).getTitle());
            textView1.setTextSize(7);
            textView1.setTextColor(Color.BLACK);
            textView1.setBackgroundColor(Color.WHITE);

//klucz w mapie 'string' (np. 20800 czyli MON 08:00), a wartością będzie 'int' komówka 11 (pierwsza cyfra to kolumna, kolejne to numer wiersza)
           // System.out.println(myScheduleList.get(0).getStart().get(Calendar.DAY_OF_WEEK)+""+myScheduleList.get(0).getStart().get(Calendar.HOUR_OF_DAY)+""+myScheduleList.get(0).getStart().get(Calendar.MINUTE));

//            for (JsonStruct jsonStruct : myScheduleList) {
//                TableRow row = new TableRow(MainActivity.this);
//                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
//                row.setLayoutParams(lp);
//                TextView title = new TextView(MainActivity.this);
//                TextView group = new TextView(MainActivity.this);
//                title.setText(jsonStruct.getTitle());
//                group.setText(jsonStruct.getGroup());
//                row.addView(title);
//                row.addView(group);
//                row.setBackgroundColor(Color.DKGRAY);
//
//
//                myAwesomeTable.addView(row);
//
//            }
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