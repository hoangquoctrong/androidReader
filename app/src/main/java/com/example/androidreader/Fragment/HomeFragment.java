package com.example.androidreader.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.example.androidreader.Activity.MainActivity;
import com.example.androidreader.Apdapter.HomeRecyclerViewAdapter;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaData;
import com.example.androidreader.R;
import com.example.androidreader.SourceID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//This class get the newest data from the source it scarpe
public class HomeFragment extends Fragment {

    //View object
    RecyclerView recyclerView;
    ProgressBar progressIndicator;
    SwipeRefreshLayout refress;
    HomeRecyclerViewAdapter homeAdapter;
    Toolbar toolbar;
    Button srcBtn;

    //Object to check
    boolean isInit = true;
    boolean isSearching = false;

    //for searching
    String searchQuery;

    //Get page
    int page = 2;

    //manga list for app
    List<MangaData> mangas = new ArrayList<>();
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    //Menu view which include search
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu,menu);
        MenuItem item = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.clearFocus();

        //Search function
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                isSearching = true;
                progressIndicator.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                new RetrieveData().execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize View

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_id);
        progressIndicator = (ProgressBar) view.findViewById(R.id.progress_circular);
        refress = (SwipeRefreshLayout) view.findViewById(R.id.homeRefresh);
        srcBtn = (Button) view.findViewById(R.id.src_btn);
        toolbar = view.findViewById(R.id.home_appbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        //Change source button
        srcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open dialog to select source
                SourceDialogFragment srcDialog = new SourceDialogFragment();
                srcDialog.show(getActivity().getSupportFragmentManager(), "sourceDialog");
            }
        });

        //Refresh button
        refress.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshContent();
            }
        });

        //Load more data after scrolled to the end
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                System.out.println("is searching: " + isSearching);
                if(isSearching == false)
                {
                    progressIndicator.setVisibility(View.VISIBLE);
                    if (! recyclerView.canScrollVertically(1)){ //1 for down
                        new RetrieveData().execute();
                    }
                }

            }
        });


        //Retrieve data using async task
        new RetrieveData().execute();
        // Inflate the layout for this fragment
        return view;


    }


    //Get Data from source
    @RequiresApi(api = Build.VERSION_CODES.N)
    void ScarperHome() throws IOException {
        System.out.println(SourceID.source);
        mangas.clear();
        //restart page
        page = 1;
        //check source
        switch (SourceID.source)
        {
            case "https://truyentranh.net/":
            {
                Document doc = Jsoup.connect("https://truyentranh.net/").userAgent("Mozilla").get();
                Elements datas = doc.select("div.content > div.box > div.card-list > div.card > a");
                for (Element data : datas) {
                    Element imgData = data.getElementsByTag("img").get(0);
                    mangas.add(new MangaData(data.attr("title"), imgData.attr("src"), data.attr("href"), "", "", false, null, 0));
                }
                break;
            }
            //For saytruyen
            default:
            {
                Document doc = Jsoup.connect("https://saytruyen.net/").userAgent("Mozilla").get();
                Elements datas = doc.select("div.manga-content > div.row.px-2.list-item > div > div.page-item-detail > div.item-thumb.hover-details.c-image-hover > a");
                for (Element data : datas) {
                    Element imgData = data.getElementsByTag("img").get(0);
                    String imgURL;
                    if(imgData.attr("data-src").isEmpty())
                    {
                        imgURL = imgData.attr("src");
                    }
                    else
                    {
                        imgURL = imgData.attr("data-src");
                    }
                    System.out.println("Image url: " + imgURL);
                    mangas.add(new MangaData(data.attr("title"), imgURL, data.attr("href"), "", "", false, null, 0));
                }
                break;
            }
        }
    }

    //Refresh content using set isInit
    void RefreshContent()
    {
        isInit = true;
        mangas.clear();
        isSearching = false;
        new RetrieveData().execute();
    }


    //Search content by getting isSearching = true
    void SearchContent() throws IOException {
        mangas.clear();
        String search = searchQuery.replaceAll(" ", "+");
        switch (SourceID.source)
        {
            case "https://truyentranh.net/":
            {
                Document doc = Jsoup.connect("https://truyentranh.net/search?q=" + search).userAgent("Mozilla").get();
                Elements datas = doc.select("div.main-content > div.content > div.box > div.card-list > div.card > a");
                for (Element data : datas)
                {
                    Element imgData = data.getElementsByTag("img").get(0);
                    mangas.add(new MangaData(data.attr("title"),imgData.attr("src"),data.attr("href"),"","",false, null,0));
                }
                break;
            }
            default:
            {
                Document doc = Jsoup.connect("https://saytruyen.net/search?s=" + search).userAgent("Mozilla").get();
                Elements datas = doc.select("div.item-thumb.hover-details.c-image-hover > a");
                for (Element data : datas)
                {
                    Element imgData = data.getElementsByTag("img").get(0);
                    mangas.add(new MangaData(data.attr("title"),imgData.attr("src"),data.attr("href"),"","",false, null,0));
                }
                break;
            }
        }

    }

    //Load more content after scrolled to the end
    void LoadMore() throws IOException {
        page++;
        switch (SourceID.source)
        {
            case "https://truyentranh.net/":
            {
                Document doc = Jsoup.connect("https://truyentranh.net/comic-latest?page=" + page).userAgent("Mozilla").get();
                Elements datas = doc.select("div.content > div.box > div.card-list > div.card > a");
                for (Element data : datas)
                {
                    Element imgData = data.getElementsByTag("img").get(0);
                    mangas.add(new MangaData(data.attr("title"),imgData.attr("src"),data.attr("href"),"","",false, null,0));
                }
                break;
            }
            default:
            {
                Document doc = Jsoup.connect("https://saytruyen.net/?page=" + page).userAgent("Mozilla").get();
                Elements datas = doc.select("div.manga-content > div.row.px-2.list-item > div > div.page-item-detail > div.item-thumb.hover-details.c-image-hover > a");
                for (Element data : datas) {
                    Element imgData = data.getElementsByTag("img").get(0);
                    String imgURL;
                    if(imgData.attr("data-src").isEmpty())
                    {
                        imgURL = imgData.attr("src");
                    }
                    else
                    {
                        imgURL = imgData.attr("data-src");
                    }
                    System.out.println("Image url: " + imgURL);
                    mangas.add(new MangaData(data.attr("title"), imgURL, data.attr("href"), "", "", false, null, 0));
                }
                break;
            }
        }

    }

    //AsyncTask loading content
    class RetrieveData extends AsyncTask<Void, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(isSearching) // is Searching
                {
                    SearchContent();
                }
                else
                {
                    if(mangas.isEmpty()) //If mangas is Empty it will refresh data
                    {
                        ScarperHome();
                    }

                    else // else it will Load more data
                    {
                        LoadMore();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            recyclerView.setVisibility(View.VISIBLE);
            if(isInit) // if refreshing or just starting it will get from the start
            {
                homeAdapter = new HomeRecyclerViewAdapter(getActivity(),mangas);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
                recyclerView.setAdapter(homeAdapter);
                isInit = false;
            }
            else // Load more data by notifyDataSetChanged in adapter
            {
                System.out.println(mangas);
                homeAdapter.notifyDataSetChanged();
            }

            //set Refresh loading view
            if (refress.isRefreshing()) {
                refress.setRefreshing(false);
            }
            progressIndicator.setVisibility(View.GONE);
        }
    }
}