package com.example.androidreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> {


    private Context mContext;
    private List<Manga> mangas;


    public HomeRecyclerViewAdapter(Context mContext, List<Manga> mangas) {
        this.mContext = mContext;
        this.mangas = mangas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_manga,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_title.setText(mangas.get(position).getTitle());
        Glide.with(mContext).load(mangas.get(position).getCoverURL()).into(holder.manga_thumbnail);
    }

    @Override
    public int getItemCount() {
        return mangas.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_title;
        ImageView manga_thumbnail;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.manga_title_id);
            manga_thumbnail = (ImageView) itemView.findViewById(R.id.manga_cover_id);
        }
    }
}
