package com.example.shopee.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.User;
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
        customerViewHolder.name.setText(user.getFirstname());
        Picasso.get().load(user.getImage_uri()).into(customerViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class CustomerViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public CircleImageView image;
    public CustomerViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        image = itemView.findViewById(R.id.image);
    }
}
