package com.example.androidreader.Model;

import java.util.List;

public class MangaDetail extends Manga{
    private String description;
    private String category;
    private String artist;
    private List<MangaChapter> mangaChapters;

    public MangaDetail(){}

    public MangaDetail(String description, String category, List<MangaChapter> mangaChapters,String artist) {
        this.description = description;
        this.category = category;
        this.mangaChapters = mangaChapters;
        this.artist = artist;
    }

    public MangaDetail(String title, String coverURL, String linkURL, String description, String category, List<MangaChapter> mangaChapters,String artist) {
        super(title, coverURL, linkURL);
        this.description = description;
        this.category = category;
        this.mangaChapters = mangaChapters;
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<MangaChapter> getMangaChapters() {
        return mangaChapters;
    }

    public void setMangaChapters(List<MangaChapter> mangaChapters) {
        this.mangaChapters = mangaChapters;
    }
}
