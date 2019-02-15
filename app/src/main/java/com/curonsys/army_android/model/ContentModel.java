package com.curonsys.army_android.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContentModel implements Serializable {
    private String mContentId;
    private String mSkuId;
    private String mName;
    private String mDescription;
    private boolean m3D;
    private boolean mAnimation;
    private String mModel;
    private String mThumb;
    private ArrayList<String> mTextures;

    public ContentModel() {
        mContentId = "";
        mSkuId = "";
        mName = "";
        mDescription = "";
        m3D = true;
        mAnimation = true;
        mModel = "";
        mThumb = "";
        mTextures = new ArrayList<String>();
    }

    public ContentModel(Map<String, Object> data) {
        if (data.containsKey("content_id")) {
            mContentId = (String) data.get("content_id");
        } else {
            mContentId = "";
        }

        if (data.containsKey("sku_id")) {
            mSkuId = (String) data.get("sku_id");
        } else {
            mSkuId = "";
        }

        if (data.containsKey("name")) {
            mName = (String) data.get("name");
        } else {
            mName = "";
        }

        if (data.containsKey("describe")) {
            mDescription = (String) data.get("describe");
        } else {
            mDescription = "";
        }

        if (data.containsKey("3d")) {
            m3D = (boolean) data.get("3d");
        } else {
            m3D = true;
        }

        if (data.containsKey("animation")) {
            mAnimation = (boolean) data.get("animation");
        } else {
            mAnimation = false;
        }

        if (data.containsKey("model")) {
            mModel = (String) data.get("model");
        } else {
            mModel = "";
        }

        if (data.containsKey("thumb")) {
            mThumb = (String) data.get("thumb");
        } else {
            mThumb = "";
        }

        if (data.containsKey("textures")) {
            mTextures = (ArrayList<String>) data.get("textures");
        } else {
            mTextures = new ArrayList<String>();
        }
    }

    public void setContentId(String contentid) {
        mContentId = contentid;
    }

    public void setSkuId(String skuid) {
        mSkuId = skuid;
    }

    public void setThumb(String path) {
        mThumb = path;
    }

    public void setTextures(ArrayList<String> texturesUrl) {
        mTextures = texturesUrl;
    }

    public void setModel(String modelUrl) {
        mModel = modelUrl;
    }

    public String getContentId() {
        return mContentId;
    }

    public String getSkuId() {
        return mSkuId;
    }

    public String getContentName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean get3D() {
        return m3D;
    }

    public boolean getAnimation() {
        return mAnimation;
    }

    public String getModel() {
        return  mModel;
    }

    public String getThumb() {
        return mThumb;
    }

    public ArrayList<String> getTextures() {
        return mTextures;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("content_id", mContentId);
        data.put("sku_id", mSkuId);
        data.put("name", mName);
        data.put("describe", mDescription);
        data.put("3d", m3D);
        data.put("animation", mAnimation);
        data.put("model", mModel);
        data.put("thumb", mThumb);
        data.put("textures", mTextures);

        return data;
    }
}
