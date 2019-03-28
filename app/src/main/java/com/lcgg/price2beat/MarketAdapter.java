package com.lcgg.price2beat;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.MyViewHolder>
{
    private Context mContext;
    private ArrayList<Market> markets;

    public MarketAdapter(Context mContext, ArrayList<Market> markets) {
        this.mContext = mContext;
        this.markets = markets;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price;
        public ImageView thumbnail;
        public Button btnPay;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.marketTitle);
            price = (TextView) view.findViewById(R.id.marketPrice);
            thumbnail = (ImageView) view.findViewById(R.id.marketThumbnail);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relative);
            btnPay = (Button) view.findViewById(R.id.idPay);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.market_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Market m = markets.get(position);

        holder.title.setText(m.getName());
        holder.price.setText(m.getPrice().toString());
        Picasso.get().load(m.getImageURL()).into(holder.thumbnail);

        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBox(m.getName(), m.getPrice().toString());
            }
        });
    }

    private void alertBox(String item, String price){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = LayoutInflater.from(mContext)
                                    .inflate(R.layout.fragment_pay_amount, null);

        TextView alertTitle = (TextView) dialogView.findViewById(R.id.payItem);
        alertTitle.setText(item);

        TextView alert = (TextView) dialogView.findViewById(R.id.payAmount);
        alert.setText(price);

        builder.setView(dialogView);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return markets.size();
    }
}
