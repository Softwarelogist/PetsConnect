package com.taas.petsconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taas.petsconnect.Adapter.CartAdapter;
import com.taas.petsconnect.Model.CartItem;
import com.taas.petsconnect.R;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartListener {

    private DatabaseReference cartRef;
    private FirebaseAuth auth;
    private ArrayList<CartItem> cartItems;
    private CartAdapter adapter;
    private TextView subtotalTextView;
    private Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Shopping Cart");
        subtotalTextView = findViewById(R.id.subtotal);
        // Initialize Firebase
        cartRef = FirebaseDatabase.getInstance().getReference("carts");
        auth = FirebaseAuth.getInstance();
        // Set up RecyclerView and adapter
        cartItems = new ArrayList<>();
        RecyclerView cartListRV = findViewById(R.id.cartListRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cartListRV.setLayoutManager(linearLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        cartListRV.addItemDecoration(itemDecoration);
        // Initialize the adapter here
        adapter = new CartAdapter(this, cartItems, this, auth.getCurrentUser().getUid());
        cartListRV.setAdapter(adapter);
        // Retrieve cart items from Firebase
        retrieveCartItems();

        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void retrieveCartItems() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            cartRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cartItems.clear();
                    double subtotal = 0.0; // Initialize subtotal

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String cartItemId = dataSnapshot.getKey();
                        String productName = dataSnapshot.child("prductname").getValue(String.class);
                        Double productPriceDouble = dataSnapshot.child("price").getValue(Double.class);
                        Double discountDouble = dataSnapshot.child("discount").getValue(Double.class);
                        String productimg = dataSnapshot.child("productimg").getValue(String.class);
                        String productId = dataSnapshot.child("productId").getValue(String.class);
                        String UserId=dataSnapshot.child("UserId").getValue(String.class);
                        String shopId=dataSnapshot.child("shopId").getValue(String.class);

                        // Check for null values
                        double productPrice = (productPriceDouble != null) ? productPriceDouble : 0.0;
                        double discount = (discountDouble != null) ? discountDouble : 0.0;

                        // Check if quantity is null
                        Integer quantityInteger = dataSnapshot.child("quantity").getValue(Integer.class);
                        int quantity = (quantityInteger != null) ? quantityInteger : 1;
                        CartItem cartItem = new CartItem(productName, productPrice, discount, quantity, productimg,userId,shopId);
                        cartItem.setId(cartItemId);
                        cartItems.add(cartItem);

                        // Update subtotal by adding the price (considering discount)
                        subtotal += calculateDiscountedPrice(productPrice, discount) * quantity;
                    }

                    // Update the subtotal TextView
                    subtotalTextView.setText(String.format("PKR %.2f", subtotal));

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

    @Override
    public void onQuantityChanged() {
        // Recalculate the subtotal when the quantity changes
        double subtotal = 0.0;

        for (CartItem cartItem : cartItems) {
            subtotal += calculateDiscountedPrice(cartItem.getPrice(), cartItem.getDiscount()) * cartItem.getQuantity();
        }

        // Update the subtotal TextView
        subtotalTextView.setText(String.format("PKR %.2f", subtotal));
    }

    // Calculate discounted price
    private double calculateDiscountedPrice(double originalPrice, double discountPercentage) {
        return originalPrice - (originalPrice * (discountPercentage / 100));
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
