package com.curonsys.army_android.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.curonsys.army_android.adapter.ContentsListRecyclerViewAdapter;
import com.curonsys.army_android.R;
import com.curonsys.army_android.model.ContentModel;
import com.curonsys.army_android.model.UserModel;
import com.curonsys.army_android.util.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class ItemListActivity extends AppCompatActivity {
    private static final String TAG = ItemListActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private RequestManager mRequestManager;
    private MaterialDialog mMaterialProgress = null;
    private MaterialDialog.Builder mMaterialBuilder = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    protected boolean mTwoPane;
    protected ArrayList<ContentModel> mItems = new ArrayList<ContentModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                        updateList();
                    }
                }
        );

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        mRequestManager = RequestManager.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userid = currentUser.getUid();

        mMaterialBuilder = new MaterialDialog.Builder(this)
                .title("컨텐츠 다운로드")
                .content("컨텐츠 목록을 다운로드 중입니다...")
                .progress(true, 0);
        mMaterialProgress = mMaterialBuilder.build();
        if (currentUser != null && currentUser.isEmailVerified()) {
            mMaterialProgress.show();
            getContentsList(userid);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        /*
        ItemListActivity
         -> recyclerView.setAdapter
            -> ItemDetailActivity
               -> ContentModelDetailFragment
         */
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new ContentsListRecyclerViewAdapter(this, mItems, mTwoPane));
    }

    private void getContentsList(String userid) {
        mRequestManager.requestGetUserInfo(userid, new RequestManager.UserCallback() {
            @Override
            public void onResponse(UserModel response) {
                Log.d(TAG, "onResponse: ContentListModel (" +
                        response.getUserId() + ", " + response.getName() + ", " + response.getImageUrl() + ")");
                ArrayList<String> ids = response.getContents();

                mRequestManager.requestGetContentsList(ids, new RequestManager.ContentsListCallback() {
                    @Override
                    public void onResponse(ArrayList<ContentModel> response) {
                        mItems = response;
                        mMaterialProgress.dismiss();
                        Log.d(TAG, "onResponse: contents list complete ");
                        View recyclerView = findViewById(R.id.item_list);
                        assert recyclerView != null;
                        setupRecyclerView((RecyclerView) recyclerView);
                    }
                });
            }
        });
    }

    private void updateList() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}

