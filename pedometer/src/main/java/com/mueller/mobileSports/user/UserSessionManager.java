package com.mueller.mobileSports.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.mueller.mobileSports.account.LoginActivity;
import com.mueller.mobileSports.heartRate.HeartRateData;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.pedometer.PedometerData;
import com.mueller.mobileSports.session.TrainingSessionData;
import com.mueller.mobileSports.utility.SharedValues;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Ete on 10/12/2016.
 * Class to store the current session of the user.
 * Data is persisted even if the app is closed and will only be cleared if the user decides to log out.
 */
public class UserSessionManager {

    private final static String TAG = UserSessionManager.class.getSimpleName();
    private static UserData userData;
    private ProgressDialog progress;
    private SharedValues sharedValues;
    private Context context;

    // Constructor
    public UserSessionManager(Context context) {
        this.context = context;
        sharedValues = SharedValues.getInstance(context);
        getCurrentDateAsString();
    }

    public static UserData getUserData() {
        return userData;
    }

    public static void setUserData(UserData userData) {
        UserSessionManager.userData = userData;
    }

    public void isUserLoggedIn() {

        if (isUserTokenAvailable()) {

            /*
              This was implemented to prevent some data conflicts since loading data from backendless
            sometimes takes longer than expected and the user would be provided with false data on the pedometer view.
             */
            progress = new ProgressDialog(context);
            progress.setTitle("Loading Data");
            progress.setMessage("Please wait...");
            progress.setCancelable(false);
            progress.show();

            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {

                    if (!response) {
                        logoutUser(true);
                    } else {
                        getUserDataFromServer(new Intent(context, PedometerActivity.class));
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    //Normally never reached
                    logoutUser(true);
                    System.err.println("Error - " + fault);
                }

            });
        }
    }

    /**
     * Quick mapDataToSharedValue for login user token
     *
     * @return true if user token is stored
     */
    private boolean isUserTokenAvailable() {
        String userToken = UserTokenStorageFactory.instance().getStorage().get();
        return userToken != null && !userToken.equals("");
    }

    /**
     * Method to logout the current user.
     * Removes all user relevant data and then redirects the user to the login screen
     */
    private void logoutUser(final boolean progressBarActive) {

        Backendless.UserService.logout(new AsyncCallback<Void>() {

            @Override
            public void handleResponse(Void aVoid) {
                if (progressBarActive) {
                    progress.dismiss();
                }

                UserSessionManager.setUserData(null);
                Intent i = new Intent(context, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.e(TAG, "Error User Logout: " + backendlessFault);
                if (progressBarActive) {
                    progress.dismiss();
                }
            }
        });
    }

    // save userdata to backendless server
    public void uploadUserData(final Context context, final boolean showProgressBar, final boolean logout, final boolean clearSharedValues) {
        //If user is logged in
        if (isUserTokenAvailable()) {

            if (showProgressBar) {
                progress = new ProgressDialog(context);
                progress.setTitle("Saving Data");
                progress.setMessage("Please wait...");
                progress.setCancelable(false);
                progress.show(); // disable dismiss by tapping outside of the dialog

            }

            saveDataToObjects();

            Backendless.Persistence.of(UserData.class).save(userData, new AsyncCallback<UserData>() {
                @Override
                public void handleResponse(UserData updatedData) {
                    userData = updatedData;
                    if (clearSharedValues) {
                        sharedValues.clearData();
                    }
                    if (logout) {
                        logoutUser(showProgressBar);
                    } else if (showProgressBar) {
                        progress.dismiss();
                        Toast.makeText(context, "Saved!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                    if (showProgressBar) {
                        Toast.makeText(context, "An error occurred, please try again.!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                    System.err.println(backendlessFault.toString());
                }
            });
        }
    }

    public void userLogin(String email, String password, final Intent intent) {

        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show(); // disable dismiss by tapping outside of the dialog

        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                Backendless.UserService.setCurrentUser(backendlessUser);
                sharedValues.saveString("email", backendlessUser.getEmail());
                sharedValues.saveString("username", backendlessUser.getProperty("name").toString());
                getUserFromServer(intent);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                if (Objects.equals(backendlessFault.getCode(), "3087")) {
                    Toast.makeText(context, "Please check and confirm email address", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Error logging in! Please register or check your log in details", Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }
        }, true);


    }

    //Load currentUser
    private void getUserFromServer(final Intent intent) {
        String currentUserId = Backendless.UserService.loggedInUser();
        Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser currentUser) {
                getUserDataFromServer(intent);

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                progress.dismiss();
                logoutUser(false);
                Log.e(TAG, "Could not load user: " + backendlessFault);
            }
        });
    }

    // Load current userData
    private void getUserDataFromServer(final Intent intent) {
        String currentUserId = Backendless.UserService.loggedInUser();
        String whereClause = "ownerID = '" + currentUserId + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(UserData.class).find(dataQuery, new AsyncCallback<BackendlessCollection<UserData>>() {

            @Override
            public void handleResponse(BackendlessCollection<UserData> data) {
                if (!(data.getTotalObjects() == 0)) {
                    userData = data.getData().get(0);
                    Log.e(TAG, "Loaded UserData: " + userData.getObjectId());
                    getDailyDataFromServer(intent);
                } else {
                    Log.e(TAG, "Could not load UserData (Does not exist on server)");
                    userData = new UserData();
                    userData.setEmail(sharedValues.getString("email"));
                    userData.setUsername(sharedValues.getString("name"));
                    Intent i = new Intent(context, ProfileActivity.class);
                    i.putExtra("firstTime", true);
                    mapDataToSharedValue(i);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                progress.dismiss();
                Log.e(TAG, "Error loading UserData: " + fault);
                if (Objects.equals(fault.getCode(), "1009")) {
                    userData = new UserData();
                    createTablesAndTestData();
                }
            }
        });
    }

    // Load current userData
    private void getDailyDataFromServer(final Intent intent) {
        String whereClause = "UserData[dailyData]" +
                ".objectId='" + userData.getObjectId() + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(DailyData.class).find(dataQuery, new AsyncCallback<BackendlessCollection<DailyData>>() {

            @Override
            public void handleResponse(BackendlessCollection<DailyData> data) {
                if (!(data.getTotalObjects() == 0)) {
                    ArrayList<DailyData> myList = new ArrayList<>(data.getData());
                    myList = revertList(myList);
                    userData.setDailyData(myList);
                    mapDataToSharedValue(intent);

                } else {
                    Log.e(TAG, "Could not load DailyData (Does not exist on server)");
                    mapDataToSharedValue(intent);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                progress.dismiss();
                Log.e(TAG, "Error loading UserData: " + fault);
                if (Objects.equals(fault.getCode(), "1009")) {
                    userData = new UserData();
                    createTablesAndTestData();
                }
            }
        });
    }

    /**
     * If user is not logged is, the current User will be redirected to the login activity
     */
    public void checkUserState() {

        if (!isUserTokenAvailable()) {
            goToLogin();
        } else {
            isLoginValid();
        }
    }

    //Check if loginToken is still valid
    private void isLoginValid() {
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {

                if (!response) {
                    String currentUserId = Backendless.UserService.loggedInUser();
                    Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser currentUser) {
                            logoutUser(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            progress.dismiss();
                            logoutUser(false);
                            Log.e(TAG, "Error login validation:" + backendlessFault);
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                logoutUser(false);
                System.err.println("Error - " + fault);
            }

        });
    }

    // return to login activity
    private void goToLogin() {
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private String getCurrentDateAsString() {
        Date now = new Date();
        SimpleDateFormat currDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        sharedValues.saveString("sessionDay", currDate.format(now));
        return currDate.format(now);
    }

    private void mapDataToSharedValue(final Intent intent) {

        ArrayList<DailyData> data = userData.getDailyData();
        boolean newDay;
        DailyData dailyData;
        PedometerData pedometerData;
        HeartRateData heartRateData;

        if (!(data.size() == 0)) {
            //Same date --> data from today
            if (data.get(data.size() - 1).getSessionDay().equals(getCurrentDateAsString())) {

                dailyData = data.get(data.size() - 1);
                pedometerData = dailyData.getPedometerData();
                heartRateData = dailyData.getHeartRateData();
                newDay = false;
            } else {
                //New Day
                pedometerData = new PedometerData();
                heartRateData = new HeartRateData();
                newDay = true;
            }

            //Userdata
            sharedValues.saveInt("heartRateMax", userData.getHeartRateMax());
            sharedValues.saveInt("stepGoal", userData.getStepGoal());

            //PedometerValues
            sharedValues.saveInt("stepsOverDay", pedometerData.getDailyStepCount());
            sharedValues.saveInt("energyExpenditureSteps", pedometerData.getEnergyExpenditureSteps());
            sharedValues.saveFloat("distance", (float) pedometerData.getDistance());

            if (newDay && isNewWeek()) {
                sharedValues.saveInt("stepsOverWeek", 0);
            } else if (newDay && !isNewWeek()) {
                sharedValues.saveInt("stepsOverWeek", data.get(data.size() - 1).getPedometerData().getWeeklyStepCount());
            } else {
                sharedValues.saveInt("stepsOverWeek", pedometerData.getWeeklyStepCount());
            }

            //HeartRateValues
            sharedValues.saveInt("averageHeartRate", heartRateData.getAverageHeartRate());
            sharedValues.saveInt("maxHeartRate", heartRateData.getMaxHeartRate());
            sharedValues.saveInt("minHeartRate", heartRateData.getMinHeartRate());

        }

        progress.dismiss();

        if (intent != null) {
            context.startActivity(intent);
        }
    }

    private void saveDataToObjects() {

        ArrayList<DailyData> data = userData.getDailyData();
        DailyData dailyData;

        //First time
        if (data.size() == 0) {
            dailyData = new DailyData();
        } else {
            //Same Day
            if (data.get(data.size() - 1).getSessionDay().equals(sharedValues.getString("sessionDay"))) {
                dailyData = data.get(data.size() - 1);
                data.remove(data.size() - 1);
            } else {
                dailyData = new DailyData();
            }
        }

        PedometerData pedometerData = dailyData.getPedometerData();
        HeartRateData heartRateData = dailyData.getHeartRateData();
        TrainingSessionData trainingSessionData = dailyData.getTrainingSessionData();

        //PedometerData
        pedometerData.setDailyStepCount(sharedValues.getInt("stepsOverDay"));
        pedometerData.setWeeklyStepCount(sharedValues.getInt("stepsOverWeek"));
        pedometerData.setDistance(sharedValues.getFloat("distance"));
        pedometerData.setEnergyExpenditureSteps(sharedValues.getInt("energyExpenditureSteps"));


        //HeartRateData
        heartRateData.setAverageHeartRate(sharedValues.getInt("averageHeartRateOverDay"));
        heartRateData.setMaxHeartRate(sharedValues.getInt("maxHeartRate"));
        heartRateData.setMinHeartRate(sharedValues.getInt("minHeartRate"));

        //TrainingSessionData
        trainingSessionData.setTrimpScore(sharedValues.getInt("trimpScore"));
        trainingSessionData.setSessionDuration(sharedValues.getFloat("sessionDuration"));
        trainingSessionData.setEnergyExpenditure(sharedValues.getFloat("totalEnergyExpenditureDuringSession"));
        trainingSessionData.setFatigue(sharedValues.getInt("fatigue"));
        trainingSessionData.setFitness(sharedValues.getInt("fitness"));
        trainingSessionData.setPerformance(sharedValues.getInt("performance"));

        data.add(dailyData);
        userData.setDailyData(data);

    }

    private ArrayList<DailyData> revertList(ArrayList<DailyData> arrayList) {
        Collections.reverse(arrayList);
        return arrayList;
    }

    private boolean isNewWeek() {
        try {
            return tryToCheckIfNewWeek();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean tryToCheckIfNewWeek() throws ParseException {

        SimpleDateFormat currDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        ArrayList<DailyData> myList = userData.getDailyData();
        Calendar c = Calendar.getInstance();
        Calendar checkWeek = Calendar.getInstance();
        checkWeek.setTime(currDate.parse(myList.get(myList.size() - 1).getSessionDay()));
        int weekOfYear = checkWeek.get(Calendar.WEEK_OF_YEAR);
        return (c.get(Calendar.WEEK_OF_YEAR)) > weekOfYear;
    }

    /**
     * This method is only called if no tables in backendless exists.
     */
    private void createTablesAndTestData() {
        ArrayList<DailyData> data = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        int i, j = -14;
        int[] fatigue = {33, 137, 232, 308, 371, 458, 528, 608, 565, 565, 756, 851, 963, 1023, 1145, 1278, 1352};
        int[] fitness = {1000, 1082, 1163, 1230, 1290, 1376, 1452, 1542, 1605, 1623, 1740, 1860, 1978, 2056, 2166, 2256, 2389};
        int[] performance = {934, 808, 698, 614, 547, 461, 397, 327, 293, 310, 227, 153, 245, 133, 233, 355, 486, 333, 478, 321, 201, 145};
        int[] trimp = {33, 107, 107, 96, 90, 119, 110, 126, 101, 57, 157, 163, 177, 105, 144, 106, 56, 189, 145, 136, 103, 78, 96, 177};

        for (i = 0; i < 15; i++) {
            Calendar cal = Calendar.getInstance();
            DailyData myDara = new DailyData();
            PedometerData pedometerData = new PedometerData();
            HeartRateData heartRateData = new HeartRateData();
            TrainingSessionData trainingSessionData = new TrainingSessionData();
            Random r = new Random();
            pedometerData.setDailyStepCount(r.nextInt(12000 - 6001) + 6000);
            pedometerData.setEnergyExpenditureSteps(r.nextInt(50000 - 10001) + 10000);
            pedometerData.setDistance(ThreadLocalRandom.current().nextDouble(10.0, 50.0));

            heartRateData.setMinHeartRate(r.nextInt(150 - 81) + 80);
            heartRateData.setMaxHeartRate(r.nextInt(250 - 151) + 150);
            heartRateData.setAverageHeartRate(r.nextInt(175 - 121) + 120);

            trainingSessionData.setFitness(fitness[i]);
            trainingSessionData.setFatigue(fatigue[i]);
            trainingSessionData.setTrimpScore(trimp[i]);
            trainingSessionData.setPerformance(performance[i]);
            trainingSessionData.setEnergyExpenditure((float) ThreadLocalRandom.current().nextDouble(50.0, 150.0));

            myDara.setTrainingSessionData(trainingSessionData);
            myDara.setHeartRateData(heartRateData);
            myDara.setPedometerData(pedometerData);

            cal.add(Calendar.DATE, +j);
            myDara.setSessionDay(dateFormat.format(cal.getTime()));
            data.add(myDara);
            j++;
        }

        userData.setDailyData(data);

        Backendless.Persistence.of(UserData.class).save(userData, new AsyncCallback<UserData>() {
            @Override
            public void handleResponse(UserData updatedData) {
                Log.i(TAG, "Tables Created");
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                System.err.println("Error - " + backendlessFault);
            }
        });
    }

}




