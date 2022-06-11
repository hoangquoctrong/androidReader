package com.example.androidreader.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidreader.Apdapter.ContentPagerAdapter;
import com.example.androidreader.Apdapter.HomeRecyclerViewAdapter;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaChapter;
import com.example.androidreader.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Content extends AppCompatActivity {

    List<MangaChapter> mangaChapterList;
    int position;
    List<String> contentList = new ArrayList<>();

    ViewPager contentVP;
    ProgressBar contentPB;
    View back,next;
    TextView chapterTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Intent intent = getIntent();
        mangaChapterList = (List<MangaChapter>) intent.getSerializableExtra("chapters");
        position = intent.getIntExtra("position",0);
        chapterTxt = findViewById(R.id.content_txt);
        back = findViewById(R.id.chapter_back);
        next = findViewById(R.id.chapter_next);
        contentVP = findViewById(R.id.content_VP);
        contentPB = findViewById(R.id.content_progress);

        chapterTxt.setText(mangaChapterList.get(position).getChapterName());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0)
                {
                    Toast.makeText(Content.this, "You are reading first chapter", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    position--;
                    contentList.clear();
                    new RetrieveData().execute();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == mangaChapterList.size())
                {
                    Toast.makeText(Content.this, "You are reading last chapter", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    position++;
                    contentList.clear();
                    new RetrieveData().execute();
                }
            }
        });

        new RetrieveData().execute();

    }


    void FetchManga() throws IOException {
        System.out.println("chapters: " + mangaChapterList.toString());
        System.out.println("position: " + position);
        Document doc = null;
            doc = Jsoup.connect(mangaChapterList.get(position).getChapterURL()).userAgent("Mozilla").get();

        Elements datas = doc.select("div.page-chapter > img");
        for (Element data : datas)
        {
            Element imgData = data.getElementsByTag("img").get(0);
            contentList.add(imgData.attr("src"));
        }

    }

    class RetrieveData extends AsyncTask<Void, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                FetchManga();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            System.out.println("contents " + contentList.toString());
            ContentPagerAdapter adapter = new ContentPagerAdapter(getApplicationContext(),contentList);
            contentVP.setAdapter(adapter);
            chapterTxt.setText(mangaChapterList.get(position).getChapterName());
            contentPB.setVisibility(View.GONE);
            contentVP.setVisibility(View.VISIBLE);
        }
    }}