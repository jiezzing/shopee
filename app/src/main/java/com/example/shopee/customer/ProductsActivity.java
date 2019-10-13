package com.example.shopee.customer;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.Cart;
import com.example.shopee.models.Product;
import com.example.shopee.recyclerview.CartAdapter;
import com.example.shopee.recyclerview.ProductAdapter;
import com.example.shopee.recyclerview.ShowAllSellerProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    String seller_id;
    Dialog dialog;
    FloatingActionButton cart_btn;

    LinearLayout info;

    private RecyclerView recyclerView;
    private ShowAllSellerProductAdapter adapter;
    private List<Product> list;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference productReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        dialog = new Dialog(this);
        seller_id = getIntent().getStringExtra("user_id");

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        productReference = database.getReference("Products");
        cart_btn = findViewById(R.id.cart_btn);
        info = findViewById(R.id.info);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        productReference.child(seller_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                info.setVisibility(View.VISIBLE);
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    info.setVisibility(View.GONE);
                    Product product = post.getValue(Product.class);
                    list.add(product);
                }
                adapter = new ShowAllSellerProductAdapter(ProductsActivity.this, list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cart_btn.setOnClickListener(new View.OnClickListener() {
            RecyclerView recyclerView;
            CartAdapter adapter;
            List<Cart> cartList;
            Button order_btn;
            LinearLayout info;
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.popup_cart);
                recyclerView = dialog.findViewById(R.id.recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProductsActivity.this));
                cartList = new ArrayList<>();
                order_btn = dialog.findViewById(R.id.order_btn);
                info = dialog.findViewById(R.id.info);
                FirebaseDatabase
                        .getInstance()
                        .getReference("Cart")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                cartList.clear();
                                info.setVisibility(View.VISIBLE);
                                for(DataSnapshot post : dataSnapshot.getChildren()){
                                    info.setVisibility(View.GONE);
                                    Cart cart = post.getValue(Cart.class);
                                    cartList.add(cart);
                                }
                                adapter = new CartAdapter(ProductsActivity.this, cartList);
                                recyclerView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }
}
