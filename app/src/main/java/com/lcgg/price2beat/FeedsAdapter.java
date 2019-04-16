package com.lcgg.price2beat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.MyViewHolder>
{
    Context mContext;
    ArrayList<Feeds> feeds;
    User user;

    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refFeeds, refUser;

    public FeedsAdapter(Context mContext, ArrayList<Feeds> feeds) {
        this.mContext = mContext;
        this.feeds = feeds;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView post, profile;
        ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.feedsThumbnail);
            profile = (TextView) view.findViewById(R.id.txtProfile);
            post = (TextView) view.findViewById(R.id.feedsPost);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_feeds, parent, false);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        refFeeds = database.getReference("Feeds").child(auth.getUid());

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Feeds f = feeds.get(position);

        refUser = database.getReference("User").child(f.getUser());
        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImageURL()).into(holder.thumbnail);
                holder.profile.setText(user.getDisplayName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.post.setText(f.getPost());
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }
}
