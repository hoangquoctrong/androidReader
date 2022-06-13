package com.example.androidreader.Fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.androidreader.Activity.MainActivity;
import com.example.androidreader.Apdapter.HomeRecyclerViewAdapter;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaData;
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
    SwipeRefreshLayout refress;
    boolean isInit = true;
    boolean isSearching = false;
    HomeRecyclerViewAdapter homeAdapter;
    Toolbar toolbar;

    String searchQuery;
    int page = 2;
    List<MangaData> mangas = new ArrayList<>();
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu,menu);
        MenuItem item = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.clearFocus();

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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_id);
        progressIndicator = (ProgressBar) view.findViewById(R.id.progress_circular);
        refress = (SwipeRefreshLayout) view.findViewById(R.id.homeRefresh);
        toolbar = view.findViewById(R.id.home_appbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        refress.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isInit = true;
                mangas.clear();
                isSearching = false;
                new RetrieveData().execute();
            }
        });
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


        new RetrieveData().execute();
        // Inflate the layout for this fragment
        return view;


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void ScarperHome() throws IOException {
        Document doc = Jsoup.connect("https://truyentranh.net/").userAgent("Mozilla").get();
        Elements datas = doc.select("div.content > div.box > div.card-list > div.card > a");
        for (Element data : datas)
        {
            Element imgData = data.getElementsByTag("img").get(0);
            mangas.add(new MangaData(data.attr("title"),imgData.attr("src"),data.attr("href"),"","",false, null,0));
        }
    }


    void SearchContent() throws IOException {
        mangas.clear();
        String search = searchQuery.replaceAll(" ", "+");
        System.out.println(search);
        Document doc = Jsoup.connect("https://truyentranh.net/search?q=" + search).userAgent("Mozilla").get();
        Elements datas = doc.select("div.main-content > div.content > div.box > div.card-list > div.card > a");
        for (Element data : datas)
        {
            Element imgData = data.getElementsByTag("img").get(0);
            mangas.add(new MangaData(data.attr("title"),imgData.attr("src"),data.attr("href"),"","",false, null,0));
        }
        System.out.println(mangas);
    }

    void LoadMore() throws IOException {
        page++;
        Document doc = Jsoup.connect("https://truyentranh.net/comic-latest?page=" + page).userAgent("Mozilla").get();
        Elements datas = doc.select("div.content > div.box > div.card-list > div.card > a");
        for (Element data : datas)
        {
            Element imgData = data.getElementsByTag("img").get(0);
            mangas.add(new MangaData(data.attr("title"),imgData.attr("src"),data.attr("href"),"","",false, null,0));
        }
    }

    class RetrieveData extends AsyncTask<Void, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(isSearching)
                {
                    SearchContent();
                }
                else
                {
                    if(mangas.isEmpty())
                    {
                        ScarperHome();
                    }

                    else
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
            if(isInit)
            {
                homeAdapter = new HomeRecyclerViewAdapter(getActivity(),mangas);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
                recyclerView.setAdapter(homeAdapter);
                isInit = false;
            }
            else
            {
                System.out.println(mangas);
                homeAdapter.notifyDataSetChanged();
            }

            if (refress.isRefreshing()) {
                refress.setRefreshing(false);
            }
            progressIndicator.setVisibility(View.GONE);
        }
    }
}