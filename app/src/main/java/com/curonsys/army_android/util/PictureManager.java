package com.curonsys.army_android.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ijin-yeong on 2018. 7. 26..
 */

public class PictureManager {
    private static final int MY_PERMISSION_STORAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_TAKE_ALBUM = 3;
    private static final int REQUEST_IMAGE_CROP = 4;

    Context context;
    File albumFile = null;
    Uri albumURI,photoURI;
    Bitmap inputImage;

    public PictureManager(Context context){
        this.context = context;
    }

    public Intent captureCamera(){
        String state= Environment.getExternalStorageState();
        // 외장 메모리 검사
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(takePictureIntent.resolveActivity(this.context.getPackageManager())!=null){
                File photoFile=null;
                try{
                    photoFile=createImageFile();
                }catch(IOException ex){
                    Log.e("captureCamera Error",ex.toString());
                }
                if(photoFile!=null){
                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함

                    String filepath = this.context.getFilesDir().getPath();
                    String cachepath = this.context.getCacheDir().getPath();
                    String externalstoragedir = Environment.getExternalStorageDirectory().getPath();
                    String externalfiledir = this.context.getExternalFilesDir(null).getPath();
                    String cachedir = this.context.getExternalCacheDir().getPath();

                    Uri providerURI= ContentUriProvider.getUriForFile(this.context, this.context.getPackageName(), photoFile);
//                    imageUri=providerURI;

                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,providerURI);
//                    takePictureIntent.putExtra("imageUri",providerURI);
                    this.photoURI = providerURI;

                    return takePictureIntent;
//                    startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
                }
            }
        }else{
            Toast.makeText(this.context,"저장공간이 접근 불가능한 기기입니다",Toast.LENGTH_SHORT).show();
            return null;
        }
        return null;
    }

    public File createImageFile()throws IOException{
        // Create an image file name
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="JPEG_"+timeStamp+".jpg";
        File imageFile=null;
        File storageDir=new File(Environment.getExternalStorageDirectory()+"/Pictures");
        if(!storageDir.exists()){
            Log.i("mCurrentPhotoPath1",storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile=new File(storageDir,imageFileName);
//        mCurrentPhotoPath=imageFile.getAbsolutePath();

        return imageFile;
    }

    //앨범을 가져오기
    public Intent getAlbum(){
        Log.i("getAlbum","Call");
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

        return intent;
        //startActivityForResult(intent,REQUEST_TAKE_ALBUM);
    }

    // 갤러리에서 이미지 가져오기
    public void galleryAddPic(){
        Log.i("galleryAddPic","Call");
        Intent mediaScanIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화
        File f=new File(this.albumFile.getAbsolutePath());
        Uri contentUri=Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.context.sendBroadcast(mediaScanIntent);
        Toast.makeText(this.context,"사진이 앨범에 저장되었습니다.",Toast.LENGTH_SHORT).show();
    }
    // 카메라 전용 크랍
    public Intent cropImage(){
        try {
            this.albumFile = createImageFile();

        }catch (IOException e){e.printStackTrace();}

        this.albumURI=Uri.fromFile(this.albumFile);
        Log.i("cropImage","Call");
        Log.i("cropImage","photoURI : "+photoURI+" / albumURI : "+albumURI);

        Intent cropIntent=new Intent("com.android.camera.action.CROP");

        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI,"image/*");
        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기, 결과물의 크기
        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX",1); // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY",1); // crop 박스의 y축 비율
        cropIntent.putExtra("scale",true);
        cropIntent.putExtra("output",albumURI); // 크랍된 이미지를 해당 경로에 저장
        //startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        return cropIntent;
    }

    public void setPhotoURI(Uri photoURI) {
        this.photoURI = photoURI;
    }

    public void setInputImage() {
        try{
            this.inputImage=MediaStore.Images.Media.getBitmap(context.getContentResolver(),this.photoURI);
         }catch (IOException e){e.printStackTrace();}
    }
    public Uri getAlbumURI(){
        return this.albumURI;
    }
    public Uri getPhotoURI(){
        return this.photoURI;
    }
}

