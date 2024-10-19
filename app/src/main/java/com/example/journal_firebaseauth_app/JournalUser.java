package com.example.journal_firebaseauth_app;

import android.app.Application;

public class JournalUser extends Application {
    String username;
    String userID;
    private static JournalUser instance;

    public static JournalUser getInstance(){
        if(instance==null){
            instance=new JournalUser();
        }
        return instance;
    }

    public JournalUser() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public static void setInstance(JournalUser instance) {
        JournalUser.instance = instance;
    }
}
