package com.example.musichot.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.musichot.ui.ListAlbumsActivity;
import com.example.musichot.util.App;
import com.example.musichot.ui.PlaylistActivity;
import com.example.musichot.R;
import com.example.musichot.adapter.AlbumAdapter;
import com.example.musichot.model.Album;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentAlbumHot extends Fragment implements AlbumAdapter.OnItemClickListener, View.OnClickListener {
    //    private static String url = "https://www.androidhunt.in/apps/json/playlist.php";
    private static String url = "https://apimusicn04.000webhostapp.com/Server/Album.php?sl=6";
    RequestQueue request;

    private AlbumAdapter adapter;
    private List<Album> albumList;
    View view;
    RecyclerView recyclerView;
    TextView txtXemThemAlbum;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album_hot, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        txtXemThemAlbum = view.findViewById(R.id.txtXemThem);

        txtXemThemAlbum.setOnClickListener(this::onClick);

        albumList = new ArrayList<>();
        adapter = new AlbumAdapter(getActivity(), albumList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::onItemClick);
        OnLineMode();
        return view;
    }
    @Override
    public void onItemClick(View view, Album album) {
        Intent intent = new Intent(getActivity(), PlaylistActivity.class);
        intent.putExtra("album",album);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view.findViewById(R.id.imageViewAlbum), album.getThumbnail());
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

    }

    public void OnLineMode() {

        if (isNetworkAvailable()) {

            OfflineMode();
            request = Volley.newRequestQueue(getActivity());
            JsonArrayRequest movieReq = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            albumList.clear();

                            // This Is used store response to device acess it when user offline
                            SharedPreferences sharedPref = getActivity().getSharedPreferences("SONGS", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.putString("SONGS_1", response.toString());
                            editor.commit();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    Album a = new Album(obj.getString("title"), obj.getString("artist"), obj.getString("image"), obj.getString("id"), obj.getString("status"));
                                    albumList.add(a);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                            adapter.notifyDataSetChanged();
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
        albumList.clear();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("SONGS", Context.MODE_PRIVATE);
        String res = sharedPref.getString("SONGS_1", "");
        try {
            JSONArray response = new JSONArray(res);
            for (int i = 0; i < response.length(); i++) {

                JSONObject obj = response.getJSONObject(i);
                Album a = new Album(obj.getString("title"), obj.getString("artist"), obj.getString("image"), obj.getString("id"), obj.getString("status"));
                albumList.add(a);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter.notifyDataSetChanged();
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


    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), ListAlbumsActivity.class);
        startActivity(i);

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
