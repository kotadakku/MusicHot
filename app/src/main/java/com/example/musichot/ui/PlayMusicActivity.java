package com.example.musichot.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.musichot.MainActivity;
import com.example.musichot.R;
import com.example.musichot.fragment.FragmentDisk;
import com.example.musichot.fragment.FragmentListSongPlay;
import com.example.musichot.fragment.ViewPagerPlayMusic;
import com.example.musichot.services.OnClearFromRecentService;
import com.example.musichot.util.CreateNotification;
import com.example.musichot.util.ImageUtils;
import com.example.musichot.util.Mylistener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import dm.audiostreamer.MediaMetaData;
public class PlayMusicActivity extends AppCompatActivity implements Mylistener {
    MediaMetaData song;
    TextView txtTimesong,txtTotaltimesong, txtNameSong, txtArtistSong;
    SeekBar sktime;
    ImageView image_songBackground;
    ImageButton imgplay,imgrepeat,imgnext,imgpre,imgrandom, downArrow;
    ViewPager viewPagerplaynhac;
    static MediaPlayer mediaPlayer;
    int position = 0;
    boolean repeat = false;
    boolean checkrandom = false;
    boolean next = false;
    Boolean isPlaying=false;
    RelativeLayout slideBottomView;
    public static ArrayList<MediaMetaData> metaDataArrayList = new ArrayList<>();
    public static ViewPagerPlayMusic viewPagerPlayMusic;
    FragmentDisk fragmentDisk;
    FragmentListSongPlay fragmentListSongPlay;
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        getDataFromIntent();
        init();
        setListener();
        evenClick();
        UpdateTime();

    }

    private void setListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }
        if(metaDataArrayList.size()>0){
            new playMp3().execute(metaDataArrayList.get(position).getMediaUrl());
            imgplay.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
            CreateNotification.createNotification(PlayMusicActivity.this,  metaDataArrayList.get(position),
                    R.drawable.ic_pause_black_24dp,position, metaDataArrayList.size()-1);
            isPlaying = true;
        }
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayMusicActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case CreateNotification.ACTION_PREVIUOS:
                    previousMusic();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (isPlaying){
                        pauseMusic();
                    } else {
                        playMusic();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    nextMusic();
                    break;
            }
        }
    };
    private void init() {
        txtTimesong = findViewById(R.id.textviewtimesong);
        txtTotaltimesong = findViewById(R.id.textviewtotal);
        sktime = findViewById(R.id.seekbarsong);
        imgplay = findViewById(R.id.imagebuttonplay);
        imgrepeat = findViewById(R.id.imagebuttonrepeat);
        imgnext = findViewById(R.id.imagebuttonnext);
        imgrandom = findViewById(R.id.imagebuttonmix);
        imgpre = findViewById(R.id.imagebuttonprev);
        txtNameSong = findViewById(R.id.text_songName);
        txtArtistSong = findViewById(R.id.text_songArtist);
        downArrow = findViewById(R.id.down_arrow);
        image_songBackground = findViewById(R.id.image_songBackground);
        viewPagerplaynhac = findViewById(R.id.viewpageplaynhac);
        fragmentDisk = new FragmentDisk();
        fragmentListSongPlay = new FragmentListSongPlay();
        viewPagerPlayMusic = new ViewPagerPlayMusic(getSupportFragmentManager());
        viewPagerPlayMusic.addFragment(fragmentDisk);
        viewPagerPlayMusic.addFragment(fragmentListSongPlay);
        viewPagerplaynhac.setAdapter(viewPagerPlayMusic);
        fragmentDisk = (FragmentDisk) viewPagerPlayMusic.getItem(0);
    }
    private void getDataFromIntent(){
        Intent intent = getIntent();
        metaDataArrayList.clear();
        if(intent !=null){
            if(intent.hasExtra("song")){
                song = intent.getParcelableExtra("song");
                metaDataArrayList.add(song);


            }
            if(intent.hasExtra("songs")){
                metaDataArrayList = intent.getParcelableArrayListExtra("songs");
                position =0;
            }
            if(intent.hasExtra("index")){
                position =intent.getIntExtra("index", 0);

            }
        }
    }

    @Override
    public void getPosition(int pos) {
        position = pos;
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
        new playMp3().execute(metaDataArrayList.get(position).getMediaUrl());
        imgplay.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        CreateNotification.createNotification(PlayMusicActivity.this,  metaDataArrayList.get(position),
                R.drawable.ic_pause_black_24dp,position, metaDataArrayList.size()-1);
        setInfo(position);
        isPlaying = true;
    }


    class playMp3 extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }

        @Override
        protected void onPostExecute(String baihat) {
            super.onPostExecute(baihat);
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.setDataSource(baihat);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            TimeSong();
        }
    }


    private void TimeSong() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        txtTotaltimesong.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
        sktime.setMax(mediaPlayer.getDuration());
    }
    private void UpdateTime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    sktime.setProgress(mediaPlayer.getCurrentPosition());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                    txtTimesong.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                    handler.postDelayed(this,300);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            next = true;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        },300);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (next == true){
                    if(position < metaDataArrayList.size()){
                        imgplay.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                        position++;
                        if(repeat == true){
                            if(position == 0 ){
                                position = metaDataArrayList.size();
                            }
                            position-=1;
                        }
                        if(checkrandom == true){
                            Random random = new Random();
                            int index = random.nextInt(metaDataArrayList.size());
                            if(index == position){
                                position = index - 1;
                            }
                            position = index;
                            if(position > metaDataArrayList.size() -1){
                                position = 0 ;
                            }

                        }
                        new playMp3().execute(metaDataArrayList.get(0).getMediaUrl());
                        setInfo(0);
                        UpdateTime();
                    }
                    imgpre.setClickable(false);
                    imgnext.setClickable(false);
                    Handler handler1  = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imgpre.setClickable(true);
                            imgnext.setClickable(true);
                        }
                    },5000);

                    next = false;
                    handler1.removeCallbacks(this);
                }else {
                    handler1.postDelayed(this,1000);
                }
            }
        },1000);
    }
    private void evenClick() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(viewPagerPlayMusic.getItem(1)!=null){
                    if (metaDataArrayList.size() > 0) {
                        setInfo(position);
                        handler.removeCallbacks(this);
                    }else {
                        handler.postDelayed(this,300);
                    }
                }
            }
        },500);
        imgplay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    playMusic();
                }
            }
        });
        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMusic();
            }
        });
        imgpre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               previousMusic();
            }
        });
        imgrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatMusic();
            }
        });
        imgrandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               randomMusic();
            }
        });
        sktime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pauseMusic(){
        mediaPlayer.pause();
        imgplay.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        isPlaying = false;
        if (fragmentDisk.objectAnimator != null) {
            fragmentDisk.objectAnimator.pause();
        }
        CreateNotification.createNotification(PlayMusicActivity.this, metaDataArrayList.get(position),
                R.drawable.ic_play_arrow_black_24dp, position, metaDataArrayList.size()-1);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void playMusic(){
        mediaPlayer.start();
        isPlaying=true;
        imgplay.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        if (fragmentDisk.objectAnimator != null) {
            fragmentDisk.objectAnimator.resume();
        }
        CreateNotification.createNotification(PlayMusicActivity.this, metaDataArrayList.get(position),
                R.drawable.ic_pause_black_24dp, position, metaDataArrayList.size()-1);
    }
    public void nextMusic(){
        if((metaDataArrayList.size() > 0) && (metaDataArrayList.size() > 2)){
            if(mediaPlayer.isPlaying() || mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if(position < metaDataArrayList.size()){
                imgplay.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                position++;
                if(repeat == true){
                    if(position == 0 ){
                        position = metaDataArrayList.size()-1;
                    }
                    position-=1;
                }
                if(checkrandom == true){
                    Random random = new Random();
                    int index = random.nextInt(metaDataArrayList.size()-1);
                    if(index == position){
                        position = index + 1;
                    }
                    position = index;

                    if(position > metaDataArrayList.size() -1){
                        position = 0 ;
                    }
                }
                new playMp3().execute(metaDataArrayList.get(position).getMediaUrl());
                setInfo(position);
                UpdateTime();
            }
            imgpre.setClickable(false);
            imgnext.setClickable(false);
            Handler handler1  = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imgpre.setClickable(true);
                    imgnext.setClickable(true);
                }
            },2000);
        }
        else if(metaDataArrayList.size() == 1){
            if(mediaPlayer.isPlaying() || mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            new playMp3().execute(metaDataArrayList.get(0).getMediaUrl());
            fragmentDisk.PlayNhac(metaDataArrayList.get(0).getMediaArt());

            UpdateTime();
        }
        CreateNotification.createNotification(PlayMusicActivity.this, metaDataArrayList.get(position),
                R.drawable.ic_pause_black_24dp, position, metaDataArrayList.size()-1);
    }
    public void previousMusic(){
        if(metaDataArrayList.size() > 0) {
            if (mediaPlayer.isPlaying() || mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if (position < metaDataArrayList.size()) {
                imgplay.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                position--;
                if (position < 0) {
                    position = metaDataArrayList.size() - 1;
                }
                if (repeat == true) {

                    position += 1;
                }
                if (checkrandom == true) {
                    Random random = new Random();
                    int index = random.nextInt(metaDataArrayList.size());
                    if (index == position) {
                        position = index - 1;
                    }
                    position = index;
                }
            }
            else {
                position =0;
            }
            imgpre.setClickable(false);
            imgnext.setClickable(false);
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imgpre.setClickable(true);
                    imgnext.setClickable(true);
                }
            }, 2000);
        }
        new playMp3().execute(metaDataArrayList.get(position).getMediaUrl());
        setInfo(position);
        UpdateTime();
        CreateNotification.createNotification(PlayMusicActivity.this, metaDataArrayList.get(position),
                R.drawable.ic_pause_black_24dp, position, metaDataArrayList.size()-1);
    }
    public void randomMusic() {
        if(checkrandom == false){
            if(repeat == true){
                repeat = false;
                imgrepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                imgrepeat.setColorFilter(Color.argb(255, 255, 255, 255));
            }
            imgrandom.setColorFilter(Color.argb(100, 168, 69, 222));
            checkrandom = true;
        }
        else {
            imgrandom.setColorFilter(Color.argb(255, 255, 255, 255));
            checkrandom = false;
        }
    }
    public void repeatMusic() {
        if(repeat == false){
            if(checkrandom == true){
                checkrandom = false;
                imgrandom.setColorFilter(Color.argb(255, 255, 255, 255));
            }
            imgrepeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
            imgrepeat.setColorFilter(Color.argb(100, 168, 69, 222));
            repeat = true;
        }
        else {
            imgrepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
            imgrepeat.setColorFilter(Color.argb(255, 255, 255, 255));
            repeat = false;
        }
    }
    public void setInfo(int position){
        fragmentDisk.PlayNhac(metaDataArrayList.get(position).getMediaArt());
        txtNameSong.setText(metaDataArrayList.get(position).getMediaTitle());
        txtArtistSong.setText(metaDataArrayList.get(position).getMediaArtist());
        Picasso.with(getBaseContext()).load(metaDataArrayList.get(position).getMediaArt()).into(new Target() {

            @Override
            public void onBitmapFailed(Drawable arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onBitmapLoaded(Bitmap arg0, Picasso.LoadedFrom arg1) {
                image_songBackground.setImageBitmap(ImageUtils.fastblur(arg0, 0.1f, 10));
            }

            @Override
            public void onPrepareLoad(Drawable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }

        unregisterReceiver(broadcastReceiver);
    }
}
