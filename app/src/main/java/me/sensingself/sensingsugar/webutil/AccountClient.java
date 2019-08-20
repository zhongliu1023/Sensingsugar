package me.sensingself.sensingsugar.webutil;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.RequestParams;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.GmtUtil;
import me.sensingself.sensingsugar.Common.util.JsonKeys;
import me.sensingself.sensingsugar.Common.util.JsonUtil;
import me.sensingself.sensingsugar.Common.util.URLConstant;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Common.webutil.APIResponseHandler;
import me.sensingself.sensingsugar.Common.webutil.HttpClient;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.webutil.mapper.AccountMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.entity.InputStreamEntity;

/**
 * Created by ashishshah on 13/05/17.
 */

public class AccountClient {
    public void accessToken(final Context context, SharedPreferencesManager sharedPreferences, String generatedCode, long currentTime, final APICallbacks apiCallbacks) {
        HttpClient.post(context, URLConstant.ACCESS_TOKEN, new AccountMapper().mapAccessTokenRequest(generatedCode, currentTime), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                try {
                    apiCallbacks.onSuccess(new AccountMapper().mapAccessTokenResponse(context, responseJsonObject), statusCode, errorMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, null);
            }
        });
    }

    public void loginWihtPhone(final Context context, SharedPreferencesManager sharedPreferences, String MobileNumber, final APICallbacks apiCallbacks) {
        String headers = JsonKeys.ACCESS_TOKEN +  " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ACCESS_TOKEN);
        HttpClient.post(context, URLConstant.LOGIN, new AccountMapper().mapLogin(MobileNumber), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                try {
                    JSONObject jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                    int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                    String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                    apiCallbacks.onSuccess(new AccountMapper().mapStartPhoneNumResponse(context, responseJsonObject), statusCode, errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, errorMessage);
            }
        }, headers);
    }
    public void startPhoneNumber(final Context context, SharedPreferencesManager sharedPreferences, String MobileNumber, final APICallbacks apiCallbacks) {
        String headers = JsonKeys.ACCESS_TOKEN +  " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ACCESS_TOKEN);
        HttpClient.post(context, URLConstant.ACTION_START, new AccountMapper().mapStartPhoneNumRequest(MobileNumber), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                try {
                    JSONObject jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                    int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                    String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                    apiCallbacks.onSuccess(new AccountMapper().mapStartPhoneNumResponse(context, responseJsonObject), statusCode, errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, errorMessage);
            }
        }, headers);
    }

    public void verifyCode(final Context context, SharedPreferencesManager sharedPreferences, String MobileNumber, String code, final APICallbacks apiCallbacks) {
        String headers = JsonKeys.ACCESS_TOKEN + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ACCESS_TOKEN);
        HttpClient.post(context, URLConstant.VERIFY_CODE, new AccountMapper().mapVerificationCodeRequest(MobileNumber, code), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                JSONObject jsonObjectApi = null;
                try {
                    jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                    int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                    String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                    apiCallbacks.onSuccess(new AccountMapper().mapVerifyCodeResponse(context, responseJsonObject), statusCode, errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, errorMessage);
            }
        }, headers);
    }
    public void fetchProfile(final Context context, SharedPreferencesManager sharedPreferences,  final APICallbacks apiCallbacks) {
        String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
        HttpClient.get(context, URLConstant.FETCH_PROFILE, new RequestParams(null, null), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {

                JSONObject jsonObjectApi = null;
                try {
                    jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                    int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                    String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                    apiCallbacks.onSuccess(new AccountMapper().mapFetchProfileResponse(context, responseJsonObject), statusCode, errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, errorMessage);
            }
        }, headers);
    }

    public void saveProfile(final Context context, SharedPreferencesManager sharedPreferences, UserVo userVo, final APICallbacks apiCallbacks) {
        String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
        HttpClient.post(context, URLConstant.SAVE_PROFILE, new AccountMapper().mapSaveProfileRequest(userVo), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                JSONObject jsonObjectApi = null;
                try {
                    jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                    int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                    String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                    apiCallbacks.onSuccess(responseJsonObject, statusCode, errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, null);
            }
        },headers);
    }
    public void fetchPatient(final Context context, SharedPreferencesManager sharedPreferences,  final APICallbacks apiCallbacks) {
        String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
        HttpClient.get(context, URLConstant.FETCH_PATIENT, new RequestParams(null, null), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {

                JSONObject jsonObjectApi = null;
                try {
                    jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                    int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                    String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                    apiCallbacks.onSuccess(new AccountMapper().mapFetchPatientResponse(context, responseJsonObject), statusCode, errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, errorMessage);
            }
        }, headers);
    }

    public void createPatient(final Context context, SharedPreferencesManager sharedPreferences, Patient patient, final APICallbacks apiCallbacks){
        String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
        HttpClient.post(context, URLConstant.ADD_PATIENT, new AccountMapper().mapSavePatientRequest(patient), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                JSONObject jsonObjectApi = null;
                try {
                    jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                    int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                    String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                    apiCallbacks.onSuccess(new AccountMapper().mapFetchPatientResponse(context, responseJsonObject), statusCode, errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, null);
            }
        },headers);
    }
    public void updatePatient(final Context context, SharedPreferencesManager sharedPreferences, Patient patient, final APICallbacks apiCallbacks) {
        String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
        HttpClient.post(context, URLConstant.FETCH_PATIENT + "/" + patient.getPatientId(), new AccountMapper().mapSavePatientRequest(patient), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                JSONObject jsonObjectApi = null;
                try {
                    jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                    int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                    String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                    apiCallbacks.onSuccess(new AccountMapper().mapSavePatientResponse(context, responseJsonObject), statusCode, errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {
                Log.w("", "");
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, null);
            }
        },headers);
    }
    public void searchPatients(final Context context, SharedPreferencesManager sharedPreferences, String searchKey, int mode,  final APICallbacks apiCallbacks) {
        String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
        String url = "";
        if (mode == 1){
            url = URLConstant.SEARCH_PATIENTS_BY_AADHAAR + "" + searchKey;
        }else{
            url = URLConstant.SEARCH_PATIENTS_BY_PHONE + "" + searchKey;
        }
        HttpClient.get(context, url, new RequestParams(null, null), new APIResponseHandler() {
            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                JSONObject jsonObjectApi = null;
                try {
                    apiCallbacks.onSuccess(new AccountMapper().mapSearchUsersResponse(context, responseJsonObject), statusCode, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, errorMessage);
            }
        }, headers);
    }
    public void saveTestingResult(final Context context, SharedPreferencesManager sharedPreferences, TestingResult testingResult, final APICallbacks apiCallbacks) {
        try {
            JSONObject jsonObject = new AccountMapper().mapFetchTestingResult(testingResult);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            InputStream stream = new ByteArrayInputStream(jsonArray.toString().getBytes(StandardCharsets.UTF_8.name()));
            InputStreamEntity reqEntity = new InputStreamEntity(stream);

            String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
            HttpClient.post(context, URLConstant.POSTTESTINGRESULT, reqEntity, new APIResponseHandler() {
                @Override
                public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                    JSONObject jsonObjectApi = null;
                    try {
                        jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                        int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                        String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                        apiCallbacks.onSuccess(new AccountMapper().mapSaveTestingResultResponse(context, responseJsonObject), statusCode, errorMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                    apiCallbacks.onFailure(errorCode, errorMessage, null);
                }
            },headers);
        }catch (IOException e){

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void searchPatientsForTestingResult(final Context context, SharedPreferencesManager sharedPreferences, String key, int type,
                                               String fromDate, String toDate, final APICallbacks apiCallbacks) {
        String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
        String url = "";
        if (!key.equals("")){
            if (type == 1){
                url = URLConstant.GETPATIENTFORTESTINGRESULT + "" + key;
            }else{
                url = URLConstant.GETPATIENTTESTINGRESULTBYAADHAAR + "" + key;
            }
        }else{
            long fromTimeStamp = DateUtils.dayString2Long(GmtUtil.localToGmt1(fromDate))/1000;
            long toTimeStamp = DateUtils.dayString2Long(GmtUtil.localToGmt1(toDate))/1000 + 86400;
            url = URLConstant.GETPATIENTFORTESTINGRESULTBASE + "from:" + fromTimeStamp + ",to:" + toTimeStamp;
        }
        HttpClient.get(context, url, new RequestParams(null, null), new APIResponseHandler() {
            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {
            }
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                JSONObject jsonObjectApi = null;
                try {
                    apiCallbacks.onSuccess(new AccountMapper().mapSearchPatientsResponse(context, responseJsonObject), statusCode, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                    apiCallbacks.onFailure(statusCode, errorMessage, errorMessage);
                }
            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, errorMessage);
            }
        }, headers);
    }
    public void searchTestingResult(final Context context, SharedPreferencesManager sharedPreferences, final int patientID,
                                    int type, String key, String fromDate, String toDate, final APICallbacks apiCallbacks) {
        String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
        String url = "";
        if (!key.equals("")){
            if (type == 1){
                url = URLConstant.GETPATIENTS + "" + patientID + URLConstant.GETTTESTINGRESULT + key;
            }else{
                url = URLConstant.GETPATIENTS + "" + patientID + URLConstant.GETTTESTINGRESULTBYAADHAAR + key;
            }
        }else{
            long fromTimeStamp = DateUtils.dayString2Long(GmtUtil.localToGmt1(fromDate))/1000;
            long toTimeStamp = DateUtils.dayString2Long(GmtUtil.localToGmt1(toDate))/1000 + 86400; // E.g, 2018-2-1 23:59:59 no 2018-2-1 00:00:00 if toDate is 2018-2-1
            url = URLConstant.GETPATIENTS + "" + patientID + URLConstant.GETTTESTINGRESULTBASE + "from:" + fromTimeStamp + ",to:" + toTimeStamp;
        }
        HttpClient.get(context, url, new RequestParams(null, null), new APIResponseHandler() {
            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {
            }
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {
                JSONObject jsonObjectApi = null;
                try {
                    apiCallbacks.onSuccess(new AccountMapper().mapSearchTestingResultsResponse(context, patientID, responseJsonObject), statusCode, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                    apiCallbacks.onFailure(statusCode, errorMessage, errorMessage);
                }
            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, errorMessage);
            }
        }, headers);
    }
    public void getReports(final Context context, SharedPreferencesManager sharedPreferences,  final APICallbacks apiCallbacks) {
        String headers = JsonKeys.BEARER + " " + sharedPreferences.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
        HttpClient.get(context, URLConstant.GETREPORTS, new RequestParams(null, null), new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage) {

                JSONObject jsonObjectApi = null;
                try {
                    jsonObjectApi = JsonUtil.getJsonObject(responseJsonObject, JsonKeys.API);
                    int errorCode = JsonUtil.getInt(jsonObjectApi, JsonKeys.ERROR_CODE);
                    String errorMsg = JsonUtil.getString(jsonObjectApi, JsonKeys.ERROR_STRING);
                    apiCallbacks.onSuccess(new AccountMapper().mapGetReportsResponse(context, responseJsonObject), statusCode, errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage) {

            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                apiCallbacks.onFailure(errorCode, errorMessage, errorMessage);
            }
        }, headers);
    }

}



