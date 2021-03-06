package com.example.androidreader.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidreader.Apdapter.ContentPagerAdapter;
import com.example.androidreader.Apdapter.HomeRecyclerViewAdapter;
import com.example.androidreader.DAO.MangaDAO;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaChapter;
import com.example.androidreader.Model.MangaData;
import com.example.androidreader.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Content extends AppCompatActivity {

    List<MangaChapter> mangaChapterList;
    int position;
    String source;
    List<String> contentList = new ArrayList<>();

    //View object
    ViewPager contentVP;
    ProgressBar contentPB;
    View back,next;
    MangaData mangaData;
    TextView chapterTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init view
        setContentView(R.layout.activity_content);
        Intent intent = getIntent();
        mangaChapterList = (List<MangaChapter>) intent.getSerializableExtra("chapters");
        position = intent.getIntExtra("position",0);
        mangaData = (MangaData) intent.getSerializableExtra("data");
        chapterTxt = findViewById(R.id.content_txt);
        back = findViewById(R.id.chapter_back);
        next = findViewById(R.id.chapter_next);
        contentVP = findViewById(R.id.content_VP);
        contentPB = findViewById(R.id.content_progress);
        chapterTxt.setText(mangaChapterList.get(position).getChapterName());

        //Get link source like "https://truyentranh.net/onepunch-man" it will get "https://truyentranh.net/"
        source = mangaChapterList.get(position).getChapterURL().split(".net")[0] + (".net/");


        //To previous chapter button
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
                    saveHistory();
                    contentList.clear();
                    new RetrieveData().execute();
                }
            }
        });

        //To next chapter button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == mangaChapterList.size() - 1)
                {
                    Toast.makeText(Content.this, "You are reading last chapter", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    position++;
                    saveHistory();
                    contentList.clear();
                    new RetrieveData().execute();
                }
            }
        });

        new RetrieveData().execute();

    }

    //Save data into history
    void saveHistory()
    {
        try
        {
            MangaDAO mangaDAO = new MangaDAO(getApplicationContext());
            mangaDAO.checkDatabase();
            mangaData.setDate(Calendar.getInstance().getTime());
            mangaData.setChapterName(mangaChapterList.get(position).getChapterName());
            mangaData.setChapterURL(mangaChapterList.get(position).getChapterURL());
            mangaData.setChapterIndex(position);
            mangaDAO.EditManga(mangaData);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            System.out.println("Somthing is wrong in chapter");
        }
    }


    //Scrape Data from content home
    void FetchManga() throws IOException {
        Document doc = Jsoup.connect(mangaChapterList.get(position).getChapterURL()).userAgent("Mozilla").get();

        System.out.println("Source: " + source);
        //Check source to start scraping
        switch (source)
        {
            case "https://truyentranh.net/":
            {
                Elements datas = doc.select("div.page-chapter > img");
                for (Element data : datas)
                {
                    Element imgData = data.getElementsByTag("img").get(0);
                    contentList.add(imgData.attr("src"));
                }
                System.out.println("Rungning truyentranh");
                break;
            }
            default:
            {
                Elements datas = doc.select("div.reading-content > div.page-break > img");
                for (Element data : datas)
                {
                    contentList.add(data.attr("src"));
                }
                System.out.println("Running saytruyen");
                break;
            }
        }



    }

    //AsyncTask to show loading while scraping data
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
            //Show data after scraping successfully
            super.onPostExecute(unused);
            System.out.println("contents " + contentList.toString());
            ContentPagerAdapter adapter = new ContentPagerAdapter(getApplicationContext(),contentList);
            contentVP.setAdapter(adapter);
            chapterTxt.setText(mangaChapterList.get(position).getChapterName());
            contentPB.setVisibility(View.GONE);
            contentVP.setVisibility(View.VISIBLE);
        }
    }}