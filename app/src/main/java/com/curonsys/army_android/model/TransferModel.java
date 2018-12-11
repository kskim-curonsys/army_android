package com.curonsys.army_android.model;

import java.util.HashMap;
import java.util.Map;

public class TransferModel {
    private String mPath;
    private String mSuffix;
    private String mContentType;
    private String mName;
    private String mMD5Hash;
    private String mErrorMessage;
    private String mUserId;

    private long mSize;
    private long mCreationTimeMillis;
    private long mUpdatedTimeMillis;

    public TransferModel() {
        mPath = "";
        mSuffix = "";
        mContentType = "";
        mName = "";
        mMD5Hash = "";
        mErrorMessage = "";
        mUserId = "";
        mSize = 0;
        mCreationTimeMillis = 0;
        mUpdatedTimeMillis = 0;
    }

    public TransferModel(Map<String, Object> data) {
        if (data.containsKey("path")) {
            mPath = (String) data.get("path");
        } else {
            mPath = "";
        }

        if (data.containsKey("suffix")) {
            mSuffix = (String) data.get("suffix");
        } else {
            mSuffix = "";
        }

        if (data.containsKey("content_type")) {
            mContentType = (String) data.get("content_type");
        } else {
            mContentType = "";
        }

        if (data.containsKey("name")) {
            mName = (String) data.get("name");
        } else {
            mName = "";
        }

        if (data.containsKey("md5hash")) {
            mMD5Hash = (String) data.get("md5hash");
        } else {
            mMD5Hash = "";
        }

        if (data.containsKey("error")) {
            mErrorMessage = (String) data.get("error");
        } else {
            mErrorMessage = "";
        }

        if (data.containsKey("user_id")) {
            mUserId = (String) data.get("user_id");
        } else {
            mUserId = "";
        }

        if (data.containsKey("size")) {
            mSize = (long) data.get("size");
        } else {
            mSize = 0;
        }

        if (data.containsKey("creation_time")) {
            mCreationTimeMillis = (long) data.get("creation_time");
        } else {
            mCreationTimeMillis = 0;
        }

        if (data.containsKey("updated_time")) {
            mUpdatedTimeMillis = (long) data.get("updated_time");
        } else {
            mUpdatedTimeMillis = 0;
        }
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("path", mPath);
        data.put("suffix", mSuffix);
        data.put("content_type", mContentType);
        data.put("name", mName);
        data.put("md5hash", mMD5Hash);
        data.put("error", mErrorMessage);
        data.put("user_id", mUserId);
        data.put("size", mSize);
        data.put("creation_time", mCreationTimeMillis);
        data.put("updated_time", mUpdatedTimeMillis);

        return data;
    }

    public String getPath() {
        return mPath;
    }

    public String getSuffix() {
        return mSuffix;
    }

    public String getContentType() {
        return mContentType;
    }

    public String getName() {
        return mName;
    }

    public String getMD5Hash() {
        return mMD5Hash;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public String getUserId() {
        return mUserId;
    }

    public long getSize() {
        return mSize;
    }

    public long getCreationTimeMillis() {
        return mCreationTimeMillis;
    }

    public long getUpdatedTimeMillis() {
        return mUpdatedTimeMillis;
    }
}
