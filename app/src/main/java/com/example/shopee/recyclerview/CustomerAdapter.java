package com.example.shopee.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.events.ItemClickListener;
import com.example.shopee.models.User;
import com.example.shopee.seller.CustomerOrderActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerViewHolder>{
    private Context context;
    private List<User> list;

    public CustomerAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_header, viewGroup, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder customerViewHolder, int i) {
        final User user = list.get(i);
        customerViewHolder.name.setText(user.getFirstname() + " " + user.getLastname());
        customerViewHolder.address.setText(user.getAddress());
        Picasso.get().load(user.getImage_uri()).into(customerViewHolder.image);

        customerViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, CustomerOrderActivity.class);
                intent.putExtra("id", user.getId());
                intent.putExtra("name", user.getFirstname() + " " + user.getLastname());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class CustomerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView name, address;
    public CircleImageView image;
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CustomerViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        address = itemView.findViewById(R.id.address);
        image = itemView.findViewById(R.id.image);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
