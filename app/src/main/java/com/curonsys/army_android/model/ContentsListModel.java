package com.curonsys.army_android.model;

public class ContentsListModel {
    String content_id;
    String content_name;
    String content_describe;
    String content_version;
    String thumbnail_url;

    public ContentsListModel(String id, String name, String describe, String version, String imgUrl) {
        this.content_id = id;
        this.content_name = name;
        this.content_describe = describe;
        this.content_version = version;
        this.thumbnail_url = imgUrl;
    }

    public ContentsListModel() {
        this.content_id = "";
        this.content_name = "";
        this.content_describe = "";
        this.content_version = "";
        this.thumbnail_url = "";
    }

    public String getId() {
        return content_id;
    }

    public String getName() {
        return content_name;
    }

    public String getDescribe() {
        return content_describe;
    }

    public String getThumbURL() {
        return thumbnail_url;
    }

    public String getVersion() {
        return "0.0.1"; //temp
    }

    public Integer getImage() {
        return 0;   //temp
    }
}
