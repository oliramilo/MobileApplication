package com.curtin.mathtest.Server;

import android.content.Context;
import android.content.SharedPreferences;

import com.curtin.mathtest.R;

public class SessionManager {
    final String USERNAME = "username";
    final String PASSWORD = "password";

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    public SessionManager(Context ctx) {
        pref = ctx.getSharedPreferences(ctx.getString(R.string.user_details), Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void loginUser(String username, String password) {
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD,password);
        editor.apply();
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    public String getPassword() {
        return pref.getString(PASSWORD,null);
    }
    public String getUser() {
        return pref.getString(USERNAME,null);
    }

    public boolean hasUserSession() {
        return pref.contains(PASSWORD) && pref.contains(USERNAME);
    }
}
