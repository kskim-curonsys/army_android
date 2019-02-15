package com.curonsys.army_android.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BusinessCardModel {
    private String mMarkerId;
    private String mUserId;
    private String mFile;
    private float mRating;
    private String phoneNumber;
    private String mContentId;
    private ArrayList<Float> mContentRotation;
    private float mContentScale;
    private ArrayList<String> mAdditionalMediaId;

    public BusinessCardModel() {
        mMarkerId = "";
        mUserId = "";
        mFile = "";
        mRating = 0;
        phoneNumber = "";
        mContentId = "";
        mContentRotation = new ArrayList<Float>();
        mContentScale = 0;
        mAdditionalMediaId = new ArrayList<String>();
    }

    public BusinessCardModel(Map<String, Object> data) {
        if (data.containsKey("marker_id")) {
            mMarkerId = (String) data.get("marker_id");
        } else {
            mMarkerId = "";
        }

        if (data.containsKey("user_id")) {
            mUserId = (String) data.get("user_id");
        } else {
            mUserId = "";
        }

        if (data.containsKey("file")) {
            mFile = (String) data.get("file");
        } else {
            mFile = "";
        }

        if (data.containsKey("rating")) {
            mRating = (float)Float.parseFloat(String.valueOf(data.get("rating")));
        } else {
            mRating = 0;
        }

        if (data.containsKey("phone")) {
            phoneNumber = (String) data.get("phone");
        } else {
            phoneNumber = "";
        }

        if (data.containsKey("content_id")) {
            mContentId = (String) data.get("content_id");
        } else {
            mContentId = "";
        }

        if (data.containsKey("content_rotation")) {
            mContentRotation = (ArrayList<Float>) data.get("content_rotation");
        } else {
            mContentRotation = new ArrayList<Float>();
        }

        if (data.containsKey("content_scale")) {
            mContentScale = Float.parseFloat(String.valueOf(data.get("content_scale")));
        } else {
            mContentScale = 0;
        }

        if (data.containsKey("additional_media_id")) {
            mAdditionalMediaId = (ArrayList<String>) data.get("additional_media_id");
        } else {
            mAdditionalMediaId = new ArrayList<String>();
        }
    }

    public void setMarkerId(String id) {
        mMarkerId = id;
    }

    public String getMarkerId() {
        return mMarkerId;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getFile() {
        return mFile;
    }

    public float getRating() {
        return mRating;
    }


    public String getContentId() {
        return mContentId;
    }

    public ArrayList<Float> getContentRotation() {
        return mContentRotation;
    }

    public float getScale() {
        return mContentScale;
    }

    public ArrayList<String> getMediaId() {
        return mAdditionalMediaId;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("marker_id", mMarkerId);
        data.put("user_id", mUserId);
        data.put("file", mFile);
        data.put("rating", mRating);
        data.put("phone", phoneNumber);
        data.put("content_id", mContentId);
        data.put("content_rotation", mContentRotation);
        data.put("content_scale", mContentScale);
        data.put("additional_media_id", mAdditionalMediaId);

        return data;
    }
}
