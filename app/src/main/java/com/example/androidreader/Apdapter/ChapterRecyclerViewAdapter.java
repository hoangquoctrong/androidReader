package com.example.androidreader.Apdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaChapter;
import com.example.androidreader.R;

import java.io.Serializable;
import java.util.List;

public class ChapterRecyclerViewAdapter  extends RecyclerView.Adapter<ChapterRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<MangaChapter> chapters;

    public ChapterRecyclerViewAdapter(Context mContext, List<MangaChapter> chapters) {
        this.mContext = mContext;
        this.chapters = chapters;
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
                Intent intent = new Intent(mContext, Content.class);
                intent.putExtra("chapters", (Serializable) chapters);
                intent.putExtra("position",position);
                mContext.startActivity(intent);
                Toast.makeText(mContext, chapters.get(position).getChapterURL(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }



}
