package com.mueller.mobileSports.pedometer;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ete on 17/12/2016.
 * <p>
 * Class meant to check whether a new day or week started to adjust date, and other data accordingly.
 */

class PedometerUtility {

    private SharedValues sharedValues;

    public PedometerUtility(Context context) {
        this.sharedValues = SharedValues.getInstance(context);
    }

    /**
     * Method to check wheter a new week or day has began
     */
    void checkTime() {
        try {
            checkIfNewDay();
            checkIfNewWeek();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to check if a new day started.
     *
     * @throws ParseException
     */
    private void checkIfNewDay() throws ParseException {

        Date todayDate = new Date();
        SimpleDateFormat currDate = new SimpleDateFormat("EE dd MMM yyyy", Locale.getDefault());
        String currentDate = currDate.format(todayDate);

        if (!(sharedValues.checkIfContained("checkDate"))) {
            //First time use...
            sharedValues.saveString("checkDate", currentDate);
        } else {

            Date oldDate = currDate.parse((sharedValues.getString("checkDate")));
            Date now = currDate.parse(currentDate);
            if (oldDate.before(now)) {
                //Before today...
                sharedValues.saveInt("dayCount", 0);
                sharedValues.saveString("checkDate", currentDate);
            }
        }
    }

    /**
     * Method to check if a new week started;
     */
    private void checkIfNewWeek() {

        Calendar c = Calendar.getInstance();
        if (!sharedValues.checkIfContained("weekOfYear")) {
            sharedValues.saveInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));

        } else {

            if ((c.get(Calendar.WEEK_OF_YEAR)) > (sharedValues.getInt("weekOfYear"))) {
                //New week...
                sharedValues.saveInt("weekCount", 0);
                sharedValues.saveInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));

            }
        }
    }
}
