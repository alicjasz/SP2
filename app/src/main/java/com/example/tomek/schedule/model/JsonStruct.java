package com.example.tomek.schedule.model;

import java.util.ArrayList;

/**
 * Created by Tomek on 2016-11-24.
 */

public class JsonStruct {
    private String title;
    private String start;
    private String end;
    private String eid;
    //        private String atid;
    private String group;
//        private String backgroundColor;

    public JsonStruct(String _title, String _start, String _end, String _eid, String _group) {
        this.title = _title;
        this.start = _start;
        this.end = _end;
        this.eid = _eid;
//            this.atid = _atid;
        this.group = _group;
//            this.backgroundColor = _backgroundColor;
    }

    public ArrayList<String> getAll() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(title);
        result.add(start);
        result.add(end);
        result.add(eid);
        result.add(group);
        return result;
    }

    public String getTitle(){
        return title;
    }

    public String getGroup(){
        return group;
    }
}
