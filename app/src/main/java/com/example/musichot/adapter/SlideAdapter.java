package com.example.musichot.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.musichot.R;
import com.example.musichot.model.Advertisement;
import com.example.musichot.ui.PlayMusicActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dm.audiostreamer.MediaMetaData;

public class SlideAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<Advertisement> adArrayList;
    private ArrayList<MediaMetaData> mediaMetaDataArrayList= new ArrayList<>();

    public SlideAdapter(Context context, ArrayList<Advertisement> adArrayList, ArrayList<MediaMetaData> mediaMetadata) {
        this.context = context;
        this.adArrayList = adArrayList;
        this.mediaMetaDataArrayList= mediaMetadata;
    }

    @Override
    public int getCount() {
        return mediaMetaDataArrayList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_slideshow, container, false);
        ImageView imgsong = (ImageView) view.findViewById(R.id.img_slideshow);
        ImageView background = view.findViewById(R.id.image_slideshow);
        TextView txtTitleSong = view.findViewById(R.id.txtTitleSong);
        TextView txtContent = view.findViewById(R.id.txtContent);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PlayMusicActivity.class);
                i.putExtra("songs", mediaMetaDataArrayList);
                i.putExtra("index", position);
                context.startActivity(i);

            }
        });
        MediaMetaData media = mediaMetaDataArrayList.get(position);
        Picasso.with(context).load(adArrayList.get(position).getImage()).into(background);
        Picasso.with(context).load(media.getMediaArt()).into(imgsong);
        txtTitleSong.setText(media.getMediaTitle());
        txtContent.setText(media.getMediaArtist());
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
}
