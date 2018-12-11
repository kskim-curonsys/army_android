package com.curonsys.army_android.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private String mUserId;
    private String mName;
    private String mEmail;
    private boolean mNotification;
    private String mImageUrl;
    private ArrayList<String> mContents;
    private ArrayList<String> mPurchases;
    private ArrayList<GeoPoint> mLocations;

    public UserModel() {
        mUserId = "";
        mName = "";
        mEmail = "";
        mNotification = true;
        mImageUrl = "";
        mContents = new ArrayList<String>();
        mPurchases = new ArrayList<String>();
        mLocations = new ArrayList<GeoPoint>();
    }

    public UserModel(Map<String, Object> data) {
        if (data.containsKey("user_id")) {
            mUserId = (String) data.get("user_id");
        } else {
            mUserId = "";
        }

        if (data.containsKey("name")) {
            mName = (String) data.get("name");
        } else {
            mName = "";
        }

        if (data.containsKey("email")) {
            mEmail = (String) data.get("email");
        } else {
            mEmail = "";
        }

        if (data.containsKey("notification")) {
            mNotification = (boolean) data.get("notification");
        } else {
            mNotification = true;
        }

        if (data.containsKey("image_url")) {
            mImageUrl = (String) data.get("image_url");
        } else {
            mImageUrl = "";
        }

        if (data.containsKey("contents")) {
            mContents = (ArrayList<String>) data.get("contents");
        } else {
            mContents = new ArrayList<String>();
        }

        if (data.containsKey("purchase")) {
            mPurchases = (ArrayList<String>) data.get("purchase");
        } else {
            mPurchases = new ArrayList<String>();
        }

        if (data.containsKey("locations")) {
            mLocations = (ArrayList<GeoPoint>) data.get("locations");
        } else {
            mLocations = new ArrayList<GeoPoint>();
        }
    }

    public void setUserId(String userid) {
        mUserId = userid;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public boolean getNotification() {
        return mNotification;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public ArrayList<String> getContents() {
        return mContents;
    }

    public ArrayList<String> getPurchases() {
        return mPurchases;
    }

    public ArrayList<GeoPoint> getLocations() {
        return mLocations;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("user_id", mUserId);
        data.put("name", mName);
        data.put("email", mEmail);
        data.put("notification", mNotification);
        data.put("image_url", mImageUrl);
        data.put("contents", mContents);
        data.put("purchase", mPurchases);
        data.put("locations", mLocations);

        return data;
    }
}
