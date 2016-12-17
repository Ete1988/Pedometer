package com.mueller.mobileSports.account;

import android.content.Context;
import android.content.Intent;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.mueller.mobileSports.user.UserProfileData;


/**
 * Created by Ete on 10/12/2016.
 * Class to store the current session of the user.
 * Data is persisted even if the app is closed and will only be cleared if the user decides to log out.
 */

public class SessionManager {

    private boolean isValidLogin;
    private Context context;

    private AsyncCallback<Void> logoutResponder = new AsyncCallback<Void>() {
        @Override
        public void handleResponse(Void aVoid) {
            isValidLogin = Backendless.UserService.isValidLogin();

        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {

        }
    };
    private UserProfileData myData;

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        myData = new UserProfileData();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {


        if (!this.isLoggedIn()) {

            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }
    }

    /**
     * Method to logout the current user.
     * Removes all userrelevant data and then redirects the user to the login screen
     */
    public void logoutUser() {

        if (this.isLoggedIn()) {

            myData.deleteAll();
            Backendless.UserService.logout(logoutResponder);
            // After logout redirect user to Login Activity
            Intent LoginIntent = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Staring Login Activity
            context.startActivity(LoginIntent);
        }
    }

    /**
     * Quick check for login
     *
     **/
    private boolean isLoggedIn() {

        String userToken = UserTokenStorageFactory.instance().getStorage().get();
        if (userToken != null && !userToken.equals("")) {
            loadCurrentUser();
            loadCurrentUserData();
            return true;
        } else {
            return false;
        }
    }

    //Load currentUser
    private void loadCurrentUser() {

        String currentUserId = Backendless.UserService.loggedInUser();
        Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser currentUser) {
                Backendless.UserService.setCurrentUser(currentUser);
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

        Backendless.Persistence.of(UserProfileData.class).find(dataQuery, new AsyncCallback<BackendlessCollection<UserProfileData>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserProfileData> data) {
                if (data.getTotalObjects() == 0) {
                    myData = new UserProfileData("", "", "", 0, 0, 0, 0, 0, 0, 0, 0, null);
                } else {
                    myData = data.getData().get(0);
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });

    }

    public boolean uploadUserData() {

        return true;
    }
}
