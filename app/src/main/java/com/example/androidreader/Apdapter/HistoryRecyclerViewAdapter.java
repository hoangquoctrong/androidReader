package com.example.androidreader.Apdapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.androidreader.Activity.DetailManga;
import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaData;
import com.example.androidreader.R;

import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.MyViewHolder>{


    private Context mContext;
    private List<MangaData> mangas;


    public HistoryRecyclerViewAdapter(Context mContext, List<MangaData> mangas) {
        this.mContext = mContext;
        this.mangas = mangas;
    }

    @NonNull
    @Override
    public HistoryRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.history_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.chapter_name.setText(mangas.get(position).getTitle());
        if(mangas.get(position).getChapterName().isEmpty())
        {
            holder.current_chapter.setText("Have not start reading yet");
        }
        else
        {
            holder.current_chapter.setText(mangas.get(position).getChapterName());
        }

        CircularProgressDrawable drawable = new CircularProgressDrawable(mContext.getApplicationContext());
        drawable.setColorSchemeColors(R.color.primaryColor, R.color.purple_700, R.color.teal_700);
        drawable.setCenterRadius(30f);
        drawable.setStrokeWidth(5f);
        // set all other properties as you would see fit and start it
        drawable.start();
        GlideUrl glideUrl = new GlideUrl(mangas.get(position).getCoverURL(), new LazyHeaders.Builder()
                .addHeader("User-Agent", "Mozilla")
                .build());
        Glide.with(mContext).load(glideUrl).placeholder(drawable).into(holder.manga_thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailManga.class);
                intent.putExtra("manga", mangas.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangas.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView chapter_name;
        ImageView manga_thumbnail;
        TextView current_chapter;
        public MyViewHolder(View itemView)
        {
            super(itemView);

            chapter_name = (TextView) itemView.findViewById(R.id.chapter_name);
            manga_thumbnail = (ImageView) itemView.findViewById(R.id.history_iv);
            current_chapter = (TextView) itemView.findViewById(R.id.current_chap);
        }
    }
}
