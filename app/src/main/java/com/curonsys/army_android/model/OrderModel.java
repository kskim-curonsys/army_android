package com.curonsys.army_android.model;

import com.google.firebase.Timestamp;
import com.google.type.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderModel {
    private String mOrderId;
    private String mShopId;
    private String mUserId;
    private String mStatus;
    private ArrayList<String> mItems;
    private ArrayList<Number> mPrices;
    private ArrayList<Number> mQuantities;
    private Number mTotalPrice;
    private Timestamp mOrderDatetime;
    private Timestamp mPaymentDatetime;

    public OrderModel() {
        mOrderId = "";
        mShopId = "";
        mUserId = "";
        mStatus = "";
        mItems = new ArrayList<String>();
        mPrices = new ArrayList<Number>();
        mQuantities = new ArrayList<Number>();
        mTotalPrice = 0;
        mOrderDatetime = Timestamp.now();
        mPaymentDatetime = Timestamp.now();

    }

    public OrderModel(Map<String, Object> data) {
        if (data.containsKey("order_id")) {
            mOrderId = (String) data.get("order_id");
        } else {
            mOrderId = "";
        }

        if (data.containsKey("shop_id")) {
            mShopId = (String) data.get("shop_id");
        } else {
            mShopId = "";
        }

        if (data.containsKey("user_id")) {
            mUserId = (String) data.get("user_id");
        } else {
            mUserId = "";
        }

        if (data.containsKey("status")) {
            mStatus = (String) data.get("status");
        } else {
            mStatus = "";
        }

        if (data.containsKey("items")) {
            mItems = (ArrayList<String>) data.get("items");
        } else {
            mItems = new ArrayList<String>();
        }

        if (data.containsKey("prices")) {
            mPrices = (ArrayList<Number>) data.get("prices");
        } else {
            mPrices = new ArrayList<Number>();
        }

        if (data.containsKey("quantities")) {
            mQuantities = (ArrayList<Number>) data.get("quantities");
        } else {
            mQuantities = new ArrayList<Number>();
        }

        if (data.containsKey("order_datetime")) {
            mOrderDatetime = (Timestamp) data.get("order_datetime");
        } else {
            mOrderDatetime = Timestamp.now();
        }

        if (data.containsKey("payment_datetime")) {
            mPaymentDatetime = (Timestamp) data.get("payment_datetime");
        } else {
            mPaymentDatetime = Timestamp.now();
        }
    }

    public void setOrderId(String id) {
        mOrderId = id;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public String getShopId() {
        return mShopId;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getStatus() {
        return mStatus;
    }

    public ArrayList<String> getItems() {
        return mItems;
    }

    public ArrayList<Number> getPrices() {
        return mPrices;
    }

    public ArrayList<Number> getQuantities() {
        return mQuantities;
    }

    public Number getTotalPrice() {
        return mTotalPrice;
    }

    public Timestamp getOrderDatetime() {
        return mOrderDatetime;
    }

    public void setPaymentDatetime(Timestamp datetime) {
        mPaymentDatetime = datetime;
    }

    public Timestamp getPaymentDatetime() {
        return mPaymentDatetime;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("order_id", mOrderId);
        data.put("shop_id", mShopId);
        data.put("user_id", mUserId);
        data.put("status", mStatus);
        data.put("items", mItems);
        data.put("prices", mPrices);
        data.put("quantities", mQuantities);
        data.put("order_datetime", mOrderDatetime);
        data.put("payment_datetime", mPaymentDatetime);

        return data;
    }

}
