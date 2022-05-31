package com.example.androidreader.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaChapter;
import com.example.androidreader.Model.MangaDetail;
import com.example.androidreader.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailManga extends AppCompatActivity {

    TextView titleTV, categoryTV,descriptionTV, authorTV;
    ImageView thumbnailIV;
    LinearLayout descriptionLL;
    ProgressBar progressBar;
    ToggleButton favoriteBtn;
    Manga manga;
    MangaDetail mangaDetail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_manga);

        Intent intent = getIntent();
        manga = (Manga) intent.getSerializableExtra("manga");
        titleTV = (TextView) findViewById(R.id.detail_title_TV);
        categoryTV = (TextView) findViewById(R.id.detail_category_TV);
        descriptionTV = (TextView) findViewById(R.id.detail_description_TV);
        thumbnailIV = (ImageView) findViewById(R.id.detail_image_view);
        authorTV = (TextView) findViewById(R.id.detail_author_TV);
        progressBar = (ProgressBar) findViewById(R.id.detail_progress);
        descriptionLL = (LinearLayout) findViewById(R.id.description_LL);
        favoriteBtn = (ToggleButton) findViewById(R.id.favorite_button);

        titleTV.setText(manga.getTitle());

        Glide.with(getApplication()).load(manga.getCoverURL()).into(thumbnailIV);

        new RetrieveData().execute();


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void ScarperHome() throws IOException {
        Document doc = Jsoup.connect(manga.getLinkURL()).userAgent("Mozilla").get();
        Elements details = doc.select("div.post-content > div.post-content_item > div.summary-content");
        Elements description = doc.select("div.description-summary > div.summary__content.show-more");
        Elements chapterList = doc.select("ul.list-item.box-list-chapter.limit-height > li.wp-manga-chapter > a");

        System.out.println(description.html());
        Element e= details.get(5);

        List<MangaChapter> mangaChapters= new ArrayList<>();
        for (Element chapter : chapterList)
        {
//            System.out.println(chapter.attr("title"));
//            System.out.println(chapter.attr("href"));
            mangaChapters.add(new MangaChapter(chapter.attr("title"),chapter.attr("href")));
        }

        for(int i = 0 ; i< mangaChapters.size();i++)
        {
            System.out.println(mangaChapters.get(i).getChapterName());
        }

        mangaDetail = new MangaDetail(
                manga.getTitle(),manga.getCoverURL(),
                manga.getLinkURL(), description.get(0).text(),
                details.get(5).text().replaceAll(" ", " - ") , mangaChapters,details.get(2).text());



    }

    public void onCustomToggleClick(View view) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
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

            progressBar.setVisibility(View.GONE);
            titleTV.setVisibility(View.VISIBLE);
            descriptionLL.setVisibility(View.VISIBLE);
            categoryTV.setVisibility(View.VISIBLE);
            authorTV.setVisibility(View.VISIBLE);
            favoriteBtn.setVisibility(View.VISIBLE);
        }
    }
}