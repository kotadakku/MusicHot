package com.example.musichot.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichot.R;
import com.example.musichot.model.Album;
import com.example.musichot.model.Topic;
import com.example.musichot.ui.ListCategoryActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.ViewHolder> {

    private List<Topic> topicList;
    private Context context;
    private OnItemClickListener onItemClickListener;


    public TopicListAdapter(Context context, List<Topic> topicList) {
        this.context = context;
        this.topicList = topicList;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_topic, parent, false);
        return new TopicListAdapter.ViewHolder(v);
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

            final Topic m = topicList.get(position);

            holder.title.setText(m.getName());
            holder.title.setText(m.getName());
            Picasso.with(context).load(m.getThumbnail()).into(((ViewHolder) holder).imageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Topic source = topicList.get(position);
                    Intent intent = new Intent(context, ListCategoryActivity.class);
                    intent.putExtra("topic", source);
                    context.startActivity(intent);
                }
            });


    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, Album album);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.imgbackground)
        ImageView imageView;
//        @BindView(R.id.txtNameTopic)
        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgbackground);
            title = itemView.findViewById(R.id.txtNameTopic);
        }
    }

}








