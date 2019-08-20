package me.sensingself.sensingsugar.Model;

/**
 * Created by liujie on 1/26/18.
 */

public class TestingResult {
    private int patientId;
    private String testResultId;
    private String bloodSugarType;
    private long currentDate;
    private long mgdl;
    private float mmol;
    private String sample;
    private boolean isSelected;

    public TestingResult() {
        this.patientId = 0;
        this.testResultId = "";
        this.bloodSugarType = "";
        this.currentDate = 0;
        this.mgdl = 0;
        this.mmol = 0;
        this.sample = "";
        this.isSelected = false;
    }

    public int getPatientId() {
        return patientId;
    }
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String gettestResultId() {
        return testResultId;
    }
    public void settestResultId(String testResultId) {
        this.testResultId = testResultId;
    }

    public String getBloodSugarType() {
        return bloodSugarType;
    }
    public void setBloodSugarType(String bloodSugarType) {
        this.bloodSugarType = bloodSugarType;
    }

    public long getCurrentDate() {
        return currentDate;
    }
    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }

    public long getMgdl() {
        return mgdl;
    }
    public void setMgdl(long mgdl) {
        this.mgdl = mgdl;
    }

    public float getMmol() {
        return mmol;
    }
    public void setMmol(float mmol) {
        this.mmol = mmol;
    }

    public String getSample(){
        return this.sample;
    }
    public void setSample(String sample){
        this.sample = sample;
    }

    public boolean getSelected(){
        return this.isSelected;
    }
    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", testResultId=" + testResultId + '\'' +
                ", bloodSugarType='" + bloodSugarType + '\'' +
                ", currentDate='" + currentDate + '\'' +
                ", mgdl='" + mgdl + '\'' +
                ", mmol='" + mmol + '\'' +
                ", sample='" + sample + '\'' +
                '}';
    }
}
