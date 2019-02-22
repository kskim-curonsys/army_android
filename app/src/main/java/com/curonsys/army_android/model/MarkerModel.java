package com.curonsys.army_android.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MarkerModel {
    private String mMarkerId;
    private String mUserId;
    private String mFile;
    private Double mRating;
    private GeoPoint mGeoPoint;
    private String mCountryCode;
    private String mLocality;
    private String mThoroughfare;
    private String mContentId;
    private ArrayList<Float> mContentRotation;
    private Double mContentScale;
    private ArrayList<String> mAdditionalMediaId;

    public MarkerModel() {
        mMarkerId = "";
        mUserId = "";
        mFile = "";
        mRating = 0.0;
        mGeoPoint = new GeoPoint(0, 0);
        mCountryCode = "";
        mLocality = "";
        mThoroughfare = "";
        mContentId = "";
        mContentRotation = new ArrayList<Float>();
        mContentScale = 0.0;
        mAdditionalMediaId = new ArrayList<String>();
    }

    public MarkerModel(Map<String, Object> data) {
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
            mRating = (Double) data.get("rating");
        } else {
            mRating = 0.0;
        }

        if (data.containsKey("location")) {
            mGeoPoint = (GeoPoint) data.get("location");
        } else {
            mGeoPoint = new GeoPoint(0, 0);
        }

        if (data.containsKey("country_code")) {
            mCountryCode = (String) data.get("country_code");
        } else {
            mCountryCode = "";
        }

        if (data.containsKey("locality")) {
            mLocality = (String) data.get("locality");
        } else {
            mLocality = "";
        }

        if (data.containsKey("thoroughfare")) {
            mThoroughfare = (String) data.get("thoroughfare");
        } else {
            mThoroughfare = "";
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
            mContentScale = (Double) data.get("content_scale");
        } else {
            mContentScale = 0.0;
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

    public Double getRating() {
        return mRating;
    }

    public double getLatitude() {
        return mGeoPoint.getLatitude();
    }

    public double getLongitude() {
        return mGeoPoint.getLongitude();
    }

    public String getContentId() {
        return mContentId;
    }

    public ArrayList<Float> getContentRotation() {
        return mContentRotation;
    }

    public Double getScale() {
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
        data.put("location", mGeoPoint);
        data.put("country_code", mCountryCode);
        data.put("locality", mLocality);
        data.put("thoroughfare", mThoroughfare);
        data.put("content_id", mContentId);
        data.put("content_rotation", mContentRotation);
        data.put("content_scale", mContentScale);
        data.put("additional_media_id", mAdditionalMediaId);

        return data;
    }
}
