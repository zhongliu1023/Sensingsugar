package me.sensingself.sensingsugar.Common.util;

import me.sensingself.sensingsugar.HandleToAws.Constants;

/**
 * Created by ashishshah on 13/05/17.
 */

public class URLConstant {
    //development URL
    public static final String BASE_URL = "http://dev-api.sensingself.me";

    //Production URL
    // public static final String BASE_URL = "";

    public static final String ACCESS_TOKEN = BASE_URL + "/authorize";
    public static final String LOGIN = BASE_URL + "/oauth/token";
    public static final String ACTION_START = BASE_URL + "/passwordless/start";
    public static final String VERIFY_CODE = BASE_URL + "/passwordless/verify";
    public static final String FETCH_PROFILE = BASE_URL + "/userinfo";
    public static final String SAVE_PROFILE = BASE_URL + "/userinfo";
    public static final String ADD_PATIENT = BASE_URL + "/api/users";
    public static final String FETCH_PATIENT = BASE_URL + "/api/users";
    public static final String SEARCH_PATIENTS = BASE_URL + "/api/users?q=name:";
    public static final String SEARCH_PATIENTS_BY_AADHAAR = BASE_URL + "/api/users?q=aadhaar:";
    public static final String SEARCH_PATIENTS_BY_PHONE = BASE_URL + "/api/users?q=phone:";

    public static final String POSTTESTINGRESULT = BASE_URL + "/api/testresults";
    public static final String GETPATIENTFORTESTINGRESULT = BASE_URL + "/api/testresults?q=patient:";
    public static final String GETPATIENTTESTINGRESULTBYAADHAAR = BASE_URL + "/api/testresults?q=aadhaar:";
    public static final String GETPATIENTFORTESTINGRESULTBASE = BASE_URL + "/api/testresults?q=";
    public static final String GETPATIENTS = BASE_URL + "/api/patients/";
    public static final String GETTTESTINGRESULTBASE = "/testresults?q=";
    public static final String GETTTESTINGRESULT = "/testresults?q=patient:";
    public static final String GETTTESTINGRESULTBYAADHAAR = "/testresults?q=aadhaar:";
    public static final String AVATARBASEURL = "http://" + Constants.BUCKET_NAME + ".s3.amazonaws.com/profile/";
    public static final String GETREPORTS = BASE_URL + "/api/reports";



    public static final String SMS_CON = "sms";
    public static final String EMAIL_CON = "email";
    public static final String SEND_CODE = "code";

    public static final String CLIENT_ID = "tTzHh3AQ6DENtuuw";
    public static final String CLIENT_SECRET = "LECd9xbAiXEQGZLpxaohzkmMN3TP692h";
}
