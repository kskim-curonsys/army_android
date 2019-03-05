package com.curonsys.army_android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.curonsys.army_android.R;
import com.curonsys.army_android.model.ContentModel;
import com.curonsys.army_android.util.SharedDataManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTrackableListener;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;
import eu.kudan.kudan.ARTextureMaterial;
import eu.kudan.kudan.ARVideoNode;
import eu.kudan.kudan.ARVideoTexture;


public class MarkerTestActivity extends ARActivity {
    SharedDataManager mSDManager = SharedDataManager.getInstance();
    String path;
    SeekBar scale_seekBar;
    ImageButton uxbtn,dxbtn,uybtn,dybtn,uzbtn,dzbtn;
    float scaleValue = 1.0f;
    ARModelNode node3d;
    ARVideoNode videoNode;
    ARImageNode imageNode;
    Button completeBtn;
    float setScale = 3.0f ,setRotateX = 1.0f, setRotateY = 1.0f, setRotateZ = 1.0f;
    String nodeType;

    ContentModel contentModel;
    ArrayList<String> textures = new ArrayList<String>();
    Vibrator vibrator;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        //getContentsFiles();
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        try{
            getActionBar().hide();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        contentModel = (ContentModel) getIntent().getSerializableExtra("info");
        Log.e("model_path",contentModel.getModel());
        Log.e("texture_path",contentModel.getTextures().toString());
        setContentView(R.layout.activity_marker_test);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        //for 3d contents
        showLayout();
    }

    private void showLayout() {
        scale_seekBar = findViewById(R.id.scaleSeekbar);
        uxbtn = findViewById(R.id.arrow_ux);
        dxbtn = findViewById(R.id.arrow_dx);
        uybtn = findViewById(R.id.arrow_uy);
        dybtn = findViewById(R.id.arrow_dy);
        uzbtn = findViewById(R.id.arrow_uz);
        dzbtn = findViewById(R.id.arrow_dz);

        completeBtn  = findViewById(R.id.completeBtn);
        scale_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("seekbar Value",progress+"");
                scaleValue = Float.parseFloat(String.valueOf(progress))/50;
                scaleValue = scaleValue * scaleValue;
                Log.d("scaleValue",scaleValue+"");
//              Vector3f scales = node3d.getScale();
                float newX;
                float newY;
                float newZ;
                newX = 1 * scaleValue;
                newY = 1 * scaleValue;
                newZ = 1 * scaleValue;
                setScale = newX;
                switch (nodeType){
                    case "3d":
                        node3d.setScale(newX,newY,newZ);
                        break;
                    case "video":
                        videoNode.setScale(newX,newY,newZ);
                        break;
                    case "image":
                        imageNode.setScale(newX,newY,newZ);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        uxbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRotateX++;
                //node3d.rotateByDegrees(45,1, 0, 0);
                switch (nodeType){
                    case "3d":
                        node3d.rotateByDegrees(45,1, 0, 0);
                        break;
                    case "video":
                        videoNode.rotateByDegrees(45,1, 0, 0);
                        break;
                    case "image":
                        imageNode.rotateByDegrees(45,1, 0, 0);
                        break;
                }
            }
        });
        dxbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setRotateX==0){
                    setRotateX = 8;
                }
                setRotateX--;
                //node3d.rotateByDegrees(-45,1, 0, 0);
                switch (nodeType){
                    case "3d":
                        node3d.rotateByDegrees(-45,1, 0, 0);
                        break;
                    case "video":
                        videoNode.rotateByDegrees(-45,1, 0, 0);
                        break;
                    case "image":
                        imageNode.rotateByDegrees(-45,1, 0, 0);
                        break;
                }

            }
        });
        uybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRotateY++;
                switch (nodeType){
                    case "3d":
                        node3d.rotateByDegrees(45,0, 1, 0);
                        break;
                    case "video":
                        videoNode.rotateByDegrees(45,0, 1, 0);
                        break;
                    case "image":
                        imageNode.rotateByDegrees(45,0, 1, 0);
                        break;
                }
            }
        });
        dybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setRotateY==0){
                    setRotateY = 8;
                }
                setRotateY--;
                switch (nodeType){
                    case "3d":
                        node3d.rotateByDegrees(-45,0, 1, 0);
                        break;
                    case "video":
                        videoNode.rotateByDegrees(-45,0, 1, 0);
                        break;
                    case "image":
                        imageNode.rotateByDegrees(-45,0, 1, 0);
                        break;
                }
            }
        });
        uzbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRotateZ++;
                switch (nodeType){
                    case "3d":
                        node3d.rotateByDegrees(45,0, 0, 1);
                        break;
                    case "video":
                        videoNode.rotateByDegrees(45,0, 0, 1);
                        break;
                    case "image":
                        imageNode.rotateByDegrees(45,0, 0, 1);
                        break;
                }
            }
        });
        dzbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setRotateZ==0){
                    setRotateZ = 8;
                }
                setRotateZ--;
//                node3d.rotateByDegrees(-45,0, 0, 1);
                switch (nodeType){
                    case "3d":
                        node3d.rotateByDegrees(-45,0, 0, 1);
                        break;
                    case "video":
                        videoNode.rotateByDegrees(-45,0, 0, 1);
                        break;
                    case "image":
                        imageNode.rotateByDegrees(-45,0, 0, 1);
                        break;
                }
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRotateX = setRotateX%8;
                setRotateY = setRotateY%8;
                setRotateZ = setRotateZ%8;
                //Toast.makeText(getApplicationContext(),setRotateX+""+','+setRotateY+""+','+setRotateZ+"",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();

                intent.putExtra("scale",String.valueOf(setScale));
                intent.putExtra("rotateX",String.valueOf(setRotateX));
                intent.putExtra("rotateY",String.valueOf(setRotateY));
                intent.putExtra("rotateZ",String.valueOf(setRotateZ));

                setResult(1,intent);
                finish();
            }
        });
    }

    @Override
    public void setup() {
        path = mSDManager.imageURI.toString();
        if(mSDManager.is3D){
            nodeType="3d";
            add3DNode();
            Log.e("this","3d");
        }else {
            String extension = contentModel.getModel().substring(contentModel.getModel().lastIndexOf('.'));
            Log.e("extension",extension);
            if(extension.equals(".mp4")){
                addVideoNode();
                nodeType="video";
                Log.e("this","video");
            }else {
                nodeType="image";
                addImageNode();
                Log.e("this","image");
            }
        }
    }
    public void addVideoNode(){
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
            Toast.makeText(getApplicationContext(), getString(R.string.register_marker_first), Toast.LENGTH_LONG).show();
        }

        // Get the single instance of the image tracker.
        ARImageTracker imageTracker;
        //인스턴스생성
        imageTracker = ARImageTracker.getInstance();
        //트래커 초기화
        imageTracker.initialise();
        imageTracker.addTrackable(imageTrackable);

        final ARVideoTexture videoTexture = new ARVideoTexture();
        videoTexture.loadFromPath(contentModel.getModel());

        // Initialise video node with video texture
        videoNode = new ARVideoNode(videoTexture);

        //Add video node to image trackable
        imageTrackable.getWorld().addChild(videoNode);

        // Video scale
        float scale = imageTrackable.getWidth() / videoTexture.getWidth();
        videoNode.scaleByUniform(scale);
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
                if(true) {
                }
            }

            @Override
            public void didLose (ARImageTrackable arImageTrackable){
                if(true){
                }
            }
        });

    }
    public void addImageNode(){
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
            Toast.makeText(getApplicationContext(), getString(R.string.register_marker_first), Toast.LENGTH_LONG).show();
        }

        // Get the single instance of the image tracker.
        ARImageTracker imageTracker;
        //인스턴스생성
        imageTracker = ARImageTracker.getInstance();
        //트래커 초기화
        imageTracker.initialise();
        imageTracker.addTrackable(imageTrackable);

        imageNode = new ARImageNode().initWithPath(contentModel.getModel());

        // Add image node to image trackable
        imageTrackable.getWorld().addChild(imageNode);

        // Image scale
        ARTextureMaterial textureMaterial = (ARTextureMaterial)imageNode.getMaterial();
        float scale = imageTrackable.getWidth() / textureMaterial.getTexture().getWidth();
        imageNode.scaleByUniform(scale);

        // Hide image node
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
                if(true) {
                }
            }

            @Override
            public void didLose (ARImageTrackable arImageTrackable){
                if(true){
                }
            }
        });
    }

    public void add3DNode(){
        ARImageTrackable imageTrackable;
        //이름지정
        imageTrackable = new ARImageTrackable("MarkerForAR");
        //에셋에서 이미지 로딩
        //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "kudan");
        if (path != null) {
            Uri uri = Uri.parse(path);
            imageTrackable.loadFromPath(uri.getPath());
            Log.d("marker_path",uri.getPath());
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.register_marker_first), Toast.LENGTH_LONG).show();
        }
        //imageTrackable.loadFromAsset("lego.jpg");

        // Get the single instance of the image tracker.
        ARImageTracker imageTracker;
        //인스턴스생성
        imageTracker = ARImageTracker.getInstance();
        //트래커 초기화
        imageTracker.initialise();
        imageTracker.addTrackable(imageTrackable);

        ARModelImporter arModelImporter = new ARModelImporter();
        arModelImporter.loadFromPath(contentModel.getModel());
        //arModelImporter.loadFromAsset("ben.jet");

        //model's file name
        textures = contentModel.getTextures();

        if(textures.size()<2){
//          arModelImporter.loadFromAsset(mSDManager.contentFileName);
            //model info[0]
            node3d = arModelImporter.getNode();
            node3d.setName("somthing");
//          node3d.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
//          node3d.rotateByDegrees(180.0f, 1.0f, 100.0f, 0.0f);
            ARTexture2D texture2D = new ARTexture2D();
            if(textures.size()<2){
//              texture2D.loadFromAsset(mSDManager.contentTextureFiles[0]);
                Log.d("textures_path",textures.get(0));
                texture2D.loadFromPath(textures.get(0));
                //texture2D.loadFromAsset("bigBenTexture.png");
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
        else if (textures.size()>1){
            //snake, animation
//          arModelImporter.loadFromAsset(mSDManager.contentFileName);
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
//              texture2DS[i].loadFromAsset(mSDManager.contentTextureFiles[i]);
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
//          i = 0;

            for(ARMeshNode meshNode : arModelImporter.getMeshNodes()){
                String name = meshNode.getName();
                Log.d("mate_name",name);
                Log.d("\n",materialMap.toString());
                meshNode.setMaterial(materialMap.get(name));
            }
            node3d.scaleByUniform(3.0f);
        }

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
                if(true){
                }
            }

            @Override
            public void didLose (ARImageTrackable arImageTrackable){
                if(true){
                    node3d.pause();
                }
            }
        });
    }
}


