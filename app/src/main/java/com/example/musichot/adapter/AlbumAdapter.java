package com.example.musichot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichot.R;
import com.example.musichot.model.Album;
import com.squareup.picasso.Picasso;
import java.util.List;
import butterknife.ButterKnife;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    Context context;
    List<Album> albums;
    private OnItemClickListener onItemClickListener;

    public AlbumAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_album, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       Album album = albums.get(position);
       holder.txtArtist.setText(album.getArtist());
       holder.txtNameAlbum.setText(album.getName());
       Picasso.with(context).load(album.getThumbnail()).into(holder.imageAlbum);
       holder.imageAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Album source = albums.get(position);
                String status = source.getStatus();
                if (status.contains("inactive")) {
                    String mesg = "Sorry Currently Un Available";
                    Toast.makeText(context, mesg, Toast.LENGTH_LONG).show();
                } else {
                    onItemClickListener.onItemClick(v, source);
                }

            }
        });
    }
    public void setOnItemClickListener(AlbumAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public int getItemCount() {
        return albums.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Album album);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageAlbum;
        TextView txtNameAlbum;
        TextView txtArtist;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAlbum = (ImageButton) itemView.findViewById(R.id.imageViewAlbum);
            txtNameAlbum =(TextView) itemView.findViewById(R.id.txtTitleAlbum) ;
            txtArtist =(TextView) itemView.findViewById(R.id.txtArtistAlbum) ;
            ButterKnife.bind(this, itemView);
        }
    }

}
