package com.curonsys.army_android.util;

import android.app.Activity;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.curonsys.army_android.model.BusinessCardModel;
import com.curonsys.army_android.model.MarkerModel;
import com.curonsys.army_android.model.TransferModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ijin-yeong on 2018. 11. 19..
 */

public class MarkerUploader {
    private static final String TAG = "MarkerUploader";

    private Activity mContext;
    private SharedDataManager mSDManager;
    private RequestManager mRequestManager;

    private FirebaseAuth mAuth;

    MaterialDialog mMaterialDialog = null;
    MaterialDialog.Builder mBuilder = null;

    public MarkerUploader(Activity activity){
        mContext = activity;
        mRequestManager = RequestManager.getInstance();
        mSDManager = SharedDataManager.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
    public void start(){
        showDialog("잠시만 기다려주세요.\n마커를 등록중입니다...");
        uploadMarkerImage();
    }

    public void startCardUpload(){
        showDialog("잠시만 기다려주세요.\n마커를 등록중입니다...");
        uploadMarkerCard();
    }

    private void uploadMarkerCard(){
        try {
            String name = null;
            long size = 0;
            if (mSDManager.imageURI.toString().startsWith("file:")) {
                name = mSDManager.imageURI.getPath();
                int cut = name.lastIndexOf('/');
                if (cut != -1) {
                    name = name.substring(cut + 1);
                }
            }else{
                Log.d("uri",mSDManager.imageURI.toString());
                Cursor returnCursor = mContext.getContentResolver().query(mSDManager.imageURI,
                        null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                Log.d("nameIndex",nameIndex+"");
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                Log.d("sizeIndex",sizeIndex+"");
                returnCursor.moveToFirst();
                name = returnCursor.getString(nameIndex);
                size = returnCursor.getLong(sizeIndex);
            }
            String path = mSDManager.imageURI.getPath();
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
            String phoneNumber = mSDManager.phoneNumber;

            // upload marker
            mRequestManager.requestUploadCardMarkerToStorage(model, phoneNumber, mSDManager.imageURI, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel result) {
                    Map<String, Object> data = new HashMap<>();

                    data.put("marker_id", ""); // new marker
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userid = currentUser.getUid();
                    data.put("user_id", userid);
                    data.put("file", result.getPath());
                    data.put("rating", mSDManager.markerRating);
                    data.put("phone", mSDManager.phoneNumber);
                    data.put("content_id", mSDManager.contentId);
                    data.put("content_rotation", mSDManager.contentRotation);
                    data.put("content_scale", mSDManager.contentScale);

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
            if (mSDManager.imageURI.toString().startsWith("file:")) {
                name = mSDManager.imageURI.getPath();
                int cut = name.lastIndexOf('/');
                if (cut != -1) {
                    name = name.substring(cut + 1);
                }
            }else{
                Log.d("uri",mSDManager.imageURI.toString());
                Cursor returnCursor = mContext.getContentResolver().query(mSDManager.imageURI,
                        null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                Log.d("nameIndex",nameIndex+"");
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                Log.d("sizeIndex",sizeIndex+"");
                returnCursor.moveToFirst();
                name = returnCursor.getString(nameIndex);
                size = returnCursor.getLong(sizeIndex);
            }
            String path = mSDManager.imageURI.getPath();
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
            address.put("country_code", mSDManager.currentCountryCode);
            address.put("locality", mSDManager.currentLocality);
            address.put("thoroughfare", mSDManager.currentThoroughfare);

            // upload marker
            mRequestManager.requestUploadMarkerToStorage(model, mSDManager.imageURI, address, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel result) {
                    Map<String, Object> data = new HashMap<>();

                    data.put("marker_id", ""); // new marker
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userid = currentUser.getUid();
                    data.put("user_id", userid);
                    data.put("file", result.getPath());
                    data.put("rating", mSDManager.markerRating);
                    GeoPoint location = new GeoPoint(mSDManager.currentLatitude, mSDManager.currentLongtitude);
                    data.put("location", location);
                    data.put("content_id", mSDManager.contentId);
                    data.put("content_rotation", mSDManager.contentRotation);
                    data.put("content_scale", mSDManager.contentScale);
                    data.put("country_code", mSDManager.currentCountryCode);
                    data.put("locality", mSDManager.currentLocality);
                    data.put("thoroughfare", mSDManager.currentThoroughfare);

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
                mMaterialDialog.dismiss();
                Toast.makeText(mContext, "마커를 성공적으로 등록하였습니다.", Toast.LENGTH_LONG).show();
                mContext.finish();
            }
        });
    }

    private void updateCardDB(Map<String, Object> data){
        BusinessCardModel card = new BusinessCardModel(data);
        mRequestManager.requestSetCardInfo(card, new RequestManager.SuccessCallback() {
            @Override
            public void onResponse(boolean success) {
                mMaterialDialog.dismiss();
                Toast.makeText(mContext, "마커를 성공적으로 등록하였습니다.", Toast.LENGTH_LONG).show();
                mContext.finish();
            }
        });
    }

    public void showDialog(String msg){
        mBuilder = new MaterialDialog.Builder(mContext)
                .title("등록")
                .content(msg)
                .progress(true,0);
        mMaterialDialog = mBuilder.build();
        mMaterialDialog.show();
    }
}
