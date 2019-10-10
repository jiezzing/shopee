package com.example.shopee.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shopee.R;
import com.example.shopee.models.User;

import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantViewHolder> {
    private Context context;
    private List<User> list;

    public MerchantAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MerchantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.merchant, viewGroup, false);
        return new MerchantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantViewHolder merchantViewHolder, int i) {
        final User user = list.get(i);
        final String name = user.getFirstname() + " " + user.getLastname();
        final String phone = "Phone #: " + user.getPhone();
        final String address =user.getAddress();
        final String type = user.getType();

        merchantViewHolder.name.setText(name);
        merchantViewHolder.phone.setText(phone);
        merchantViewHolder.address.setText(address);
        merchantViewHolder.type.setText(type);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class MerchantViewHolder extends RecyclerView.ViewHolder{
    public TextView name, phone, address, type;
    public MerchantViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        phone = itemView.findViewById(R.id.phone);
        address = itemView.findViewById(R.id.address);
        type = itemView.findViewById(R.id.type);
    }
}
