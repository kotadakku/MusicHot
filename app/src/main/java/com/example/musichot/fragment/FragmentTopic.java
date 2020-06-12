package com.example.musichot.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.musichot.util.App;
import com.example.musichot.R;
import com.example.musichot.model.Topic;
import com.example.musichot.ui.ListCategoryActivity;
import com.example.musichot.ui.ListTopicActivity;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentTopic extends Fragment {

    View view;
    HorizontalScrollView horizontalScrollView;
    TextView txtXemThem;
    ArrayList<Topic> topics = new ArrayList<>();
    private static String url = "https://apimusicn04.000webhostapp.com/Server/Topic.php?sl=6";
    RequestQueue request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frament_topic, container, false);
        horizontalScrollView = view.findViewById(R.id.horizontal);
        txtXemThem = view.findViewById(R.id.txtXemThem);
        txtXemThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListTopicActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(580,250);
        layoutParams.setMargins(10, 10, 10, 10);
        OnLineMode();
        for(int i=0; i<topics.size(); i++){
            CardView cardView = new CardView(getActivity());
            cardView.setRadius(10);
            ImageView imageView = new ImageView(getActivity());

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Picasso.with(getActivity()).load(topics.get(i).getThumbnail()).into(imageView);
            cardView.setLayoutParams(layoutParams);
            int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Topic source = topics.get(finalI);
                    Intent intent = new Intent(getActivity(), ListCategoryActivity.class);
                    intent.putExtra("topic", source);
                    getActivity().startActivity(intent);
                }
            });
            cardView.addView(imageView);
            linearLayout.addView(cardView);
        }
        horizontalScrollView.addView(linearLayout);
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
                            topics.clear();

                            // This Is used store response to device acess it when user offline
                            SharedPreferences sharedPref = getActivity().getSharedPreferences("TOPIC", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.putString("TOPIC_1", response.toString());
                            editor.commit();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    Topic a = new Topic(obj.getString("id"), obj.getString("name"), obj.getString("image"));
                                    topics.add(a);
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
        topics.clear();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("TOPIC", Context.MODE_PRIVATE);
        String res = sharedPref.getString("TOPIC_1", "");
        try {
            JSONArray response = new JSONArray(res);
            for (int i = 0; i < response.length(); i++) {

                JSONObject obj = response.getJSONObject(i);
                Topic a = new Topic(obj.getString("id"), obj.getString("name"), obj.getString("image"));
                topics.add(a);

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
