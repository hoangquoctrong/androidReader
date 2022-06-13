package com.example.androidreader.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.androidreader.Apdapter.ChapterRecyclerViewAdapter;
import com.example.androidreader.Apdapter.HomeRecyclerViewAdapter;
import com.example.androidreader.DAO.MangaDAO;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaChapter;
import com.example.androidreader.Model.MangaData;
import com.example.androidreader.Model.MangaDetail;
import com.example.androidreader.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DetailManga extends AppCompatActivity {

    TextView titleTV, categoryTV,descriptionTV, authorTV;
    ImageView thumbnailIV;
    LinearLayout descriptionLL,chapterLL;
    ProgressBar progressBar;
    ToggleButton favoriteBtn;
    RecyclerView chapterRecyclerView;
    FloatingActionButton playButton;
    MangaData manga;
    MangaDetail mangaDetail;
    MangaData mangaData;

    List<MangaChapter> mangaChapters= new ArrayList<>();
    MangaDAO mangaDAO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_manga);

        Intent intent = getIntent();
        manga = (MangaData) intent.getSerializableExtra("manga");
        titleTV = (TextView) findViewById(R.id.detail_title_TV);
        categoryTV = (TextView) findViewById(R.id.detail_category_TV);
        descriptionTV = (TextView) findViewById(R.id.detail_description_TV);
        thumbnailIV = (ImageView) findViewById(R.id.detail_image_view);
        authorTV = (TextView) findViewById(R.id.detail_author_TV);
        progressBar = (ProgressBar) findViewById(R.id.detail_progress);
        descriptionLL = (LinearLayout) findViewById(R.id.description_LL);
        chapterLL = (LinearLayout) findViewById(R.id.chapter_LL);
        favoriteBtn = (ToggleButton) findViewById(R.id.favorite_button);
        chapterRecyclerView = (RecyclerView) findViewById(R.id.recycler_chapter);
        playButton = (FloatingActionButton) findViewById(R.id.play_fab);


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mangaData = mangaDAO.getData(mangaData.getLinkURL());
                int position = mangaData.getChapterIndex();
                if(position == 0)
                {
                    try
                    {
                        mangaDAO = new MangaDAO(getApplicationContext());
                        mangaDAO.checkDatabase();
                        mangaData.setDate(Calendar.getInstance().getTime());
                        mangaData.setChapterName(mangaChapters.get(position).getChapterName());
                        mangaData.setChapterURL(mangaChapters.get(position).getChapterURL());
                        mangaData.setChapterIndex(position);
                        mangaDAO.EditManga(mangaData);
                    }
                    catch (SQLiteException e)
                    {
                        e.printStackTrace();
                        System.out.println("Somthing is wrong in chapter");
                    }
                }
                Intent intent = new Intent(getApplicationContext(), Content.class);
                intent.putExtra("chapters", (Serializable) mangaChapters);
                intent.putExtra("position",position);
                intent.putExtra("data",mangaData);
                System.out.println("chapter name: " + mangaChapters.get(position).getChapterName());
                System.out.println("chapte url: " + mangaChapters.get(position).getChapterURL());
                getApplicationContext().startActivity(intent);
            }
        });

        titleTV.setText(manga.getTitle());


        Glide.with(getApplication()).load(manga.getCoverURL()).into(thumbnailIV);
        new RetrieveData().execute();

        try{
            mangaDAO = new MangaDAO(getApplicationContext());
            mangaDAO.checkDatabase();
        }
        catch(SQLiteException e)
        {
            e.printStackTrace();
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void ScarperHome() throws IOException {
        Document doc = Jsoup.connect(manga.getLinkURL()).userAgent("Mozilla").get();
        Elements details = doc.select("div.detail-banner-info > ul > li");
        Elements description = doc.select("div.detail-manga-intro");
        Elements chapterList = doc.select("div.chapter-list-item-box > div.chapter-select > a");

        int i = 0;
        for (Element chapter : chapterList)
        {
            mangaChapters.add(new MangaChapter(chapter.text(),chapter.attr("href"),i));
            i++;
        }
        mangaData = new MangaData(manga.getTitle(),manga.getCoverURL(),manga.getLinkURL(),"","",false, Calendar.getInstance().getTime(),0);
        try {
            if(!mangaDAO.checkExist(manga.getLinkURL()))
            {
                mangaDAO.addOne(mangaData);
            }
            mangaData = mangaDAO.getData(manga.getLinkURL());
            favoriteBtn.setChecked(mangaData.isFavorited());
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        Collections.sort(mangaChapters);

        mangaDetail = new MangaDetail(
                manga.getTitle(),manga.getCoverURL(),
                manga.getLinkURL(), description.get(0).text(),
                details.get(0).text().replaceAll(" ", " - "), mangaChapters,details.get(2).child(1).text());
    }

    public void onCustomToggleClick(View view) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        System.out.println("Link URL: " + mangaData.getLinkURL());
        boolean Favorite = mangaData.isFavorited();
        mangaData.setFavorited(!Favorite);
        mangaDAO.EditManga(mangaData);
        MangaData checkData = mangaDAO.getData(manga.getLinkURL());
        System.out.println("Check data: " + checkData.isFavorited());
    }


    class RetrieveData extends AsyncTask<Void, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                ScarperHome();

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
            titleTV.setText(mangaDetail.getTitle());
            descriptionTV.setText(mangaDetail.getDescription());
            categoryTV.setText("Thể loại: " + mangaDetail.getCategory());
            authorTV.setText("Tác giả: " + mangaDetail.getArtist());

            System.out.println("chapters: " + mangaChapters.toString());



            ChapterRecyclerViewAdapter chapterAdapter = new ChapterRecyclerViewAdapter(getApplicationContext(),mangaChapters,manga.getLinkURL());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            chapterRecyclerView.setLayoutManager(mLayoutManager);
            chapterRecyclerView.setAdapter(chapterAdapter);
            chapterRecyclerView.setNestedScrollingEnabled(false);


            progressBar.setVisibility(View.GONE);
            chapterRecyclerView.setVisibility(View.VISIBLE);
            chapterLL.setVisibility(View.VISIBLE);
            titleTV.setVisibility(View.VISIBLE);
            descriptionLL.setVisibility(View.VISIBLE);
            categoryTV.setVisibility(View.VISIBLE);
            authorTV.setVisibility(View.VISIBLE);
            favoriteBtn.setVisibility(View.VISIBLE);
        }
    }
}