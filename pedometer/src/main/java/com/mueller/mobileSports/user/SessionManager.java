package com.mueller.mobileSports.user;

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


/**
 * Created by Ete on 10/12/2016.
 * Class to store the current session of the user.
 * Data is persisted even if the app is closed and will only be cleared if the user decides to log out.
 */

public class SessionManager {

    private static UserData userData;
    private Context context;
    private AsyncCallback<Void> logoutResponder = new AsyncCallback<Void>() {
        @Override
        public void handleResponse(Void aVoid) {
            boolean isValidLogin = Backendless.UserService.isValidLogin();

        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {

        }
    };

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        if (userData == null) {
            userData = UserData.getInstance();
        }
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {

        if (!isLoggedIn()) {
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            loadCurrentUser();
            loadCurrentUserData();
        }
    }

    /**
     * Quick check for login
     **/
    private boolean isLoggedIn() {

        String userToken = UserTokenStorageFactory.instance().getStorage().get();
        return userToken != null && !userToken.equals("");


    }

    /**
     * Method to logout the current user.
     * Removes all userrelevant data and then redirects the user to the login screen
     */
    public void logoutUser() {
        if (isLoggedIn()) {

            userData.deleteAll();
            Backendless.UserService.logout(logoutResponder);
            // After logout redirect user to Login Activity
            Intent LoginIntent = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Staring Login Activity
            context.startActivity(LoginIntent);
        }
    }



    //Load currentUser
    private void loadCurrentUser() {

        final String currentUserId = Backendless.UserService.loggedInUser();
        Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser currentUser) {
                Backendless.UserService.setCurrentUser(currentUser);
                userData.setEmail(currentUser.getEmail());
            }
            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    // Load current userData
    private void loadCurrentUserData() {
        String currentUserId = Backendless.UserService.loggedInUser();
        String whereClause = "ownerID = '" + currentUserId + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        Backendless.Persistence.of(UserData.class).find(dataQuery, new AsyncCallback<BackendlessCollection<UserData>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserData> data) {
                if (data.getTotalObjects() == 0) {
                    String mail = userData.getEmail();
                    userData = UserData.getInstance("", mail);

                } else {
                    userData = UserData.getInstance(data.getData().get(0));
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });

    }

    public UserData getUserData() {

        if (isLoggedIn()) {
            loadCurrentUserData();
            return userData;
        } else {
            return new UserData();
        }
    }

    public void uploadUserData(final Context context) {

        System.out.println("Session : " + userData.getObjectId());


        Backendless.Persistence.of(UserData.class).save(userData, new AsyncCallback<UserData>() {
            @Override
            public void handleResponse(UserData updatedData) {
                Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(context, "Failure!", Toast.LENGTH_LONG).show();
                System.out.println(backendlessFault.toString());

            }
        });
    }
}
