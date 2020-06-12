package com.example.musichot.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.view.MenuItem;
import android.view.View;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.musichot.MainActivity;
import com.example.musichot.util.App;
import com.example.musichot.R;
import com.example.musichot.adapter.CategoryListAdapter;
import com.example.musichot.model.Category;
import com.example.musichot.model.Topic;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListCategoryActivity extends AppCompatActivity {

    Topic topic;
    RecyclerView recyclerView;
    Toolbar toolbar;
    List<Category> categoryList = new ArrayList<>();
    CategoryListAdapter adapter;
    String idtopic;
    RequestQueue request;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_category);
        toolbar =(Toolbar) findViewById(R.id.tool_bar1);


        recyclerView = findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        topic = intent.getParcelableExtra("topic");
        idtopic = topic.getId();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(topic.getName()+"");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        OnLineMode();
        adapter = new CategoryListAdapter(categoryList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new ListCategoryActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
    public void OnLineMode() {

        if (isNetworkAvailable()) {

            OfflineMode();
            request = Volley.newRequestQueue(this);
            String url = "https://apimusicn04.000webhostapp.com/Server/Category.php?idtopic="+idtopic;
            JsonArrayRequest movieReq = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            categoryList.clear();

                            // This Is used store response to device acess it when user offline
                            SharedPreferences sharedPref = getSharedPreferences("ListCategory"+idtopic, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.putString("ListCategory_1"+idtopic, response.toString());
                            editor.commit();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    Category category = new Category(obj.getString("id"), obj.getString("name"), obj.getString("image"));
                                    categoryList.add(category);
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
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), "No Internet Connection", Snackbar.LENGTH_LONG);
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
        categoryList.clear();
        SharedPreferences sharedPref = getSharedPreferences("ListCategory"+idtopic, Context.MODE_PRIVATE);
        String res = sharedPref.getString("ListCategory_1"+idtopic, "");
        try {
            JSONArray response = new JSONArray(res);
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                Category category = new Category(obj.getString("id"), obj.getString("name"), obj.getString("image"));
                categoryList.add(category);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent i = new Intent(this, ListTopicActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
