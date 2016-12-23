package com.mueller.mobileSports.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.mueller.mobileSports.pedometer.pedometerUtility.PedometerData;

/**
 * Created by Ete on 10/12/2016.
 * Class to store the current session of the user.
 * Data is persisted even if the app is closed and will only be cleared if the user decides to log out.
 *
 */
//TODO check all methods
public class SessionManager {

    private static UserData userData;
    ProgressDialog progress;
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
     * Method to check if the User is still logged in
     * If user is not logged is, the current User will be redirected to the login activity
     *
     */
    public boolean checkUserState() {

        if (!isUserTokenAvailable()) {
            goToLogin();
            return false;
        } else {
            getUserFromServer();
            return true;
        }
    }

    public boolean checkIfUserDataAvailable() {
        return java.util.Objects.equals(userData, null);
    }

    /**
     * Quick check for login user token
     * @return true if user token is stored
     */
    public boolean isUserTokenAvailable() {
        String userToken = UserTokenStorageFactory.instance().getStorage().get();
        return userToken != null && !userToken.equals("");
    }

    /**
     * Method to logout the current user.
     * Removes all userrelevant data and then redirects the user to the login screen
     */
    public void logoutUser() {

        if (isUserTokenAvailable()) {
            userData.deleteAll();
            Backendless.UserService.logout(new AsyncCallback<Void>() {

                @Override
                public void handleResponse(Void aVoid) {
                    Toast.makeText(context, "GoodBye!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    System.err.println("Error - " + backendlessFault);
                }
            });

            goToLogin();
        }
    }

    // save userdata to backendless server
    public void uploadUserData(final Context context, final Intent intent) {
        sharedValues.saveBool("loading", true);

        if (!(intent == null)) {
            progress = new ProgressDialog(context);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show(); // disable dismiss by tapping outside of the dialog
        }

        PedometerData pedometerData = userData.getPedometerData();
        HeartRateData heartRateData = userData.getHeartRateData();

        //UserData
        userData.setUsername(sharedValues.getString("username"));
        userData.setAge(sharedValues.getInt("age"));
        userData.setWeight(sharedValues.getInt("weight"));
        userData.setHeight(sharedValues.getInt("height"));
        userData.setEmail(sharedValues.getString("email"));
        userData.setGender(sharedValues.getString("gender"));

        //PedometerData
        pedometerData.setSessionDay(sharedValues.getString("sessionDay"));
        pedometerData.setDaylyStepCount(sharedValues.getInt("stepsOverDay"));
        pedometerData.setWeeklyStepCount(sharedValues.getInt("stepsOverWeek"));
        pedometerData.setStepGoal(sharedValues.getInt("stepGoal"));

        //HeartRateData
        heartRateData.setAverageHeartRate(sharedValues.getInt("averageHeartRate"));
        heartRateData.setSessionDay(sharedValues.getString("sessionDay"));
        heartRateData.setMaxHeartRate(sharedValues.getInt("maxHeartRate"));
        heartRateData.setMinHeartRate(sharedValues.getInt("minHeartRate"));
        heartRateData.setActivityLevel(sharedValues.getInt("physicalActivityLevel"));

        System.out.println(heartRateData.getMinHeartRate());
        System.out.println(heartRateData.getMaxHeartRate());
        System.out.println(heartRateData.getAverageHeartRate());


        userData.setHeartRateData(heartRateData);
        userData.setPedometerData(pedometerData);

        Backendless.Persistence.of(UserData.class).save(userData, new AsyncCallback<UserData>() {
            @Override
            public void handleResponse(UserData updatedData) {
                // Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
                HeartRateData heartRateData = updatedData.getHeartRateData();
                PedometerData pedometerData = updatedData.getPedometerData();
                userData.setPedometerData(pedometerData);
                userData.setHeartRateData(heartRateData);

                if (!(intent == null)) {
                    context.startActivity(intent);
                }


                System.out.println(userData.getObjectId());
                System.out.println(userData.getPedometerData().getObjectId());
                System.out.println(userData.getHeartRateData().getObjectId());

                sharedValues.saveBool("loading", false);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(context, "Ooops!", Toast.LENGTH_LONG).show();
                progress.dismiss();
                System.out.println(backendlessFault.toString());
            }
        });
    }

    // return to login activity
    private void goToLogin() {
        userData.deleteAll();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    //Check if loginToken is stil valid
    public void isLoginValid() {
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {

                if (!response) {
                    logoutUser();
                    System.out.println("[ASYNC] Is login valid? - " + response);
                }
                System.out.println("[ASYNC] Is login valid? - " + response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                logoutUser();
                System.err.println("Error - " + fault);
            }

        });
    }

    // Load current userData
    public void getUserDataFromServer() {
        String currentUserId = Backendless.UserService.loggedInUser();
        String whereClause = "ownerID = '" + currentUserId + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(UserData.class).find(dataQuery, new AsyncCallback<BackendlessCollection<UserData>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserData> data) {
                if (!(data.getTotalObjects() == 0)) {
                    userData = new UserData(data.getData().get(0));
                    getUserPedometerDataFromServer();
                    System.out.println("Second Step!");

                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.err.println("Error - " + fault);
            }
        });

    }

    //Load currentUser
    private void getUserFromServer() {

        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show(); // disable dismiss by tapping outside of the dialog

        String currentUserId = Backendless.UserService.loggedInUser();
        Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser currentUser) {
                Backendless.UserService.setCurrentUser(currentUser);
                sharedValues.saveString("email", currentUser.getEmail());
                System.out.println("First Step!");
                getUserDataFromServer();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                logoutUser();
                System.err.println("Error - " + backendlessFault);
            }
        });
    }

    //Get heartRateData
    private void getUserHeartRateDataFromServer() {

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
                    System.out.println("Finished Step!");
                    sharedValues.saveBool("loading", false);
                    check();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                System.err.println("Error - " + backendlessFault);
            }
        });
    }

    //get pedometerData
    private void getUserPedometerDataFromServer() {
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
                    getUserHeartRateDataFromServer();
                    System.out.println("Third Step!");
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                System.err.println("Error - " + backendlessFault);
            }
        });
    }

    //just for testing
    private void check() {

        progress.dismiss();
        System.out.println(userData.getObjectId());
        System.out.println(userData.getPedometerData().getObjectId());
        System.out.println(userData.getHeartRateData().getObjectId());

    }

}
