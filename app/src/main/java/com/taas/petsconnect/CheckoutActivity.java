package com.taas.petsconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taas.petsconnect.Adapter.CartAdapter;
import com.taas.petsconnect.Model.CartItem;
import com.taas.petsconnect.Model.Order;
import com.taas.petsconnect.Model.Product;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity implements CartAdapter.CartListener {
    private DatabaseReference cartRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ArrayList<CartItem> cartItems;
    private CartAdapter adapter;
    private TextView subtotalTextView, taxTextView, totalTextView;
    private double subtotal, tax, total; // Declare these variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Checkout");
        subtotalTextView = findViewById(R.id.subtotal);
        taxTextView = findViewById(R.id.tax);
        totalTextView = findViewById(R.id.total);
        // Initialize Firebase
        cartRef = FirebaseDatabase.getInstance().getReference("carts");
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
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
        Button checkoutBtn = findViewById(R.id.checkoutBtn);
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input from EditText fields
                String name = ((EditText) findViewById(R.id.nameBox)).getText().toString();
                String email = ((EditText) findViewById(R.id.emailBox)).getText().toString();
                String phone = ((EditText) findViewById(R.id.phoneBox)).getText().toString();
                String address = ((EditText) findViewById(R.id.addressBox)).getText().toString();
                String date = ((EditText) findViewById(R.id.dateBox)).getText().toString();
                String comments = ((EditText) findViewById(R.id.commentBox)).getText().toString();
               /* String productId = cartItems.get(0).getProductId();
                Product product = cartItems.get(0).getProduct();*/
                String shopId = cartItems.get(0).getShopId();
                // Create an Order object or a HashMap to represent the order details
                Order order = new Order(name, email, phone, address, date, comments, cartItems, subtotal, tax, total,shopId);

                // Save the order to Firebase
                saveOrderToFirebase(order);
            }
        });
    }

    private void saveOrderToFirebase(Order order) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("shops")
                /*.child(userId)*/.child("products").child("orders");

        // Generate a unique key for the order
        String orderId = ordersRef.push().getKey();

        // Save the order details under the generated key
        ordersRef.child(orderId).setValue(order);

        // Remove cart items after a successful order
        clearCartItems();
    }




    private void retrieveCartItems() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            cartRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cartItems.clear();
                    subtotal = 0.0; // Initialize subtotal

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String cartItemId = dataSnapshot.getKey();
                        String productName = dataSnapshot.child("prductname").getValue(String.class);
                        Double productPriceDouble = dataSnapshot.child("price").getValue(Double.class);
                        Double discountDouble = dataSnapshot.child("discount").getValue(Double.class);
                        String productimg = dataSnapshot.child("productimg").getValue(String.class);
                        String productId = dataSnapshot.child("productId").getValue(String.class);
                        String shopId = dataSnapshot.child("shopId").getValue(String.class); // Retrieve shopId

                        // Check for null values
                        double productPrice = (productPriceDouble != null) ? productPriceDouble : 0.0;
                        double discount = (discountDouble != null) ? discountDouble : 0.0;

                        // Check if quantity is null
                        Integer quantityInteger = dataSnapshot.child("quantity").getValue(Integer.class);
                        int quantity = (quantityInteger != null) ? quantityInteger : 1;

                        // Create CartItem with shopId
                        CartItem cartItem = new CartItem(productName, productPrice, discount, quantity, productimg, userId, shopId);
                        cartItem.setId(cartItemId);
                        cartItems.add(cartItem);

                        // Update subtotal by adding the price (considering discount)
                        subtotal += calculateDiscountedPrice(productPrice, discount) * quantity;
                    }

                    // Calculate tax (2% of subtotal)
                    tax = subtotal * 0.02;
                    // Calculate total (subtotal + tax)
                    total = subtotal + tax;

                    // Update the TextViews
                    subtotalTextView.setText(String.format("PKR %.2f", subtotal));
                    taxTextView.setText(String.format("PKR %.2f", tax));
                    totalTextView.setText(String.format("PKR %.2f", total));

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
        subtotal = 0.0;

        for (CartItem cartItem : cartItems) {
            subtotal += calculateDiscountedPrice(cartItem.getPrice(), cartItem.getDiscount()) * cartItem.getQuantity();
        }

        // Update the subtotal TextView
        subtotalTextView.setText(String.format("PKR %.2f", subtotal));
    }

    private double calculateDiscountedPrice(double originalPrice, double discountPercentage) {
        return originalPrice - (originalPrice * (discountPercentage / 100));
    }

    // Implement the clearCartItems method

    private void clearCartItems() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            cartRef.child(userId).removeValue();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}