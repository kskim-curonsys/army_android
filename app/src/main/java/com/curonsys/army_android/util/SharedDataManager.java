package com.curonsys.army_android.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Leejuhwan on 2018-05-22.
 */

public class SharedDataManager {

    // step1
    public Uri imageURI = null;
    public double markerRating = 0;
    public String generatorId = null;

    // step2
    public double currentLongtitude = 0;
    public double currentLatitude = 0;
    public String currentCountryCode = "";
    public String currentLocality = "";
    public String currentThoroughfare = "";

    // step3
    public String contentId = "";
    public String contentName = "";
    public String contentFileName = "";
    public int textureCount = 0;
    public String[] contentTextureNames;
    public String[] contentTextureFiles;
    public Boolean contentHasAnimation = false;

    // step4
    public float contentScale = 0;
    public ArrayList<Float> contentRotation = new ArrayList<Float>();

    public SharedDataManager() {
    }

    private static SharedDataManager instance;
    public static SharedDataManager getInstance () {
        if (instance == null)
            instance = new SharedDataManager();
        return instance;
    }
}