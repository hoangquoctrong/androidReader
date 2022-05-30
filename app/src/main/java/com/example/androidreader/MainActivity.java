package com.example.androidreader;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    List<Manga> mangas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new RetrieveData().execute();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_id);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void ScarperHome() throws IOException {
        String url = "https://saytruyen.net/";
        Document doc = Jsoup.connect("https://saytruyen.net/").userAgent("Mozilla").get();
        ArrayList<String> mangaList = new ArrayList<>();
        Elements datas = doc.select("div.manga-content > div.row.px-2.list-item > div > div.page-item-detail > div.item-thumb.hover-details.c-image-hover > a ");
        Elements mangaURLs = doc.select("div.manga-content > div.row.px-2.list-item > div > div.page-item-detail > div.item-thumb.hover-details.c-image-hover > a");
        for (Element data : datas)
        {
            Element imgData = data.getElementsByTag("img").get(0);
            imgData.toString();
            if(imgData.attr("src").isEmpty())
            {
                mangas.add(new Manga(data.attr("title"),imgData.attr("data-src"),data.attr("href")));

            }
            else
            {
                mangas.add(new Manga(data.attr("title"),imgData.attr("src"),data.attr("href")));
            }
        }

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
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            HomeRecyclerViewAdapter homeAdapter = new HomeRecyclerViewAdapter(MainActivity.this,mangas);
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,3));
            recyclerView.setAdapter(homeAdapter);
        }
    }
}