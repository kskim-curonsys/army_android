package com.curonsys.army_android.model;

import com.google.firebase.Timestamp;
import com.google.type.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductModel {
    private String mProductId;
    private String mShopId;
    private String mName;
    private String mDescription;
    private ArrayList<String> mSizes;
    private ArrayList<Number> mPrices;
    private ArrayList<String> mImageUrls;
    private Timestamp mRegistrationDatetime;
    private Timestamp mUpdatedDatetime;

    public ProductModel() {
        mProductId = "";
        mShopId = "";
        mName = "";
        mDescription = "";
        mSizes = new ArrayList<String>();
        mPrices = new ArrayList<Number>();
        mImageUrls = new ArrayList<String>();
        mRegistrationDatetime = Timestamp.now();
        mUpdatedDatetime = Timestamp.now();

    }

    public ProductModel(Map<String, Object> data) {
        if (data.containsKey("product_id")) {
            mProductId = (String) data.get("product_id");
        } else {
            mProductId = "";
        }

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

        if (data.containsKey("size")) {
            mSizes = (ArrayList<String>) data.get("size");
        } else {
            mSizes = new ArrayList<String>();
        }

        if (data.containsKey("price")) {
            mPrices = (ArrayList<Number>) data.get("phone");
        } else {
            mPrices = new ArrayList<Number>();
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

    public void setProductId(String id) {
        mProductId = id;
    }

    public String getProductId() {
        return mProductId;
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

    public ArrayList<String> getSizes() {
        return mSizes;
    }

    public ArrayList<Number> getPrices() {
        return mPrices;
    }

    public ArrayList<String> getImageUrls() {
        return mImageUrls;
    }

    public Timestamp getRegistrationDatetime() {
        return mRegistrationDatetime;
    }

    public void setUpdatedDatetime(Timestamp datetime) {
        mUpdatedDatetime = datetime;
    }

    public Timestamp getUpdatedDatetime() {
        return mUpdatedDatetime;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("product_id", mProductId);
        data.put("shop_id", mShopId);
        data.put("name", mName);
        data.put("description", mDescription);
        data.put("sizes", mSizes);
        data.put("prices", mPrices);
        data.put("image_url", mImageUrls);
        data.put("registration_datetime", mRegistrationDatetime);
        data.put("updated_datetime", mUpdatedDatetime);

        return data;
    }
}
