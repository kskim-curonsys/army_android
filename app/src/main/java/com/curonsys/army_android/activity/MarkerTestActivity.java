package com.curonsys.army_android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.curonsys.army_android.R;
import com.curonsys.army_android.util.SharedDataManager;

import java.io.File;
import java.util.HashMap;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTrackableListener;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;

public class MarkerTestActivity extends ARActivity {

    SharedDataManager dbManager= SharedDataManager.getInstance();
    String path;
    SeekBar seekBar;
    float scaleValue = 1.0f;
    ARModelNode node3d;
    Button completeBtn;
    float setScale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_test);

        seekBar = findViewById(R.id.scaleSeekbar);
        completeBtn  = findViewById(R.id.completeBtn);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("seekbar Value",progress+"");
                scaleValue = Float.parseFloat(String.valueOf(progress)) / 100;
                Log.d("scaleValue",scaleValue+"");
                float newX;
                float newY;
                float newZ;
                newX = 1 * scaleValue;
                newY = 1 * scaleValue;
                newZ = 1 * scaleValue;
                setScale = newX;
                node3d.setScale(newX,newY,newZ);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("scale", String.valueOf(setScale));
                setResult(1, intent);
                finish();
            }
        });
    }

    @Override
    public void setup() {
        path = dbManager.imageURI.toString();
        //step1, 추적 가능한 이미지를 등록해라. 여기서 해야 할 일은 Trackable 객체 만들기, Tracker 생성
        ARImageTrackable imageTrackable;
        //이름지정
        imageTrackable = new ARImageTrackable("MarkerForAR");
        //에셋에서 이미지 로딩
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "kudan");
        Log.d("123123", file.getAbsolutePath());
        if (path != null) {
            Uri uri = Uri.parse(path);
            imageTrackable.loadFromPath(uri.getPath());
            Log.d("uri",uri.getPath());
        } else {
            Toast.makeText(getApplicationContext(), "마커를 먼저 등록해주세요.", Toast.LENGTH_LONG).show();
        }

        // Get the single instance of the image tracker.
        ARImageTracker imageTracker;
        //인스턴스생성
        imageTracker = ARImageTracker.getInstance();
        //트래커 초기화
        imageTracker.initialise();
        imageTracker.addTrackable(imageTrackable);

        //트래커 객체생성

        /* 모델 파일 테스트 */


        ARModelImporter arModelImporter = new ARModelImporter();

        //model's file name

        if(dbManager.textureCount<2){

            arModelImporter.loadFromAsset(dbManager.contentFileName);
            //model info[0]
            node3d = arModelImporter.getNode();
            node3d.setName("somthing");
            node3d.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
            node3d.rotateByDegrees(180.0f, 1.0f, 100.0f, 0.0f);
            ARTexture2D texture2D = new ARTexture2D();
            if(dbManager.contentTextureFiles.length<2){
                texture2D.loadFromAsset(dbManager.contentTextureFiles[0]);
            }
            //model info[1]
            final ARLightMaterial material = new ARLightMaterial();
            material.setTexture(texture2D);
            material.setColour(1, 1, 1);
            material.setAmbient(0.8f, 0.8f, 0.8f);//조명

            for (ARMeshNode meshNode : arModelImporter.getMeshNodes()) {
                meshNode.setMaterial(material);
            }
            node3d.scaleByUniform(1.0f);
        }
        else if (dbManager.textureCount>1){
            //snake, animation
            arModelImporter.loadFromAsset(dbManager.contentFileName);

            node3d = arModelImporter.getNode();
            node3d.setName("somthing");
            node3d.rotateByDegrees(90.0f,1.0f,0.0f,0.0f);
            node3d.rotateByDegrees(180.0f,1.0f,100.0f,0.0f);

//            ARTexture2D texture2D_1 = new ARTexture2D();

//            texture2D_1.loadFromAsset(dbManager.contentTextureFiles[0]);
//            ARLightMaterial material1 = new ARLightMaterial();
//            material1.setTexture(texture2D_1);
//            material1.setColour(1,1,1);
//            material1.setAmbient(0.8f,0.8f,0.8f);
//            material1.setName("");

            ARTexture2D[] texture2DS = new ARTexture2D[dbManager.textureCount];
            ARLightMaterial[] material = new ARLightMaterial[dbManager.textureCount];

            int i = 0;
            HashMap<String,ARLightMaterial> materialMap = new HashMap();

            for(i=0;i<dbManager.textureCount;i++){
                Log.d("null i",i+"");
                Log.d("null value",dbManager.contentTextureFiles[i]);
                texture2DS[i] = new ARTexture2D();
                texture2DS[i].loadFromAsset(dbManager.contentTextureFiles[i]);
                material[i] = new ARLightMaterial();
                material[i].setTexture(texture2DS[i]);
                material[i].setColour(1,1,1);
                material[i].setAmbient(0.8f,0.8f,0.8f);
                material[i].setName(dbManager.contentTextureNames[i]);
                materialMap.put(material[i].getName(),material[i]);
            }
//            i = 0;


            for(ARMeshNode meshNode : arModelImporter.getMeshNodes()){

               String name = meshNode.getName();
                meshNode.setMaterial(materialMap.get(name));

            }
            node3d.scaleByUniform(1.0f);
        }

        imageTrackable.getWorld().addChild(node3d);
        imageTrackable.addListener(new ARImageTrackableListener() {
            @Override
            public void didDetect (ARImageTrackable arImageTrackable){
                if(dbManager.contentHasAnimation){
                    node3d.play();
                }
            }

            @Override
            public void didTrack (ARImageTrackable arImageTrackable){

            }

            @Override
            public void didLose (ARImageTrackable arImageTrackable){
                if(dbManager.contentHasAnimation){
                    node3d.pause();
                }
            }
        });
    }
}


