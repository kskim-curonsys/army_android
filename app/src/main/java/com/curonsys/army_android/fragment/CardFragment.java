package com.curonsys.army_android.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.curonsys.army_android.util.CallBackListener;
import com.curonsys.army_android.R;
import com.curonsys.army_android.activity.MarkerGenerationActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment {
    private CallBackListener callBackListener;
    private Activity mActivity;
    private Context thisContext;
    Button nextStepBtn;
    private EditText editText1,editText2,editText3;




    public CardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_card, container, false);
        callBackListener = (CallBackListener) getActivity();

        nextStepBtn = mActivity.findViewById(R.id.nextstepBtn);
        editText1 = view.findViewById(R.id.card_edt1);
        editText2 = view.findViewById(R.id.card_edt2);
        editText3 = view.findViewById(R.id.card_edt3);

        callBackListener.onDoneBack();

        return view;
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

    @Override
    public void onDestroy() {
        Log.i("card_timer", "cancle");
        super.onDestroy();
    }

    public String getPhoneNumber(){
        if(editText1.getText().toString().equals("")){
            return "";
        }
        if(editText2.getText().toString().equals("")){
            return "";
        }
        if(editText3.getText().toString().equals("")){
            return "";
        }

        return editText1.getText().toString()+editText2.getText().toString()+editText3.getText().toString();
    }
}
