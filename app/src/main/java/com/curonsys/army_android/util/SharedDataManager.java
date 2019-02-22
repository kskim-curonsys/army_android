package com.curonsys.army_android.util;

import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.util.Log;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Leejuhwan on 2018-05-22.
 */

public class SharedDataManager {

    //frag1
    public Uri imageURI = null;
    public Double markerRating = 0.0;
    public String generatorId = null;

    //frag2
    public double currentLongtitude = 0;
    public double currentLatitude = 0;
    public String currentCountryCode = "";
    public String currentLocality = "";
    public String currentThoroughfare = "";
    public Address currentAddress = null;

    //frag3
    public String contentId = "";
    public String contentName = "";
    public String contentFileName = "";
    public int textureCount = 0;
    public String[] contentTextureNames;
    public String[] contentTextureFiles;

    //수정!
    public Boolean is3D = true;

    //frag4
    public Double contentScale = 0.0;
    public ArrayList<Float> contentRotation = new ArrayList<Float>();

    public String phoneNumber = "";

    public SharedDataManager() {
    }

    private static SharedDataManager instance;
    public static SharedDataManager getInstance () {
        if (instance == null)
            instance = new SharedDataManager();
        return instance;
    }
}
