package com.lcgg.price2beat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.lcgg.price2beat.Config.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.MyViewHolder>
{
    private Context mContext;
    private ArrayList<Market> markets;
    private Market mkt;
    private Wallet wallet, walletStore;
    private Transaction transaction;


    Store storeName;
    String p, it , amount, mId, referenceId, store, refDatePay;

    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refWallet, refTransactions, refPoints, refStore, refMarket;

    public MarketAdapter(Context mContext, ArrayList<Market> markets) {
        this.mContext = mContext;
        this.markets = markets;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price,qty;
        public ImageView thumbnail;
        public Button btnPay;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.marketTitle);
            price = (TextView) view.findViewById(R.id.marketPrice);
            qty = (TextView) view.findViewById(R.id.marketQtyTxt);
            thumbnail = (ImageView) view.findViewById(R.id.marketThumbnail);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relative);
            btnPay = (Button) view.findViewById(R.id.idPay);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.market_item, parent, false);
        auth = FirebaseAuth.getInstance();
        refWallet = database.getReference("Wallet");
        refMarket = database.getReference("Market");
        refTransactions = database.getReference("Transactions").child(auth.getUid()).child("pay");
        refPoints = database.getReference("Points").child(auth.getUid());

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Market m = markets.get(position);
        viewWallet();

        holder.title.setText(m.getName());
        holder.price.setText(m.getPrice().toString());
        holder.qty.setText(m.getQty().toString());
        Picasso.get().load(m.getImageURL()).into(holder.thumbnail);
        store = m.getStore();

        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBoxPay(m.getName(), m.getPrice().toString(), m.getMarketId(), wallet.getAmount().toString());
            }
        });
    }

    private void alertBox(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_alert, null);

        TextView alerTitle = (TextView) dialogView.findViewById(R.id.titleAlert);
        alerTitle.setText(title);

        TextView alert = (TextView) dialogView.findViewById(R.id.txtAlert);
        alert.setText(message);

        builder.setView(dialogView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //get fragment
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void alertBoxPay(String item, String price, String mktId, String wallet){

        it = item;
        p = price;
        mId = mktId;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = LayoutInflater.from(mContext)
                                    .inflate(R.layout.fragment_pay_amount, null);

        TextView alertTitle = (TextView) dialogView.findViewById(R.id.payItem);
        alertTitle.setText(item + ":");

        TextView alert = (TextView) dialogView.findViewById(R.id.payAmount);
        alert.setText(price);

        TextView alertWallet = (TextView) dialogView.findViewById(R.id.idWallet);
        alertWallet.setText(wallet);

        builder.setView(dialogView);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                processPay(it, p);

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

    private void viewWallet() {
        refWallet.child(auth.getUid()).addListenerForSingleValueEvent(valueEventListenerPay);
    }
    private void addTransaction() {
        refTransactions.addListenerForSingleValueEvent(valueEventListenerTransaction);
    }
    private void processPay(String name, String price) {

        if(Double.valueOf(price) > wallet.getAmount()){
            alertBox("Insufficient Funds", "Please relaod your wallet");
        }
        else
            addTransaction();
    }
    private ValueEventListener valueEventListenerPay = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            wallet = dataSnapshot.getValue(Wallet.class);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private ValueEventListener valueEventListenerTransaction = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            refDatePay = df.format(c);

            referenceId = database.getReference().push().getKey().replace("-","").replace("_","");

            refWallet.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    wallet = dataSnapshot.getValue(Wallet.class);
                    //Less Payment of Wallet owner
                    Double payment = wallet.getAmount() - Double.valueOf(p);
                    refWallet.child(auth.getUid()).child("amount").setValue(payment);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            refWallet.child(store).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    walletStore = dataSnapshot.getValue(Wallet.class);
                    //Add Payment of Wallet owner
                    Double payment = walletStore.getAmount() + Double.valueOf(p);
                    refWallet.child(store).child("amount").setValue(payment);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            refPoints.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double earnedPoints = Double.valueOf(p) / 25;
                    refPoints.child("earned").setValue(earnedPoints);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            refMarket.child(mId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mkt = dataSnapshot.getValue(Market.class);
                    refMarket.child(mId).child("qty").setValue(mkt.getQty() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            refTransactions.child(referenceId);
            refTransactions.child(referenceId).child("refNumber").setValue(referenceId);
            refTransactions.child(referenceId).child("date").setValue(refDatePay);
            refTransactions.child(referenceId).child("item").setValue(it);
            refTransactions.child(referenceId).child("amount").setValue(Double.valueOf(p));
            refTransactions.child(referenceId).child("claimed").setValue(false);

            refStore = database.getReference("Store").child(store);

            refTransactions.child(referenceId).child("payFrom").setValue(auth.getUid());
            refTransactions.child(referenceId).child("payTo").setValue(store);

            refStore.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    storeName = dataSnapshot.getValue(Store.class);

                    refTransactions.child(referenceId).child("imageURL").setValue(storeName.getImageUrl());

                    Intent intent = new Intent(mContext, TransactionActivity.class);
                    intent.putExtra("payRefId", referenceId);
                    intent.putExtra("payName", it);
                    intent.putExtra("payPrice", p);
                    intent.putExtra("payDate", refDatePay);
                    intent.putExtra("payTo",storeName.getName());
                    intent.putExtra("transferTo","Paid To:");
                    mContext.startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public int getItemCount() {
        return markets.size();
    }
}
