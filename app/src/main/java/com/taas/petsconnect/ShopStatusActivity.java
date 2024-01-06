package com.taas.petsconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShopStatusActivity extends AppCompatActivity {

    private TextView shopStatusTextView;
    private Button manageProductsButton;
    private DatabaseReference verificationRequestsRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_status);
        Toolbar toolbar = findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ShopStatusActivity.this.setTitle("Shop Verification Status");

        shopStatusTextView = findViewById(R.id.shopStatusTextView);
        manageProductsButton = findViewById(R.id.manageProductsButton);

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        // Reference to the "verificationRequests" node in the database
        verificationRequestsRef = FirebaseDatabase.getInstance().getReference("shops").child("verificationRequests").child(userId);

        // Add a listener to fetch the status whenever it changes
        verificationRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the status from the database
                    String shopStatus = dataSnapshot.child("status").getValue(String.class);
                    updateShopStatus(shopStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // Handle click on "Manage Products" button
        manageProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProductAddActivity or your desired product management activity
                Intent intent = new Intent(ShopStatusActivity.this, Productadd.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void updateShopStatus(String shopStatus) {
        int textColor;

        if ("approved".equals(shopStatus)) {
            // Shop is verified
            shopStatusTextView.setText("Verification approved");
            manageProductsButton.setVisibility(View.VISIBLE);
            textColor = getResources().getColor(R.color.verifiedTextColor); // Change to your verified text color
        } else if ("pending".equals(shopStatus)) {
            // Shop is pending verification
            shopStatusTextView.setText("Pending Verification");
            manageProductsButton.setVisibility(View.GONE);
            textColor = getResources().getColor(R.color.pendingTextColor); // Change to your pending text color
        }
         else if ("rejected".equals(shopStatus)) {
            // Shop verification is rejected
            shopStatusTextView.setText("Verification Rejected");
            manageProductsButton.setVisibility(View.GONE);
            textColor = getResources().getColor(R.color.rejectedTextColor); // Change to your rejected text color
        } else {
            // Default color
            textColor = getResources().getColor(android.R.color.black);
        }

        // Set the text color
        shopStatusTextView.setTextColor(textColor);
    }
}
