package com.curonsys.army_android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.curonsys.army_android.R;
import com.curonsys.army_android.arcore.AugmentedImageActivity;
import com.curonsys.army_android.arcore.ChromaKeyVideoActivity;
import com.curonsys.army_android.arcore.SolarActivity;

public class ARCoreTestActivity extends AppCompatActivity {

    Button mBtnAugmentedImage;
    Button mBtnSceneform;
    Button mBtnCloudAnchors;
    Button mBtnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcore_test);

        mBtnAugmentedImage = (Button) findViewById(R.id.button_arcore1);
        mBtnAugmentedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAugmentedImageActivity();
            }
        });

        mBtnSceneform = (Button) findViewById(R.id.button_arcore2);
        mBtnSceneform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSceneformActivity();
            }
        });

        mBtnCloudAnchors = (Button) findViewById(R.id.button_arcore3);
        mBtnCloudAnchors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSceneViewActivity();
            }
        });

        mBtnTest = (Button) findViewById(R.id.button_arcore4);
        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCloudAnchorsActivity();
            }
        });

    }

    private void goAugmentedImageActivity() {
        Intent intent = new Intent(this, AugmentedImageActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void goSceneformActivity() {
        Intent intent = new Intent(this, ChromaKeyVideoActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void goSceneViewActivity() {
        Intent intent = new Intent(this, SolarActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void goCloudAnchorsActivity() {

    }

}
