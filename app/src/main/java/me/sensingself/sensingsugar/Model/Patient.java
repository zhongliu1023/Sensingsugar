package me.sensingself.sensingsugar.Model;

import java.util.ArrayList;

/**
 * Created by liujie on 1/25/18.
 */

public class Patient {
    private int patientId;
    private String firstName;
    private String lastName;
    private float height;
    private float weight;
    private String gender;
    private String birthday;
    private String aadhaar;
    private String phoneNumber;
    private long testdate;
    private ArrayList<TestingResult> testingResults;
    private boolean isSelected;
    private String district;
    private String state;

    public Patient() {
        this.patientId = 0;
        this.firstName = "";
        this.lastName = "";
        this.height = 0;
        this.weight = 0;
        this.aadhaar = "";
        this.gender = "";
        this.birthday = "";
        this.phoneNumber = "";
        this.district = "";
        this.state = "";
        this.isSelected = false;
        this.testingResults = new ArrayList<TestingResult>();

    }

    public int getPatientId() {
        return patientId;
    }
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getAadhaar() {
        return aadhaar;
    }
    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public long getTestdate() {
        return testdate;
    }
    public void setTestdate(long testdate) {
        this.testdate = testdate;
    }

    public boolean getSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public ArrayList<TestingResult> getTestingResults() {
        return testingResults;
    }
    public void setTestingResults(ArrayList<TestingResult> testingResults) {
        this.testingResults = testingResults;
    }
    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", aadhaar=" + aadhaar +
                ", phoneNumber=" + phoneNumber +
                '}';
    }
}
