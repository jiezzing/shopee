package com.example.shopee.seller;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.OrderDetail;
import com.example.shopee.models.OrderHeader;
import com.example.shopee.models.Seller;
import com.example.shopee.models.User;
import com.example.shopee.recyclerview.CustomerAdapter;
import com.example.shopee.recyclerview.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<User> list;

    FirebaseDatabase database;
    FirebaseAuth auth;

    ArrayList<String> customerId = new ArrayList<>();
    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        list = new ArrayList<>();

        database.getReference("SellerFile")
                .child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot mUser : dataSnapshot.getChildren()){
                    database.getReference("User")
                        .orderByChild("id")
                        .equalTo(mUser.getKey())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                list.clear();
                                for(DataSnapshot post: dataSnapshot.getChildren()){
                                    User user = post.getValue(User.class);
                                    list.add(user);
                                }
                                adapter = new CustomerAdapter(getActivity(), list);
                                recyclerView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}
