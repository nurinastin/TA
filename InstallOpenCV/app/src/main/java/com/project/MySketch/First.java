package com.project.MySketch;

import android.content.Context;
import android.content.SharedPreferences;

public class First {
    private static First mInstance;
    private static Context mCtx;
    private static final String SHARED_PREF_NAME = "first";
    private static final String kode = "kodeauth";
    private First(Context context){
        mCtx = context;
    }
    public static synchronized First getInstance(Context context){
        if (mInstance == null){
            mInstance = new First(context);
        }
        return mInstance;
    }

    public boolean setKode(String xkode){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(kode, xkode);
        editor.apply();

        return true;
    }
    public String getKode() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(kode, null);
    }
    public boolean isFirst() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(kode, null) != null) {
            return false;
        }
        return true;
    }
}
