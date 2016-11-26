package com.example.tomek.schedule.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tomek on 2016-11-24.
 */

public class JsonStruct {
    private String title;
    private String type;
    private String classroom;
    private String teacher;
    private Calendar start;
    private Calendar end;
    private String eid;
    //        private String atid;
    private String group;
//        private String backgroundColor;

    public JsonStruct(String _title, String _start, String _end, String _eid, String _group) {
        String[] title_data = setTitles(_title);
        title = title_data[0];
        type = title_data[1];
        classroom = title_data[2];
        teacher = title_data[3];
        this.start = convertToDate(_start);
        this.end = convertToDate(_end);
        this.eid = _eid;
//            this.atid = _atid;
        this.group = _group;
//            this.backgroundColor = _backgroundColor;
    }

    public ArrayList<String> getAll() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(title);
        result.add(start.toString());
        result.add(end.toString());
        result.add(eid);
        result.add(group);
        return result;
    }

    public String[] getTitleData(){
        String[] result ={title, type, classroom, teacher};
        return result;
    }

    public String getTitle(){
        return title;
    }

    public String getGroup(){
        return group;
    }


    private Calendar convertToDate(String _date){
        // 2016-11-28T16:15:00+0100
        /**
         * Żeby coś z tego wyciągnąć
         * start.get(Calendar.DAY_OF_WEEK)
         * end.get(Calendar.MINUTE)
         * Date deprecated
         */
        Calendar cal = Calendar.getInstance();
        String date = _date.substring(0, _date.length()-5);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            cal.setTime(format.parse(_date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cal;
    }

    private String[] setTitles(String _from_title){
        /**
         * titles[0] = nazwa zajęć
         * titles[1] = rodzaj zajęć, Cwwicz. lab
         * titles[2] = sala, C3 210
         * titles[3] = prowadzący, mgr inż. Franek Kimono
         */
        String[] titles = _from_title.split(", ");
        if (titles.length == 5) {
            // Lab i inne
            titles[2] = titles[2].replaceAll(".*: ", ""); // grupa 1b<br/>Sala: C3 123 -> C3 123
            titles[3] = titles[4] + " " + titles[3].replace("prowadzący: ", "");
        }else{
            // Wykład jakiś jeden spiepszony, na razie zostawiam
        }
        return titles;
    }
}
