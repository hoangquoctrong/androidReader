package com.example.androidreader.Fragment;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.androidreader.Apdapter.HomeRecyclerViewAdapter;
import com.example.androidreader.DAO.MangaDAO;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaData;
import com.example.androidreader.R;

import java.util.List;


public class FavoriteFragment extends Fragment {

    //View object
    RecyclerView recyclerView;
    SwipeRefreshLayout refresh;
    HomeRecyclerViewAdapter homeAdapter;

    //List manga favorite and mangaDAO to get data
    List<MangaData> manga;
    MangaDAO mangaDAO;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize data
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.favorite_rv);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.favorite_Refresh);
        //Get data, catch if something wrong with the database
        try {
            mangaDAO = new MangaDAO(getActivity());
            mangaDAO.checkDatabase();
            manga = mangaDAO.getAllFavorite();
            System.out.println("manga size: " + manga.size());
            homeAdapter = new HomeRecyclerViewAdapter(getActivity(),manga);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
            recyclerView.setAdapter(homeAdapter);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            System.out.println("There's something wrong in favorite!");
        }
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Refresh data
                homeAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}