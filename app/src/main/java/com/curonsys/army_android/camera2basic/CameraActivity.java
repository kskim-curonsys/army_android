/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.curonsys.army_android.camera2basic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.curonsys.army_android.util.CallBackListener;
import com.curonsys.army_android.R;
import com.curonsys.army_android.activity.GeneralARActivity;

public class CameraActivity extends AppCompatActivity implements CallBackListener {
    Camera2BasicFragment cameraFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (null == savedInstanceState) {
            cameraFrag = Camera2BasicFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onDoneBack() {

    }
    @Override
    public void onSuccess(String message){
        //not thing..
    }

    @Override
    public void onSuccess(String message, boolean isMarker) {

        switch (message){
            case "capture":
                Log.d("capture","sucess");
//                cameraFrag.initLoaction(this);
                if(isMarker){
                    cameraFrag.uploadData(this);
                }else {
                    cameraFrag.uploadCardData(this);
                }

                break;
            case "upload":
                Log.d("upload","sucess");
//                Toast.makeText(getApplicationContext(),cameraFrag.getMarkerUrl(),Toast.LENGTH_LONG).show();

                if(isMarker){
                    cameraFrag.showDialog("마커 정보를 가져오는 중입니다...",this);
                    cameraFrag.getMarkerModel((CallBackListener)this);
                }else {
                    cameraFrag.showDialog("명함 정보를 가져오는 중입니다...",this);
                    cameraFrag.getCardModel((CallBackListener)this);
                }

                break;

            case "getMarkerModel":
//                Log.d("marker","sucess");
                cameraFrag.materialDialog.dismiss();
                cameraFrag.getMarker(this);
                cameraFrag.showDialog("마커를 다운받는 중입니다...",this);
                break;

            case "markerImg":
                cameraFrag.materialDialog.dismiss();
                cameraFrag.getContentsModel(this);
                cameraFrag.showDialog("컨텐츠 정보를 가져오는 중입니다...",this);
                break;

            case "contentsModel":
                cameraFrag.materialDialog.dismiss();
                Log.e("getContentsModel :","sucess");
                cameraFrag.showDialog("모델 파일을 가져오는 중입니다...",this);
                cameraFrag.getModelFromStorage(this);
                break;

            case "model":
                cameraFrag.materialDialog.dismiss();
                Log.e("getJet :","sucess");
                cameraFrag.showDialog("텍스쳐를 가져오는 중입니다...",this);
                cameraFrag.getTextures(this);
                break;

            case "textures":
                cameraFrag.materialDialog.dismiss();
                Log.e("getTextures :","sucess");
                //Toast.makeText(getApplicationContext(),"컨텐츠를 정상적으로 가져왔습니다",Toast.LENGTH_SHORT).show();
                cameraFrag.setContentsModel();
                //Toast.makeText(getApplicationContext(),"dd",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, GeneralARActivity.class);
                intent.putExtra("contents_model",cameraFrag.contentModel_putExtra);
                startActivity(intent);
                break;
        }
    }
}
