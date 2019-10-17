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
import com.example.shopee.customer.ProductsActivity;
import com.example.shopee.customer.SellerFragment;
import com.example.shopee.models.Cart;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private Context context;
    private List<Cart> list;
    String google_id;

    public CartAdapter(Context context, List<Cart> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.carted_product, viewGroup, false);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account != null){
            google_id = account.getId();
        }
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, int i) {
        final Cart cart =  list.get(i);
        final String name =  cart.getName();
        final String desc =  cart.getDescription();
        final String price =  cart.getPrice();
        final String uri =  cart.getImage_uri();
        final String id =  cart.getId();
        final String seller_id =  cart.getSeller_id();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation");
        BigDecimal currency = new BigDecimal(price);

        cartViewHolder.name.setText(name);
        cartViewHolder.desc.setText(desc);
        cartViewHolder.price.setText(String.valueOf(currency.setScale(2, RoundingMode.CEILING)));
        Picasso.get().load(uri).into(cartViewHolder.image);

        SellerFragment.food_id.add(id);
        SellerFragment.food_name.add(name);
        SellerFragment.food_desc.add(desc);
        SellerFragment.food_price.add(price);
        SellerFragment.food_image_uri.add(uri);
        SellerFragment.seller_id.add(seller_id);

        ProductsActivity.food_id.add(id);
        ProductsActivity.food_name.add(name);
        ProductsActivity.food_desc.add(desc);
        ProductsActivity.food_price.add(price);
        ProductsActivity.food_image_uri.add(uri);
        ProductsActivity.seller_id.add(seller_id);

        FirebaseDatabase
                .getInstance()
                .getReference("User")
                .child(seller_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String firstname = dataSnapshot.child("firstname").getValue().toString();
                        String lastname = dataSnapshot.child("lastname").getValue().toString();
                        String name = firstname + " " + lastname;
                        cartViewHolder.seller_name.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        cartViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Do you want to delete " + name + "?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                        FirebaseDatabase
                                .getInstance()
                                .getReference("Cart")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();
                                    SellerFragment.food_id.remove(id);
                                    SellerFragment.food_name.remove(name);
                                    SellerFragment.food_desc.remove(desc);
                                    SellerFragment.food_price.remove(price);
                                    SellerFragment.food_image_uri.remove(uri);
                                    SellerFragment.seller_id.remove(seller_id);

                                    ProductsActivity.food_id.remove(id);
                                    ProductsActivity.food_name.remove(name);
                                    ProductsActivity.food_desc.remove(desc);
                                    ProductsActivity.food_price.remove(price);
                                    ProductsActivity.food_image_uri.remove(uri);
                                    ProductsActivity.seller_id.remove(seller_id);
                                }
                                else{
                                    Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        FirebaseDatabase
                                .getInstance()
                                .getReference("Cart")
                                .child(google_id)
                                .child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();
                                    SellerFragment.food_id.remove(id);
                                    SellerFragment.food_name.remove(name);
                                    SellerFragment.food_desc.remove(desc);
                                    SellerFragment.food_price.remove(price);
                                    SellerFragment.food_image_uri.remove(uri);
                                    SellerFragment.seller_id.remove(seller_id);

                                    ProductsActivity.food_id.remove(id);
                                    ProductsActivity.food_name.remove(name);
                                    ProductsActivity.food_desc.remove(desc);
                                    ProductsActivity.food_price.remove(price);
                                    ProductsActivity.food_image_uri.remove(uri);
                                    ProductsActivity.seller_id.remove(seller_id);
                                }
                                else{
                                    Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

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
    public TextView name, desc, price, seller_name, edit, delete;
    public ImageView image;
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        desc = itemView.findViewById(R.id.desc);
        price = itemView.findViewById(R.id.price);
        image = itemView.findViewById(R.id.image);
        seller_name = itemView.findViewById(R.id.seller_name);
        edit = itemView.findViewById(R.id.edit);
        delete = itemView.findViewById(R.id.delete);
    }
}
