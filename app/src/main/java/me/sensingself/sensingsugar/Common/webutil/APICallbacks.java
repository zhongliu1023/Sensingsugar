package me.sensingself.sensingsugar.Common.webutil;

/**
 * Created by ashishshah on 13/05/17.
 */

public interface APICallbacks {

    <E> void onSuccess(E responseObject, int statusCode, String errorMessage);

    <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage);

    <E> void onFailure(int errorCode, String errorMessage, E failureDetails);
}
