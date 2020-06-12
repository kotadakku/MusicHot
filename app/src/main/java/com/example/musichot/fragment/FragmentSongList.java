package com.example.musichot.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.musichot.util.App;
import com.example.musichot.R;
import com.example.musichot.adapter.SongAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dm.audiostreamer.MediaMetaData;

public class FragmentSongList extends Fragment {
    private static String url = "https://apimusicn04.000webhostapp.com/Server/SongListHot.php?sl=10";
    MediaMetaData mediaMetaData;
    View view;
    RecyclerView recyclerView;
    SongAdapter adapter;
    ArrayList<MediaMetaData> mSongList = new ArrayList<MediaMetaData>();
    RequestQueue request;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_song, container, false);
        recyclerView =(RecyclerView) view.findViewById(R.id.recycler_view);


       OnLineMode();

        adapter = new SongAdapter(mSongList, view.getContext());
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
                            mSongList.clear();

                            // This Is used store response to device acess it when user offline
                            SharedPreferences sharedPref = getActivity().getSharedPreferences("SONGHOT", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.putString("SONGHOT_1", response.toString());
                            editor.commit();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);

                                    mediaMetaData = new MediaMetaData();
                                    mediaMetaData.setMediaArt(obj.getString("image"));
                                    mediaMetaData.setMediaArtist(obj.getString("artist"));
                                    mediaMetaData.setMediaUrl(obj.getString("url"));
                                    mediaMetaData.setMediaDuration(obj.getString("duration"));
                                    mediaMetaData.setMediaTitle(obj.getString("title"));
                                    mediaMetaData.setMediaId(obj.getString("id"));

                                    mSongList.add(mediaMetaData);
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
            // Adding request to request queue
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
        mSongList.clear();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("SONGHOT", Context.MODE_PRIVATE);
        String res = sharedPref.getString("SONGHOT_1", "");
        try {
            JSONArray response = new JSONArray(res);
            for (int i = 0; i < response.length(); i++) {

                JSONObject obj = response.getJSONObject(i);
                mediaMetaData = new MediaMetaData();
                mediaMetaData.setMediaArt(obj.getString("image"));
                mediaMetaData.setMediaArtist(obj.getString("artist"));
                mediaMetaData.setMediaUrl(obj.getString("url"));
                mediaMetaData.setMediaDuration(obj.getString("duration"));
                mediaMetaData.setMediaTitle(obj.getString("title"));
                mediaMetaData.setMediaId(obj.getString("id"));
                mSongList.add(mediaMetaData);

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
