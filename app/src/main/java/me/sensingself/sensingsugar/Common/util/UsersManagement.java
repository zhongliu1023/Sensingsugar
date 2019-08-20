package me.sensingself.sensingsugar.Common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.UserVo;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by liujie on 1/26/18.
 */

public class UsersManagement {

    public static void saveCurrentTestingResult(TestingResult testingResult, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<TestingResult>() {
        }.getType();
        String json = gson.toJson(testingResult, type);
        sharedPreferencesManager.setCurrentPatient(SharedPreferencesKeys.CURRENTTESTINGRESULT, json);
    }
    public static void saveCurrentPatent(Patient initPatient, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<Patient>() {
        }.getType();
        String json = gson.toJson(initPatient, type);
        sharedPreferencesManager.setCurrentPatient(SharedPreferencesKeys.CURRENTPATIENT, json);
    }
    public static void saveCurrentUser(UserVo userVo, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<UserVo>() {
        }.getType();
        String json = gson.toJson(userVo, type);
        sharedPreferencesManager.setCurrentPatient(SharedPreferencesKeys.CURRENTUSER, json);
    }
    public static void addOrUpdateNewPatient(Patient newPatient, SharedPreferencesManager sharedPreferencesManager, String preferenceKey){
        String registeredPatientStr = sharedPreferencesManager.getRegisteredPatients(preferenceKey);
        ArrayList<Patient> patients = new ArrayList<Patient>();
        if (!registeredPatientStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Patient>>() {
            }.getType();
            patients = gson.fromJson(registeredPatientStr, type);
            if (newPatient.getPatientId() == 0){
                newPatient.setPatientId(patients.size()+1);
                patients.add(newPatient);
            }else{
                boolean isExist = false;
                for (int i = 0; i < patients.size(); i ++){
                    Patient onePatient = patients.get(i);
                    if (onePatient.getPatientId() == newPatient.getPatientId()){
                        isExist = true;
                        patients.remove(i);
                        patients.add(i, newPatient);
                    }
                }
                if (!isExist){
                    patients.add(newPatient);
                }
            }
        }else{
            if (newPatient.getPatientId() == 0){
                newPatient.setPatientId(patients.size()+1);
            }
            patients.add(newPatient);
        }
        saveCurrentPatent(newPatient, sharedPreferencesManager);
        savePatients(patients, sharedPreferencesManager, preferenceKey);
    }
    public static void savePatients(ArrayList<Patient> patients, SharedPreferencesManager sharedPreferencesManager, String preferenceKey){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Patient>>() {
        }.getType();
        String json = gson.toJson(patients, type);
        sharedPreferencesManager.setRegisteredPatients(preferenceKey, json);
    }

    public static void saveReports(ArrayList<Patient> patients, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Patient>>() {
        }.getType();
        String json = gson.toJson(patients, type);
        sharedPreferencesManager.setRegisteredPatients(SharedPreferencesKeys.SEARCHEDREPORTS, json);
    }

    public static void saveSearchedTestingResult(ArrayList<TestingResult> testingResults, SharedPreferencesManager sharedPreferencesManager, int patientId, String preferenceKey){
        String registeredPatientStr = sharedPreferencesManager.getRegisteredPatients(preferenceKey);
        ArrayList<Patient> patients = new ArrayList<Patient>();
        if (!registeredPatientStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Patient>>() {
            }.getType();
            patients = gson.fromJson(registeredPatientStr, type);
            boolean isExist = false;
            for (int i = 0; i < patients.size(); i ++){
                Patient onePatient = patients.get(i);
                if (onePatient.getPatientId() == patientId){
                    isExist = true;
                    onePatient.setTestingResults(testingResults);
                    break;
                }
            }
            savePatients(patients, sharedPreferencesManager, preferenceKey);
        }
    }

}
