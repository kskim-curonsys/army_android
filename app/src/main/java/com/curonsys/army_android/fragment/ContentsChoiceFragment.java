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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by ijin-yeong on 2018. 5. 21..
 */

public class ContentsChoiceFragment extends Fragment {

    Context thisContext;
    private FirebaseAuth mAuth;
    UserModel userModel;
    ArrayList<ContentModel> contentsModel;
    CallBackListener callBackListener;

    TextView tv;
    MaterialDialog.Builder builder = null;
    MaterialDialog materialDialog = null;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<ContentsListModel> data;
    public static View.OnClickListener myOnClickListener;
    //    private static ArrayList<Integer> removedItems;
    SharedDataManager dbManager = SharedDataManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_contents_choice, container, false);
        FragmentManager fragmentManager = this.getChildFragmentManager();

        //myOnClickListener = new MyOnClickListener(thisContext,getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(thisContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        callBackListener = (CallBackListener) getActivity();

        // will be implemented..
        // contents = getContentsList();

        data = new ArrayList<ContentsListModel>();
//        initContentsList(data, contents);

//        removedItems = new ArrayList<Integer>();


        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("hm5207@gmail.com", "12345678")
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            //nextStep();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            //updateUI(null);
                        }
                        //showProgress(false);
                    }
                });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userid = currentUser.getUid();
//        Log.d("userid_check",userid);
        //final String userid = "rA3tNJormsYZEa5nGfIV1RHuLRF3";


        final RequestManager requestManager = RequestManager.getInstance();

        requestManager.requestGetUserInfo(userid, new RequestManager.UserCallback() {
            @Override
            public void onResponse(UserModel response) {
                userModel = response;
                Log.d("userInfo", userModel.getEmail());

                requestManager.requestGetContentsList(userModel.getContents(), new RequestManager.ContentsListCallback() {
                    @Override
                    public void onResponse(ArrayList<ContentModel> response) {
                        contentsModel = response;
                        Log.d("response Check",contentsModel.size()+"");
                        adapter = new CustomAdapter(contentsModel);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(thisContext, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Log.d("clikced", "test");
                ContentModel selected = contentsModel.get(position);
                dbManager.contentId = selected.getContentId();
                dbManager.contentName = selected.getContentName();
                dbManager.is3D = selected.get3D();
                Log.d("clicked",selected.getContentId());
                Log.d("clicked",selected.getContentName());
                new MaterialDialog.Builder(thisContext)
                        .title("해당 컨텐츠를 선택하시겠습니까?")
                        .titleColor(Color.BLACK)
                        .positiveText("예")
                        .negativeText("아니요")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                RecyclerView.ViewHolder viewHolder
                                        = recyclerView.findViewHolderForPosition(position);
                                final TextView textView = viewHolder.itemView.findViewById(R.id.textViewName);
                                Toast.makeText(thisContext,textView.getText()+"", Toast.LENGTH_SHORT).show();
                                callBackListener.onDoneBack();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(thisContext,"취소",Toast.LENGTH_SHORT).show();
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

    private String getContentsList() {
        return null;
    }


//
//    private void initContentsList(ArrayList<ContentsListModel> li, JSONArray gotted_contents) {
//        try{
//            for (int i = 0; i < gotted_contents.length(); i++) {
//                li.add(new ContentsListModel(
//                        gotted_contents.getJSONObject(i).getString("ContentsIndentify"),
//                        gotted_contents.getJSONObject(i).getString("ContentsName"),
//                        gotted_contents.getJSONObject(i).getString("ContentsDescribe"),
//                        gotted_contents.getJSONObject(i).getString("ThumbNailUrl")
//                ));
//            }
//        }catch (JSONException e){e.printStackTrace();}
//
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        thisContext = activity;
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
//                Toast.makeText(thisContext, "Nothing to add", Toast.LENGTH_SHORT).show();
//            }
//        }
        return true;
    }
}