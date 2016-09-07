package com.yarik.salaryshare.network;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yarik.salaryshare.model.Salary;

import java.util.List;

public class UnfavoriteSalary {

    public static void unfavoriteSalary(Salary salary) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("salaryId", salary.getSalaryId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                for (ParseObject obj : parseObjects) {
                    obj.deleteInBackground();
                }
            }
        });

        salary.setFavorited(false);
    }
}