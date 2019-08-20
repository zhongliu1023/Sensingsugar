package me.sensingself.sensingsugar.webutil.mapper;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.JsonKeys;
import me.sensingself.sensingsugar.Common.util.JsonUtil;
import me.sensingself.sensingsugar.Common.util.URLConstant;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.UserVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by ashishshah on 13/05/17.
 */

public class AccountMapper {
    public RequestParams mapAccessTokenRequest(String generatedCode, long currentTime) {
        RequestParams params = new RequestParams();
        params.put(JsonKeys.CLIENT_ID, URLConstant.CLIENT_ID);
        params.put(JsonKeys.CODE, generatedCode);
        //params.put(JsonKeys.TIMESTAMP, currentTime);
        params.put(JsonKeys.TIMESTAMP, currentTime);
        return params;
    }
    public String mapAccessTokenResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        SharedPreferencesManager.getInstance(context).setPrefernceValueString(SharedPreferencesKeys.ACCESS_TOKEN,
                (String) jsonObjectResponse.get(JsonKeys.ACCESS_TOKEN));
        return JsonUtil.getString(jsonObjectResponse, JsonKeys.ACCESS_TOKEN);
    }
    public RequestParams mapLogin(String mobileNumber) {
        RequestParams params = new RequestParams();
        params.put(JsonKeys.CLIENT_ID, URLConstant.CLIENT_ID);
        params.put(JsonKeys.GRANTTYPE, URLConstant.SMS_CON);
        params.put(JsonKeys.PHONENUMBER, mobileNumber);
        return params;
    }
    public RequestParams mapStartPhoneNumRequest(String mobileNumber) {
        RequestParams params = new RequestParams();
        params.put(JsonKeys.CLIENT_ID, URLConstant.CLIENT_ID);
        params.put(JsonKeys.CONNECTION, URLConstant.SMS_CON);
        params.put(JsonKeys.PHONENUMBER, mobileNumber);
        params.put(JsonKeys.EMAIL, "dev-sensingself@culabs.org");
        params.put(JsonKeys.SEND, URLConstant.SEND_CODE);
        return params;
    }
    public String mapStartPhoneNumResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        JSONObject jsonAPI = JsonUtil.getJsonObject(jsonObjectResponse, JsonKeys.API);
        return JsonUtil.getString(jsonAPI, JsonKeys.ERROR_STRING);
    }


    public RequestParams mapVerificationCodeRequest(String mobileNumber, String verificationCode) {
        RequestParams params = new RequestParams();
        params.put(JsonKeys.CLIENT_ID, URLConstant.CLIENT_ID);
        params.put(JsonKeys.CONNECTION, URLConstant.SMS_CON);
        params.put(JsonKeys.PHONENUMBER, mobileNumber);
        params.put(JsonKeys.EMAIL, "dev-sensingself@culabs.org");
        params.put(JsonKeys.VERIFICATION_CODE, verificationCode);
        return params;
    }

    public String mapVerifyCodeResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        JSONObject jsonResult = JsonUtil.getJsonObject(jsonObjectResponse, JsonKeys.RESULT);
        SharedPreferencesManager.getInstance(context).setPrefernceValueString(SharedPreferencesKeys.ID_TOKEN,
                (String) jsonObjectResponse.get(JsonKeys.ID_TOKEN));
        return JsonUtil.getString(jsonObjectResponse, JsonKeys.SESSION);
    }


    public UserVo mapFetchProfileResponse(Context context, JSONObject responseJsonObject) throws JSONException {
        UserVo uservo = new UserVo();
        uservo.setUserId(JsonUtil.getInt(responseJsonObject, JsonKeys.USER_ID));
        uservo.setFirstName(JsonUtil.getString(responseJsonObject, JsonKeys.FIRST_NAME));
        uservo.setLastName(JsonUtil.getString(responseJsonObject, JsonKeys.LAST_NAME));
        uservo.setAadhaar(JsonUtil.getString(responseJsonObject, JsonKeys.AADHAAR));
        uservo.setInstitutatinType(JsonUtil.getString(responseJsonObject, JsonKeys.INSTITUTION_TYPE));
        uservo.setInstitutationName(JsonUtil.getString(responseJsonObject, JsonKeys.INSTITUTION_NAME));
        uservo.setState(JsonUtil.getString(responseJsonObject, JsonKeys.STATE));
        uservo.setCity(JsonUtil.getString(responseJsonObject, JsonKeys.CITY));
        uservo.setPhoneNumber(JsonUtil.getString(responseJsonObject, JsonKeys.PHONENUMBER));
        uservo.setAvatarUrl(JsonUtil.getString(responseJsonObject, JsonKeys.AVATAR));

        JSONObject statusJsonObjective = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.STATS);
        uservo.setTotalReading(JsonUtil.getInt(statusJsonObjective, JsonKeys.TOTALREADING));
        uservo.setTotalPatients(JsonUtil.getInt(statusJsonObjective, JsonKeys.TOTALPATIENTS));
        uservo.setTestingPeriod(JsonUtil.getInt(statusJsonObjective, JsonKeys.TESTINGPERIOD));
        return uservo;
    }

    public RequestParams mapSaveProfileRequest(UserVo userVo) {
        RequestParams params = new RequestParams();
        params.setForceMultipartEntityContentType(true);
        params.put(JsonKeys.FIRST_NAME, userVo.getFirstName());
        params.put(JsonKeys.LAST_NAME, userVo.getLastName());
        params.put(JsonKeys.INSTITUTION_TYPE, userVo.getInstitutionType());
        params.put(JsonKeys.INSTITUTION_NAME, userVo.getInstitutationName());
        params.put(JsonKeys.AVATAR, userVo.getAvatarUrl());
        params.put(JsonKeys.STATE, userVo.getState());
        params.put(JsonKeys.CITY, userVo.getCity());
        params.put(JsonKeys.PHONENUMBER, userVo.getPhoneNumber());
        params.put(JsonKeys.AADHAAR, userVo.getAadhaar());
        return params;
    }
    public String mapSaveProfileResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        return JsonUtil.getString(jsonObjectResponse, JsonKeys.ERROR_STRING);
    }

    public Patient mapFetchPatientResponse(Context context, JSONObject responseJsonObject) throws JSONException {
        Patient patient = new Patient();
        patient.setPatientId(JsonUtil.getInt(responseJsonObject, JsonKeys.USER_ID));
        patient.setFirstName(JsonUtil.getString(responseJsonObject, JsonKeys.FIRST_NAME));
        patient.setLastName(JsonUtil.getString(responseJsonObject, JsonKeys.LAST_NAME));
        patient.setAadhaar(JsonUtil.getString(responseJsonObject, JsonKeys.AADHAAR));
        patient.setHeight(JsonUtil.getLong(responseJsonObject, JsonKeys.HEIGHT));
        patient.setWeight(JsonUtil.getLong(responseJsonObject, JsonKeys.WEIGHT));
        if (JsonUtil.getString(responseJsonObject, JsonKeys.GENDER).equals("M")){
            patient.setGender("Male");
        }else{
            patient.setGender("Female");
        }
        patient.setBirthday(JsonUtil.getString(responseJsonObject, JsonKeys.BIRTHDATE));
        patient.setPhoneNumber(JsonUtil.getString(responseJsonObject, JsonKeys.PHONENUMBER));
        return patient;
    }

    public RequestParams mapSavePatientRequest(Patient patient) {
        RequestParams params = new RequestParams();
        params.setForceMultipartEntityContentType(false);
        params.put(JsonKeys.FIRST_NAME, patient.getFirstName());
        params.put(JsonKeys.LAST_NAME, patient.getLastName());
        if (patient.getGender().equals("Male")){
            params.put(JsonKeys.GENDER, "M");
        }else{
            params.put(JsonKeys.GENDER, "F");
        }
        params.put(JsonKeys.HEIGHT, patient.getHeight());
        params.put(JsonKeys.WEIGHT, patient.getWeight());
        params.put(JsonKeys.BIRTHDATE, patient.getBirthday());
        params.put(JsonKeys.AADHAAR, patient.getAadhaar());
        params.put(JsonKeys.PHONENUMBER, patient.getPhoneNumber());
        params.put(JsonKeys.CITY, patient.getDistrict());
        params.put(JsonKeys.STATE, patient.getState());
        return params;
    }
    public String mapSavePatientResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        JSONObject jsonAPI = JsonUtil.getJsonObject(jsonObjectResponse, JsonKeys.API);
        return JsonUtil.getString(jsonObjectResponse, JsonKeys.ERROR_STRING);
    }
    public String mapSaveTestingResultResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        JSONObject jsonAPI = JsonUtil.getJsonObject(jsonObjectResponse, JsonKeys.API);
        return JsonUtil.getString(jsonObjectResponse, JsonKeys.ERROR_STRING);
    }
    public ArrayList<Patient> mapSearchUsersResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        JSONArray jsonArray = JsonUtil.getJsonArray(jsonObjectResponse, JsonKeys.GETPATIENTS);
        ArrayList<Patient> patients = new ArrayList<Patient>();
        for (int i = 0; i < jsonArray.length(); i ++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Patient patient = new Patient();
            patient.setPatientId(JsonUtil.getInt(jsonObject, JsonKeys.PATIENT_ID));
            patient.setFirstName(JsonUtil.getString(jsonObject, JsonKeys.FIRST_NAME));
            patient.setLastName(JsonUtil.getString(jsonObject, JsonKeys.LAST_NAME));
            patient.setAadhaar(JsonUtil.getString(jsonObject, JsonKeys.AADHAAR));
            patient.setHeight(JsonUtil.getLong(jsonObject, JsonKeys.HEIGHT));
            patient.setWeight(JsonUtil.getLong(jsonObject, JsonKeys.WEIGHT));
            if (JsonUtil.getString(jsonObject, JsonKeys.GENDER).equals("M")){
                patient.setGender("Male");
            }else{
                patient.setGender("Female");
            }
            patient.setBirthday(JsonUtil.getString(jsonObject, JsonKeys.BIRTHDATE));
            patient.setPhoneNumber(JsonUtil.getString(jsonObject, JsonKeys.PHONENUMBER));
            patient.setTestdate(JsonUtil.getLong(jsonObject, JsonKeys.TESTDATE));
            patient.setDistrict(JsonUtil.getString(jsonObject, JsonKeys.CITY));
            patient.setState(JsonUtil.getString(jsonObject, JsonKeys.STATE));
            patients.add(patient);
        }
        return patients;
    }
    public ArrayList<TestingResult>  mapSearchTestingResultsResponse(Context context, int patientID, JSONObject jsonObjectResponse) throws JSONException {
        JSONArray jsonArray = JsonUtil.getJsonArray(jsonObjectResponse, JsonKeys.GETTESTINGRESULTS);
        ArrayList<TestingResult> testingResults = new ArrayList<TestingResult>();
        for (int i = 0; i < jsonArray.length(); i ++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            TestingResult testingResult = new TestingResult();
            testingResult.setPatientId(patientID);
            testingResult.settestResultId(JsonUtil.getString(jsonObject, JsonKeys.RESULTID));
            testingResult.setSample(JsonUtil.getString(jsonObject, JsonKeys.SAMPLE));
            testingResult.setBloodSugarType(JsonUtil.getString(jsonObject, JsonKeys.TESTTYPE));
            testingResult.setMgdl(JsonUtil.getLong(jsonObject, JsonKeys.MGDL));
            testingResult.setMmol(JsonUtil.getLong(jsonObject, JsonKeys.MMOL));
            testingResult.setCurrentDate(JsonUtil.getLong(jsonObject, JsonKeys.TESTINGTIME));
            testingResults.add(testingResult);
        }
        return testingResults;
    }
    public ArrayList<Patient>  mapSearchPatientsResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        JSONArray jsonArray = JsonUtil.getJsonArray(jsonObjectResponse, JsonKeys.GETPATIENTS);
        ArrayList<Patient> patients = new ArrayList<Patient>();
        for (int i = 0; i < jsonArray.length(); i ++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Patient patient = new Patient();
            patient.setPatientId(JsonUtil.getInt(jsonObject, JsonKeys.PATIENT_ID));
            patient.setFirstName(JsonUtil.getString(jsonObject, JsonKeys.FIRST_NAME));
            patient.setLastName(JsonUtil.getString(jsonObject, JsonKeys.LAST_NAME));
            patient.setAadhaar(JsonUtil.getString(jsonObject, JsonKeys.AADHAAR));
            patient.setHeight(JsonUtil.getLong(jsonObject, JsonKeys.HEIGHT));
            patient.setWeight(JsonUtil.getLong(jsonObject, JsonKeys.WEIGHT));
            if (JsonUtil.getString(jsonObject, JsonKeys.GENDER).equals("M")){
                patient.setGender("Male");
            }else{
                patient.setGender("Female");
            }
            patient.setBirthday(JsonUtil.getString(jsonObject, JsonKeys.BIRTHDATE));
            patient.setPhoneNumber(JsonUtil.getString(jsonObject, JsonKeys.PHONENUMBER));
            patient.setTestdate(JsonUtil.getLong(jsonObject, JsonKeys.TESTDATE));
            patient.setDistrict(JsonUtil.getString(jsonObject, JsonKeys.CITY));
            patient.setState(JsonUtil.getString(jsonObject, JsonKeys.STATE));
            patients.add(patient);
        }
        return patients;
    }
    public JSONObject mapFetchTestingResult(TestingResult testingResult) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonKeys.RESULTID, testingResult.gettestResultId());
        jsonObject.put(JsonKeys.SAMPLE, testingResult.getSample());
        jsonObject.put(JsonKeys.TESTTYPE, testingResult.getBloodSugarType());
        jsonObject.put(JsonKeys.MGDL, testingResult.getMgdl());
        jsonObject.put(JsonKeys.MMOL, testingResult.getMmol());
        jsonObject.put(JsonKeys.TESTINGTIME, testingResult.getCurrentDate());
        return jsonObject;
    }
    public ArrayList<Patient>  mapGetReportsResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        JSONArray jsonArray = JsonUtil.getJsonArray(jsonObjectResponse, JsonKeys.GETREPORTS);
        ArrayList<Patient> patients = new ArrayList<Patient>();
        for (int i = 0; i < jsonArray.length(); i ++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Patient patient = new Patient();
            patient.setPatientId(JsonUtil.getInt(jsonObject, JsonKeys.PATIENT_ID));
            patient.setFirstName(JsonUtil.getString(jsonObject, JsonKeys.FIRST_NAME));
            patient.setLastName(JsonUtil.getString(jsonObject, JsonKeys.LAST_NAME));
            patient.setTestdate(JsonUtil.getLong(jsonObject, JsonKeys.TESTDATE));

            JSONObject jsonObjectTestingResults = JsonUtil.getJsonObject(jsonObject, JsonKeys.RESULT);

            TestingResult testingResult = new TestingResult();
            testingResult.setPatientId(JsonUtil.getInt(jsonObject, JsonKeys.PATIENT_ID));
            testingResult.settestResultId(JsonUtil.getString(jsonObjectTestingResults, JsonKeys.RESULTID));
            testingResult.setSample(JsonUtil.getString(jsonObjectTestingResults, JsonKeys.SAMPLE));
            testingResult.setBloodSugarType(JsonUtil.getString(jsonObjectTestingResults, JsonKeys.TESTTYPE));
            testingResult.setMgdl(JsonUtil.getLong(jsonObjectTestingResults, JsonKeys.MGDL));
            testingResult.setMmol(JsonUtil.getLong(jsonObjectTestingResults, JsonKeys.MMOL));
            testingResult.setCurrentDate(JsonUtil.getLong(jsonObjectTestingResults, JsonKeys.TESTINGTIME));
            ArrayList<TestingResult> testingResults = new ArrayList<TestingResult>();
            testingResults.add(testingResult);
            patient.setTestingResults(testingResults);
            patients.add(patient);
        }
        return patients;
    }

}
