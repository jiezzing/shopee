package com.example.shopee.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopee.R;
import com.example.shopee.customer.ProductsActivity;
import com.example.shopee.events.ItemClickListener;
import com.example.shopee.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantViewHolder> implements Filterable {
    private Context context;
    private List<User> list;
    private List<User> listTemp;

    public MerchantAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
        listTemp = new ArrayList<>(list);
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
        final String uri = user.getImage_uri();
        final String id = user.getId();

        merchantViewHolder.name.setText(name);
        merchantViewHolder.phone.setText(phone);
        merchantViewHolder.address.setText(address);
        merchantViewHolder.type.setText(type);
        Picasso.get().load(uri).into(merchantViewHolder.image);

        merchantViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, ProductsActivity.class);
                intent.putExtra("user_id", id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(listTemp);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(User user : listTemp){
                    if(user.getFirstname().toLowerCase().contains(filterPattern)
                        || user.getLastname().toLowerCase().contains(filterPattern)){
                            filteredList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values =  filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}

class MerchantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView name, phone, address, type;
    public ItemClickListener itemClickListener;
    public ImageView image;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MerchantViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        phone = itemView.findViewById(R.id.phone);
        address = itemView.findViewById(R.id.address);
        type = itemView.findViewById(R.id.type);
        image = itemView.findViewById(R.id.food_image);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
