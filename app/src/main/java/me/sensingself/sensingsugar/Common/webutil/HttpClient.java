package me.sensingself.sensingsugar.Common.webutil;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import me.sensingself.sensingsugar.Common.util.JsonKeys;
import me.sensingself.sensingsugar.Common.util.JsonUtil;
import me.sensingself.sensingsugar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;


public class HttpClient {
    public static final AsyncHttpClient httpClient;
    public static final int CONNECTION_TIMEOUT = 600000;
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM_WWW = "application/x-www-form-urlencoded";

    static {
        httpClient = new AsyncHttpClient(true, 80, 443);
        httpClient.setTimeout(CONNECTION_TIMEOUT);
        try {
            if (!TextUtils.isEmpty(System.getProperty("http.proxyHost")) && !TextUtils.isEmpty(System.getProperty("http.proxyPort"))) {
                httpClient.setProxy(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort")));
            }
        } catch (Exception e) {

        }
    }


    public static void post(final Context context, String url, HttpEntity httpEntity,
                            final APIResponseHandler responseHandler) {

        httpClient.post(context, url, httpEntity, CONTENT_TYPE_JSON, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                switch (statusCode) {
                    case 200:    //OK
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 201:    //Created
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 202:   //Accept
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                responseHandler.onFailure(statusCode, "");
            }
        });
    }

    public static void post(final Context context, String url, RequestParams requestParams,
                            final APIResponseHandler responseHandler) {

        httpClient.post(context, url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                switch (statusCode) {
                    case 200:    //OK
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 201:    //Created
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 202:   //Accept
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                responseHandler.onFailure(statusCode, "");
            }
        });
    }


    public static void post(final Context context, String url, RequestParams requestParams,
                            final APIResponseHandler responseHandler,String headers) {
        httpClient.addHeader("Authorization",headers);
        httpClient.post(context, url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                switch (statusCode) {
                    case 200:    //OK
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 201:    //Created
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 202:   //Accept
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                if (responseBody == null){
                    responseHandler.onFailure(statusCode, "");
                }else{
                    try {
                        JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                        String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                        responseHandler.onFailure(statusCode, errorMessage);
                    } catch (JSONException e) {
                        responseHandler.onFailure(statusCode, context.getResources().getString(R.string.error_in_json));
                    }
                }
            }
        });
    }
    public static void getJsonArray(final Context context, String url, RequestParams requestParams,
                           final APIResponseHandler responseHandler,String headers) {
        httpClient.addHeader("Authorization",headers);
        httpClient.get(context, url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                switch (statusCode) {
                    case 200:    //OK
                        try {
                            JSONArray responseJsonObject = new JSONArray(new String(responseBody));
                            responseHandler.onSuccessJsonArray(responseJsonObject, statusCode, "");
                        } catch (JSONException e) {
                            responseHandler.onSuccessJsonArray(new JSONArray(), statusCode, "");
                        }
                        break;
                    case 201:    //Created
                        try {
                            JSONArray responseJsonObject = new JSONArray(new String(responseBody));
                            responseHandler.onSuccessJsonArray(responseJsonObject, statusCode, "");
                        } catch (JSONException e) {
                            responseHandler.onSuccessJsonArray(new JSONArray(), statusCode, "");
                        }
                        break;
                    case 202:   //Accept
                        try {
                            JSONArray responseJsonObject = new JSONArray(new String(responseBody));
                            responseHandler.onSuccessJsonArray(responseJsonObject, statusCode, "");
                        } catch (JSONException e) {
                            responseHandler.onSuccessJsonArray(new JSONArray(), statusCode, "");
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                    String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                    responseHandler.onFailure(statusCode, errorMessage);
                } catch (JSONException e) {
                    responseHandler.onFailure(statusCode, context.getResources().getString(R.string.error_in_json));
                }
            }
        });
    }

    public static void get(final Context context, String url, RequestParams requestParams,
                            final APIResponseHandler responseHandler,String headers) {
        httpClient.addHeader("Authorization",headers);
        httpClient.get(context, url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                switch (statusCode) {
                    case 200:    //OK
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 201:    //Created
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 202:   //Accept
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody == null){
                    responseHandler.onFailure(statusCode, "");
                }else{
                    try {
                        JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                        String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                        responseHandler.onFailure(statusCode, errorMessage);
                    } catch (JSONException e) {
                        responseHandler.onFailure(statusCode, context.getResources().getString(R.string.error_in_json));
                    }
                }
            }
        });
    }




    public static void post(final Context context, String url, HttpEntity httpEntity,
                            final APIResponseHandler responseHandler, String headers) {

        httpClient.addHeader("Content-Type", CONTENT_TYPE_FORM_WWW);
        httpClient.post(context, url, httpEntity, CONTENT_TYPE_FORM_WWW, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                switch (statusCode) {
                    case 200:    //OK
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 201:    //Created
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 202:   //Accept
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                    String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                    responseHandler.onFailure(statusCode, errorMessage);
                } catch (JSONException e) {
                    responseHandler.onFailure(statusCode, context.getResources().getString(R.string.error_in_json));
                }
            }
        });
    }


    public static void get(final Context context, String url, HttpEntity httpEntity,
                           final APIResponseHandler responseHandler) {

        httpClient.get(context, url, httpEntity, CONTENT_TYPE_JSON, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                switch (statusCode) {
                    case 200:    //OK
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 201:    //Created
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    case 202:   //Accept
                        try {
                            JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                            String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                            responseHandler.onSuccess(responseJsonObject, statusCode, errorMessage);
                        } catch (JSONException e) {
                            responseHandler.onSuccess(new JSONObject(), statusCode, "");
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    JSONObject responseJsonObject = new JSONObject(new String(responseBody));
                    String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                    responseHandler.onFailure(statusCode, errorMessage);
                } catch (JSONException e) {
                    responseHandler.onFailure(statusCode, context.getResources().getString(R.string.error_in_json));
                }
            }
        });
    }

    /*public static void get(final Context context, String url, HttpClient httpClient, final APIResponseHandler apiResponseHandler) {
        httpClient.get(context, url, httpClient, new APIResponseHandler() {
            @Override
            public void onSuccess(JSONObject responseJsonObject) {
                try {
                    String errorMessage = JsonUtil.getString(responseJsonObject, JsonKeys.ERROR_CODE);
                    if (TextUtils.isEmpty(errorMessage))
                        apiResponseHandler.onSuccess(responseJsonObject);
                    else
                        apiResponseHandler.onFailure(200, errorMessage);
                } catch (JSONException e) {
                    apiResponseHandler.onFailure(200, context.getResources().getString(R.string.error_in_json));
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMessageCode) {
                apiResponseHandler.onFailure(200, context.getResources().getString(R.string.error_in_json));
            }
        });
    }*/

}
