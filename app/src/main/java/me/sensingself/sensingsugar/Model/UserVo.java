package me.sensingself.sensingsugar.Model;

/**
 * Created by liujie on 1/19/18.
 */

public class UserVo {
    private int userId;
    private String firstName;
    private String lastName;
    private String aadhaar;
    private String institutatinType;
    private String institutationName;
    private String state;
    private String city;
    private String phoneNumber;
    private String avatarUrl;
    private int totalReading;
    private int totalPatients;
    private int testingPeriod;

    public UserVo() {
        this.userId = 0;
        this.firstName = "";
        this.lastName = "";
        this.aadhaar = "";
        this.institutatinType = "";
        this.institutationName = "";
        this.state = "";
        this.city = "";
        this.phoneNumber = "";
        this.avatarUrl = "";
        this.totalReading = 0;
        this.totalPatients = 0;
        this.testingPeriod = 0;

    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getAadhaar() {
        return aadhaar;
    }
    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getInstitutionType() {
        return institutatinType;
    }
    public void setInstitutatinType(String institutatinType) {
        this.institutatinType = institutatinType;
    }

    public String getInstitutationName() {
        return institutationName;
    }
    public void setInstitutationName(String institutationName) {
        this.institutationName = institutationName;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAvatarUrl(String avatarUrl){
        this.avatarUrl = avatarUrl;
    }
    public String getAvatarUrl(){
        return this.avatarUrl;
    }

    public void setTotalReading(int totalReading){
        this.totalReading = totalReading;
    }
    public int getTotalReading(){
        return this.totalReading;
    }

    public void setTotalPatients(int totalPatients){
        this.totalPatients = totalPatients;
    }
    public int getTotalPatients(){
        return this.totalPatients;
    }

    public void setTestingPeriod(int testingPeriod){
        this.testingPeriod = testingPeriod;
    }
    public int getTestingPeriod(){
        return this.testingPeriod;
    }
    @Override
    public String toString() {
        return "UserVo{" +
                "userId=" + userId +
                ", userName='" + firstName + '\'' +
                ", email='" + lastName + '\'' +
                ", aadhaar='" + aadhaar + '\'' +
                ", institutationType='" + institutatinType  + '\'' +
                ", institutationName='" + institutationName  + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", phoneNumber='" + phoneNumber  + '\'' +
                ", totalReading=" + totalReading +
                ", totalPatients=" + totalPatients +
                ", testingPeriod=" + testingPeriod +
                '}';
    }
}