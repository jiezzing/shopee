package com.example.shopee.customer;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.Cart;
import com.example.shopee.models.OrderDetail;
import com.example.shopee.models.OrderHeader;
import com.example.shopee.models.Product;
import com.example.shopee.models.Seller;
import com.example.shopee.recyclerview.CartAdapter;
import com.example.shopee.recyclerview.ProductAdapter;
import com.example.shopee.recyclerview.ShowAllSellerProductAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    String id;
    Dialog dialog;
    FloatingActionButton cart_btn;

    LinearLayout info;

    private RecyclerView recyclerView;
    private ShowAllSellerProductAdapter adapter;
    private List<Product> list;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference productReference;
    double total = 0.0;
    int order_no;
    String google_id;
    OrderDetail detail;

    public static ArrayList<String> food_id = new ArrayList<>();
    public static ArrayList<String> food_name = new ArrayList<>();
    public static ArrayList<String> food_desc = new ArrayList<>();
    public static ArrayList<String> food_price = new ArrayList<>();
    public static ArrayList<String> food_image_uri = new ArrayList<>();
    public static ArrayList<String> seller_id = new ArrayList<>();
    public static ArrayList<String> food_qty = new ArrayList<>();
    public static ArrayList<String> food_subtotal = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        dialog = new Dialog(this);
        id = getIntent().getStringExtra("user_id");

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        productReference = database.getReference("Products");
        cart_btn = findViewById(R.id.cart_btn);
        info = findViewById(R.id.info);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            google_id = account.getId();
        }

        productReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                info.setVisibility(View.VISIBLE);
                list.clear();
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
            TextView mTotal;
            LinearLayout info;
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.popup_cart);
                recyclerView = dialog.findViewById(R.id.recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProductsActivity.this));
                cartList = new ArrayList<>();
                order_btn = dialog.findViewById(R.id.order_btn);
                mTotal = dialog.findViewById(R.id.total);
                info = dialog.findViewById(R.id.info);
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
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
                                        total += Double.parseDouble(cart.getSubtotal());
                                        cartList.add(cart);
                                    }
                                    mTotal.setText("TOTAL: " + total);
                                    adapter = new CartAdapter(ProductsActivity.this, cartList);
                                    recyclerView.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                else{
                    FirebaseDatabase
                        .getInstance()
                        .getReference("Cart")
                        .child(google_id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                cartList.clear();
                                info.setVisibility(View.VISIBLE);
                                for(DataSnapshot post : dataSnapshot.getChildren()){
                                    info.setVisibility(View.GONE);
                                    Cart cart = post.getValue(Cart.class);
                                    total += Double.parseDouble(cart.getSubtotal());


                                    cartList.add(cart);
                                }
                                mTotal.setText("TOTAL: " + total);
                                adapter = new CartAdapter(ProductsActivity.this, cartList);
                                recyclerView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                }
                order_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(cartList.size() != 0 && ProductsActivity.seller_id.size() != 0){
                            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                                FirebaseDatabase
                                        .getInstance()
                                        .getReference("OrderHeader")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                order_no = (int)(dataSnapshot.getChildrenCount());
                                                Log.d("Order No: ", "" + order_no);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                OrderHeader orderHeader = new OrderHeader(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                        String.valueOf(order_no + 1), String.valueOf(total), "Pending");
                                FirebaseDatabase
                                        .getInstance()
                                        .getReference("OrderHeader")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(String.valueOf(order_no + 1))
                                        .setValue(orderHeader)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    for (int i = 0; i < seller_id.size(); i++){
                                                        detail = new OrderDetail(food_id.get(i),
                                                                food_name.get(i),
                                                                food_desc.get(i),
                                                                food_price.get(i),
                                                                food_image_uri.get(i),
                                                                seller_id.get(i),
                                                                "Pending",
                                                                food_qty.get(i),
                                                                food_subtotal.get(i));
                                                        FirebaseDatabase
                                                                .getInstance()
                                                                .getReference("OrderDetail")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child(String.valueOf(order_no))
                                                                .child(food_id.get(i))
                                                                .setValue(detail)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            Toast.makeText(ProductsActivity.this, "Your order is now on process.", Toast.LENGTH_SHORT).show();
                                                                            mTotal.setText("TOTAL: 0.00");
                                                                        }
                                                                        else{
                                                                            Toast.makeText(ProductsActivity.this, "An error occurred: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });


                                                        String mKey = FirebaseDatabase.getInstance().getReference("Seller").push().getKey();
                                                        Seller seller = new Seller(
                                                                mKey,
                                                                String.valueOf(order_no),
                                                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                                food_id.get(i),
                                                                food_name.get(i),
                                                                food_desc.get(i),
                                                                food_price.get(i),
                                                                food_image_uri.get(i),
                                                                seller_id.get(i),
                                                                "Pending",
                                                                food_qty.get(i),
                                                                food_subtotal.get(i)
                                                        );
                                                        FirebaseDatabase
                                                                .getInstance()
                                                                .getReference("Seller")
                                                                .child(seller_id.get(i))
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child(mKey)
                                                                .setValue(seller);

                                                        FirebaseDatabase
                                                                .getInstance()
                                                                .getReference("Cart")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child(food_id.get(i))
                                                                .removeValue();
                                                    }
                                                    food_id.clear();
                                                    seller_id.clear();
                                                }
                                            }
                                        });
                            }
                            else{
                                FirebaseDatabase
                                        .getInstance()
                                        .getReference("OrderHeader")
                                        .child(google_id)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                order_no = (int)(dataSnapshot.getChildrenCount());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                OrderHeader orderHeader = new OrderHeader(google_id,
                                        String.valueOf(order_no + 1), String.valueOf(total), "Pending");

                                FirebaseDatabase
                                        .getInstance()
                                        .getReference("OrderHeader")
                                        .child(google_id)
                                        .child(String.valueOf(order_no + 1))
                                        .setValue(orderHeader)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    for (int i = 0; i < seller_id.size(); i++){
                                                        detail = new OrderDetail(food_id.get(i),
                                                                food_name.get(i),
                                                                food_desc.get(i),
                                                                food_price.get(i),
                                                                food_image_uri.get(i),
                                                                seller_id.get(i),
                                                                "Pending",
                                                                food_qty.get(i),
                                                                food_subtotal.get(i));
                                                        FirebaseDatabase
                                                                .getInstance()
                                                                .getReference("OrderDetail")
                                                                .child(google_id)
                                                                .child(String.valueOf(order_no))
                                                                .child(food_id.get(i))
                                                                .setValue(detail)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            Toast.makeText(ProductsActivity.this, "Your order is now on process.", Toast.LENGTH_SHORT).show();
                                                                            mTotal.setText("TOTAL: 0.00");
                                                                        }
                                                                        else{
                                                                            Toast.makeText(ProductsActivity.this, "An error occurred: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });


                                                        String mKey = FirebaseDatabase.getInstance().getReference("Seller").push().getKey();
                                                        Seller seller = new Seller(
                                                                mKey,
                                                                String.valueOf(order_no),
                                                                google_id,
                                                                food_id.get(i),
                                                                food_name.get(i),
                                                                food_desc.get(i),
                                                                food_price.get(i),
                                                                food_image_uri.get(i),
                                                                seller_id.get(i),
                                                                "Pending",
                                                                food_qty.get(i),
                                                                food_subtotal.get(i)
                                                        );
                                                        FirebaseDatabase
                                                                .getInstance()
                                                                .getReference("Seller")
                                                                .child(seller_id.get(i))
                                                                .child(google_id)
                                                                .child(mKey)
                                                                .setValue(seller);

                                                        FirebaseDatabase
                                                                .getInstance()
                                                                .getReference("Cart")
                                                                .child(google_id)
                                                                .child(food_id.get(i))
                                                                .removeValue();
                                                    }
                                                    food_id.clear();
                                                    seller_id.clear();
                                                }
                                            }
                                        });
                            }
                        }
                        else{
                            Toast.makeText(ProductsActivity.this, "Please select products.", Toast.LENGTH_SHORT).show();
                        }
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
