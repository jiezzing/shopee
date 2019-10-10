package com.example.shopee.customer;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopee.LoginActivity;
import com.example.shopee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Dialog dialog;
    BottomSheetDialog bottomSheetDialog;
    View view;
    EditText firstname, lastname, phone, address, email, password ,type;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = database.getReference("User");

        bottomSheetDialog = new BottomSheetDialog(this);
        view = getLayoutInflater().inflate(R.layout.account_bottomsheet_layout, null);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        type = view.findViewById(R.id.user_type);
        bottomSheetDialog.setContentView(view);

        dialog = new Dialog(this);
        drawerLayout = findViewById(R.id.drawer);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SellerFragment()).commit();
            navigationView.setCheckedItem(R.id.merchant);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Merchants");
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.merchant:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SellerFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Merchant");
                break;
            case R.id.order:
                break;
            case R.id.profile:
                String user_id = auth.getCurrentUser().getUid();
                user.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        firstname.setText(dataSnapshot.child("firstname").getValue().toString());
                        lastname.setText(dataSnapshot.child("lastname").getValue().toString());
                        phone.setText(dataSnapshot.child("phone").getValue().toString());
                        address.setText(dataSnapshot.child("address").getValue().toString());
                        email.setText(dataSnapshot.child("email").getValue().toString());
                        password.setText(dataSnapshot.child("password").getValue().toString());
                        type.setText(dataSnapshot.child("type").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomSheetDialog.show();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(com.example.shopee.customer.HomeActivity.this, LoginActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
