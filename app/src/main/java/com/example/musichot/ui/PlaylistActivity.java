package com.example.musichot.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.musichot.R;
import com.example.musichot.adapter.SongListAdapter;
import com.example.musichot.model.Album;
import com.example.musichot.model.Category;
import com.example.musichot.model.Playlist;
import com.example.musichot.util.App;
import com.example.musichot.view.SquareImageView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import dm.audiostreamer.MediaMetaData;

public class PlaylistActivity extends AppCompatActivity {



    private SongListAdapter adapter;
    private boolean isExpand = false;
    private Context context;
    //For  Implementation
    private ArrayList<MediaMetaData> listOfSongs = new ArrayList<MediaMetaData>();
    private String title;
    private String id;
    private String thumbnail;
    String content;
    Album album;
    Playlist playlist;
    Category category;
    FloatingActionButton floatingActionButton;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    RecyclerView playList;
    ProgressBar avi;
    SquareImageView albumImage;
    TextView text_songName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.BaseTheme_playlist);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        initActivityTransitions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);
        init();
        Intent i = getIntent();
        if( i.hasExtra("album")){
            content = "idalbum";
            album = i.getParcelableExtra("album");
            title = album.getName();
            id = album.getId();
            thumbnail = album.getThumbnail();

        }
        else if(i.hasExtra("playlist")){
            content = "idplaylist";
            playlist = i.getParcelableExtra("playlist");
            title = playlist.getTitle();
            id = playlist.getId();
            thumbnail = playlist.getImage();
        }
        else if(i.hasExtra("category")){
            content = "idcategory";
            category = i.getParcelableExtra("category");
            title = category.getName();
            id = category.getId();
            thumbnail = category.getImage();

        }
        //Used  or Element View Transitions
        ViewCompat.setTransitionName(appBarLayout, thumbnail);
        supportPostponeEnterTransition();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new SongListAdapter(this, listOfSongs, title);
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        playList.setLayoutManager(mLayoutManager);
        playList.setItemAnimator(new DefaultItemAnimator());
        playList.setAdapter(adapter);
        Picasso.with(getBaseContext()).load(thumbnail).into(albumImage);
        Picasso.with(this).load(thumbnail).into(albumImage, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) albumImage.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });
            }

            @Override
            public void onError() {

            }
        });

        adapter.setOnClickListener(new SongListAdapter.SetOnclickListner() {
            @Override
            public void onClickSong(MediaMetaData song, int postion) {
                Intent intent = new Intent(PlaylistActivity.this, PlayMusicActivity.class);
                intent.putExtra("song", song);
                startActivity(intent);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistActivity.this, PlayMusicActivity.class);
                intent.putExtra("songs", listOfSongs);
                startActivity(intent);
            }
        });
        this.context = PlaylistActivity.this;
        onLoadPlaylist(id, thumbnail, content);
    }

    private void init() {
        floatingActionButton = findViewById(R.id.fab);
        appBarLayout = findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        toolbar = findViewById(R.id.tool_bar);
        playList = findViewById(R.id.playlist_layout);
        avi = findViewById(R.id.avi_loader);
        albumImage = findViewById(R.id.image_album);
        text_songName = findViewById(R.id.text_songName);
    }


    public void onLoadPlaylist(String movie, final String img, String content) {

        if (isNetworkAvailable()) {
            OfflineMode();
            if (!movie.equals("")) {
                avi.setVisibility(View.VISIBLE);



//                String songs = "https://api.soundcloud.com/playlists/" + movie + "?client_id=95f22ed54a5c297b1c41f72d713623ef";
                String songs ="https://apimusicn04.000webhostapp.com/Server/ListSongPlayList.php?"+content+"="+movie;

                JsonArrayRequest movieReq = new JsonArrayRequest(songs,
                        new Response.Listener<JSONArray>()
                        {
                    @Override
                    public void onResponse(JSONArray response) {
                        storeResponse(response.toString());
                        avi.setVisibility(View.GONE);
                        listOfSongs.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject media = response.getJSONObject(i);
                                MediaMetaData mediaMetaData = new MediaMetaData();
                                mediaMetaData.setMediaAlbum(media.getString("title"));
                                mediaMetaData.setMediaArt(media.getString("image"));
                                mediaMetaData.setMediaArtist(media.getString("artist"));

                                String songUrl = media.getString("stream_url");
                                String songtitle = media.getString("title").replaceAll(".mp3", "").replaceAll("[0-9]", "").replaceAll("\\[.*?\\]", "").replaceAll("-", "").replaceAll("_", "").replaceAll("\\.", "").replaceAll("\\p{P}", "").replaceAll("Kbps", "");
                                mediaMetaData.setMediaUrl(songUrl);
                                long duration = Long.parseLong(media.getString("duration"));
                                mediaMetaData.setMediaDuration(TimeUnit.MILLISECONDS.toSeconds(duration) + "");
                                mediaMetaData.setMediaTitle(songtitle);
                                mediaMetaData.setMediaId(songtitle);
                                listOfSongs.add(mediaMetaData);
                            }
                            // Parsing json


                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        avi.setVisibility(View.GONE);

                    }
                });
                // Adding request to request queue
                App.getInstance().addToRequestQueue(movieReq);
            }
        } else {
            OfflineMode();
            avi.setVisibility(View.GONE);
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.sliding_layout), "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.setAction("Offline", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }


    }

    private void storeResponse(String response) {
        SharedPreferences sharedPref = getSharedPreferences(title, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString(title + "123", response);
        editor.commit();
    }

    public void OfflineMode() {
        listOfSongs.clear();
        SharedPreferences sharedPref = getSharedPreferences(title, Context.MODE_PRIVATE);
        String res = sharedPref.getString(title + "123", "");
        try {
            JSONArray response = new JSONArray(res);
            for (int i = 0; i < response.length(); i++) {
                JSONObject media = response.getJSONObject(i);
                MediaMetaData mediaMetaData = new MediaMetaData();
                mediaMetaData.setMediaAlbum(media.getString("title"));
                mediaMetaData.setMediaArt(media.getString("image"));
                mediaMetaData.setMediaArtist(media.getString("artist"));

                String songUrl = media.getString("stream_url");
//                                        + "?client_id=95f22ed54a5c297b1c41f72d713623ef";
                String songtitle = media.getString("title").replaceAll(".mp3", "").replaceAll("[0-9]", "").replaceAll("\\[.*?\\]", "").replaceAll("-", "").replaceAll("_", "").replaceAll("\\.", "").replaceAll("\\p{P}", "").replaceAll("Kbps", "");
                mediaMetaData.setMediaUrl(songUrl);
                long duration = Long.parseLong(media.getString("duration"));
                mediaMetaData.setMediaDuration(TimeUnit.MILLISECONDS.toSeconds(duration) + "");
                mediaMetaData.setMediaTitle(songtitle);
                mediaMetaData.setMediaId(songtitle);
                listOfSongs.add(mediaMetaData);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        updateBackground((FloatingActionButton) findViewById(R.id.fab), palette);
        supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.accent));
        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
