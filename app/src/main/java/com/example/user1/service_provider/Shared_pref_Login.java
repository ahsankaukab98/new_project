package com.example.user1.service_provider;

import android.content.Context;
import android.content.SharedPreferences;

public class Shared_pref_Login {
    Context context;

    Shared_pref_Login(Context context)
    {
        this.context = context;

    }
    public void saveLoginDetails(String email, String password,String name,String mobile,String firebase_name,String image_uri) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.putString("Name",name);
        editor.putString("Mobile",mobile);
        editor.putString("Firebase",firebase_name);
        editor.putString("Firebase_uri",image_uri);
        editor.commit();
    }
    public void changeData(String key,String Value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,Value);
        editor.commit();
    }
    public String getData(String key)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public boolean isUserLogedOut()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        boolean isPasswordEmpty = sharedPreferences.getString("Password", "").isEmpty();
        return isEmailEmpty || isPasswordEmpty;
    }
    public void logoutsession()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

    }
}
