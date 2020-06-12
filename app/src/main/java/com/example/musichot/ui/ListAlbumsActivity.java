package com.example.musichot.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.musichot.util.App;
import com.example.musichot.R;
import com.example.musichot.adapter.AlbumListAdapter;
import com.example.musichot.model.Album;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAlbumsActivity extends AppCompatActivity implements AlbumListAdapter.OnItemClickListener {
    private static final String TAG = ListAlbumsActivity.class.getSimpleName();
    private static String url = "https://apimusicn04.000webhostapp.com/Server/Album.php";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swiperefresh;
    @BindView(R.id.avi)
    ProgressBar progressBar;
    @BindView(R.id.toobar)
    Toolbar toolbar;

    RequestQueue request;
    private AlbumListAdapter adapter;
    private List<Album> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh sách album");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {

                    swiperefresh.setRefreshing(false);
                } else {
                    swiperefresh.setRefreshing(true);
                    OnLineMode();
                }
            }
        });
        albumList = new ArrayList<>();
        adapter = new AlbumListAdapter(this, albumList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        OnLineMode();
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {

                    swiperefresh.setRefreshing(false);
                } else {
                    swiperefresh.setRefreshing(true);
                    OnLineMode();
                }
            }
        });
    }


    /**
     * Adding few albums for testing
     */

    public void OnLineMode() {

        if (isNetworkAvailable()) {
            showProgress();

            OfflineMode();
            request = Volley.newRequestQueue(this);
            JsonArrayRequest movieReq = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, response.toString());
                            albumList.clear();
                            hideProgress();

                            Log.d(TAG, response.toString());
                            // This Is used store response to device acess it when user offline
                            SharedPreferences sharedPref = getSharedPreferences("SONGS", Context.MODE_PRIVATE);

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
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    OnLineMode();


                }
            });
            // Adding request to request queue
            App.getInstance().addToRequestQueue(movieReq);
        } else {
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.setAction("Offline", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            hideProgress();
            OfflineMode();
        }

    }

    public void OfflineMode() {
        albumList.clear();
        SharedPreferences sharedPref = getSharedPreferences("SONGS", Context.MODE_PRIVATE);
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

    //to hide indicators
    void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgress() {

        progressBar.setVisibility(View.GONE);
        swiperefresh.setRefreshing(false);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onItemClick(View view, Album album) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra("album",album);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.findViewById(R.id.thumbnail), album.getThumbnail());
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
