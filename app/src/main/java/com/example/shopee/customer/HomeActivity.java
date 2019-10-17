package com.example.shopee.customer;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.LoginActivity;
import com.example.shopee.R;
import com.example.shopee.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Dialog dialog;
    BottomSheetDialog bottomSheetDialog;
    View view;
    EditText firstname, lastname, phone, address, email, password ,type;
    TextView account_info, google_email;
    Button update;
    CircleImageView user_image;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference user;

    GoogleApiClient googleApiClient;

    String gFirstname, gLastname, gEmail;
    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = database.getReference("User");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,  this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        bottomSheetDialog = new BottomSheetDialog(this);
        view = getLayoutInflater().inflate(R.layout.account_bottomsheet_layout, null);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        type = view.findViewById(R.id.user_type);
        account_info = view.findViewById(R.id.account_info);
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
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            gFirstname = account.getGivenName();
            gLastname = account.getFamilyName();
            gEmail = account.getEmail();
            uri = account.getPhotoUrl();
            Picasso.get().load(String.valueOf(uri)).into(user_image);
            google_email.setText(gEmail);


            FirebaseDatabase.getInstance().getReference("User").child(account.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() == 0){
                        User user = new User(
                                account.getId(),
                                account.getGivenName(),
                                account.getFamilyName(),
                                account.getEmail(),
                                "********",
                                "Customer",
                                "No address attached",
                                "No phone # attached",
                                account.getPhotoUrl().toString(),
                                "active");
                        FirebaseDatabase.getInstance().getReference("User")
                                .child(account.getId()).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
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
        }

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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("My Orders");
                break;
            case R.id.profile:
                if(auth.getCurrentUser() != null){
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
                }
                else{
                    firstname.setText(gFirstname);
                    lastname.setText(gLastname);
                    phone.setText("No mobile # attached");
                    address.setText("No address attached");
                    email.setText(gEmail);
                    password.setText("********");
                    type.setText("Customer");
                    firstname.setEnabled(false);
                    lastname.setEnabled(false);
                    phone.setEnabled(false);
                    address.setEnabled(false);
                    account_info.setText("If you want to update your personal information, please use the Google application.");
                    update.setVisibility(View.GONE);
                }

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("firstname", firstname.getText().toString());
                            result.put("lastname", lastname.getText().toString());
                            result.put("phone", phone.getText().toString());
                            result.put("address", address.getText().toString());
                            if(auth.getCurrentUser() != null){
                                user.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(result).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                if (auth.getCurrentUser() != null){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(com.example.shopee.customer.HomeActivity.this, LoginActivity.class));
                    finish();
                }
                else{
                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                        startActivity(new Intent(com.example.shopee.customer.HomeActivity.this, LoginActivity.class));
                        finish();
                        }
                    });
                }
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Error: " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bottomSheetDialog.dismiss();
    }
}
