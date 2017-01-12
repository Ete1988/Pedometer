package com.mueller.mobileSports.general;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ete on 17/12/2016.
 * <p>
 * Class meant to check whether a new day or week started to adjust date and other data accordingly.
 *
 */

public class TimeManager {

    private SharedValues sharedValues;

    public TimeManager(Context context) {
        this.sharedValues = SharedValues.getInstance(context);
    }

    /**
     * Method to check if a new week or day has began
     */
    public void checkTime() {
        try {
            checkIfNewDay();
            checkIfNewWeek();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void checkIfNewDay() throws ParseException {
        SimpleDateFormat currDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        if (!(sharedValues.checkIfContained("sessionDay"))) {
            //First time use...
            sharedValues.saveString("sessionDay", getCurrentDateAsString());
        } else {
            Date oldDate = currDate.parse((sharedValues.getString("sessionDay")));
            Date now = currDate.parse(getCurrentDateAsString());
            if (oldDate.before(now)) {
                //Before today...
                sharedValues.saveString("sessionDay", getCurrentDateAsString());
            }
        }
    }

    private void checkIfNewWeek() {

        Calendar c = Calendar.getInstance();
        if (!sharedValues.checkIfContained("weekOfYear")) {
            sharedValues.saveInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));

        } else {

            if ((c.get(Calendar.WEEK_OF_YEAR)) > (sharedValues.getInt("weekOfYear"))) {
                //New week...
                sharedValues.saveInt("stepsOverWeek", 0);
                sharedValues.saveInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));

            }
        }
    }

    private String getCurrentDateAsString() {
        Date todayDate = new Date();
        SimpleDateFormat currDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return currDate.format(todayDate);
    }

}
