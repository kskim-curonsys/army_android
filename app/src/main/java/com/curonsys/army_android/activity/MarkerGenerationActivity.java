package com.curonsys.army_android.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.curonsys.army_android.R;
import com.curonsys.army_android.util.CallBackListener;
import com.curonsys.army_android.fragment.CardFragment;
import com.curonsys.army_android.fragment.ContentsChoiceFragment;
import com.curonsys.army_android.fragment.ImageChoiceFragment;
import com.curonsys.army_android.fragment.LocationChoiceFragment;
import com.curonsys.army_android.fragment.MarkerConfirmForCardFragment;
import com.curonsys.army_android.fragment.MarkerConfirmFragment;
import com.curonsys.army_android.util.SharedDataManager;
import com.curonsys.army_android.util.MarkerUploader;


public class MarkerGenerationActivity extends AppCompatActivity implements CallBackListener {
    static final int FRAGMENT1 = 1;
    static final int FRAGMENT2 = 2;
    static final int FRAGMENT3 = 3;
    static final int FRAGMENT4 = 4;
    int mCurrentFragment = 1;
    Button mNextBtn;
    Activity mActivity;
    boolean mMarkerType = true;
    SharedDataManager mSDManager = SharedDataManager.getInstance();

    @Override
    public void onSuccess(String message) {
    }

    @Override
    public void onSuccess(String message, boolean isMarker) {
    }

    @Override
    public void onDoneBack() {
        mNextBtn.setEnabled(true);
        mNextBtn.setBackgroundColor(getResources().getColor(R.color.button_blue));
        mNextBtn.setTextColor(Color.WHITE);
        Drawable btnDraw = mNextBtn.getBackground();
        btnDraw.setAlpha(255);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_marker_generation);

        mActivity = this;
        mNextBtn = findViewById(R.id.nextstepBtn);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_simple, new ImageChoiceFragment());
        fragmentTransaction.commit();
        mNextBtn.setEnabled(false);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNextBtn.setEnabled(false);
                mNextBtn.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (mCurrentFragment) {
                    case FRAGMENT1:
                        if (mMarkerType) {
                            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
                            fragmentTransaction.replace(R.id.fragment_simple, new LocationChoiceFragment());
                            fragmentTransaction.commit();
                            mNextBtn.setTextColor(Color.BLACK);
                            mCurrentFragment = FRAGMENT2;
                        } else {
                            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
                            fragmentTransaction.replace(R.id.fragment_simple, new CardFragment());
                            fragmentTransaction.commit();
                            mNextBtn.setTextColor(Color.BLACK);
                            mCurrentFragment = FRAGMENT2;
                        }
                        break;

                    case FRAGMENT2:
                        if (mMarkerType) {
                            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
                            fragmentTransaction.replace(R.id.fragment_simple, new ContentsChoiceFragment());
                            fragmentTransaction.commit();
                            mNextBtn.setTextColor(Color.BLACK);
                            mCurrentFragment = FRAGMENT3;
                        } else {
                            CardFragment frag = (CardFragment) fragmentManager.findFragmentById(R.id.fragment_simple);
                            String phoneNum = frag.getPhoneNumber();
                            if (phoneNum.equals("")) {
                                Toast.makeText(getApplicationContext(), getString(R.string.check_your_data), Toast.LENGTH_SHORT).show();
                                onDoneBack();
                            } else {
                                mSDManager.phoneNumber = phoneNum;
                                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
                                fragmentTransaction.replace(R.id.fragment_simple, new ContentsChoiceFragment());
                                fragmentTransaction.commit();
                                mNextBtn.setTextColor(Color.BLACK);
                                mCurrentFragment = FRAGMENT3;
                            }
                        }
                        break;

                    case FRAGMENT3:
                        if (mMarkerType) {
                            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
                            fragmentTransaction.replace(R.id.fragment_simple, new MarkerConfirmFragment());
                            fragmentTransaction.commit();
                            mNextBtn.setTextColor(Color.BLACK);
                            mCurrentFragment = FRAGMENT4;
                        } else {
                            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
                            fragmentTransaction.replace(R.id.fragment_simple, new MarkerConfirmForCardFragment());
                            fragmentTransaction.commit();
                            mNextBtn.setTextColor(Color.BLACK);
                            mCurrentFragment = FRAGMENT4;
                        }
                        break;

                    case FRAGMENT4:
                        if (mMarkerType) {
                            MarkerUploader markerUploader = new MarkerUploader(mActivity);
                            markerUploader.start();
                        } else {
                            MarkerUploader markerUploader = new MarkerUploader(mActivity);
                            markerUploader.startCardUpload();
                        }
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        if (v instanceof EditText) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            // calculate the relative position of the clicking position against the position of the view
            float x = event.getRawX() - scrcoords[0];
            float y = event.getRawY() - scrcoords[1];

            // check whether action is up and the clicking position is outside of the view
            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < 0 || x > v.getRight() - v.getLeft()
                    || y < 0 || y > v.getBottom() - v.getTop())) {
                if (v.getOnFocusChangeListener() != null) {
                    v.getOnFocusChangeListener().onFocusChange(v, false);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setMarkerType(boolean markertype) {
        mMarkerType = markertype;
    }
}
