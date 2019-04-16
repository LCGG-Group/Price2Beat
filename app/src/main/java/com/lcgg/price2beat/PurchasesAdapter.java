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
import android.widget.Toast;

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
    FirebaseDatabase database;
    DatabaseReference refTransactions;

    String referenceId;

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

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        refTransactions = database.getReference("Transactions").child(auth.getUid()).child("pay");

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Purchases p = purchases.get(position);

        holder.title.setText(p.getItem());
        holder.amount.setText(String.valueOf(p.getAmount()));
        Picasso.get().load(p.getImageURL()).into(holder.thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBox("Unclaimed Purchase","This item is to be claimed", p.getRefNumber());
            }
        });

        referenceId = p.getRefNumber();
    }

    private void alertBox(String title, String message, String refId){
        referenceId = refId;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_alert, null);

        TextView alerTitle = (TextView) dialogView.findViewById(R.id.titleAlert);
        alerTitle.setText(title);

        TextView alert = (TextView) dialogView.findViewById(R.id.txtAlert);
        alert.setText(message);

        builder.setView(dialogView);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //get fragment
                refTransactions.addListenerForSingleValueEvent(valueEventListenerTransaction);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private ValueEventListener valueEventListenerTransaction = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            String cTransId = database.getReference().push().getKey().replace("-","").replace("_","");

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String cTransDate = df.format(c);

            refTransactions.child(referenceId).child("claimed").setValue(true);
            refTransactions.child(referenceId).child("claimedTransId").setValue(cTransId);
            refTransactions.child(referenceId).child("claimedDate").setValue(cTransDate);

            Intent i = new Intent(mContext, MainActivity.class);
            mContext.startActivity(i);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public int getItemCount() {
        return purchases.size();
    }
}
