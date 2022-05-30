package com.example.androidreader;

import android.graphics.Bitmap;

public class Manga {
    private String Title;
    private String CoverURL;
    private String LinkURL;

    Manga(){}

    public Manga(String title, String coverURL, String linkURL) {
        Title = title;
        CoverURL = coverURL;
        LinkURL = linkURL;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }



    public String getCoverURL() {
        return CoverURL;
    }

    public void setCoverURL(String coverURL) {
        CoverURL = coverURL;
    }

    public String getLinkURL() {
        return LinkURL;
    }

    public void setLinkURL(String linkURL) {
        LinkURL = linkURL;
    }

    @Override
    public String toString() {
        return "Manga{" +
                "Title='" + Title + '\'' +
                ", CoverURL='" + CoverURL + '\'' +
                ", LinkURL='" + LinkURL + '\'' +
                '}';
    }
}
