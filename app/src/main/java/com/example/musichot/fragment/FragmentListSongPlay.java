package com.example.musichot.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichot.R;
import com.example.musichot.adapter.PlayMusicAdapter;
import com.example.musichot.ui.PlayMusicActivity;
import com.example.musichot.util.Mylistener;

public class FragmentListSongPlay extends Fragment implements PlayMusicAdapter.OnItemClickListener {
    View view;
    RecyclerView recyclerViewplaynhac;
    PlayMusicAdapter playMusicAdapter;
    PlayMusicAdapter.OnItemClickListener onItemClickListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_song_play,container,false);
        recyclerViewplaynhac = view.findViewById(R.id.recyclerviewplaybaihat);
        if (PlayMusicActivity.metaDataArrayList.size()>0){
            playMusicAdapter = new PlayMusicAdapter(getActivity(), PlayMusicActivity.metaDataArrayList);
            recyclerViewplaynhac.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerViewplaynhac.setAdapter(playMusicAdapter);
            playMusicAdapter.setOnItemClickListener(this::onItemClick);
        }
        return view;
    }

    @Override
    public void onItemClick(View view, int possition) {
        Mylistener mylistener = (Mylistener) getActivity();
        mylistener.getPosition(possition);
    }

}
