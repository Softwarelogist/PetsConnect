package com.taas.petsconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.taas.petsconnect.Model.Shop;
import com.taas.petsconnect.Model.ShopVerificationRequest;

import java.util.Objects;

public class ShopRegistrationActivity extends AppCompatActivity {

    private EditText shopNameEditText, descriptionEditText, locationEditText, phoneEditText, paymentTrxid,senderaccEditText,accounttitleEditText,banknameEditText;
    private Button registerShopButton, paymentButton;
    private ImageView paymentReceiptImageView;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_registration);
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ShopRegistrationActivity.this.setTitle("Shop Registration");

        shopNameEditText = findViewById(R.id.shopNameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        phoneEditText = findViewById(R.id.shopephoneitText);
        registerShopButton = findViewById(R.id.registerShopButton);
        paymentTrxid = findViewById(R.id.paymentTrxid);
        paymentReceiptImageView = findViewById(R.id.receiptimage);
        senderaccEditText=findViewById(R.id.senderaccEditText);
        accounttitleEditText=findViewById(R.id.accounttitleEditText);
        banknameEditText=findViewById(R.id.banknameEditText);
        paymentButton = findViewById(R.id.paymentbtn);

        // Initialize Firebase Database and FirebaseAuth
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to select an image
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        registerShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopName = shopNameEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String location = locationEditText.getText().toString().trim();
                String phoneShop = phoneEditText.getText().toString().trim();
                String payment = paymentTrxid.getText().toString().trim();
                String  accounttitle =  accounttitleEditText.getText().toString().trim();
                String  bankname =  banknameEditText.getText().toString().trim();
                String  senderacc =  senderaccEditText.getText().toString().trim();


                String receiptimage = (selectedImageUri != null) ? selectedImageUri.toString() : "";
                String status="pending";


                if (shopName.isEmpty() || description.isEmpty() || location.isEmpty() || phoneShop.isEmpty() || payment.isEmpty()||accounttitle.isEmpty()||bankname.isEmpty()||senderacc.isEmpty()) {
                    Toast.makeText(ShopRegistrationActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Shop shop = new Shop(shopName, description, location, phoneShop, payment,accounttitle,bankname,senderacc);

                String userId = auth.getCurrentUser().getUid();
                String shopId = databaseReference.child("shops").push().getKey();

                databaseReference.child("shops").child(userId).child(shopId).setValue(shop)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ShopRegistrationActivity.this, "Shop registered successfully!", Toast.LENGTH_SHORT).show();
                                    // Start ShopStatusActivity with shop status "pending"
                                    Intent intent = new Intent(ShopRegistrationActivity.this, ShopStatusActivity.class);
                                    intent.putExtra("shopStatus", "pending");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ShopRegistrationActivity.this, "Failed to register shop. Please try again.", Toast.LENGTH_SHORT).show();
                                }

                                DatabaseReference verificationRef = FirebaseDatabase.getInstance().getReference("shops").child("verificationRequests").child(userId);
                                ShopVerificationRequest request = new ShopVerificationRequest(userId, shopName, status, payment, receiptimage,accounttitle,bankname,senderacc);

                                // Set the receiptimage field
                                request.setReceiptimage(receiptimage);

                                verificationRef.setValue(request);

                                // Save image URL to Firebase Storage and update the shop data
                                if (selectedImageUri != null) {
                                    saveImageToFirebase(userId, selectedImageUri);
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            paymentReceiptImageView.setVisibility(View.VISIBLE);
            paymentReceiptImageView.setImageURI(selectedImageUri);
        }
    }

    private void saveImageToFirebase(String userId, Uri imageUri) {
        // Implement your code to save the image to Firebase Storage and get the image URL
        // For example:
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("payment_receipts").child(userId);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the image URL to the shop data in the Realtime Database
                        databaseReference.child("shops").child(userId).child("imageUrl").setValue(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(ShopRegistrationActivity.this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}