package com.curonsys.army_android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.curonsys.army_android.util.CallBackListener;
import com.curonsys.army_android.model.ContentsListModel;
import com.curonsys.army_android.adapter.CustomAdapter;
import com.curonsys.army_android.R;
import com.curonsys.army_android.util.RecyclerItemClickListener;
import com.curonsys.army_android.util.RequestManager;
import com.curonsys.army_android.model.ContentModel;
import com.curonsys.army_android.model.UserModel;
import com.curonsys.army_android.util.SharedDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by ijin-yeong on 2018. 5. 21..
 */

public class ContentsChoiceFragment extends Fragment {
    private Context mContext;
    private FirebaseAuth mAuth;
    private UserModel mUserModel;
    ArrayList<ContentModel> mContentsModel;
    CallBackListener mCallBackListener;

    private static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static RecyclerView mRecyclerView;
    private static ArrayList<ContentsListModel> mData;
    public static View.OnClickListener myOnClickListener;
    SharedDataManager mSDManager = SharedDataManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_contents_choice, container, false);

        //myOnClickListener = new MyOnClickListener(mContext,getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCallBackListener = (CallBackListener) getActivity();

        // will be implemented..
        // contents = getContentsList();

        mData = new ArrayList<ContentsListModel>();
//      initContentsList(data, contents);
//      removedItems = new ArrayList<Integer>();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userid = currentUser.getUid();

        final RequestManager requestManager = RequestManager.getInstance();
        requestManager.requestGetUserInfo(userid, new RequestManager.UserCallback() {
            @Override
            public void onResponse(UserModel response) {
                mUserModel = response;
                Log.d("userInfo", mUserModel.getEmail());

                requestManager.requestGetContentsList(mUserModel.getContents(), new RequestManager.ContentsListCallback() {
                    @Override
                    public void onResponse(ArrayList<ContentModel> response) {
                        mContentsModel = response;
                        Log.d("response Check",mContentsModel.size()+"");
                        mAdapter = new CustomAdapter(mContentsModel);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Log.d("clikced", "test");
                ContentModel selected = mContentsModel.get(position);
                mSDManager.contentId = selected.getContentId();
                mSDManager.contentName = selected.getContentName();
                mSDManager.is3D = selected.get3D();
                Log.d("clicked",selected.getContentId());
                Log.d("clicked",selected.getContentName());
                new MaterialDialog.Builder(mContext)
                        .title("해당 컨텐츠를 선택하시겠습니까?")
                        .titleColor(Color.BLACK)
                        .positiveText("예")
                        .negativeText("아니요")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                RecyclerView.ViewHolder viewHolder
                                        = mRecyclerView.findViewHolderForPosition(position);
                                final TextView textView = viewHolder.itemView.findViewById(R.id.textViewName);
                                Toast.makeText(mContext,textView.getText()+"", Toast.LENGTH_SHORT).show();
                                mCallBackListener.onDoneBack();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(mContext,"취소",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));

        return view;
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
        //https://www.journaldev.com/10024/android-recyclerview-android-cardview-example-tutorial
        //step 3는 위의 url을 이용해서 cardview로 구현할 예정임
        //컨텐츠를 단순하게 선택하는 기능을 가지는 fragment, recyclerview 이용
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
//        if (item.getItemId() == R.id.add_item) {
//            //check if any items to add
//            if (removedItems.size() != 0) {
//                addRemovedItemToList();
//            } else {
//                Toast.makeText(mContext, "Nothing to add", Toast.LENGTH_SHORT).show();
//            }
//        }
        return true;
    }

    private String getContentsList() {
        return null;
    }

}