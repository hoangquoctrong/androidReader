package com.example.androidreader.Model;

import java.util.Date;

public class MangaData extends Manga{
    boolean favorited;
    String chapterURL;
    String chapterName;
    int chapterIndex;
    Date date;


    public MangaData( String chapterURL, String chapterName, boolean favorited, Date date) {
        this.favorited = favorited;
        this.chapterURL = chapterURL;
        this.chapterName = chapterName;
        this.date = date;
    }

    public MangaData(String title, String coverURL, String linkURL, String chapterURL, String chapterName,boolean favorited,Date date,int chapterIndex) {
        super(title, coverURL, linkURL);
        this.favorited = favorited;
        this.chapterURL = chapterURL;
        this.chapterName = chapterName;
        this.chapterIndex = chapterIndex;
        this.date = date;
    }


    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public String getChapterURL() {
        return chapterURL;
    }

    public void setChapterURL(String chapterURL) {
        this.chapterURL = chapterURL;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
