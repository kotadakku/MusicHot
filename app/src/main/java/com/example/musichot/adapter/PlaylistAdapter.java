package com.example.musichot.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musichot.ui.PlaylistActivity;
import com.example.musichot.R;
import com.example.musichot.model.Playlist;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {

    public PlaylistAdapter(@NonNull Context context, int resource, @NonNull List<Playlist> objects) {
        super(context, resource, objects);
    }
    class ViewHolder{
        TextView txtNamePlaylist;
        ImageView imgBackground, imgPlaylist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_playlist, null);
            viewHolder = new ViewHolder();
            viewHolder.txtNamePlaylist = convertView.findViewById(R.id.txtNamePlaylist);
            viewHolder.imgBackground = convertView.findViewById(R.id.imgbackground);
            viewHolder.imgPlaylist = convertView.findViewById(R.id.imgPlaylist);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Playlist playlist = getItem(position);
        Picasso.with(getContext()).load(playlist.getImage()).into(viewHolder.imgBackground);
        Picasso.with(getContext()).load(playlist.getIcon()).into(viewHolder.imgPlaylist);
        viewHolder.txtNamePlaylist.setText(playlist.getTitle());
        viewHolder.imgBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PlaylistActivity.class);
                i.putExtra("playlist", playlist);
                getContext().startActivity(i);
            }
        });
        return convertView;
    }
}
