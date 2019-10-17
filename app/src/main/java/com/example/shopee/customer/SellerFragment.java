package com.example.shopee.customer;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.models.Cart;
import com.example.shopee.models.OrderDetail;
import com.example.shopee.models.OrderHeader;
import com.example.shopee.models.Seller;
import com.example.shopee.models.User;
import com.example.shopee.recyclerview.CartAdapter;
import com.example.shopee.recyclerview.MerchantAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class SellerFragment extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private MerchantAdapter adapter;
    private List<User> list;
    Dialog dialog;
    FloatingActionButton cart_btn;
    LinearLayout info;

    String google_id;

    FirebaseDatabase database;
    DatabaseReference merchant;
    int order_no;
    double total = 0.0;

    public static ArrayList<String> food_id = new ArrayList<>();
    public static ArrayList<String> food_name = new ArrayList<>();
    public static ArrayList<String> food_desc = new ArrayList<>();
    public static ArrayList<String> food_price = new ArrayList<>();
    public static ArrayList<String> food_image_uri = new ArrayList<>();
    public static ArrayList<String> seller_id = new ArrayList<>();
    OrderDetail detail;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public SellerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller, container, false);
        dialog = new Dialog(getActivity());
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if(account != null){
            google_id = account.getId();
        }

        cart_btn = view.findViewById(R.id.cart_btn);
        info = view.findViewById(R.id.info);

        database = FirebaseDatabase.getInstance();
        merchant = database.getReference("User");

        list = new ArrayList<>();

        merchant.orderByChild("type").equalTo("Seller").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                info.setVisibility(View.VISIBLE);
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    info.setVisibility(View.GONE);
                    User user = post.getValue(User.class);
                    list.add(user);
                }
                adapter = new MerchantAdapter(getActivity(), list);
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
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                cartList = new ArrayList<>();
                order_btn = dialog.findViewById(R.id.order_btn);
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
                                    total += Double.parseDouble(cart.getPrice());


                                    cartList.add(cart);
                                }
                                adapter = new CartAdapter(getActivity(), cartList);
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
                                        total += Double.parseDouble(cart.getPrice());


                                        cartList.add(cart);
                                    }
                                    adapter = new CartAdapter(getActivity(), cartList);
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
                    if(cartList.size() != 0){
                        if(FirebaseAuth.getInstance().getCurrentUser() != null){
                            FirebaseDatabase
                                    .getInstance()
                                    .getReference("OrderHeader")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            order_no = (int)(dataSnapshot.getChildrenCount());
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
                                                            "Pending");
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
                                                                        Toast.makeText(getActivity(), "Your order is now on process. :)", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else{
                                                                        Toast.makeText(getActivity(), "An error occurred: " + task.getException(), Toast.LENGTH_SHORT).show();
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
                                                            "Pending"
                                                    );

                                                    FirebaseDatabase
                                                            .getInstance()
                                                            .getReference("Seller")
                                                            .child(seller_id.get(i))
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child(String.valueOf(mKey))
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
                                                            "Pending");
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
                                                                        Toast.makeText(getActivity(), "Your order is now on process. :(", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else{
                                                                        Toast.makeText(getActivity(), "An error occurred: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });


                                                    String mKey = FirebaseDatabase.getInstance().getReference("Seller").push().getKey();
                                                    Seller seller = new Seller(
                                                            mKey,
                                                            String.valueOf(order_no),
                                                            account.getId(),
                                                            food_id.get(i),
                                                            food_name.get(i),
                                                            food_desc.get(i),
                                                            food_price.get(i),
                                                            food_image_uri.get(i),
                                                            seller_id.get(i),
                                                            "Pending"
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
                        Toast.makeText(getActivity(), "Please select products.", Toast.LENGTH_SHORT).show();
                    }
                    }
                });
                dialog.show();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) new SearchView(((com.example.shopee.customer.HomeActivity) getActivity()).getSupportActionBar().getThemedContext());
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
