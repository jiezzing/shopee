package com.example.shopee.seller;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.LoginActivity;
import com.example.shopee.R;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Dialog dialog;
    CircleImageView user_image;
    TextView google_email;
    BottomSheetDialog bottomSheetDialog;
    View view;
    EditText firstname, lastname, phone, address, email, password ,type;
    Button update;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        update = view.findViewById(R.id.update);
        bottomSheetDialog.setContentView(view);

        dialog = new Dialog(this);
        drawerLayout = findViewById(R.id.drawer);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        user_image = header.findViewById(R.id.user_image);
        google_email = header.findViewById(R.id.google_email);
        FirebaseDatabase
            .getInstance()
            .getReference("User")
            .child(auth.getCurrentUser().getUid())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String uri = dataSnapshot.child("image_uri").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    Picasso.get().load(uri).into(user_image);
                    google_email.setText(email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        actionBarDrawerToggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrdersFragment()).commit();
            navigationView.setCheckedItem(R.id.order);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Customers");
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.order:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrdersFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Customers");
                break;
            case R.id.products:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductsFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Product");
                break;
            case R.id.profile:
                final String user_id = auth.getCurrentUser().getUid();
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

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("firstname", firstname.getText().toString());
                        result.put("lastname", lastname.getText().toString());
                        result.put("phone", phone.getText().toString());
                        result.put("address", address.getText().toString());
                        if(auth.getCurrentUser() != null){
                            user.child(user_id).updateChildren(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(HomeActivity.this, "Account successfully updated.", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(HomeActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                bottomSheetDialog.show();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(com.example.shopee.seller.HomeActivity.this, LoginActivity.class));
                finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bottomSheetDialog.dismiss();
    }

}
