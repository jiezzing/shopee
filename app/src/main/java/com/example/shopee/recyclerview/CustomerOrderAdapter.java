package com.example.shopee.recyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.Product;
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
    public void onBindViewHolder(@NonNull CustomerOrderViewHolder customerOrderViewHolder, int i) {
        final Seller seller = list.get(i);
        String price = seller.getPrice();
        BigDecimal currency = new BigDecimal(price);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation");
        dialog.setMessage("Is this out of stock or not available?");

        customerOrderViewHolder.name.setText(seller.getName());
        customerOrderViewHolder.price.setText(String.valueOf(currency.setScale(2, RoundingMode.CEILING)));
        customerOrderViewHolder.desc.setText(seller.getDescription());
        customerOrderViewHolder.status.setText(seller.getStatus());
        Picasso.get().load(seller.getImage_uri()).into(customerOrderViewHolder.image);

        customerOrderViewHolder.out_of_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "Out of stock");
                        FirebaseDatabase.getInstance().getReference("Seller")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(seller.getCustomer_no())
                                .child(seller.getSeller_no())
                                .updateChildren(result)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference("OrderDetail")
                                                    .child(seller.getCustomer_no())
                                                    .child(seller.getOrder_no())
                                                    .child(seller.getId())
                                                    .updateChildren(result);
                                        }
                                        else{
                                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class CustomerOrderViewHolder extends RecyclerView.ViewHolder {
    public TextView name, price, desc, status, out_of_stock, delivered;
    public ImageView image;
    public CustomerOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.price);
        desc = itemView.findViewById(R.id.desc);
        status = itemView.findViewById(R.id.status);
        image = itemView.findViewById(R.id.image);
        out_of_stock = itemView.findViewById(R.id.out_of_stock);
        delivered = itemView.findViewById(R.id.delivered);
    }
}
