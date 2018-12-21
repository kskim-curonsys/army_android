package com.curonsys.army_android.util;

public class PurchaseManager {

    private static PurchaseManager mInstance;
    public static PurchaseManager getInstance() {
        if (mInstance == null) {
            mInstance = new PurchaseManager();
        }
        return mInstance;
    }

    public PurchaseManager() {
    }

}
