package com.curonsys.army_android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.curonsys.army_android.util.CallBackListener;
import com.curonsys.army_android.activity.MarkerGenerationActivity;
import com.curonsys.army_android.util.PictureManager;
import com.curonsys.army_android.R;
import com.curonsys.army_android.util.RequestManager;
import com.curonsys.army_android.util.SharedDataManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ijin-yeong on 2018. 5. 21..
 * 마커로 사용할 이미지 선택 엑티비티
 */

public class ImageChoiceFragment extends Fragment{
    private static final int MY_PERMISSION_STORAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_TAKE_ALBUM = 3;
    private static final int REQUEST_IMAGE_CROP = 4;
    private CallBackListener callBackListener;
    MaterialDialog.Builder builder = null;
    MaterialDialog materialDialog = null;

    PictureManager pictureManager;
    ImageView showingImg; // 원래는 Previewimg 이름
    RatingBar ratingBar;
    Activity mActivity;
    Context thisContext;
    Button card_btn, marker_btn;
    boolean state_register = true;

    private Bitmap inputImage; // sift 연산시 사용할 이미지

    SharedDataManager dbManager = SharedDataManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        callBackListener = (CallBackListener) getActivity();
        pictureManager = new PictureManager(thisContext);

        View view = inflater.inflate(R.layout.fragment_image_choice, container, false);
        marker_btn = view.findViewById(R.id.marker_select_btn);
        card_btn = view.findViewById(R.id.card_select_btn);

        ratingBar = view.findViewById(R.id.ratingbar);
        showingImg = view.findViewById(R.id.preview_img);

        showingImg.setBackground(getResources().getDrawable(R.drawable.round_fg));
        showingImg.setClipToOutline(true);

        showingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(thisContext)
                        .title("이미지 선택")
                        .titleColor(getResources().getColor(R.color.colorToTintWith))
                        .items(R.array.image_get_choice_list)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Intent intent = null;
                                if(which==0){
                                    intent = pictureManager.captureCamera();
                                    startActivityForResult(intent,REQUEST_TAKE_PHOTO);
                                }else {
                                    intent = pictureManager.getAlbum();
                                    startActivityForResult(intent,REQUEST_TAKE_ALBUM);
                                }
                            }
                        })
                        .show();
            }
        });


        marker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker_btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                card_btn.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
                state_register = true;
                ((MarkerGenerationActivity)getActivity()).setMarkerType(state_register);
            }
        });

        card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                marker_btn.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
                state_register = false;
                ((MarkerGenerationActivity)getActivity()).setMarkerType(state_register);
            }
        });




        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data){
        switch(requestCode){
            case REQUEST_TAKE_PHOTO:
                if(resultCode== Activity.RESULT_OK){
                    try{
                        pictureManager.setInputImage();
                        Intent cropIntent = pictureManager.cropImage();
                        startActivityForResult(cropIntent,REQUEST_IMAGE_CROP);
                    }catch(Exception e){
                        Log.e("REQUEST_TAKE_PHOTO",e.toString());
                    }
                }else{
                    Toast.makeText(thisContext,"사진찍기를 취소하였습니다.",Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_TAKE_ALBUM:
                if(resultCode==Activity.RESULT_OK){

                    if(data.getData()!=null){
                        try{
                            Uri photoURI = data.getData();
//                            pictureManager.setAlbumURI();
                            pictureManager.setPhotoURI(photoURI);
                            pictureManager.setInputImage();

                            Intent cropIntent = pictureManager.cropImage();
                            startActivityForResult(cropIntent,REQUEST_IMAGE_CROP);

                        }catch(Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR",e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if(resultCode==Activity.RESULT_OK){

                    pictureManager.galleryAddPic();
                    Uri albumURI = pictureManager.getAlbumURI();
                    showingImg.setImageURI(albumURI);
                    Log.d("showingImg URI",albumURI.toString());
                    //image save

                    //이부분 코드에 대해, 마커 등록과 관련된 클래스 정의하여 다시, DBManager 클래스는 실제 내부디비 클래스 이름으로 사용할 것.

                    dbManager.imageURI = albumURI;
                    dbManager.generatorId = "admin";
                    try{


                        builder = new MaterialDialog.Builder(thisContext)
                                .title("유효성 검사중")
                                .content("시간이 조금 걸릴 수 있습니다...")
                                .progress(true,0);

                        materialDialog = builder.build();
                        materialDialog.show();

                        inputImage=MediaStore.Images.Media.getBitmap(thisContext.getContentResolver(),albumURI);
                        RequestManager requestManager = RequestManager.getInstance();
                        requestManager.markerEvaluationToDjango(new File(albumURI.getPath()), new RequestManager.DjangoImageUploadCallback() {
                                    @Override
                                    public void onCallback(JSONObject result) {
                                        materialDialog.dismiss();
                                        try{
                                            Double rating = Double.parseDouble(result.getString("rating"));
                                            dbManager.markerRating = rating;
                                            Toast.makeText(thisContext,"유효성 검사가 완료되었습니다."+String.valueOf(rating),Toast.LENGTH_SHORT).show();
                                            ratingBar.setVisibility(View.VISIBLE);
                                            ratingBar.setRating(rating.floatValue());

                                            Button nextStepBtn = mActivity.findViewById(R.id.nextstepBtn);
                                            nextStepBtn.setClickable(true);
                                            nextStepBtn.setEnabled(true);
                                            callBackListener.onDoneBack();
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }

                                    }
                                });

//                        imagProcessingTask = new ImageProcessingAsyncTask();
//                        imagProcessingTask.execute();
                    }catch(FileNotFoundException e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }catch(IOException e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    break;
                }
        }
    }



    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        thisContext=activity;
        if(activity.getClass()== MarkerGenerationActivity.class){
            mActivity = (MarkerGenerationActivity) activity;
        }
    }

}
