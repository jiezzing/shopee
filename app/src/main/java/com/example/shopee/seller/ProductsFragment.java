package com.example.shopee.seller;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.Product;
import com.example.shopee.recyclerview.ProductAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment implements SearchView.OnQueryTextListener {
    FloatingActionButton add_product;
    BottomSheetDialog bottomSheetDialog;
    ImageView add_item_photo;
    int PICK_IMAGE_REQUEST = 1;
    Boolean selected = false;
    Uri uri = null;
    Button add_product_btn;
    ProgressDialog progressDialog;
    EditText name, desc, price, qty;

    LinearLayout info;

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> list;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference productReference;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_products, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        productReference = database.getReference("Products");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity());
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.product_bottomsheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        add_product = view.findViewById(R.id.add_product);
        info = view.findViewById(R.id.info);
        add_item_photo = bottomSheetView.findViewById(R.id.add_item_photo);
        add_product_btn = bottomSheetView.findViewById(R.id.add_product_btn);
        name = bottomSheetView.findViewById(R.id.product_name);
        desc = bottomSheetView.findViewById(R.id.product_desc);
        price = bottomSheetView.findViewById(R.id.product_price);
        qty = bottomSheetView.findViewById(R.id.product_stock);

        productReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                info.setVisibility(View.VISIBLE);
                list.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    info.setVisibility(View.GONE);
                    Product product = post.getValue(Product.class);
                    list.add(product);
                }
                adapter = new ProductAdapter(getActivity(), list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        add_item_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        add_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(name.getText())){
                    name.setError("Required");
                    name.requestFocus();
                }
                else if(TextUtils.isEmpty(desc.getText())){
                    desc.setError("Required");
                    desc.requestFocus();
                }
                else if(TextUtils.isEmpty(price.getText())){
                    price.setError("Required");
                    price.requestFocus();
                }
                else if(TextUtils.isEmpty(qty.getText())){
                    qty.setError("Required");
                    qty.requestFocus();
                }
                else if(uri == null){
                    Toast.makeText(getActivity(), "Plase select an image.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String userId = auth.getCurrentUser().getUid();
                    String key = database.getReference("Product").push().getKey();
                    StorageReference storage = FirebaseStorage.getInstance().getReference("ProductPhotos").child(userId).child(key);
                    storage.putFile(uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            int currentProgress = (int) progress;
                            progressDialog.setMessage("Uploaded in " + currentProgress + "%");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    String userId = auth.getCurrentUser().getUid();
                                    String id = database.getReference("Products").push().getKey();
                                    String prod_name = name.getText().toString();
                                    String prod_desc = desc.getText().toString();
                                    String prod_price = price.getText().toString();
                                    String prod_qty = qty.getText().toString();
                                    String image_uri = uri.toString();
                                    Product product;
                                    product = new Product(id, prod_name, prod_desc, prod_price, prod_qty, image_uri, auth.getCurrentUser().getUid(), "available");
                                    productReference.child(userId).child(id).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getActivity(), "Successfully uploaded.", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                name.getText().clear();
                                                desc.getText().clear();
                                                price.getText().clear();
                                                add_item_photo.setImageDrawable(null);
                                                bottomSheetDialog.dismiss();
                                            }
                                            else{
                                                Toast.makeText(getActivity(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                uri = data.getData();
                if(selected) {
                    Picasso.get().load(uri).into(add_item_photo);
                }
            }
        }
        else{
            Toast.makeText(getActivity(), "Please select an image.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) new SearchView(((com.example.shopee.seller.HomeActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(searchItem, searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(adapter != null)
            adapter.getFilter().filter(s);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }
}
