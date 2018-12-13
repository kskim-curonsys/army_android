package com.curonsys.army_android.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.curonsys.army_android.R;
import com.curonsys.army_android.fragment.ContentModelDetailFragment;
import com.curonsys.army_android.model.ContentModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class ItemDetailActivity extends AppCompatActivity {
    private static final String TAG = ItemDetailActivity.class.getSimpleName();

    private ImageView mBGImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        mBGImage = (ImageView) findViewById(R.id.backdrop);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail activity action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            String itemID = getIntent().getStringExtra(ContentModelDetailFragment.ARG_ITEM_ID);
            ContentModel itemModel = (ContentModel) getIntent().getSerializableExtra(ContentModelDetailFragment.ARG_ITEM);

            String thumbpath = itemModel.getThumb();
            String separator = thumbpath.substring(0, 7);
            if (separator.compareTo("models/") != 0) {
                File imgFile = new File(thumbpath);
                if (imgFile.exists()) {
                    Bitmap thumbBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    mBGImage.setImageBitmap(thumbBitmap);
                }
            } else {
                AssetManager am = getResources().getAssets();
                InputStream is = null;

                try {
                    is = am.open("lake.png");
                    if (is != null) {
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        mBGImage.setImageBitmap(bm);
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            arguments.putString(ContentModelDetailFragment.ARG_ITEM_ID, itemID);
            arguments.putSerializable(ContentModelDetailFragment.ARG_ITEM, itemModel);

            ContentModelDetailFragment fragment = new ContentModelDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
