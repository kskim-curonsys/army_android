package com.curonsys.army_android.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.curonsys.army_android.activity.MarkerGenerationActivity;
import com.curonsys.army_android.util.RequestManager;
import com.curonsys.army_android.model.BusinessCardModel;
import com.curonsys.army_android.model.MarkerModel;
import com.curonsys.army_android.model.TransferModel;
import com.curonsys.army_android.service.FetchAddressIntentService;
import com.curonsys.army_android.service.GeofenceTransitionsIntentService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.curonsys.army_android.util.Constants.STORAGE_BASE_URL;

/**
 * Created by ijin-yeong on 2018. 11. 19..
 */

public class MarkerUploader {

    private static final String TAG_GEO = "Geofence";

    private Activity mContext;
    private SharedDataManager mDBManager;
    private LocationManager mLocationManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private RequestManager mRequestManager;
    private GeofencingClient mGeofencingClient;
    private List<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private PendingIntent mGeofencePendingIntent;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAnalytics mAnalytics;

    private AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());
    private MarkerAddressReceiver mAddressReceiver;
    private boolean mLocationUpdateState = true;
    protected Location mLastLocation = null;

    private String mOutput = "";

    private ArrayList<String> myDataset;
    MaterialDialog materialDialog = null;
    MaterialDialog.Builder builder = null;

    boolean for_upload = true;

    public MarkerUploader(Activity activity){
        this.mContext = activity;
        mAddressReceiver = new MarkerAddressReceiver(new Handler());

        this.mRequestManager = RequestManager.getInstance();
        this.mDBManager = SharedDataManager.getInstance();
        try{
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
            mGeofencingClient = LocationServices.getGeofencingClient(mContext);
            mAuth = FirebaseAuth.getInstance();
            mStorage = FirebaseStorage.getInstance(STORAGE_BASE_URL);
            mAnalytics = FirebaseAnalytics.getInstance(mContext);

            getLastLocation();
            myDataset = new ArrayList<String>();
            String str = "Location Tracking..";
            myDataset.add(str);
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.d("null","markerUploader function");
        }
    }
    public void start(boolean for_upload_state){
        this.for_upload = for_upload_state;
        startMarkerAddressIntentService();
        startFetchAddressIntentService();
    }

    public void startCardUpload(){
        showDialog("잠시만 기다려주세요.\n마커를 등록중입니다...");
        uploadMarkerCard();
    }


    private void uploadMarkerCard(){
        try {
            String name = null;
            long size = 0;
            if (mDBManager.imageURI.toString().startsWith("file:")) {
                name = mDBManager.imageURI.getPath();
                int cut = name.lastIndexOf('/');
                if (cut != -1) {
                    name = name.substring(cut + 1);
                }
            }else{
                Log.d("uri",mDBManager.imageURI.toString());
                Cursor returnCursor = mContext.getContentResolver().query(mDBManager.imageURI,
                        null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                Log.d("nameIndex",nameIndex+"");
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                Log.d("sizeIndex",sizeIndex+"");
                returnCursor.moveToFirst();
                name = returnCursor.getString(nameIndex);
                size = returnCursor.getLong(sizeIndex);
            }
            String path = mDBManager.imageURI.getPath();
            String suffix = name.substring(name.indexOf('.'), name.length());
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userid = currentUser.getUid();

            Map<String, Object> values = new HashMap<>();
            values.put("path", path);
            values.put("name", name);
            values.put("suffix", suffix);
            values.put("size", size);
            values.put("user_id", userid);
            TransferModel model = new TransferModel(values);
            ((MarkerGenerationActivity)mContext).getPhoneNumber();


            // upload marker
            mRequestManager.requestUploadCardMarkerToStorage(model,((MarkerGenerationActivity)mContext).getPhoneNumber(), mDBManager.imageURI, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel result) {
                    Map<String, Object> data = new HashMap<>();

                    data.put("marker_id", ""); // new marker
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userid = currentUser.getUid();
                    data.put("user_id", userid);
                    data.put("file", result.getPath());
                    data.put("rating", (float) mDBManager.markerRating);
                    data.put("phone", ((MarkerGenerationActivity)mContext).getPhoneNumber());
                    data.put("content_id", mDBManager.contentId);
                    data.put("content_rotation", mDBManager.contentRotation);
                    data.put("content_scale", mDBManager.contentScale);

                    // update marker database
                    updateCardDB(data);
                }
            });

        } catch (Exception e) {
            Log.e("TAKE_ALBUM getData failed. ", e.toString());
            e.printStackTrace();
        }
    }

    private void uploadMarkerImage() {
        try {
            String name = null;
            long size = 0;
            if (mDBManager.imageURI.toString().startsWith("file:")) {
                name = mDBManager.imageURI.getPath();
                int cut = name.lastIndexOf('/');
                if (cut != -1) {
                    name = name.substring(cut + 1);
                }
            }else{
                Log.d("uri",mDBManager.imageURI.toString());
                Cursor returnCursor = mContext.getContentResolver().query(mDBManager.imageURI,
                        null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                Log.d("nameIndex",nameIndex+"");
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                Log.d("sizeIndex",sizeIndex+"");
                returnCursor.moveToFirst();
                name = returnCursor.getString(nameIndex);
                size = returnCursor.getLong(sizeIndex);
            }
            String path = mDBManager.imageURI.getPath();
            String suffix = name.substring(name.indexOf('.'), name.length());
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userid = currentUser.getUid();

            Map<String, Object> values = new HashMap<>();
            values.put("path", path);
            values.put("name", name);
            values.put("suffix", suffix);
            values.put("size", size);
            values.put("user_id", userid);
            TransferModel model = new TransferModel(values);

            Map<String, Object> address = new HashMap<>();
            address.put("country_code", mDBManager.currentCountryCode);
            address.put("locality", mDBManager.currentLocality);
            address.put("thoroughfare", mDBManager.currentThoroughfare);

            // upload marker
            mRequestManager.requestUploadMarkerToStorage(model, mDBManager.imageURI, address, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel result) {
                    Map<String, Object> data = new HashMap<>();

                    data.put("marker_id", ""); // new marker
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userid = currentUser.getUid();
                    data.put("user_id", userid);
                    data.put("file", result.getPath());
                    data.put("rating", (float) mDBManager.markerRating);
                    GeoPoint location = new GeoPoint(mDBManager.currentLatitude, mDBManager.currentLongtitude);
                    data.put("location", location);
                    data.put("content_id", mDBManager.contentId);
                    data.put("content_rotation", mDBManager.contentRotation);
                    data.put("content_scale", mDBManager.contentScale);
                    data.put("country_code", mDBManager.currentCountryCode);
                    data.put("locality", mDBManager.currentLocality);
                    data.put("thoroughfare", mDBManager.currentThoroughfare);

                    // update marker database
                    updateMarkerDB(data);
                }
            });

        } catch (Exception e) {
            Log.e("TAKE_ALBUM getData failed. ", e.toString());
            e.printStackTrace();
        }
    }

    private void updateMarkerDB(Map<String, Object> data) {
        MarkerModel marker = new MarkerModel(data);
        mRequestManager.requestSetMarkerInfo(marker, new RequestManager.SuccessCallback() {
            @Override
            public void onResponse(boolean success) {
                materialDialog.dismiss();
                Toast.makeText(mContext,
                        "마커를 성공적으로 등록하였습니다.",
                        Toast.LENGTH_LONG).show();
                mContext.finish();
            }
        });
    }

    private void updateCardDB(Map<String, Object> data){
        BusinessCardModel card = new BusinessCardModel(data);
        mRequestManager.requestSetCardInfo(card, new RequestManager.SuccessCallback() {
            @Override
            public void onResponse(boolean success) {
                materialDialog.dismiss();
                Toast.makeText(mContext,
                        "마커를 성공적으로 등록하였습니다.",
                        Toast.LENGTH_LONG).show();
                mContext.finish();
            }
        });
    }

    private void getLastLocation() {
        try {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(mContext, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;

                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                float speed = location.getSpeed();

                                mOutput += "last lat : " + latitude + "\n" + "last lon : " + longitude + "\n" + "speed : " + speed + "m/s" + "\n\n";
                                //mTestResult.setText(mOutput);
                            }
                        }
                    });

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void startFetchAddressIntentService() {
        Intent intent = new Intent(mContext, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        mContext.startService(intent);
        Log.d("startFetchAddressIntentService","true");
    }

    protected void startMarkerAddressIntentService() {
        Intent intent = new Intent(mContext, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mAddressReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        mContext.startService(intent);
        Log.d("startMarkerAddressIntentService","true");
    }

    private void startMonitorGeofences() {
        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_SERVICE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }

        if (mGeofenceList.size() < 1) {
            Log.d(TAG_GEO, "startMonitorGeofences: geofence list is empty!");
            return;
        }

        try {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(mContext, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG_GEO, "onSuccess: add geofences success");
                        }
                    })
                    .addOnFailureListener(mContext, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG_GEO, "onFailure: " + e.getMessage());
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(mContext, 0,
                intent, PendingIntent. FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    class MarkerAddressReceiver extends ResultReceiver {
        public MarkerAddressReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            if (resultCode == Constants.SUCCESS_RESULT) {
                mDBManager.currentCountryCode = resultData.getString(Constants.RESULT_COUNTRY_KEY);
                mDBManager.currentLocality = resultData.getString(Constants.RESULT_LOCALITY_KEY);
                mDBManager.currentThoroughfare = resultData.getString(Constants.RESULT_THOROUGHFARE_KEY);

                // upload marker image to storage
                if(for_upload){
                    showDialog("잠시만 기다려주세요.\n마커를 등록중입니다...");
                    uploadMarkerImage();
                }

            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            //mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            String info = resultData.getString(Constants.RESULT_DATA_KEY);
            if (info != null) {
                info += "\n";
                myDataset.add(info);
                //mAdapter.notifyItemInserted(myDataset.size() - 1);
            }

            /*
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            */
            if (resultCode == Constants.SUCCESS_RESULT) {
                //updateUI();
            }
        }
    }

    public void showDialog(String msg){
        builder = new MaterialDialog.Builder(mContext)
                .title("등록")
                .content(msg)
                .progress(true,0);
        materialDialog = builder.build();
        materialDialog.show();


    }


}
