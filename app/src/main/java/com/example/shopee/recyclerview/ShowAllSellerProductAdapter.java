package com.example.shopee.recyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.shopee.customer.SellerFragment;
import com.example.shopee.events.ItemClickListener;
import com.example.shopee.models.Cart;
import com.example.shopee.models.Product;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowAllSellerProductAdapter extends RecyclerView.Adapter<ShowAllSellerProductViewHolder> implements Filterable {
    private Context context;
    private List<Product> list;
    private List<Product> listTemp;
    BottomSheetDialog bottomSheetDialog;
    Button add_to_cart;
    EditText qty;
    String google_id;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference cartRef;
    Boolean flag;

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
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account != null){
            google_id = account.getId();
        }
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.add_cart_bottomsheet_layout, null);
        add_to_cart = bottomSheetView.findViewById(R.id.add_to_cart);
        qty = bottomSheetView.findViewById(R.id.qty);
        bottomSheetDialog.setContentView(bottomSheetView);
        return new ShowAllSellerProductViewHolder(view);
    }

    String itemId, itemName, itemDesc, itemPrice, itemQty, itemUri, sellerId;
    @Override
    public void onBindViewHolder(@NonNull final ShowAllSellerProductViewHolder showAllSellerProductViewHolder, int i) {
        final Product product =  list.get(i);
        final String name =  product.getName();
        final String desc =  product.getDescription();
        String price =  product.getPrice();
        final String uri =  product.getImage_uri();
        final String id =  product.getId();
        final String mQty =  product.getQty();
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation");
        final BigDecimal currency = new BigDecimal(price);

        showAllSellerProductViewHolder.name.setText(name);
        showAllSellerProductViewHolder.desc.setText(desc);
        showAllSellerProductViewHolder.price.setText(String.valueOf(currency.setScale(2, RoundingMode.CEILING)));
        showAllSellerProductViewHolder.qty.setText(product.getQty());
        Picasso.get().load(uri).into(showAllSellerProductViewHolder.image);
        if(auth.getCurrentUser() != null){
            FirebaseDatabase.getInstance().getReference("Cart").child(auth.getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot post : dataSnapshot.getChildren()){
                                if(id.equals(post.getKey())){
                                    showAllSellerProductViewHolder.in_cart.setVisibility(View.VISIBLE);
                                    flag = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        else{
            FirebaseDatabase.getInstance().getReference("Cart").child(google_id)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot post : dataSnapshot.getChildren()){
                                if(id.equals(post.getKey())){
                                    showAllSellerProductViewHolder.in_cart.setVisibility(View.VISIBLE);
                                    flag = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        showAllSellerProductViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                itemId = id;
                itemName = name;
                itemDesc = desc;
                itemPrice = String.valueOf(currency.setScale(2, RoundingMode.CEILING));
                itemUri = uri;
                itemQty = mQty;
                sellerId = product.getSeller_id();

                if(auth.getCurrentUser() != null){
                    FirebaseDatabase.getInstance().getReference("Cart").child(auth.getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot post : dataSnapshot.getChildren()){
                                        if(id.equals(post.getKey())){
                                            showAllSellerProductViewHolder.in_cart.setVisibility(View.VISIBLE);
                                            flag = true;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                else{
                    FirebaseDatabase.getInstance().getReference("Cart").child(google_id)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot post : dataSnapshot.getChildren()){
                                        if(id.equals(post.getKey())){
                                            showAllSellerProductViewHolder.in_cart.setVisibility(View.VISIBLE);
                                            flag = true;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                bottomSheetDialog.show();
            }
        });

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(add_to_cart.getText().toString().equalsIgnoreCase("ADD TO CART")){
                    if(TextUtils.isEmpty(qty.getText())){
                        qty.setError("Required");
                        qty.requestFocus();
                    }
                    else if(Integer.parseInt(itemQty) < Integer.parseInt(qty.getText().toString())){
                        Toast.makeText(context, "Not enough stock", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(qty.getText().toString()) == 0){
                        Toast.makeText(context, "Quantity must be greater than zero", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        double subtotal = Float.parseFloat(qty.getText().toString()) * Float.parseFloat(itemPrice);
                        if(auth.getCurrentUser() != null){
                            Cart cart = new Cart(itemId, itemName, itemDesc, itemPrice, qty.getText().toString(), String.valueOf(subtotal), itemUri, sellerId);
                            cartRef.child(auth.getCurrentUser().getUid()).child(itemId).setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, "Added to cart.", Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference("Cart").child(auth.getCurrentUser().getUid())
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot post : dataSnapshot.getChildren()){
                                                            if(id.equals(post.getKey())){
                                                                showAllSellerProductViewHolder.in_cart.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                        HashMap<String, Object> result = new HashMap<>();
                                        int newQty = Integer.parseInt(itemQty) - Integer.parseInt(qty.getText().toString());
                                        result.put("qty", String.valueOf(newQty));
                                        FirebaseDatabase.getInstance().getReference("Products")
                                            .child(sellerId)
                                            .child(itemId)
                                            .updateChildren(result);
                                    }
                                    else{
                                        Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Cart cart = new Cart(itemId, itemName, itemDesc, itemPrice, qty.getText().toString(), String.valueOf(subtotal), itemUri, sellerId);
                            cartRef.child(google_id).child(itemId).setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, "Added to cart.", Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference("Cart").child(google_id)
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot post : dataSnapshot.getChildren()){
                                                            if(id.equals(post.getKey())){
                                                                showAllSellerProductViewHolder.in_cart.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                        HashMap<String, Object> result = new HashMap<>();
                                        int newQty = Integer.parseInt(itemQty) - Integer.parseInt(qty.getText().toString());
                                        result.put("qty", String.valueOf(newQty));
                                        FirebaseDatabase.getInstance().getReference("Products")
                                                .child(sellerId)
                                                .child(itemId)
                                                .updateChildren(result);
                                    }
                                    else{
                                        Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
                else{
                    if(TextUtils.isEmpty(qty.getText())){
                        qty.setError("Required");
                        qty.requestFocus();
                    }
                    else if(Integer.parseInt(itemQty) < Integer.parseInt(qty.getText().toString())){
                        Toast.makeText(context, "Not enough stock", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(qty.getText().toString()) == 0){
                        Toast.makeText(context, "Quantity must be greater than zero", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        double subtotal = Float.parseFloat(qty.getText().toString()) * Float.parseFloat(itemPrice);
                        final HashMap<String, Object> result = new HashMap<>();
                        result.put("qty", qty.getText().toString());
                        result.put("subtotal", String.valueOf(subtotal));
                        if(auth.getCurrentUser() != null){
                            FirebaseDatabase.getInstance().getReference("Cart")
                                .child(auth.getCurrentUser().getUid())
                                .child(itemId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final int currentQty = Integer.parseInt(dataSnapshot.child("qty").getValue(String.class));
                                        final int mQty = Integer.parseInt(qty.getText().toString());

                                        FirebaseDatabase.getInstance().getReference("Cart")
                                            .child(auth.getCurrentUser().getUid())
                                            .child(itemId)
                                            .updateChildren(result)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(context, "Quantity successfully updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        Toast.makeText(context, "An error occurred: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                            FirebaseDatabase.getInstance().getReference("Products")
                                                .child(sellerId)
                                                .child(itemId)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        int totalQty = Integer.parseInt(dataSnapshot.child("qty").getValue(String.class));
                                                        if(mQty < currentQty){
                                                            final int remQty = currentQty - mQty;
                                                            final int newTotal = totalQty + remQty;
                                                                HashMap<String, Object> result = new HashMap<>();
                                                                result.put("qty", String.valueOf(newTotal));
                                                                FirebaseDatabase.getInstance().getReference("Products")
                                                                        .child(sellerId)
                                                                        .child(itemId)
                                                                        .updateChildren(result);
                                                        }
                                                        else{
                                                            if(mQty > currentQty){
                                                                final int remQty2 = mQty - currentQty;
                                                                final int newTotal2 = totalQty - remQty2;
                                                                HashMap<String, Object> result = new HashMap<>();
                                                                result.put("qty", String.valueOf(newTotal2));
                                                                FirebaseDatabase.getInstance().getReference("Products")
                                                                        .child(sellerId)
                                                                        .child(itemId)
                                                                        .updateChildren(result);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        }
                        else{
                            FirebaseDatabase.getInstance().getReference("Cart")
                                    .child(google_id)
                                    .child(itemId)
                                    .updateChildren(result)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(context, "Quantity successfully updated", Toast.LENGTH_SHORT).show();
                                                HashMap<String, Object> result = new HashMap<>();
                                                int newQty = Integer.parseInt(itemQty) - Integer.parseInt(qty.getText().toString());
                                                result.put("qty", String.valueOf(newQty));
                                                FirebaseDatabase.getInstance().getReference("Products")
                                                        .child(sellerId)
                                                        .child(itemId)
                                                        .updateChildren(result);
                                            }
                                            else{
                                                Toast.makeText(context, "An error occurred: " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }
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
    public TextView name, desc, price, qty, in_cart;
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
        image = itemView.findViewById(R.id.image);
        qty = itemView.findViewById(R.id.qty);
        in_cart = itemView.findViewById(R.id.in_cart);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
