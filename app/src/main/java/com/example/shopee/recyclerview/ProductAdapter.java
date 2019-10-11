package com.example.shopee.recyclerview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.Product;
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
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> implements PreferenceManager.OnActivityResultListener {
    private Context context;
    private List<Product> list;
    private BottomSheetDialog bottomSheetDialog;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference productReference;

    int PICK_IMAGE_REQUEST = 1;
    Boolean selected = false;
    Uri uri;

    EditText mName, mDesc, mPrice, mQty;
    TextView mProduct;
    ImageView add_item_photo;
    Button update_btn;

    public ProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.product, viewGroup, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        productReference = database.getReference("Products");
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.product_bottomsheet_layout, null);
        add_item_photo = bottomSheetView.findViewById(R.id.add_item_photo);
        mName = bottomSheetView.findViewById(R.id.product_name);
        mDesc = bottomSheetView.findViewById(R.id.product_desc);
        mPrice = bottomSheetView.findViewById(R.id.product_price);
        mQty = bottomSheetView.findViewById(R.id.product_qty);
        update_btn = bottomSheetView.findViewById(R.id.add_product_btn);
        mProduct = bottomSheetView.findViewById(R.id.product);
        bottomSheetDialog.setContentView(bottomSheetView);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i) {
        final Product product =  list.get(i);
        final String name =  product.getName();
        final String desc =  product.getDescription();
        final String price =  product.getPrice();
        final String qty =  product.getQty();
        final String uri =  product.getImage_uri();
        final String id =  product.getId();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation");
        BigDecimal currency = new BigDecimal(price);

        productViewHolder.name.setText(name);
        productViewHolder.desc.setText(desc);
        productViewHolder.price.setText(String.valueOf(currency.setScale(2, RoundingMode.CEILING)));
        productViewHolder.qty.setText(qty);
        Picasso.get().load(uri).into(productViewHolder.image);

        productViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Do you want to delete " + name + "?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        productReference.child(auth.getCurrentUser().getUid()).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "Product deleted successfully.", Toast.LENGTH_SHORT).show();
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

        productViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productReference.child(auth.getCurrentUser().getUid()).child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mProduct.setText("PRODUCT DETAILS");
                        mName.setText(dataSnapshot.child("name").getValue().toString());
                        mDesc.setText(dataSnapshot.child("description").getValue().toString());
                        mPrice.setText(dataSnapshot.child("price").getValue().toString());
                        mQty.setText(dataSnapshot.child("qty").getValue().toString());
                        update_btn.setText("UPDATE PRODUCT");
                        Picasso.get().load(dataSnapshot.child("image_uri").getValue().toString()).into(add_item_photo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomSheetDialog.show();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> result = new HashMap<>();
                result.put("name", mName.getText().toString());
                result.put("description", mDesc.getText().toString());
                result.put("price", mPrice.getText().toString());
                result.put("qty", mQty.getText().toString());
                productReference.child(auth.getCurrentUser().getUid()).child(id).updateChildren(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Product successfully updated.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Toast.makeText(context, "Hahaha", Toast.LENGTH_SHORT).show();
            }
        });

        add_item_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Toast.makeText(context, "HAHAHAHAH", Toast.LENGTH_SHORT).show();
                ((Activity)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }
}

class ProductViewHolder extends RecyclerView.ViewHolder{
    public TextView name, desc, price, qty, edit, delete;
    public ImageView image;
    public ProductViewHolder(@NonNull View itemView) {
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