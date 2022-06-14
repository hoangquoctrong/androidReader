package com.example.androidreader.Apdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidreader.Activity.Content;
import com.example.androidreader.Activity.DetailManga;
import com.example.androidreader.DAO.MangaDAO;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaChapter;
import com.example.androidreader.Model.MangaData;
import com.example.androidreader.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChapterRecyclerViewAdapter  extends RecyclerView.Adapter<ChapterRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<MangaChapter> chapters;
    private String mangaURL;
    private MangaData mangaData;


    public ChapterRecyclerViewAdapter(Context mContext, List<MangaChapter> chapters, MangaData mangaData) {
        this.mContext = mContext;
        this.chapters = chapters;
        this.mangaData = mangaData;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_chapter;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_chapter = (TextView) itemView.findViewById(R.id.txt_chapter);
        }
    }

    @NonNull
    @Override
    public ChapterRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.chapter_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String name = chapters.get(position).getChapterName();
        holder.txt_chapter.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    MangaDAO mangaDAO = new MangaDAO(mContext);
                    System.out.println("Data before update:" +  mangaData.getLinkURL());

                    mangaDAO.checkDatabase();
                    mangaData.setDate(Calendar.getInstance().getTime());
                    mangaData.setChapterName(chapters.get(position).getChapterName());
                    mangaData.setChapterURL(chapters.get(position).getChapterURL());
                    mangaData.setChapterIndex(position);
                    mangaDAO.EditManga(mangaData);
                    System.out.println("Data after update:" +  mangaData.getLinkURL());
                    Intent intent = new Intent(mContext, Content.class);
                    intent.putExtra("chapters", (Serializable) chapters);
                    intent.putExtra("position",position);
                    intent.putExtra("data",mangaData);

                    mContext.startActivity(intent);
                }
                catch (SQLiteException e)
                {
                    e.printStackTrace();
                    System.out.println("Somthing is wrong in chapter");
                }

                Toast.makeText(mContext, chapters.get(position).getChapterURL(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }



}
