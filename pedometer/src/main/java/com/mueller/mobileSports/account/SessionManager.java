package com.mueller.mobileSports.account;

import android.content.Context;
import android.content.Intent;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.mueller.mobileSports.user.UserProfileData;


/**
 * Created by Ete on 10/12/2016.
 * Class to store the current session of the user.
 * Data is persisted even if the app is closed and will only be cleared if the user decides to log out.
 */

public class SessionManager {

    Context _context;
    AsyncCallback<Void> logoutResponder = new AsyncCallback<Void>() {
        @Override
        public void handleResponse(Void aVoid) {
            boolean isValidLogin = Backendless.UserService.isValidLogin();

        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {

        }
    };
    private UserProfileData myData;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        myData = new UserProfileData();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */

    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);

        }
    }

    public void logoutUser() {

        if (this.isLoggedIn()) {

            Backendless.UserService.logout(logoutResponder);
            // After logout redirect user to Loing Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);
        }
    }

    /**
     * Quick check for login
     **/

    // Get Login State
    public boolean isLoggedIn() {

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

    private void loadCurrentUserData() {
        Backendless.Persistence.of(UserProfileData.class).findFirst(new AsyncCallback<UserProfileData>() {
            @Override
            public void handleResponse(UserProfileData data) {
                myData = data;
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
