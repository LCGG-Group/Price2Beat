package com.lcgg.price2beat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.MyViewHolder>
{
    Context mContext;
    ArrayList<Feeds> feeds;

    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refFeeds;

    public FeedsAdapter(Context mContext, ArrayList<Feeds> feeds) {
        this.mContext = mContext;
        this.feeds = feeds;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView post;
        ImageView thumbnail, like, share;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.feedsThumbnail);
            like = (ImageView) view.findViewById(R.id.satisfied);
            share = (ImageView) view.findViewById(R.id.share);
            post = (TextView) view.findViewById(R.id.feedsPost);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_feeds, parent, false);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        refFeeds = database.getReference("Feeds");

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Feeds f = feeds.get(position);

        holder.post.setText(f.getPost());
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }
}
