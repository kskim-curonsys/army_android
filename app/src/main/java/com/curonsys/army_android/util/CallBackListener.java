package com.curonsys.army_android.util;

/**
 * Created by ijin-yeong on 2018. 9. 6..
 */

public interface CallBackListener {
    void onSuccess(String message);
    void onSuccess(String message, boolean isMarker);
    void onDoneBack();
}
