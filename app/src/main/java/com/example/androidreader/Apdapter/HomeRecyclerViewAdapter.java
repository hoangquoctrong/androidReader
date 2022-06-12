package com.example.androidreader.Apdapter;

import android.annotation.SuppressLint;
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
import com.example.androidreader.R;

import java.io.Serializable;
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_title.setText(mangas.get(position).getTitle());
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

        holder.manga_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View  view) {
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

        TextView tv_title;
        ImageView manga_thumbnail;
        CardView manga_card;
        public MyViewHolder(View itemView)
        {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.manga_title_id);
            manga_thumbnail = (ImageView) itemView.findViewById(R.id.manga_cover_id);
            manga_card = (CardView) itemView.findViewById(R.id.manga_card_id);
        }
    }
}
