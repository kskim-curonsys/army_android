package com.curonsys.army_android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

/**
 * Created by ijin-yeong on 2018. 5. 21..
 * 마커로 사용할 이미지 선택 엑티비티
 */

public class ImageChoiceFragment extends Fragment {
    private static final int MY_PERMISSION_STORAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_TAKE_ALBUM = 3;
    private static final int REQUEST_IMAGE_CROP = 4;

    private CallBackListener mCallBackListener;
    MaterialDialog.Builder mBuilder = null;
    MaterialDialog mMaterialDialog = null;
    PictureManager mPictureManager;
    ImageView mShowingImg;
    RatingBar mRatingBar;
    Activity mActivity;
    Context mContext;
    Button mCardBtn, mMarkerBtn;

    boolean mMarkerType = true;
    //private Bitmap inputImage; // sift 연산시 사용할 이미지

    SharedDataManager mSDManager = SharedDataManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mCallBackListener = (CallBackListener) getActivity();
        mPictureManager = new PictureManager(mContext);

        View view = inflater.inflate(R.layout.fragment_image_choice, container, false);
        mMarkerBtn = view.findViewById(R.id.marker_select_btn);
        mCardBtn = view.findViewById(R.id.card_select_btn);

        mRatingBar = view.findViewById(R.id.ratingbar);
        mShowingImg = view.findViewById(R.id.preview_img);

        mShowingImg.setBackground(getResources().getDrawable(R.drawable.round_fg));
        mShowingImg.setClipToOutline(true);

        mShowingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mContext)
                        .title("이미지 선택")
                        .titleColor(getResources().getColor(R.color.colorToTintWith))
                        .items(R.array.image_get_choice_list)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Intent intent = null;
                                if (which == 0) {
                                    intent = mPictureManager.captureCamera();
                                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                                } else {
                                    intent = mPictureManager.getAlbum();
                                    startActivityForResult(intent, REQUEST_TAKE_ALBUM);
                                }
                            }
                        })
                        .show();
            }
        });

        mMarkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMarkerBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mCardBtn.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
                mMarkerType = true;
                ((MarkerGenerationActivity) getActivity()).setMarkerType(mMarkerType);
            }
        });

        mCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mMarkerBtn.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
                mMarkerType = false;
                ((MarkerGenerationActivity) getActivity()).setMarkerType(mMarkerType);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        mPictureManager.setInputImage();
                        Intent cropIntent = mPictureManager.cropImage();
                        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(mContext, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {

                    if (data.getData() != null) {
                        try {
                            Uri photoURI = data.getData();
//                            mPictureManager.setAlbumURI();
                            mPictureManager.setPhotoURI(photoURI);
                            mPictureManager.setInputImage();

                            Intent cropIntent = mPictureManager.cropImage();
                            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    mPictureManager.galleryAddPic();
                    Uri albumURI = mPictureManager.getAlbumURI();
                    mShowingImg.setImageURI(albumURI);
                    Log.d("showingImg URI", albumURI.toString());
                    //image save

                    //이부분 코드에 대해, 마커 등록과 관련된 클래스 정의하여 다시, DBManager 클래스는 실제 내부디비 클래스 이름으로 사용할 것.

                    mSDManager.imageURI = albumURI;
                    mSDManager.generatorId = "admin";
                    try {
                        mBuilder = new MaterialDialog.Builder(mContext)
                                .title("유효성 검사중")
                                .content("시간이 조금 걸릴 수 있습니다...")
                                .progress(true, 0);

                        mMaterialDialog = mBuilder.build();
                        mMaterialDialog.show();

                        //inputImage = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),albumURI);
                        RequestManager requestManager = RequestManager.getInstance();
                        requestManager.markerEvaluationToDjango(new File(albumURI.getPath()), new RequestManager.DjangoImageUploadCallback() {
                            @Override
                            public void onCallback(JSONObject result) {
                                mMaterialDialog.dismiss();
                                try {
                                    Double rating = Double.parseDouble(result.getString("rating"));
                                    mSDManager.markerRating = rating;
                                    Toast.makeText(mContext, "유효성 검사가 완료되었습니다." + String.valueOf(rating), Toast.LENGTH_SHORT).show();
                                    mRatingBar.setVisibility(View.VISIBLE);
                                    mRatingBar.setRating(rating.floatValue());

                                    Button nextStepBtn = mActivity.findViewById(R.id.nextstepBtn);
                                    nextStepBtn.setClickable(true);
                                    nextStepBtn.setEnabled(true);
                                    mCallBackListener.onDoneBack();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

//                        imagProcessingTask = new ImageProcessingAsyncTask();
//                        imagProcessingTask.execute();
                    } catch (JSONException e) {
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
        mContext = activity;
        if (activity.getClass() == MarkerGenerationActivity.class) {
            mActivity = (MarkerGenerationActivity) activity;
        }
    }
}
