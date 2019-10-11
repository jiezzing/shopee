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

import com.example.shopee.R;
import com.example.shopee.models.Cart;
import com.example.shopee.models.User;
import com.example.shopee.recyclerview.CartAdapter;
import com.example.shopee.recyclerview.MerchantAdapter;
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

    FirebaseDatabase database;
    DatabaseReference merchant;

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

        cart_btn = view.findViewById(R.id.cart_btn);

        database = FirebaseDatabase.getInstance();
        merchant = database.getReference("User");

        list = new ArrayList<>();
        merchant.orderByChild("type").equalTo("Seller").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()){
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
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.popup_cart);
                recyclerView = dialog.findViewById(R.id.recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                cartList = new ArrayList<>();
                FirebaseDatabase
                        .getInstance()
                        .getReference("Cart")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                cartList.clear();
                                for(DataSnapshot post : dataSnapshot.getChildren()){
                                    Cart cart = post.getValue(Cart.class);

                                    cartList.add(cart);
                                }
                                adapter = new CartAdapter(getActivity(), cartList);
                                recyclerView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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
