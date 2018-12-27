package com.curonsys.army_android.model;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SkuDetailsModel implements Serializable {
    private String mSkuId;
    private String mTitle;
    private String mPrice;
    private String mDescription;
    private String mCurrencyCode;
    private @BillingClient.SkuType String mBillingType;

    public SkuDetailsModel() {
        mSkuId = "";
        mTitle = "";
        mPrice = "";
        mDescription = "";
        mCurrencyCode = "";
        mBillingType = BillingClient.SkuType.INAPP;
    }

    public SkuDetailsModel(SkuDetails details, @BillingClient.SkuType String billingType) {
        this.mSkuId = details.getSku();
        this.mTitle = details.getTitle();
        this.mPrice = details.getPrice();
        this.mDescription = details.getDescription();
        this.mCurrencyCode = details.getPriceCurrencyCode();
        this.mBillingType = billingType;
    }

    public String getSkuId() {
        return mSkuId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public @BillingClient.SkuType String getBillingType() {
        return mBillingType;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("sku_id", mSkuId);
        data.put("title", mTitle);
        data.put("price", mPrice);
        data.put("description", mDescription);
        data.put("currency_code", mCurrencyCode);
        data.put("billing_type", mBillingType);

        return data;
    }
}
