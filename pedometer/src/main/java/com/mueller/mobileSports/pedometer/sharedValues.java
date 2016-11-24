package com.mueller.mobileSports.pedometer;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ete on 24/11/2016.
 */

public class sharedValues {

    private static sharedValues yourPreference;
    private SharedPreferences sharedPreferences;

    public static sharedValues getInstance(Context context) {
        if (yourPreference == null) {
            yourPreference = new sharedValues(context);
        }
        return yourPreference;
    }

    private sharedValues(Context context) {
        sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
    }

    private void saveString(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public void saveInt(String key, int value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();
    }

    String getString(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }

    public int getInt(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, 0);
        }
        return 0;

    }

    private boolean checkIfContained(String key) {
        return sharedPreferences.contains(key);
    }

    void checkTime() {
        try {
            checkIfNewDay();
            checkIfNewWeek();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void checkIfNewDay() throws ParseException {

        Date todayDate = new Date();
        SimpleDateFormat currDate = new SimpleDateFormat("EE dd MMM yyyy");
        String currentDate = currDate.format(todayDate);

        if (!(checkIfContained("checkDate"))) {
            //First time use...
            saveString("checkDate", currentDate);
        } else {

            Date oldDate = currDate.parse((getString("checkDate")));
            Date now = currDate.parse(currentDate);
            if (oldDate.before(now)) {
                //Before today
                saveInt("dayCount", 0);
                saveString("checkDate", currentDate);
            }
        }
    }


    private void checkIfNewWeek() {

        Calendar c = Calendar.getInstance();
        if (!checkIfContained("weekOfYear")) {
            saveInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));

        } else {

            if ((c.get(Calendar.WEEK_OF_YEAR)) > (getInt("weekOfYear"))) {
                //New week
                saveInt("weekCount", 0);
                saveInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));

            }
        }
    }
}


