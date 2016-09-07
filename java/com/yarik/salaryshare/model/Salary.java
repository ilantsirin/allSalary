package com.yarik.salaryshare.model;

import com.parse.ParseObject;

import java.text.DecimalFormat;
import java.util.Date;


public class Salary{
    private String salaryId;
    private String fieldName;
    private String posName;
    private String salaryNum;
    private String expNum;
    private boolean favorited;
    private String distance;
    private float numericDistance;
    private Date createdAt;

    public Salary(){
    }
    public Salary(String salaryId, String fieldName, String posName, String salaryNum) {
        this.salaryId = salaryId;
        this.fieldName = fieldName;
        this.posName = posName;
        this.salaryNum = salaryNum;
    }

    public static Salary ParseToSalary(ParseObject salaryObject) {
        Salary salary = new Salary();
        salary.fieldName = salaryObject.getString("fieldName");
        salary.salaryId = salaryObject.getObjectId();
        salary.posName = salaryObject.getString("posName");
        salary.salaryNum = salaryObject.getString("salaryNum");
        salary.expNum = salaryObject.getString("expNum");
        salary.createdAt = salaryObject.getCreatedAt();
        salary.favorited = false;
        return salary;
    }
    public String getSalaryId() {
        return salaryId;
    }

    public String getField() {
        return fieldName;
    }

    public String getPosition() {
        return posName;
    }

    public String getSalary() {
        return salaryNum;
    }

    public String getExperience() {
        return expNum;
    }
    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }


    public void setField(String fieldName) {
        this.fieldName = fieldName;
    }
    public void setPosition(String posName) {
        this.posName = posName;
    }
    public void setSalary(String salaryNum) {
        this.salaryNum = salaryNum;
    }



    public String getDistance() {
        return this.distance;
    }

    public float getNumericDistance() {
        return this.numericDistance;
    }

    public void setDistance(float distance) {
        DecimalFormat df = new DecimalFormat("#.#");
        this.numericDistance = distance;
        this.distance = df.format(distance);
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }


}