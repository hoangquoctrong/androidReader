package com.example.androidreader.Model;

import java.io.Serializable;

public class MangaChapter implements Serializable,Comparable<MangaChapter> {
    private String chapterName;
    private String chapterURL;
    private int chapterID;
    MangaChapter(){}

    public MangaChapter(String chapterName, String chapterURL, int chapterID) {
        this.chapterName = chapterName;
        this.chapterURL = chapterURL;
        this.chapterID = chapterID;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterURL() {
        return chapterURL;
    }

    public void setChapterURL(String chapterURL) {
        this.chapterURL = chapterURL;
    }

    public int getChapterID() {
        return chapterID;
    }

    public void setChapterID(int chapterID) {
        this.chapterID = chapterID;
    }

    @Override
    public int compareTo(MangaChapter mangaChapter) {
        return mangaChapter.chapterID - this.chapterID ;
    }
}
