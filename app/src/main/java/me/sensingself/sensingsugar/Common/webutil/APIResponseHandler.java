package me.sensingself.sensingsugar.Common.webutil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ashishshah on 25/04/17.
 */

public interface APIResponseHandler
{

    abstract void onSuccess(JSONObject responseJsonObject, int statusCode, String errorMessage);
    abstract void onSuccessJsonArray(JSONArray responseJsonObject, int statusCode, String errorMessage);
    void onFailure(int errorCode, String errorMessage);
}
