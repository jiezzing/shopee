package com.example.shopee.recyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.Cart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private Context context;
    private List<Cart> list;

    public CartAdapter(Context context, List<Cart> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.carted_product, viewGroup, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i) {
        final Cart cart =  list.get(i);
        final String name =  cart.getName();
        final String desc =  cart.getDescription();
        final String price =  cart.getPrice();
        final String uri =  cart.getImage_uri();
        final String id =  cart.getId();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation");
        BigDecimal currency = new BigDecimal(price);

        cartViewHolder.name.setText(name);
        cartViewHolder.desc.setText(desc);
        cartViewHolder.price.setText(String.valueOf(currency.setScale(2, RoundingMode.CEILING)));
        Picasso.get().load(uri).into(cartViewHolder.image);

        cartViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Do you want to delete " + name + "?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase
                        .getInstance()
                        .getReference("Cart")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Removed from cart.", Toast.LENGTH_SHORT).show();
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
                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class CartViewHolder extends RecyclerView.ViewHolder {
    public TextView name, desc, price, qty, edit, delete;
    public ImageView image;
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        desc = itemView.findViewById(R.id.desc);
        price = itemView.findViewById(R.id.price);
        qty = itemView.findViewById(R.id.qty);
        image = itemView.findViewById(R.id.image);
        edit = itemView.findViewById(R.id.edit);
        delete = itemView.findViewById(R.id.delete);
    }
}
