package com.example.musichot.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichot.R;
import com.example.musichot.model.Album;
import com.example.musichot.ui.PlayMusicActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

import dm.audiostreamer.MediaMetaData;

public class PlayMusicAdapter extends RecyclerView.Adapter<PlayMusicAdapter.ViewHolder>{
    Context context;
    ArrayList<MediaMetaData> metaDataArrayList;
    private OnItemClickListener onItemClickListener;
    public PlayMusicAdapter(Context context, ArrayList<MediaMetaData> metaDataArrayList){
        this.context = context;
        this.metaDataArrayList = metaDataArrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_play_song, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaMetaData baihat = metaDataArrayList.get(position);
        holder.txttenbaihat.setText(baihat.getMediaTitle());
        holder.txtcasi.setText(baihat.getMediaArtist());
        holder.txtindex.setText(position + 1 + "");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, position);
            }
        });
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int possition);
    }

    @Override
    public int getItemCount() {
        return metaDataArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtindex,txttenbaihat,txtcasi;
        public ViewHolder(View itemView){
            super(itemView);
            txtcasi = itemView.findViewById(R.id.textviewplaynhactencasi);
            txtindex = itemView.findViewById(R.id.textviewplaynhacindex);
            txttenbaihat = itemView.findViewById(R.id.textviewplaynhactenbaihat);
        }
    }
}
