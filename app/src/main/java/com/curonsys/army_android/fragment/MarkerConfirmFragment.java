package com.curonsys.army_android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.curonsys.army_android.util.CallBackListener;
import com.curonsys.army_android.activity.MarkerTestActivity;
import com.curonsys.army_android.R;
import com.curonsys.army_android.util.RequestManager;
import com.curonsys.army_android.model.ContentModel;
import com.curonsys.army_android.model.TransferModel;
import com.curonsys.army_android.util.SharedDataManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by ijin-yeong on 2018. 5. 21..
 * 마커 등록 최종 확인 엑티비티
 */

public class MarkerConfirmFragment extends Fragment {
    Activity mContext;
    ContentModel mContentModel;
    ContentModel mTestContentModel;
    ArrayList<String> mTextures = new ArrayList<String>();
    String mModelUrl;

    CallBackListener mCallBackListener;

    MaterialDialog.Builder mBuilder = null;
    MaterialDialog mMaterialDialog = null;
    TextView mUserText, mRatingText, mLatitudeText, mLongitudeText, mScaleText, mTextRotateX, mTextRotateY, mTextRotateZ;
    ImageView mMarkerPreview;
    SharedDataManager mSDManager = SharedDataManager.getInstance();
    int mTextureCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marker_confirm, container, false);
        FragmentManager fragmentManager = this.getChildFragmentManager();

        //tv = view.findViewById(R.id.markerInfoTv);
        mUserText = view.findViewById(R.id.user_id_text_confirm);
        mRatingText = view.findViewById(R.id.marker_rating_text_confirm);
        mLatitudeText = view.findViewById(R.id.marker_latitude_text_confirm);
        mLongitudeText = view.findViewById(R.id.marker_longitude_text_confirm);
        mMarkerPreview = view.findViewById(R.id.marker_preview_confirm);
        mScaleText = view.findViewById(R.id.marker_scale_confirm);
        mTextRotateX = view.findViewById(R.id.marker_rotatex_confirm);
        mTextRotateY = view.findViewById(R.id.marker_rotatey_confirm);
        mTextRotateZ = view.findViewById(R.id.marker_rotatez_confirm);

        mUserText.setText(mSDManager.generatorId);
        mRatingText.setText(mRatingText.getText() + String.valueOf(mSDManager.markerRating));
        mLatitudeText.setText(mLatitudeText.getText() + String.valueOf(mSDManager.currentLatitude));
        mLongitudeText.setText(mLongitudeText.getText() + String.valueOf(mSDManager.currentLongtitude));
//      Bitmap bitmap = BitmapFactory.decodeFile(mSDManager.imageURI.toString());

        mMarkerPreview.setBackground(getResources().getDrawable(R.drawable.round_fg));
        mMarkerPreview.setClipToOutline(true);
//      mMarkerPreview.setImageBitmap(bitmap);
        mMarkerPreview.setImageURI(mSDManager.imageURI);

//        tv.setText(mSDManager.generatorId+",\n"
//                        +mSDManager.markerRating+",\n"
//                        +mSDManager.imageURI.toString()+",\n"
//                        +mSDManager.currentLongtitude+",\n"
//                        +mSDManager.currentLatitude+",\n"
//                        +mSDManager.contentId+",\n"
//                        +mSDManager.contentName+",\n");
        Button btn = view.findViewById(R.id.markerTestBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MarkerTestActivity.class);
                intent.putExtra("info", mTestContentModel);
                startActivityForResult(intent, 0);
            }
        });

        getContentsModel();
        showDialog("컨텐츠를 확인중입니다...");

        mCallBackListener = new CallBackListener() {
            @Override
            public void onSuccess(String message) {
                switch (message) {
                    case "contentsModel":
                        mMaterialDialog.dismiss();
                        Log.e("getContentsModel :", "sucess");
                        showDialog("모델 파일을 가져오는 중입니다...");
                        getModelFromStorage(mSDManager.is3D);

                        break;
                    case "model":
                        mMaterialDialog.dismiss();
                        showDialog("텍스쳐를 가져오는 중입니다...");

                        if (mSDManager.is3D) {
                            Log.e("getJet :", "sucess");
                            getTextures();
                        } else {
                            Log.e("is3d", "false");
                            mCallBackListener.onSuccess("textures");
                        }

                        break;
                    case "textures":
                        mMaterialDialog.dismiss();
                        Log.e("getTextures :", "sucess");
                        Toast.makeText(mContext, "컨텐츠를 정상적으로 가져왔습니다", Toast.LENGTH_SHORT).show();
                        setContentsModel();
                        break;
                }

            }

            @Override
            public void onSuccess(String message, boolean isMarker) {

            }

            @Override
            public void onDoneBack() {
                mCallBackListener = (CallBackListener) getActivity();
                mCallBackListener.onDoneBack();
            }
        };
        return view;
    }

    private void setContentsModel() {
        if (mSDManager.is3D) {
            mTestContentModel = new ContentModel();
            mTestContentModel.setTextures(mTextures);
            mTestContentModel.setModel(mModelUrl);
        } else {
            mTestContentModel = new ContentModel();
            mTestContentModel.setModel(mModelUrl);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mContext = activity;
        //step 4

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "okok");
        switch (resultCode) {
            case 1:
                Log.d("resultInput", "ok");
                String scale = data.getStringExtra("scale");
                String rotateX = data.getStringExtra("rotateX");
                String rotateY = data.getStringExtra("rotateY");
                String rotateZ = data.getStringExtra("rotateZ");

                mScaleText.setText(mScaleText.getText() + scale);
                mTextRotateX.setText(mTextRotateX.getText() + rotateX);
                mTextRotateY.setText(mTextRotateY.getText() + rotateY);
                mTextRotateZ.setText(mTextRotateZ.getText() + rotateZ);
                mSDManager.contentScale = Float.parseFloat(scale);

                ArrayList<Float> rotates = new ArrayList<>();
                rotates.add(Float.parseFloat(rotateX));
                rotates.add(Float.parseFloat(rotateY));
                rotates.add(Float.parseFloat(rotateZ));
                mSDManager.contentRotation = rotates;

                mCallBackListener.onDoneBack();
                break;
        }
    }

    public String saveTemptoJet(FileInputStream fis, String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath(); // Get Absolute Path in External Sdcard
        String foler_name = "/kudan/" + folder + "/";
        String file_name = name + ".jet";
        String string_path = ex_storage + foler_name;
        File file_path;
        try {
            file_path = new File(string_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }

            BufferedInputStream bis = new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream(string_path + file_name);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int data = 0;

//            final byte[] buffer = new byte[1024];
            while ((data = bis.read()) != -1) {
                bos.write(data);
            }

//            BufferedInputStream bIS = new BufferedInputStream(in);
            bis.close();
            fis.close();
            bos.close();
            fos.close();

            Log.d("model_path", string_path + file_name);

        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
        return string_path + file_name;
    }

    public String saveBitmaptoJpeg(Bitmap bitmap, String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath(); // Get Absolute Path in External Sdcard
        String foler_name = "/kudan/" + folder + "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage + foler_name;
        File file_path;
        try {
            file_path = new File(string_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path + file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            //Log.d("mTextures_path",string_path+file_name);
        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
        return string_path + file_name;
    }


    public String saveTemptoMp4(FileInputStream fis, String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath(); // Get Absolute Path in External Sdcard
        String foler_name = "/kudan/" + folder + "/";
        String file_name = name + ".mp4";
        String string_path = ex_storage + foler_name;
        File file_path;
        try {
            file_path = new File(string_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }

            BufferedInputStream bis = new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream(string_path + file_name);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int data = 0;

            while ((data = bis.read()) != -1) {
                bos.write(data);
            }

            bis.close();
            fis.close();
            bos.close();
            fos.close();

            Log.d("model_path", string_path + file_name);

        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
        return string_path + file_name;
    }


    public void getContentsModel() {
        final String contentId = mSDManager.contentId;
        RequestManager requestManager = RequestManager.getInstance();
        requestManager.requestGetContentInfo(contentId, new RequestManager.ContentCallback() {
            @Override
            public void onResponse(ContentModel response) {
                mContentModel = response;
                mCallBackListener.onSuccess("contentsModel");
            }
        });
    }


    public void getTextures() {
        try {
            Log.d("markertest", mContentModel.toString());

            for (int i = 0; i < mContentModel.getTextures().size(); i++) {
                Log.d("texture real name", mContentModel.getTextures().get(i) + "");
                String texture_url = mContentModel.getTextures().get(i);
                getTexture(mContentModel.getContentId(), texture_url, i, mContentModel.getTextures().size());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void getTexture(final String name, final String url, final int request_count, final int last_count) {
        try {
            String suffix = url.substring(url.indexOf('.'), url.length());
            Log.d("texture_request_suffix", suffix);
            Log.d("texture_request_url", url);
            RequestManager mRequestManager = RequestManager.getInstance();
            mRequestManager.requestDownloadFileFromStorage(name, url, suffix, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel response) {
                    if (response.getSuffix().compareTo(".jpg") == 0 || response.getSuffix().compareTo(".png") == 0) {
                        Bitmap downBitmap = BitmapFactory.decodeFile(response.getPath());
                        //imgView.setImageBitmap(downBitmap);

                        //String texture_file_name=response.getPath().substring(response.getPath().lastIndexOf("/")+1,response.getPath().length()-4);
                        String texture_file_name = url.substring(url.lastIndexOf("/") + 1, url.length() - 4);
                        Log.d("getTexture_name", texture_file_name);
                        mTextures.add(saveBitmaptoJpeg(downBitmap, name, texture_file_name));
                    }
                    Log.d(TAG, "onResponse: content download complete ");
                    String texture_url = response.getPath();
                    //Log.d("texture_path",texture_url);
                    //mTextures.add(texture_url);
                    //setup();

                    //very important
                    mTextureCount++;
                    if (mTextureCount == last_count) {
                        mCallBackListener.onSuccess("textures");
                    }
                }
            });
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void getModelFromStorage(boolean is3D) {
        if (is3D == true) {
            String url = mContentModel.getModel();
            String suffix = url.substring(url.indexOf('.'), url.length());
            RequestManager mRequestManager = RequestManager.getInstance();
            mRequestManager.requestDownloadFileFromStorage(mContentModel.getContentId(), url, suffix, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel response) {
                    if (response.getSuffix().compareTo(".jet") == 0) {
                        String model_file_name = mContentModel.getContentId();
                        Log.d("getModel_name", model_file_name);
                        try {
                            FileInputStream file_readed = new FileInputStream(new File(response.getPath()));
                            mModelUrl = saveTemptoJet(file_readed, mContentModel.getContentId(), model_file_name);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    mCallBackListener.onSuccess("model");
                }
            });
        } else {
            final String url = mContentModel.getModel();
            String suffix = url.substring(url.indexOf('.'), url.length());
            RequestManager mRequestManager = RequestManager.getInstance();
            mRequestManager.requestDownloadFileFromStorage(mContentModel.getContentId(), url, suffix, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel response) {
                    if (response.getSuffix().compareTo(".jpg") == 0 || response.getSuffix().compareTo(".png") == 0) {
                        Bitmap downBitmap = BitmapFactory.decodeFile(response.getPath());
                        //imgView.setImageBitmap(downBitmap);

                        //String texture_file_name=response.getPath().substring(response.getPath().lastIndexOf("/")+1,response.getPath().length()-4);
                        String texture_file_name = url.substring(url.lastIndexOf("/") + 1, url.length() - 4);
                        Log.d("getTexture_name", texture_file_name);

                        // name to be folder name
                        mModelUrl = saveBitmaptoJpeg(downBitmap, mContentModel.getContentId(), texture_file_name);
                        mCallBackListener.onSuccess("model");
                    } else if (response.getSuffix().compareTo(".mp4") == 0) {

                        String model_file_name = mContentModel.getContentId();
                        Log.d("getModel_name", model_file_name);
                        try {
                            FileInputStream file_readed = new FileInputStream(new File(response.getPath()));
                            mModelUrl = saveTemptoMp4(file_readed, mContentModel.getContentId(), model_file_name);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        mCallBackListener.onSuccess("model");
                    }
                }
            });
        }
    }

    public void showDialog(String msg) {
        mBuilder = new MaterialDialog.Builder(mContext)
                .title("요청")
                .content(msg)
                .progress(true, 0);
        mMaterialDialog = mBuilder.build();
        mMaterialDialog.show();
    }

}
