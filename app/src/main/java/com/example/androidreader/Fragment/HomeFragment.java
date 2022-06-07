package com.example.androidreader.Fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.androidreader.Activity.MainActivity;
import com.example.androidreader.Apdapter.HomeRecyclerViewAdapter;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    RecyclerView recyclerView;
    ProgressBar progressIndicator;

    int page = 2;
    List<Manga> mangas = new ArrayList<>();
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_id);
        progressIndicator = (ProgressBar) view.findViewById(R.id.progress_circular);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (! recyclerView.canScrollVertically(1)){ //1 for down
                    new RetrieveData().execute();
                }
            }
        });
        new RetrieveData().execute();
        // Inflate the layout for this fragment
        return view;


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void ScarperHome() throws IOException {

        Document doc = Jsoup.connect("https://saytruyen.net/").userAgent("Mozilla").get();
        ArrayList<String> mangaList = new ArrayList<>();
        Elements datas = doc.select("div.manga-content > div.row.px-2.list-item > div > div.page-item-detail > div.item-thumb.hover-details.c-image-hover > a ");
        for (Element data : datas)
        {
            Element imgData = data.getElementsByTag("img").get(0);
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

    void LoadMore() throws IOException {
        page++;
        Document doc = Jsoup.connect("https://saytruyen.net/?page=" + page).userAgent("Mozilla").get();
        Elements datas = doc.select("div.manga-content > div.row.px-2.list-item > div > div.page-item-detail > div.item-thumb.hover-details.c-image-hover > a ");
        for (Element data : datas)
        {
            Element imgData = data.getElementsByTag("img").get(0);
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
                if(mangas.isEmpty())
                    ScarperHome();
                else
                    LoadMore();

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
            recyclerView.setVisibility(View.VISIBLE);
            HomeRecyclerViewAdapter homeAdapter = new HomeRecyclerViewAdapter(getActivity(),mangas);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
            recyclerView.setAdapter(homeAdapter);
            progressIndicator.setVisibility(View.GONE);
        }
    }
}