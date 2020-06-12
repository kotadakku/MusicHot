package com.example.musichot.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.musichot.util.App;
import com.example.musichot.R;
import com.example.musichot.adapter.SlideAdapter;
import com.example.musichot.model.Advertisement;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dm.audiostreamer.MediaMetaData;
import me.relex.circleindicator.CircleIndicator;

public class FragmentSlideShow extends Fragment {
    private static String url = "https://apimusicn04.000webhostapp.com/Server/Advertisement.php?sl=5";

    private View view;
    private ViewPager viewPager;
    private SlideAdapter slideAdapter;
    private CircleIndicator circleIndicator;

    private Runnable runnable;
    private Handler handler;
    private RequestQueue request;

    private ArrayList<Advertisement> advertisements = new ArrayList<>();
    private ArrayList<MediaMetaData> mSongList = new ArrayList<>();
    private MediaMetaData mediaMetaData;


    private int current=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_slide_show, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPagerSlideshow);
        circleIndicator = view.findViewById(R.id.circleview);
        OnLineMode();
        slideAdapter = new SlideAdapter(getActivity(), advertisements, mSongList);
        viewPager.setAdapter(slideAdapter);
        circleIndicator.setViewPager(viewPager);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                current = viewPager.getCurrentItem();
                current++;
                if(current>=viewPager.getAdapter().getCount()){
                    current =0;
                }
                viewPager.setCurrentItem(current, true);
                handler.postDelayed(runnable, 4500);
            }
        };
        handler.postDelayed(runnable, 4500);

        return view;
    }


    public void OnLineMode() {

        if (isNetworkAvailable()) {

            OfflineMode();
            request = Volley.newRequestQueue(getActivity());
            JsonArrayRequest movieReq = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            advertisements.clear();
                            mSongList.clear();
                            SharedPreferences sharedPref = getActivity().getSharedPreferences("ADV", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.putString("ADV_1", response.toString());
                            editor.commit();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    Advertisement advertisement= new Advertisement(obj.getString("id"), obj.getString("content"), obj.getString("idsong"),obj.getString("imageAdv") );
                                    mediaMetaData = new MediaMetaData();
                                    mediaMetaData.setMediaArt(obj.getString("image"));
                                    mediaMetaData.setMediaArtist(obj.getString("artist"));
                                    mediaMetaData.setMediaUrl(obj.getString("url"));
                                    mediaMetaData.setMediaDuration(obj.getString("duration"));
                                    mediaMetaData.setMediaTitle(obj.getString("title"));
                                    mediaMetaData.setMediaId(obj.getString("idsong"));
                                    mSongList.add(mediaMetaData);
                                    advertisements.add(advertisement);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    OnLineMode();


                }
            });
            App.getInstance().addToRequestQueue(movieReq);
        } else {
            final Snackbar snackbar = Snackbar.make(view.findViewById(R.id.main_content), "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.setAction("Offline", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            OfflineMode();
        }

    }

    public void OfflineMode() {
        advertisements.clear();
        mSongList.clear();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("ADV", Context.MODE_PRIVATE);
        String res = sharedPref.getString("ADV_1", "");
        try {
            JSONArray response = new JSONArray(res);
            for (int i = 0; i < response.length(); i++) {

                JSONObject obj = response.getJSONObject(i);
                Advertisement advertisement= new Advertisement(obj.getString("id"), obj.getString("content"), obj.getString("idsong"),obj.getString("imageAdv") );
                mediaMetaData = new MediaMetaData();
                mediaMetaData.setMediaArt(obj.getString("image"));
                mediaMetaData.setMediaArtist(obj.getString("artist"));
                mediaMetaData.setMediaUrl(obj.getString("url"));
                mediaMetaData.setMediaDuration(obj.getString("duration"));
                mediaMetaData.setMediaTitle(obj.getString("title"));
                mediaMetaData.setMediaId(obj.getString("idsong"));
                mSongList.add(mediaMetaData);
                advertisements.add(advertisement);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
