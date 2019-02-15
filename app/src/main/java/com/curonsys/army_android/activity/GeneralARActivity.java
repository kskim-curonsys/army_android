package com.curonsys.army_android.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.curonsys.army_android.R;
import com.curonsys.army_android.model.ContentModel;
import com.curonsys.army_android.util.SharedDataManager;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARCamera;
import eu.kudan.kudan.ARCameraStream;
import eu.kudan.kudan.ARCameraStreamListener;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTrackableListener;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARNode;
import eu.kudan.kudan.ARRenderer;
import eu.kudan.kudan.ARTexture2D;
import eu.kudan.kudan.ARTextureMaterial;
import eu.kudan.kudan.ARVideoNode;
import eu.kudan.kudan.ARVideoTexture;
import eu.kudan.kudan.ARView;


public class GeneralARActivity extends ARActivity {

    ContentModel contentModel;
    SharedDataManager mDBManager;
    Vibrator vibrator;
    ARModelNode node3d;
    ARImageNode imageNode;
    ARVideoNode videoNode;
    TextView frame_tv;

    float frame_count = 0;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
            getActionBar().hide();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_general_ar);
        Log.d("ar_activity","oncreate");
        contentModel = (ContentModel) getIntent().getSerializableExtra("contents_model");
        frame_tv = findViewById(R.id.frame_tv);
        mDBManager = SharedDataManager.getInstance();
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
//        Log.d("1",contentModel.getTextures().toString());
//        Log.d("2",contentModel.getModel());
//        Log.d("3", mDBManager.imageURI.toString());
//        Log.d("4",mDBManager.contentRotation.toString());
//        Log.d("5",mDBManager.contentScale+"");

        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                // 원래 하려던 동작 (UI변경 작업 등)
                frame_count = frame_count/6;
                String frame_string = String.format("%.2f", frame_count);
                frame_tv.setText(frame_string);
                frame_count = 0;
            }
        };


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("frame_rate",frame_count+"");
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        };

        timer = new Timer();
        timer.schedule(timerTask,0, 6000);


        ARCameraStream  arCameraStream = ARCameraStream.getInstance();

        arCameraStream.addListener(new ARCameraStreamListener() {
            @Override
            public void didRecieveCameraFrame(byte[] bytes) {
                frame_count = frame_count+1;
            }
        });
    }

    @Override
    public void setup() {
        super.setup();
        Log.d("ar_activity","setup");

        Log.d("setup","called");


        String path = mDBManager.imageURI.toString();
        //step1, 추적 가능한 이미지를 등록해라. 여기서 해야 할 일은 Trackable 객체 만들기, Tracker 생성
        ARImageTrackable imageTrackable;
        //이름지정
        imageTrackable = new ARImageTrackable("MarkerForAR");
        //에셋에서 이미지 로딩
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "kudan");
        //Log.d("123123", file.getAbsolutePath());
        if (path != null) {
            Uri uri = Uri.parse(path);
            imageTrackable.loadFromPath(uri.getPath());
            Log.d("marker_path",uri.getPath());
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

        String extension = contentModel.getModel().substring(contentModel.getModel().lastIndexOf("."));
        switch (extension){
            case ".jet":
                add3dNode(imageTrackable);
                break;
            case ".jpg":
                addImageNode(imageTrackable);
                break;
            case ".png":
                addImageNode(imageTrackable);
                break;
            case ".mp4":
                addVideoNode(imageTrackable);
                break;
        }
    }

    private void addVideoNode(ARImageTrackable imageTrackable) {
        final ARVideoTexture videoTexture = new ARVideoTexture();
        videoTexture.loadFromPath(contentModel.getModel());

        // Initialise video node with video texture
        videoNode = new ARVideoNode(videoTexture);

        //Add video node to image trackable
        imageTrackable.getWorld().addChild(videoNode);

        // Video scale
        float scale = imageTrackable.getWidth() / videoTexture.getWidth();
        initRoateAndScale(videoNode);

        imageTrackable.getWorld().addChild(videoNode);
        imageTrackable.addListener(new ARImageTrackableListener() {
            @Override
            public void didDetect (ARImageTrackable arImageTrackable){
                if(true){
                    vibrator.vibrate(500);
                    videoTexture.start();
                }
            }

            @Override
            public void didTrack (ARImageTrackable arImageTrackable){

            }

            @Override
            public void didLose (ARImageTrackable arImageTrackable){
                if(true){
                }
            }
        });

    }

    private void addImageNode(ARImageTrackable imageTrackable) {
        imageNode = new ARImageNode().initWithPath(contentModel.getModel());


        // Add image node to image trackable
        imageTrackable.getWorld().addChild(imageNode);

        // Image scale
        ARTextureMaterial textureMaterial = (ARTextureMaterial)imageNode.getMaterial();
        float scale = imageTrackable.getWidth() / textureMaterial.getTexture().getWidth();

        // Hide image node
        initRoateAndScale(imageNode);
        imageTrackable.getWorld().addChild(imageNode);
        imageTrackable.addListener(new ARImageTrackableListener() {
            @Override
            public void didDetect (ARImageTrackable arImageTrackable){
                if(true){
                    vibrator.vibrate(500);
                }
            }

            @Override
            public void didTrack (ARImageTrackable arImageTrackable){

            }

            @Override
            public void didLose (ARImageTrackable arImageTrackable){
                if(true){
                }
            }
        });
    }

    private void add3dNode(ARImageTrackable imageTrackable) {
        ARModelImporter arModelImporter = new ARModelImporter();
        arModelImporter.loadFromPath(contentModel.getModel());
        //model's file name
        ArrayList<String> textures = contentModel.getTextures();

        if(textures.size()<2){

//                arModelImporter.loadFromAsset(dbManager.contentFileName);

            //model info[0]
            node3d = arModelImporter.getNode();
            node3d.setName("somthing");

//                node3d.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
//                node3d.rotateByDegrees(180.0f, 1.0f, 100.0f, 0.0f);
            ARTexture2D texture2D = new ARTexture2D();
            if(textures.size()<2){
//                    texture2D.loadFromAsset(dbManager.contentTextureFiles[0]);
                Log.d("textures_path",textures.get(0));
                texture2D.loadFromPath(textures.get(0));
            }
            //model info[1]
            final ARLightMaterial material = new ARLightMaterial();
            material.setTexture(texture2D);
            material.setColour(1, 1, 1);
            material.setAmbient(0.8f, 0.8f, 0.8f);//조명

            for (ARMeshNode meshNode : arModelImporter.getMeshNodes()) {
                meshNode.setMaterial(material);
            }

        }
        else if (textures.size()>1){
            //snake, animation
//                arModelImporter.loadFromAsset(dbManager.contentFileName);

            node3d = arModelImporter.getNode();
            node3d.setName("somthing");

            ARTexture2D[] texture2DS = new ARTexture2D[textures.size()];

            ARLightMaterial[] material = new ARLightMaterial[textures.size()];

            int i = 0;
            HashMap<String,ARLightMaterial> materialMap = new HashMap();

            for(i=0;i<textures.size();i++){
                Log.d("textures's",i+"");
                Log.d("textures_path",textures.get(i));
                texture2DS[i] = new ARTexture2D();
//                    texture2DS[i].loadFromAsset(dbManager.contentTextureFiles[i]);
                texture2DS[i].loadFromPath(textures.get(i));
                material[i] = new ARLightMaterial();
                material[i].setTexture(texture2DS[i]);
                material[i].setColour(1,1,1);
                material[i].setAmbient(0.8f,0.8f,0.8f);

                String material_name = textures.get(i).substring(textures.get(i).lastIndexOf("/")+1,textures.get(i).length()-4);
                material[i].setName(material_name);
                Log.d("material_name",material_name);
                materialMap.put(material[i].getName(),material[i]);
            }
//            i = 0;


            for(ARMeshNode meshNode : arModelImporter.getMeshNodes()){
                String name = meshNode.getName();
                Log.d("mate_name",name);
                Log.d("\n",materialMap.toString());
                meshNode.setMaterial(materialMap.get(name));

            }
        }
        initRoateAndScale(node3d);

        imageTrackable.getWorld().addChild(node3d);
        imageTrackable.addListener(new ARImageTrackableListener() {
            @Override
            public void didDetect (ARImageTrackable arImageTrackable){
                if(true){
                    node3d.play();
                    vibrator.vibrate(500);
                }
            }

            @Override
            public void didTrack (ARImageTrackable arImageTrackable){
//                mDisplay = getARView();
//                Log.d("Frame",mDisplay.toString()+"");
//                try{
//                    CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
//                    for (String cameraId : manager.getCameraIdList()) {
//                        int id = Integer.valueOf(cameraId);
//                        if (CamcorderProfile.hasProfile(id, CamcorderProfile.QUALITY_HIGH_SPEED_LOW)) {
//                            CamcorderProfile profile = CamcorderProfile.get(id, CamcorderProfile.QUALITY_HIGH_SPEED_LOW);
//                            int videoFrameRate = profile.videoFrameRate;
//                            //...
//
//                        }
//                    }
//                }catch (CameraAccessException e){
//                    e.printStackTrace();
//                }
            }

            @Override
            public void didLose (ARImageTrackable arImageTrackable){
                if(true){
                    node3d.pause();
                }
            }
        });
    }



    public void initRoateAndScale(ARNode node){
        node.scaleByUniform(mDBManager.contentScale);
        setRotate(node,String.valueOf(mDBManager.contentRotation.get(0)),"x");
        setRotate(node,String.valueOf(mDBManager.contentRotation.get(1)),"y");
        setRotate(node,String.valueOf(mDBManager.contentRotation.get(2)),"z");
    }

    public void setRotate(ARNode node, String num, String axis){
        int angle = 45;
        int count = (int)Float.parseFloat(num);
        switch (axis){
            case "x":
                for(int i=1; i<count; i++){
                    node.rotateByDegrees(angle,1, 0, 0);
                }
                break;
            case "y":
                for(int i=1; i<count; i++){
                    node.rotateByDegrees(angle,0, 1, 0);
                }
                break;
            case "z":
                for(int i=1; i<count; i++){
                    node.rotateByDegrees(angle,0, 0, 1);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
