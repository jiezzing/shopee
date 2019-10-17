package com.example.shopee.seller;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.Product;
import com.example.shopee.models.Seller;
import com.example.shopee.models.User;
import com.example.shopee.recyclerview.CustomerAdapter;
import com.example.shopee.recyclerview.CustomerOrderAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerOrderActivity extends AppCompatActivity {
    String id, name;
    private RecyclerView recyclerView;
    private CustomerOrderAdapter adapter;
    private List<Seller> list;

    FirebaseDatabase database;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        Objects.requireNonNull(getSupportActionBar()).setTitle(name);

        database.getReference("Seller")
                .child(auth.getCurrentUser().getUid())
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for(DataSnapshot post: dataSnapshot.getChildren()){
                            Seller seller = post.getValue(Seller.class);
                            list.add(seller);
                        }
                        adapter = new CustomerOrderAdapter(CustomerOrderActivity.this, list);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
