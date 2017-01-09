package com.mueller.mobileSports.general;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ete on 24/11/2016.
 * <p>
 * Class to managed shared preferences for all activities in the app
 */


public class SharedValues {

    private static SharedValues myValues;
    private SharedPreferences sharedPreferences;

    private SharedValues(Context context) {
        sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
    }

    /**
     * Method to get the a sharedValues instance shared by all activities within the app
     *
     * @param context Context
     * @return Instance of SharedValues
     */
    public static SharedValues getInstance(Context context) {
        if (myValues == null) {
            myValues = new SharedValues(context);
        }
        return myValues;
    }

    /**
     * Method to store an String in the shared preference
     *
     * @param key   String
     * @param value String
     */
    public void saveString(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    /**
     * Method to store an int in the shared preference
     *
     * @param key   String
     * @param value int
     */
    public void saveInt(String key, int value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();
    }

    /**
     * Method to get a stored String value from the shared preference specified by a key
     *
     * @param key string
     * @return Empty string if no shared value with specified key is found
     */
    public String getString(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, " ");
        }
        return " ";
    }

    /**
     * Method to get a stored int value from the shared preference specified by a key
     *
     * @param key string
     * @return 0 if no shared value with specified key is found
     */
    public int getInt(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, 0);
        }
        return 0;

    }

    /**
     * Clear all data from sharedValues
     */
    public void clearData() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }

    /**
     * Method ti remove a specific entry from the sharedValues
     * @param key corresponding key for the to be removed value
     */
    public void removeEntry(String key) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove(key);
        prefsEditor.apply();
    }

    /**
     * Method to check whether a value with a specified key exists in the shared values
     *
     * @param key string
     * @return true if key exists within the shared value
     */
    boolean checkIfContained(String key) {
        return sharedPreferences.contains(key);
    }

}


