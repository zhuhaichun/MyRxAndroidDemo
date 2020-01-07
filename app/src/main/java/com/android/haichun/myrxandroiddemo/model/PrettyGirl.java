package com.android.haichun.myrxandroiddemo.model;

import androidx.annotation.NonNull;

public class PrettyGirl {

    public String _id;
    public String _ns;
    public String createdAt;
    public String desc;
    public String publishedAt;
    public String source;
    public String type;
    public String url;
    public boolean used;
    public String who;

    @NonNull
    @Override
    public String toString() {
        return "PrettyGirl{" +
                "_id='" + _id + '\'' +
                ",_ns='" + _ns + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", desc='" + desc + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", used='" + used + '\'' +
                ", who='" + who + '\'' +
                '}';
    }
}
