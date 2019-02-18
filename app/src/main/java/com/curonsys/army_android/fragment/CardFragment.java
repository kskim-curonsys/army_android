package com.curonsys.army_android.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.curonsys.army_android.util.CallBackListener;
import com.curonsys.army_android.R;
import com.curonsys.army_android.activity.MarkerGenerationActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment {
    private CallBackListener mCallBackListener;
    private Activity mActivity;
    private Context mContext;
    Button mNextBtn;
    private EditText mCardEdit1, mCardEdit2, mCardEdit3;

    public CardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        mCallBackListener = (CallBackListener) getActivity();

        mNextBtn = mActivity.findViewById(R.id.nextstepBtn);
        mCardEdit1 = view.findViewById(R.id.card_edt1);
        mCardEdit2 = view.findViewById(R.id.card_edt2);
        mCardEdit3 = view.findViewById(R.id.card_edt3);

        mCallBackListener.onDoneBack();

        return view;
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

    @Override
    public void onDestroy() {
        Log.i("card_timer", "cancel");
        super.onDestroy();
    }

    public String getPhoneNumber() {
        if (mCardEdit1.getText().toString().equals("")) {
            return "";
        }
        if (mCardEdit2.getText().toString().equals("")) {
            return "";
        }
        if (mCardEdit3.getText().toString().equals("")) {
            return "";
        }

        return mCardEdit1.getText().toString() + mCardEdit2.getText().toString() + mCardEdit3.getText().toString();
    }
}

