package me.sensingself.sensingsugar.Common.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesManager {
    private final String SHARED_PREFERENCE_NAME = "SensingSelf.sugar";
    private SharedPreferences sharedPreferences;

    private Context context;

    private static SharedPreferencesManager sharedPreferenceManager;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

    }

    public static SharedPreferencesManager getInstance(Context context) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferencesManager(context);
        }
        return sharedPreferenceManager;
    }

    public void clearPrefernces() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public String getPreferenceValueString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void setCurrentCountry(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        Boolean status = editor.commit();
        //  Log.d("Status",status+"");
    }
    public String getCurrentCountry(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void setPrefernceValueString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        Boolean status = editor.commit();
        //  Log.d("Status",status+"");
    }

    public boolean getPreferenceBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void setPreferenceBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setPreferenceValueInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getPreferenceValueInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void setPhoneNumber(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public long getPhoneNumber(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    public String getRegisteredPatients(String key) {
        return sharedPreferences.getString(key, "");
    }
    public void setRegisteredPatients(String key, String json) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, json);
        editor.commit();
    }

    public void setCurrentPatient(String key, String json) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, json);
        editor.commit();
    }

    public String getCurrentPatient(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void setDeviceAddress(String key, String json) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, json);
        editor.commit();
    }

    public String getDeviceAddress(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void clearSharedData(){
        sharedPreferences.edit().clear().commit();
    }

    public void setDataVo(String key, String dataVoJson){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, dataVoJson);
        editor.commit();
    }

    public String getDataVoJson(String key){
        return sharedPreferences.getString(key, "");
    }

}
