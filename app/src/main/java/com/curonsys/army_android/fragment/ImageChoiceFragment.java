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
import com.curonsys.army_android.R;
import com.curonsys.army_android.activity.MarkerGenerationActivity;
import com.curonsys.army_android.util.SharedDataManager;
import com.curonsys.army_android.util.PictureManager;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ijin-yeong on 2018. 5. 21..
 */
public class ImageChoiceFragment extends Fragment{
    private static final int MY_PERMISSION_STORAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_TAKE_ALBUM = 3;
    private static final int REQUEST_IMAGE_CROP = 4;

    PictureManager pictureManager;
    ImageView showingImg; // 원래는 Previewimg 이름
    RatingBar ratingBar;
    Activity mActivity;
    Context thisContext;

    private Bitmap inputImage; // sift 연산시 사용할 이미지, 근데 crop 된 이미지인지 확인해야 함

    CheckingAsyncTask myAsyncTask;
    SharedDataManager dbManager = SharedDataManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pictureManager = new PictureManager(thisContext);

        View view = inflater.inflate(R.layout.fragment_image_choice, container, false);
        ratingBar = view.findViewById(R.id.ratingbar);
        showingImg = view.findViewById(R.id.preview_img);
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
        return view;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
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
                    //이부분 코드에 대해, 마커 등록과 관련된 클래스 정의하여 다시, DBManager 클래스는 실제 내부디비 클래스 이름으로 사용할 것.
                    dbManager.imageURI = albumURI;
                    dbManager.generatorId = "admin";
                    try{
                        inputImage=MediaStore.Images.Media.getBitmap(thisContext.getContentResolver(),albumURI);
                        //myAsyncTask = new CheckingAsyncTask();
                        //myAsyncTask.execute();
                        Double value = new Double(2.5);
                        dbManager.markerRating = value.doubleValue();
                        ratingBar.setVisibility(View.VISIBLE);
                        ratingBar.setRating(value.floatValue());

                        Button nextStepBtn = mActivity.findViewById(R.id.nextstepBtn);
                        nextStepBtn.setClickable(true);
                        nextStepBtn.setEnabled(true);

                    }catch(FileNotFoundException e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }catch(IOException e){
                        // TODO Auto-generated catch block
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

    public class CheckingAsyncTask extends AsyncTask<Double, Void, Double> {
        MaterialDialog.Builder builder = null;
        MaterialDialog materialDialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            builder = new MaterialDialog.Builder(thisContext)
                    .title("유효성 검사중")
                    .content("시간이 조금 걸릴 수 있습니다...")
                    .progress(true,0);
            materialDialog = builder.build();
            materialDialog.show();
        }

        @Override
        protected void onPostExecute(Double value) {
            super.onPostExecute(value);
            materialDialog.dismiss();
            dbManager.markerRating = value;
            Toast.makeText(thisContext,"유효성 검사가 완료되었습니다.",Toast.LENGTH_SHORT).show();
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(value.floatValue());

            Button nextStepBtn = mActivity.findViewById(R.id.nextstepBtn);
            nextStepBtn.setClickable(true);
            nextStepBtn.setEnabled(true);
            Log.d("asyctask result",value+"");
        }

        @Override
        protected Double doInBackground(Double... params) {
            /*
            ImageProcessingManager imgManager = new ImageProcessingManager();
            double result = imgManager.sift(inputImage);
            return result;
            */
            return 0.0;
        }
    }
}
