package com.curonsys.army_android.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by ijin-yeong on 2018. 10. 19..
 */

public class DjangoClient {


    //private static final String BASE_URL = "http://marker-220510.appspot.com/";
    //private static final String BASE_URL = "http://10.0.2.2:8000/image_server/";

    private static final String BASE_URL = "http://image-matching.mybluemix.net/image_server/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        final int DEFAULT_TIMEOUT = 20 * 1000;
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void setTimeOut(){
        final int DEFAULT_TIMEOUT = 30 * 1000;
        client.setTimeout(DEFAULT_TIMEOUT);
    }

}
