package com.example.shopee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.shopee.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CreateAccountActivity extends AppCompatActivity {
    EditText firstname, lastname, phone, address, email, password, confirm_password;
    RadioGroup radio_group;
    RadioButton user_type;
    Button create;
    ProgressDialog progressDialog;
    ImageView add_photo;
    int PICK_IMAGE_REQUEST = 1;
    Boolean selected = false;
    Uri uri;

    String type;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        userRef = database.getReference("User");

        // Initializations
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        radio_group = findViewById(R.id.group);
        create = findViewById(R.id.create);
        add_photo = findViewById(R.id.add_photo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type_id = radio_group.getCheckedRadioButtonId();
                user_type = findViewById(type_id);
                type = user_type.getText().toString();

                final String mFirstname = firstname.getText().toString();
                final String mLastName = lastname.getText().toString();
                final String mPhone = phone.getText().toString();
                final String mAddress = address.getText().toString();
                final String mEmail = email.getText().toString();
                final String mPassword = password.getText().toString();

                if(TextUtils.isEmpty(firstname.getText())){
                    firstname.setError("Required");
                    firstname.requestFocus();
                }
                else if(TextUtils.isEmpty(lastname.getText())){
                    lastname.setError("Required");
                    firstname.requestFocus();
                }
                else if(TextUtils.isEmpty(lastname.getText())){
                    lastname.setError("Required");
                    firstname.requestFocus();
                }
                else if(TextUtils.isEmpty(email.getText())){
                    email.setError("Required");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(password.getText())){
                    password.setError("Required");
                    password.requestFocus();
                }
                else if(uri == null){
                    Toast.makeText(CreateAccountActivity.this, "Please select a photo.", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(confirm_password.getText())){
                    confirm_password.setError("Required");
                    confirm_password.requestFocus();
                }
                else if(!password.getText().toString().equals(confirm_password.getText().toString())){
                    Toast.makeText(CreateAccountActivity.this, "Password does not match.", Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.createUserWithEmailAndPassword(mEmail, mPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    final String userId = auth.getCurrentUser().getUid();
                                    StorageReference storage = FirebaseStorage.getInstance().getReference("ProductPhotos").child(userId);
                                    storage.putFile(uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                            int currentProgress = (int) progress;
                                            progressDialog.setMessage("Uploaded in " + currentProgress + "%");
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {

                                                    User user = new User(userId, mFirstname, mLastName, mEmail, mPassword, type, mAddress, mPhone, uri.toString(), "active");
                                                    userRef.child(userId).setValue(user)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(CreateAccountActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                                                                }
                                                                else{
                                                                    Toast.makeText(CreateAccountActivity.this, "An error occurred, please try again.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                }
                                            });
                                        }
                                    });
                                }
                                else{
                                    progressDialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateAccountActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                uri = data.getData();
                if(selected) {
                    Picasso.get().load(uri).into(add_photo);
                }
            }
        }
        else{
            Toast.makeText(CreateAccountActivity.this, "Please select an image.",Toast.LENGTH_LONG).show();
        }
    }
}
