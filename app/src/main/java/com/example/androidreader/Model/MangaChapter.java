package com.example.androidreader.Model;

public class MangaChapter {
    private String chapterName;
    private String chapterURL;
    MangaChapter(){}

    public MangaChapter(String chapterName, String chapterURL) {
        this.chapterName = chapterName;
        this.chapterURL = chapterURL;
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
}
