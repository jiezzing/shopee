package com.example.shopee.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shopee.R;
import com.example.shopee.models.OrderDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class OrderedProductAdapter extends RecyclerView.Adapter<OrderedProductViewHolder>{
    private Context context;
    private List<OrderDetail> list;

    public OrderedProductAdapter(Context context, List<OrderDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderedProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_ordered_product, viewGroup, false);
        return new OrderedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderedProductViewHolder orderedProductViewHolder, int i) {
        final OrderDetail detail = list.get(i);
        String price =  detail.getPrice();
        BigDecimal currency = new BigDecimal(price);
        orderedProductViewHolder.name.setText(detail.getName());
        orderedProductViewHolder.price.setText(String.valueOf(currency.setScale(2, RoundingMode.CEILING)));
        orderedProductViewHolder.desc.setText(detail.getDescription());
        orderedProductViewHolder.status.setText(detail.getStatus());
        Picasso.get().load(detail.getImage_uri()).into(orderedProductViewHolder.image);
        FirebaseDatabase.getInstance().getReference("User").child(detail.getSeller_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderedProductViewHolder.seller_name.setText(dataSnapshot.child("firstname").getValue(String.class) + " " + dataSnapshot.child("lastname").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class OrderedProductViewHolder extends RecyclerView.ViewHolder{
    public TextView name, price, desc, status, seller_name;
    public ImageView image;
    public OrderedProductViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.price);
        desc = itemView.findViewById(R.id.desc);
        status = itemView.findViewById(R.id.status);
        image = itemView.findViewById(R.id.image);
        seller_name = itemView.findViewById(R.id.seller_name);
    }
}
