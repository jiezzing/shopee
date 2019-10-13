package com.example.shopee.recyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.events.ItemClickListener;
import com.example.shopee.models.Cart;
import com.example.shopee.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ShowAllSellerProductAdapter extends RecyclerView.Adapter<ShowAllSellerProductViewHolder> implements Filterable {
    private Context context;
    private List<Product> list;
    private List<Product> listTemp;
    BottomSheetDialog bottomSheetDialog;
    Button add_to_cart;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference cartRef;

    public ShowAllSellerProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
        listTemp = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ShowAllSellerProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_product, viewGroup, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        cartRef = database.getReference("Cart");
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.add_cart_bottomsheet_layout, null);
        add_to_cart = bottomSheetView.findViewById(R.id.add_to_cart);
        bottomSheetDialog.setContentView(bottomSheetView);
        return new ShowAllSellerProductViewHolder(view);
    }

    String itemId, itemName, itemDesc, itemPrice, itemUri, sellerId;
    @Override
    public void onBindViewHolder(@NonNull ShowAllSellerProductViewHolder showAllSellerProductViewHolder, int i) {
        final Product product =  list.get(i);
        final String name =  product.getName();
        final String desc =  product.getDescription();
        String price =  product.getPrice();
        final String uri =  product.getImage_uri();
        final String id =  product.getId();
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation");
        final BigDecimal currency = new BigDecimal(price);

        showAllSellerProductViewHolder.name.setText(name);
        showAllSellerProductViewHolder.desc.setText(desc);
        showAllSellerProductViewHolder.price.setText(String.valueOf(currency.setScale(2, RoundingMode.CEILING)));
        Picasso.get().load(uri).into(showAllSellerProductViewHolder.image);

        showAllSellerProductViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                itemId = id;
                itemName = name;
                itemDesc = desc;
                itemPrice = String.valueOf(currency.setScale(2, RoundingMode.CEILING));
                itemUri = uri;
                sellerId = product.getSeller_id();
                bottomSheetDialog.show();
            }
        });

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart cart = new Cart(itemId, itemName, itemDesc, itemPrice, itemUri, sellerId);
                cartRef.child(auth.getCurrentUser().getUid()).child(itemId).setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Added to cart.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
            List<Product> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(listTemp);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Product product : listTemp){
                    if(product.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(product);
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

class ShowAllSellerProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView name, desc, price, qty;
    public ImageView image;
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ShowAllSellerProductViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        desc = itemView.findViewById(R.id.desc);
        price = itemView.findViewById(R.id.price);
        qty = itemView.findViewById(R.id.qty);
        image = itemView.findViewById(R.id.image);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
