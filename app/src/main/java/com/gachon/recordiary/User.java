package com.gachon.recordiary;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String ID;
    public String name;
    public String UID;

    public User(){

    }
    public User(String ID, String name, String UID){
        this.ID = ID;
        this.name = name;
        this.UID = UID;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("ID",ID);
        result.put("name", name);
        result.put("UID", UID);

        return result;
    }
}
