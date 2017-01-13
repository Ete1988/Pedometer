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
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.heartRate.HeartRateData;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.pedometer.PedometerData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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
                Toast.makeText(context, "Error logging in! Please register or check your log in details", Toast.LENGTH_LONG).show();
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

    /**
     * This method is only called if no tables in backendless exists.
     */
    private void createTablesAndTestData() {
        ArrayList<DailyData> data = new ArrayList<>();

        DailyData d1 = new DailyData();
        DailyData d2 = new DailyData();
        DailyData d3 = new DailyData();

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        d1.setSessionDay(dateFormat.format(cal.getTime()));
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DATE, -2);
        d2.setSessionDay(dateFormat.format(cal1.getTime()));
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DATE, -3);
        d3.setSessionDay(dateFormat.format(cal2.getTime()));

        PedometerData p1 = new PedometerData();
        p1.setDailyStepCount(800);
        p1.setDistance(244.44);
        p1.setEnergyExpenditureSteps(132);
        PedometerData p2 = new PedometerData();
        p2.setDailyStepCount(900);
        p2.setEnergyExpenditureSteps(132);
        p2.setDistance(344.44);
        PedometerData p3 = new PedometerData();
        p3.setDailyStepCount(1000);
        p3.setDistance(444.44);
        p3.setEnergyExpenditureSteps(132);

        HeartRateData h1 = new HeartRateData();
        h1.setMinHeartRate(56);
        h1.setMaxHeartRate(123);
        h1.setAverageHeartRate(76);
        HeartRateData h2 = new HeartRateData();
        h2.setMinHeartRate(57);
        h2.setMaxHeartRate(124);
        h2.setAverageHeartRate(77);
        HeartRateData h3 = new HeartRateData();
        h3.setMinHeartRate(58);
        h3.setMaxHeartRate(125);
        h3.setAverageHeartRate(78);

        d1.setHeartRateData(h1);
        d1.setPedometerData(p1);
        d2.setHeartRateData(h2);
        d2.setPedometerData(p2);
        d3.setHeartRateData(h3);
        d3.setPedometerData(p3);

        data.add(d1);
        data.add(d2);
        data.add(d3);


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

    private void mapDataToSharedValue(final Intent intent) {
        ArrayList<DailyData> data = userData.getDailyData();
        boolean newDay;
        Date now = new Date();
        SimpleDateFormat currDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        DailyData dailyData;
        PedometerData pedometerData;
        HeartRateData heartRateData;

        if (!(data.size() == 0)) {
            //Same date --> data from today
            if (data.get(data.size() - 1).getSessionDay().equals(currDate.format(now))) {
                dailyData = data.get(data.size() - 1);
                pedometerData = dailyData.getPedometerData();
                heartRateData = dailyData.getHeartRateData();
                newDay = false;
            } else {
                //New Day
                pedometerData = new PedometerData();
                heartRateData = new HeartRateData();
                sharedValues.saveString("sessionDay", currDate.format(now));
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
            } else {
                dailyData = new DailyData();
            }
        }


        PedometerData pedometerData = dailyData.getPedometerData();
        HeartRateData heartRateData = dailyData.getHeartRateData();

        //PedometerData
        pedometerData.setDailyStepCount(sharedValues.getInt("stepsOverDay"));
        pedometerData.setWeeklyStepCount(sharedValues.getInt("stepsOverWeek"));
        pedometerData.setDistance(sharedValues.getFloat("distance"));
        pedometerData.setEnergyExpenditureSteps(sharedValues.getInt("energyExpenditureSteps"));
        pedometerData.setDistance(sharedValues.getFloat("distance"));

        //HeartRateData
        heartRateData.setAverageHeartRate(sharedValues.getInt("averageHeartRate"));
        heartRateData.setMaxHeartRate(sharedValues.getInt("maxHeartRate"));
        heartRateData.setMinHeartRate(sharedValues.getInt("minHeartRate"));
        heartRateData.setEnergyExpenditure(sharedValues.getFloat("totalEnergyExpenditureDuringSession"));
        heartRateData.setTrimpScore(sharedValues.getInt("trimpScore"));
        heartRateData.setSessionDuration(sharedValues.getFloat("sessionDuration"));

        dailyData.setPedometerData(pedometerData);
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
}




