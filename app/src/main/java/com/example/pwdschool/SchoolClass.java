package com.example.pwdschool;

public class SchoolClass {

    private String schoolName;
    private String buildingName;

    SchoolClass(String schoolName,String buildingName){
        this.schoolName =schoolName;
        this.buildingName = buildingName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getBuildingName() {
        return buildingName;
    }
}
