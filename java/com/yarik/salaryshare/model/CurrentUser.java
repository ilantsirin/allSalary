package com.yarik.salaryshare.model;

/**
 * Created by Yarik on 18/10/2015.
 */

import com.parse.ParseObject;
import com.parse.ParseUser;

public class CurrentUser extends FacebookUser {

    private static CurrentUser instance;
    private boolean loggedIn;

    public static CurrentUser getInstance() {
        if (instance == null) {
            if (ParseUser.getCurrentUser() == null) {
                instance = new CurrentUser();
            } else {
                instance = new CurrentUser(ParseUser.getCurrentUser());
            }
        }
        return instance;
    }

    private CurrentUser() {
        super();
        loggedIn = false;
    }

    private CurrentUser(ParseObject object) {
        super(object);
        loggedIn = true;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

}