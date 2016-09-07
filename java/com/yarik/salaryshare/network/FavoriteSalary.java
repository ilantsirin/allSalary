package com.yarik.salaryshare.network;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yarik.salaryshare.model.Salary;

public class FavoriteSalary {

    public static void favoriteFeed(Salary salary) {
        ParseObject fav = new ParseObject("Favorite");
        fav.put("user", ParseUser.getCurrentUser());
        fav.put("salaryId", salary.getSalaryId());
        fav.saveInBackground();

        salary.setFavorited(true);
    }
}