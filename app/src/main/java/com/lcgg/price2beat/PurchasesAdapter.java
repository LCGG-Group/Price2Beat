package com.lcgg.price2beat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.MyViewHolder>
{
    private Context mContext;
    private ArrayList<Purchases> purchases;

    FirebaseAuth auth;

    public PurchasesAdapter(Context mContext, ArrayList<Purchases> purchases) {
        this.mContext = mContext;
        this.purchases = purchases;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, amount;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.purchaseItem);
            amount = (TextView) view.findViewById(R.id.purchasePrice);
            thumbnail = (ImageView) view.findViewById(R.id.purchaseThumbnail);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_purchases, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Purchases p = purchases.get(position);

        holder.title.setText(p.getItem());
        holder.amount.setText(String.valueOf(p.getAmount()));
        Picasso.get().load(p.getImageURL()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }
}
