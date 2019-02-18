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

public class MarkerConfirmForCardFragment extends Fragment {
    Activity thisContext;
    ContentModel contentModel;
    ContentModel contentModel_putExtra;
    //getting from firebase file
    ArrayList<String> textures = new ArrayList<String>();
    String modelUrl;

    CallBackListener callBackListener;

    MaterialDialog.Builder builder = null;
    MaterialDialog materialDialog = null;
    TextView userText, ratingText, phoneText, scaleText, textRotateX, textRotateY, textRotateZ;
    ImageView markerPreview;
    SharedDataManager dbManager = SharedDataManager.getInstance();
    int textureCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marker_confirm_for_card, container, false);
        FragmentManager fragmentManager = this.getChildFragmentManager();

        //tv = view.findViewById(R.id.markerInfoTv);
        userText = view.findViewById(R.id.user_id_text_confirm_card);
        ratingText = view.findViewById(R.id.marker_rating_text_confirm_card);
        phoneText = view.findViewById(R.id.marker_phone_text_confirm_card);
        markerPreview = view.findViewById(R.id.marker_preview_confirm_card);
        scaleText = view.findViewById(R.id.marker_scale_confirm_card);
        textRotateX = view.findViewById(R.id.marker_rotatex_confirm_card);
        textRotateY = view.findViewById(R.id.marker_rotatey_confirm_card);
        textRotateZ = view.findViewById(R.id.marker_rotatez_confirm_card);


        userText.setText(dbManager.generatorId);
        ratingText.setText(ratingText.getText() + String.valueOf(dbManager.markerRating));
        phoneText.setText(dbManager.phoneNumber);
//        Bitmap bitmap = BitmapFactory.decodeFile(dbManager.imageURI.toString());

        markerPreview.setBackground(getResources().getDrawable(R.drawable.round_fg));
        markerPreview.setClipToOutline(true);
//        markerPreview.setImageBitmap(bitmap);
        markerPreview.setImageURI(dbManager.imageURI);

//        tv.setText(dbManager.generatorId+",\n"
//                        +dbManager.markerRating+",\n"
//                        +dbManager.imageURI.toString()+",\n"
//                        +dbManager.currentLongtitude+",\n"
//                        +dbManager.currentLatitude+",\n"
//                        +dbManager.contentId+",\n"
//                        +dbManager.contentName+",\n");
        Button btn = view.findViewById(R.id.markerTestBtn_card);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, MarkerTestActivity.class);
                intent.putExtra("info", contentModel_putExtra);
                startActivityForResult(intent, 0);
            }
        });

        //getContentsFiles();
//        getContentsModel();

        getContentsModel();
        showDialog("컨텐츠를 확인중입니다...");

        callBackListener = new CallBackListener() {
            @Override
            public void onSuccess(String message) {
                switch (message) {
                    case "contentsModel":
                        materialDialog.dismiss();
                        Log.e("getContentsModel :", "sucess");
                        showDialog("모델 파일을 가져오는 중입니다...");
                        getModelFromStorage(dbManager.is3D);

                        break;
                    case "model":
                        materialDialog.dismiss();
                        showDialog("텍스쳐를 가져오는 중입니다...");

                        if (dbManager.is3D) {
                            Log.e("getJet :", "sucess");
                            getTextures();
                        } else {
                            Log.e("is3d", "false");
                            callBackListener.onSuccess("textures");
                        }

                        break;
                    case "textures":
                        materialDialog.dismiss();
                        Log.e("getTextures :", "sucess");
                        Toast.makeText(thisContext, "컨텐츠를 정상적으로 가져왔습니다", Toast.LENGTH_SHORT).show();
                        setContentsModel();
                        break;
                }

            }

            @Override
            public void onSuccess(String message, boolean isMarker) {

            }

            @Override
            public void onDoneBack() {
                callBackListener = (CallBackListener) getActivity();
                callBackListener.onDoneBack();
            }
        };
        return view;
    }

    private void setContentsModel() {
        if (dbManager.is3D) {
            contentModel_putExtra = new ContentModel();
            contentModel_putExtra.setTextures(textures);
            contentModel_putExtra.setModel(modelUrl);
        } else {
            contentModel_putExtra = new ContentModel();
            contentModel_putExtra.setModel(modelUrl);
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
        thisContext = activity;
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

                scaleText.setText(scaleText.getText() + scale);
                textRotateX.setText(textRotateX.getText() + rotateX);
                textRotateY.setText(textRotateY.getText() + rotateY);
                textRotateZ.setText(textRotateZ.getText() + rotateZ);
                dbManager.contentScale = Float.parseFloat(scale);

                ArrayList<Float> rotates = new ArrayList<>();
                rotates.add(Float.parseFloat(rotateX));
                rotates.add(Float.parseFloat(rotateY));
                rotates.add(Float.parseFloat(rotateZ));
                dbManager.contentRotation = rotates;

                callBackListener.onDoneBack();
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

            //Log.d("textures_path",string_path+file_name);
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
        final String contentId = dbManager.contentId;
        RequestManager requestManager = RequestManager.getInstance();


        requestManager.requestGetContentInfo(contentId, new RequestManager.ContentCallback() {
            @Override
            public void onResponse(ContentModel response) {
                contentModel = response;
                callBackListener.onSuccess("contentsModel");
            }
        });
    }

    public void getTextures() {
        try {
            Log.d("markertest", contentModel.toString());

            for (int i = 0; i < contentModel.getTextures().size(); i++) {
                Log.d("texture real name", contentModel.getTextures().get(i) + "");
                String texture_url = contentModel.getTextures().get(i);
                getTexture(contentModel.getContentName(), texture_url, i, contentModel.getTextures().size());
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
                        textures.add(saveBitmaptoJpeg(downBitmap, name, texture_file_name));
                    }
                    Log.d(TAG, "onResponse: content download complete ");
                    String texture_url = response.getPath();
                    //Log.d("texture_path",texture_url);
                    //textures.add(texture_url);
                    //setup();

                    //very important
                    textureCount++;
                    if (textureCount == last_count) {
                        callBackListener.onSuccess("textures");
                    }
                }
            });
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void getModelFromStorage(boolean is3D) {
        if (is3D == true) {

            String url = contentModel.getModel();
            String suffix = url.substring(url.indexOf('.'), url.length());
            RequestManager mRequestManager = RequestManager.getInstance();
            mRequestManager.requestDownloadFileFromStorage(contentModel.getContentName(), url, suffix, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel response) {
                    if (response.getSuffix().compareTo(".jet") == 0) {
                        String model_file_name = contentModel.getContentName();
                        Log.d("getModel_name", model_file_name);
                        try {
                            FileInputStream file_readed = new FileInputStream(new File(response.getPath()));
                            modelUrl = saveTemptoJet(file_readed, contentModel.getContentName(), model_file_name);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    callBackListener.onSuccess("model");
                }
            });
        } else {
            final String url = contentModel.getModel();
            String suffix = url.substring(url.indexOf('.'), url.length());
            RequestManager mRequestManager = RequestManager.getInstance();
            mRequestManager.requestDownloadFileFromStorage(contentModel.getContentName(), url, suffix, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel response) {
                    if (response.getSuffix().compareTo(".jpg") == 0 || response.getSuffix().compareTo(".png") == 0) {
                        Bitmap downBitmap = BitmapFactory.decodeFile(response.getPath());
                        //imgView.setImageBitmap(downBitmap);

                        //String texture_file_name=response.getPath().substring(response.getPath().lastIndexOf("/")+1,response.getPath().length()-4);
                        String texture_file_name = url.substring(url.lastIndexOf("/") + 1, url.length() - 4);
                        Log.d("getTexture_name", texture_file_name);

                        // name to be folder name
                        modelUrl = saveBitmaptoJpeg(downBitmap, contentModel.getContentName(), texture_file_name);
                        callBackListener.onSuccess("model");
                    } else if (response.getSuffix().compareTo(".mp4") == 0) {

                        String model_file_name = contentModel.getContentName();
                        Log.d("getModel_name", model_file_name);
                        try {
                            FileInputStream file_readed = new FileInputStream(new File(response.getPath()));
                            modelUrl = saveTemptoMp4(file_readed, contentModel.getContentName(), model_file_name);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        callBackListener.onSuccess("model");
                    }
                }
            });
        }
    }

    public void showDialog(String msg) {
        builder = new MaterialDialog.Builder(thisContext)
                .title("요청")
                .content(msg)
                .progress(true, 0);
        materialDialog = builder.build();
        materialDialog.show();
    }

}
