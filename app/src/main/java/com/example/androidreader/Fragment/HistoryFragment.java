package com.example.androidreader;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidreader.Apdapter.HistoryRecyclerViewAdapter;
import com.example.androidreader.Apdapter.HomeRecyclerViewAdapter;
import com.example.androidreader.DAO.MangaDAO;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaData;

import java.util.List;

public class HistoryFragment extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout refresh;
    HistoryRecyclerViewAdapter historyAdapter;
    List<MangaData> manga;
    MangaDAO mangaDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.history_rv);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.history_Refresh);
        try {
            mangaDAO = new MangaDAO(getActivity());
            mangaDAO.checkDatabase();
            manga = mangaDAO.getHistory();
            System.out.println("manga history size: " + manga.size());
            historyAdapter = new HistoryRecyclerViewAdapter(getActivity(),manga);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(historyAdapter);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            System.out.println("There's something wrong in favorite!");
        }
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                manga.clear();
            }
        });

        return view;
    }
}