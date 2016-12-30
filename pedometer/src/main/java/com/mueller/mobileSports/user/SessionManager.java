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
import com.mueller.mobileSports.general.TimeManager;
import com.mueller.mobileSports.heartRate.hR_Utility.HeartRateData;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.pedometer.pedometerUtility.PedometerData;

import java.util.Objects;

/**
 * Created by Ete on 10/12/2016.
 * Class to store the current session of the user.
 * Data is persisted even if the app is closed and will only be cleared if the user decides to log out.
 */
public class SessionManager {

    private final static String TAG = SessionManager.class.getSimpleName();
    private static UserData userData;
    private ProgressDialog progress;
    private SharedValues sharedValues;
    private Context context;

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        sharedValues = SharedValues.getInstance(context);
        TimeManager timeManager = new TimeManager(context);
        timeManager.checkTime();
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

    public boolean isUserLoggedIn() {
        if (isUserTokenAvailable()) {
            progress = new ProgressDialog(context);
            progress.setTitle("Loading Data");
            progress.setMessage("Please wait...");
            progress.setCancelable(false);
            progress.show(); // disable dismiss by tapping outside of the dialog
            final Intent intent = new Intent(context, PedometerActivity.class);
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {

                    if (!response) {
                        logoutUser(true);
                        System.out.println("[ASYNC] Is login valid? - " + response);
                    } else {
                        getUserDataFromServer(intent);
                    }
                    System.out.println("[ASYNC] Is login valid? - " + response);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    logoutUser(true);
                    System.err.println("Error - " + fault);
                }

            });

            return true;
        }
        return false;
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
                sharedValues.clearData();
                Log.e(TAG, "UserLogout successful");
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
    public void uploadUserData(final Context context, final boolean showProgressBar, final boolean logout) {
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
                HeartRateData heartRateData = updatedData.getHeartRateData();
                PedometerData pedometerData = updatedData.getPedometerData();
                userData.setPedometerData(pedometerData);
                userData.setHeartRateData(heartRateData);

                if (logout) {
                    logoutUser(showProgressBar);
                } else if (showProgressBar) {
                    progress.dismiss();
                    Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
                }

                System.out.println(userData.getObjectId());
                System.out.println(userData.getPedometerData().getObjectId());
                System.out.println(userData.getHeartRateData().getObjectId());
                System.out.println(userData.getHeartRateData().getObjectId());
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(context, "Ooops!", Toast.LENGTH_LONG).show();
                if (showProgressBar) {
                    progress.dismiss();
                }
                System.out.println(backendlessFault.toString());
            }
        });
    }

    private void saveDataToObjects() {
        PedometerData pedometerData;
        HeartRateData heartRateData;

        try {
            pedometerData = userData.getPedometerData();
        } catch (NullPointerException e) {
            System.err.println("Error" + e);
            pedometerData = new PedometerData();
        }

        try {
            heartRateData = userData.getHeartRateData();
        } catch (NullPointerException e) {
            System.err.println("Error" + e);
            heartRateData = new HeartRateData();
        }

        //UserData
        userData.setUsername(sharedValues.getString("username"));
        userData.setAge(sharedValues.getInt("age"));
        userData.setWeight(sharedValues.getInt("weight"));
        userData.setHeight(sharedValues.getInt("height"));
        userData.setEmail(sharedValues.getString("email"));
        userData.setGender(sharedValues.getString("gender"));
        userData.setStepGoal(sharedValues.getInt("stepGoal"));
        userData.setActivityLevel(sharedValues.getInt("physicalActivityLevel"));
        userData.setRestingHeartRate(sharedValues.getInt("restingHeartRate"));
        userData.setHeartRateMax(sharedValues.getInt("heartRateMax"));

        //PedometerData
        pedometerData.setSessionDay(sharedValues.getString("sessionDay"));
        pedometerData.setDailyStepCount(sharedValues.getInt("stepsOverDay"));
        pedometerData.setWeeklyStepCount(sharedValues.getInt("stepsOverWeek"));

        //HeartRateData
        heartRateData.setAverageHeartRate(sharedValues.getInt("averageHeartRate"));
        heartRateData.setSessionDay(sharedValues.getString("sessionDay"));
        heartRateData.setMaxHeartRate(sharedValues.getInt("maxHeartRate"));
        heartRateData.setMinHeartRate(sharedValues.getInt("minHeartRate"));

        userData.setHeartRateData(heartRateData);
        userData.setPedometerData(pedometerData);

    }

    // return to login activity
    private void goToLogin() {
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    //Check if loginToken is still valid
    private void isLoginValid() {
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {

                if (!response) {
                    Log.e(TAG, "Is login valid? - " + response);
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
                Log.e(TAG, "Is login valid? - " + response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                logoutUser(false);
                System.err.println("Error - " + fault);
            }

        });
    }

    public void userLogin(String email, String password, final Intent intent) {

        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show(); // disable dismiss by tapping outside of the dialog
        sharedValues.saveString("email", email);

        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                Backendless.UserService.setCurrentUser(backendlessUser);
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
                Log.e(TAG, "Loaded User: " + currentUser);
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
                    userData = new UserData(data.getData().get(0));
                    getUserPedometerDataFromServer(intent);
                    userData.setEmail(sharedValues.getString("email"));
                    Log.e(TAG, "Loaded UserData: " + userData.getObjectId());
                } else {
                    Log.e(TAG, "Could not load UserData (Does not exist on server)");
                    userData = new UserData();
                    userData.setEmail(sharedValues.getString("email"));
                    getUserPedometerDataFromServer(intent);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                progress.dismiss();
                Log.e(TAG, "Error loading UserData: " + fault);
                if (Objects.equals(fault.getCode(), "1009")) {
                    userData = new UserData();
                    userData.setEmail(sharedValues.getString("email"));
                    createTables();
                }
            }
        });
    }

    //get pedometerData
    private void getUserPedometerDataFromServer(final Intent intent) {
        String whereClausePedometerData = "UserData[pedometerData]" +
                ".objectId='" + userData.getObjectId() + "'" +
                " and " +
                "sessionDay = '" + sharedValues.getString("sessionDay") + "'";

        BackendlessDataQuery dataQueryPedometerData = new BackendlessDataQuery();
        dataQueryPedometerData.setWhereClause(whereClausePedometerData);
        Backendless.Persistence.of(PedometerData.class).find(dataQueryPedometerData, new AsyncCallback<BackendlessCollection<PedometerData>>() {
            @Override
            public void handleResponse(BackendlessCollection<PedometerData> pedometerDataBackendlessCollection) {
                if (!(pedometerDataBackendlessCollection.getTotalObjects() == 0)) {
                    userData.setPedometerData(pedometerDataBackendlessCollection.getData().get(0));
                    getUserHeartRateDataFromServer(intent);
                    Log.e(TAG, "Loaded PedometerData: " + userData.getPedometerData().getObjectId());
                } else {
                    Log.e(TAG, "Could not load PedometerData (Does not exist on server)");
                    PedometerData pedometerData = new PedometerData();
                    pedometerData.setSessionDay(sharedValues.getString("sessionDay"));
                    userData.setPedometerData(pedometerData);
                    getUserHeartRateDataFromServer(intent);
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.e(TAG, "Error loading PedometerData: " + backendlessFault);
                if (Objects.equals(backendlessFault.getCode(), "1009")) {
                    userData = new UserData();
                    userData.setEmail(sharedValues.getString("email"));
                    createTables();
                }
            }
        });
    }

    //Get heartRateData
    private void getUserHeartRateDataFromServer(final Intent intent) {

        String whereClauseHeartRateData = "UserData[heartRateData]" +
                ".objectId='" + userData.getObjectId() + "'" +
                " and " +
                "sessionDay = '" + sharedValues.getString("sessionDay") + "'";

        BackendlessDataQuery dataQueryHeartRateData = new BackendlessDataQuery();
        dataQueryHeartRateData.setWhereClause(whereClauseHeartRateData);
        Backendless.Persistence.of(HeartRateData.class).find(dataQueryHeartRateData, new AsyncCallback<BackendlessCollection<HeartRateData>>() {
            @Override
            public void handleResponse(BackendlessCollection<HeartRateData> heartRateDataBackendlessCollection) {
                if (!(heartRateDataBackendlessCollection.getTotalObjects() == 0)) {
                    userData.setHeartRateData(heartRateDataBackendlessCollection.getData().get(0));
                    Log.e(TAG, "Loaded HeartRateData: " + userData.getHeartRateData().getObjectId());
                    mapDataToSharedValue(intent);
                } else {
                    Log.e(TAG, "Could not load HeartRateData (Does not exist on server)");
                    HeartRateData heartRateData = new HeartRateData();
                    heartRateData.setSessionDay(sharedValues.getString("sessionDay"));
                    userData.setHeartRateData(heartRateData);
                    mapDataToSharedValue(intent);
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.e(TAG, "Error loading HeartRateData: " + backendlessFault);
                if (Objects.equals(backendlessFault.getCode(), "1009")) {
                    userData = new UserData();
                    userData.setEmail(sharedValues.getString("email"));
                    createTables();
                }
            }
        });
    }

    private void mapDataToSharedValue(final Intent intent) {
        PedometerData pedometerData = userData.getPedometerData();

        //UserData
        sharedValues.saveString("username", userData.getUsername());
        sharedValues.saveString("gender", userData.getGender());
        sharedValues.saveInt("age", userData.getAge());
        sharedValues.saveInt("weight", userData.getWeight());
        sharedValues.saveInt("height", userData.getHeight());
        sharedValues.saveInt("stepGoal", userData.getStepGoal());
        sharedValues.saveInt("physicalActivityLevel", userData.getActivityLevel());
        sharedValues.saveInt("weight", userData.getWeight());
        sharedValues.saveInt("restingHeartRate", userData.getRestingHeartRate());
        sharedValues.saveInt("heartRateMax", userData.getHeartRateMax());

        //PedometerData
        sharedValues.saveInt("stepsOverDay", pedometerData.getDailyStepCount());
        sharedValues.saveInt("stepsOverWeek", pedometerData.getWeeklyStepCount());

        Log.e(TAG, "Data mapped to sharedValues");
        progress.dismiss();

        if (intent != null) {
            context.startActivity(intent);
        }
    }

    //FOR FIRST TIME TESTING
    private void createTables() {
        Backendless.Persistence.of(UserData.class).save(userData, new AsyncCallback<UserData>() {
            @Override
            public void handleResponse(UserData updatedData) {
                System.out.println("Tables Created");
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                System.err.println("Error - " + backendlessFault);
            }
        });
    }

}

