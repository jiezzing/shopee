package com.example.shopee.customer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.OrderDetail;
import com.example.shopee.recyclerview.OrderedProductAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderedProductActivity extends AppCompatActivity {
    String order_no;
    private RecyclerView recyclerView;
    private OrderedProductAdapter adapter;
    private List<OrderDetail> list;

    FirebaseDatabase database;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_product);

        order_no = getIntent().getStringExtra("order_no");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

        if(auth.getCurrentUser() != null){
            database.getReference("OrderDetail")
                .child(auth.getCurrentUser().getUid())
                .child(order_no)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for(DataSnapshot post: dataSnapshot.getChildren()){
                            OrderDetail detail = post.getValue(OrderDetail.class);
                            list.add(detail);
                        }
                        adapter = new OrderedProductAdapter(OrderedProductActivity.this, list);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }
        else{
            database.getReference("OrderDetail")
                .child(account.getId())
                .child(order_no)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for(DataSnapshot post: dataSnapshot.getChildren()){
                            OrderDetail detail = post.getValue(OrderDetail.class);
                            list.add(detail);
                        }
                        adapter = new OrderedProductAdapter(OrderedProductActivity.this, list);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(OrderedProductActivity.this, "An error occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        }

    }
}
