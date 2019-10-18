package com.example.shopee.recyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.Seller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderViewHolder>{
    private Context context;
    private List<Seller> list;
    Boolean hasPending = false;

    public CustomerOrderAdapter(Context context, List<Seller> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CustomerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.ordered_product, viewGroup, false);
        return new CustomerOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerOrderViewHolder customerOrderViewHolder, int i) {
        final Seller seller = list.get(i);
        String price = seller.getPrice();
        BigDecimal currency = new BigDecimal(price);

        customerOrderViewHolder.name.setText(seller.getName());
        customerOrderViewHolder.price.setText(String.valueOf(currency.setScale(2, RoundingMode.CEILING)));
        customerOrderViewHolder.desc.setText(seller.getDescription());
        customerOrderViewHolder.qty.setText(seller.getQty());
        customerOrderViewHolder.subtotal.setText(seller.getSubtotal());
        Picasso.get().load(seller.getImage_uri()).into(customerOrderViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class CustomerOrderViewHolder extends RecyclerView.ViewHolder {
    public TextView name, price, desc, qty, subtotal;
    public ImageView image;
    public CustomerOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.price);
        desc = itemView.findViewById(R.id.desc);
        image = itemView.findViewById(R.id.image);
        qty = itemView.findViewById(R.id.qty);
        subtotal = itemView.findViewById(R.id.subtotal);
    }
}
