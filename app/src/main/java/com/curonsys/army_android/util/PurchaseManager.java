package com.curonsys.army_android.util;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.curonsys.army_android.billing.BillingManager;
import com.curonsys.army_android.model.SkuDetailsModel;

import java.util.ArrayList;
import java.util.List;

public class PurchaseManager {
    private static final String TAG = "PurchaseManager";

    private static final String SKU_ID_GAS = "gas";
    private static final String SKU_ID_PREMIUM = "premium";
    private static final String SKU_ID_GOLD_MONTHLY = "gold_monthly";
    private static final String SKU_ID_GOLD_YEARLY = "gold_yearly";
    private static final String SKU_ID_3DMODEL_SNAKE = "model_3d_snake_20181122";
    private static final String SKU_ID_3DMODEL_BIGBEN = "model_3d_bigben_20181122";
    private static final String SKU_ID_3DMODEL_BOX = "model_3d_box_20181219";
    private static final String SKU_ID_VIDEO_LAKE = "model_2d_lake_20181122";
    private static final String SKU_ID_3DMODEL_HELICOPTER = "model_3d_helicopter_20181122";
    private static final String SKU_ID_3DMODEL_CAR = "model_3d_car_20181122";
    private static final String SKU_ID_IMAGE_KITTEN = "model_2d_kitten_20181122";

    private BillingManager mBillingManager;
    private UpdateListener mUpdateListener;

    private static PurchaseManager mInstance;
    public static PurchaseManager getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new PurchaseManager(activity);
        }
        return mInstance;
    }

    public interface SkuDetailsListCallback {
        public void onResponse(ArrayList<SkuDetailsModel> response);
    }

    public PurchaseManager(Activity activity) {
        mUpdateListener = new UpdateListener();
        mBillingManager = new BillingManager(activity, mUpdateListener);
    }

    private class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            //mActivity.onBillingManagerSetupFinished();
        }

        @Override
        public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {
            Log.d(TAG, "Consumption finished. Purchase token: " + token + ", result: " + result);

            if (result == BillingClient.BillingResponse.OK) {
                Log.d(TAG, "Consumption successful. Provisioning.");
                //mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
                //saveData();
                //mActivity.alert(R.string.alert_fill_gas, mTank);
            } else {
                //mActivity.alert(R.string.alert_error_consuming, result);
            }

            //mActivity.showRefreshedUi();
            Log.d(TAG, "End consumption flow.");
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            for (Purchase purchase : purchaseList) {
                String sku_id = purchase.getSku();
                String signature = purchase.getSignature();
                String purchase_token = purchase.getPurchaseToken();
                long purchase_time = purchase.getPurchaseTime();
                String package_name = purchase.getPackageName();
                String order_id = purchase.getOrderId();

                switch (purchase.getSku()) {
                    case SKU_ID_PREMIUM:
                        Log.d(TAG, "You are Premium! Congratulations!!!");
                        //mIsPremium = true;
                        break;
                    case SKU_ID_GAS:
                        Log.d(TAG, "We have gas. Consuming it.");
                        //mActivity.getBillingManager().consumeAsync(purchase.getPurchaseToken());
                        break;
                    case SKU_ID_GOLD_MONTHLY:
                        //mGoldMonthly = true;
                        break;
                    case SKU_ID_GOLD_YEARLY:
                        //mGoldYearly = true;
                        break;
                }
            }

            //mActivity.showRefreshedUi();
        }
    }

    public void requestSkuDetailsList(final @BillingClient.SkuType String billingType, final SkuDetailsListCallback callback) {
        // test
        ArrayList<String> skusList = new ArrayList<String>();
        skusList.add(SKU_ID_3DMODEL_SNAKE);
        skusList.add(SKU_ID_3DMODEL_BIGBEN);
        skusList.add(SKU_ID_3DMODEL_BOX);
        skusList.add(SKU_ID_VIDEO_LAKE);
        skusList.add(SKU_ID_3DMODEL_HELICOPTER);
        skusList.add(SKU_ID_3DMODEL_CAR);
        skusList.add(SKU_ID_IMAGE_KITTEN);

        mBillingManager.querySkuDetailsAsync(billingType, skusList, new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        ArrayList<SkuDetailsModel> inList = new ArrayList<SkuDetailsModel>();
                        if (responseCode != BillingClient.BillingResponse.OK) {
                            Log.w(TAG, "Unsuccessful query for type: " + billingType + ". Error code: " + responseCode);
                        } else if (skuDetailsList != null && skuDetailsList.size() > 0) {
                            for (SkuDetails details : skuDetailsList) {
                                Log.i(TAG, "Adding sku: " + details);
                                inList.add(new SkuDetailsModel(details, billingType));
                            }
                            if (inList.size() == 0) {
                                //displayAnErrorIfNeeded();
                            } else {
                                callback.onResponse(inList);
                            }
                        } else {
                            // Handle empty state
                            //displayAnErrorIfNeeded();
                        }
                    }
                });
    }

    public void purchaseItem(String skuid, String skutype) {
        mBillingManager.initiatePurchaseFlow(skuid, skutype);
    }
}
