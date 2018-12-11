package com.curonsys.army_android.model;

import com.google.firebase.Timestamp;
import com.google.type.Date;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopModel {
    private String mShopId;
    private String mName;
    private String mDescription;
    private String mAddress;
    private String mPhone;
    private ArrayList<String> mImageUrls;
    private Timestamp mRegistrationDatetime;
    private Timestamp mUpdatedDatetime;

    public ShopModel() {
        mShopId = "";
        mName = "";
        mDescription = "";
        mAddress = "";
        mPhone = "";
        mImageUrls = new ArrayList<String>();
        mRegistrationDatetime = Timestamp.now();
        mUpdatedDatetime = Timestamp.now();

    }

    public ShopModel(Map<String, Object> data) {
        if (data.containsKey("shop_id")) {
            mShopId = (String) data.get("shop_id");
        } else {
            mShopId = "";
        }

        if (data.containsKey("name")) {
            mName = (String) data.get("name");
        } else {
            mName = "";
        }

        if (data.containsKey("description")) {
            mDescription = (String) data.get("description");
        } else {
            mDescription = "";
        }

        if (data.containsKey("address")) {
            mAddress = (String) data.get("address");
        } else {
            mAddress = "";
        }

        if (data.containsKey("phone")) {
            mPhone = (String) data.get("phone");
        } else {
            mPhone = "";
        }

        if (data.containsKey("image_url")) {
            mImageUrls = (ArrayList<String>) data.get("image_url");
        } else {
            mImageUrls = new ArrayList<String>();
        }

        if (data.containsKey("registration_datetime")) {
            mRegistrationDatetime = (Timestamp) data.get("registration_datetime");
        } else {
            mRegistrationDatetime = Timestamp.now();
        }

        if (data.containsKey("updated_datetime")) {
            mUpdatedDatetime = (Timestamp) data.get("updated_datetime");
        } else {
            mUpdatedDatetime = Timestamp.now();
        }
    }

    public void setShopId(String id) {
        mShopId = id;
    }

    public String getShopId() {
        return mShopId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getPhone() {
        return mPhone;
    }

    public ArrayList<String> getImageUrls() {
        return mImageUrls;
    }

    public Timestamp getRegisterDatetime() {
        return mRegistrationDatetime;
    }

    public void setUpdateDatetime(Timestamp datetime) {
        mUpdatedDatetime = datetime;
    }

    public Timestamp getUpdateDatetime() {
        return mUpdatedDatetime;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("shop_id", mShopId);
        data.put("name", mName);
        data.put("description", mDescription);
        data.put("address", mAddress);
        data.put("phone", mPhone);
        data.put("image_url", mImageUrls);
        data.put("registration_datetime", mRegistrationDatetime);
        data.put("updated_datetime", mUpdatedDatetime);

        return data;
    }
}
