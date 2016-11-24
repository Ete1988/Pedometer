package com.mueller.mobileSports.pedometer.pedometerUtility;

import android.content.SharedPreferences;
import android.widget.TextView;

import com.mueller.mobileSports.pedometer.MainActivity.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ete on 17/11/2016.
 */

public class pedometerData {


    public pedometerData() {

    }

    private SharedPreferences myData;
    private SharedPreferences.Editor editor;
    private int stepsOverWeek;
    private int stepsOverDay;


    public int getStepsOverWeek() {
        return stepsOverWeek;
    }

    public void setStepsOverWeek(int stepsOverWeek) {
        this.stepsOverWeek = stepsOverWeek;
    }

    public int getStepsOverDay() {
        return stepsOverDay;
    }

    public void setStepsOverDay(int stepsOverDay) {
        this.stepsOverDay = stepsOverDay;
    }


    private void updateAll() {
        updateDayCount();
        updateWeekCount();
    }

    private void updateDayCount() {
        try {
            boolean check;
            check = checkIfNewDay();
            isNewDay(check);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void isNewDay(boolean checkDay) {
        if (checkDay) {
            stepsOverDay = 0;
        } else {
            stepsOverDay = myData.getInt("dayCount", 0);
        }
    }
    private boolean checkIfNewDay() throws ParseException {

        Date todayDate = new Date();
        SimpleDateFormat currDate = new SimpleDateFormat("EE dd MMM yyyy");
        String currentDate = currDate.format(todayDate);

        if (!(myData.contains("checkDate"))) {
            editor.putString("checkDate", currentDate);
            return true;
        } else {

            Date oldDate = currDate.parse((myData.getString("checkDate", null)));
            Date now = currDate.parse(currentDate);

            if (oldDate.before(now)) {
                editor.putInt("dayCount", 0);
                editor.putString("checkDate", currentDate);
                return true;
            } else if (oldDate.equals(now)) {
                editor.putInt("dayCount", stepsOverDay);
                return false;
            } else {
                //should never be reached
                //TODO test this out
                return true;
            }
        }
    }

    //TODO test this.
    private void updateWeekCount() {

        boolean checkWeek = checkIfNewWeek();
        if (checkWeek) {
            stepsOverWeek = 0;
        } else {
            stepsOverWeek = myData.getInt("weekCount", 0);
        }
    }

    private boolean checkIfNewWeek() {

        Calendar c = Calendar.getInstance();
        if (!myData.contains("weekOfYear")) {
            editor.putInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));
            return true;
        } else {

            if ((c.get(Calendar.WEEK_OF_YEAR)) > (myData.getInt("weekOfYear", 0))) {
                editor.putInt("weekCount", 0);
                editor.putInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));
                return true;
            } else if ((c.get(Calendar.WEEK_OF_YEAR)) == (myData.getInt("weekOfYear", 0))){
                editor.putInt("weekCount", stepsOverWeek);
                return false;
            } else{
                //should never be reached
                //TODO test this out
                return false;
            }
        }
    }

}
