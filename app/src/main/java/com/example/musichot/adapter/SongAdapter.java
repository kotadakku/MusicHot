package com.example.musichot.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichot.R;
import com.example.musichot.ui.PlayMusicActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dm.audiostreamer.MediaMetaData;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {
    private final ArrayList<MediaMetaData> songsList;
    Context context ;

    public SongAdapter(ArrayList<MediaMetaData> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_song, parent, false);
        return new SongAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MediaMetaData mediaMetaData = songsList.get(position);
        holder.titleSong.setText(mediaMetaData.getMediaTitle());
        holder.artist.setText(mediaMetaData.getMediaArtist());
        Picasso.with(context).load(mediaMetaData.getMediaArt()).into(holder.thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayMusicActivity.class);
                intent.putExtra("songs", songsList);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView titleSong, artist;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            titleSong = itemView.findViewById(R.id.song_title);
            artist = itemView.findViewById(R.id.song_artist);
        }
    }
}
