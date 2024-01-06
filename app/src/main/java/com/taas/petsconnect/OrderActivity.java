// OrderActivity.java

package com.taas.petsconnect;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taas.petsconnect.Adapter.OrderAdapter;
import com.taas.petsconnect.Model.CartItem;
import com.taas.petsconnect.Model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       OrderActivity.this.setTitle("Order Details");

        // Initialize RecyclerView and Adapter
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderAdapter = new OrderAdapter(this);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderRecyclerView.setAdapter(orderAdapter);

        // Set the click listener for "Show All Products" button
        orderAdapter.setOnShowAllProductsClickListener(new OrderAdapter.OnShowAllProductsClickListener() {
            @Override
            public void onShowAllProductsClick(List<CartItem> cartItems) {
                showProductImagesDialog(cartItems);
            }
        });

        // Fetch order data from Firebase
        fetchOrderData();
    }

    private void fetchOrderData() {
        DatabaseReference shopsReference = FirebaseDatabase.getInstance().getReference("shops");

        shopsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orders = new ArrayList<>();

                // Loop through each shop
                for (DataSnapshot shopSnapshot : dataSnapshot.getChildren()) {
                    String shopId = shopSnapshot.getKey();

                    if (shopId != null) {
                        DatabaseReference shopOrdersReference = FirebaseDatabase.getInstance().getReference("shops")
                              .child("products").child("orders");


                        shopOrdersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Loop through orders for each shop
                                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                                    Order order = orderSnapshot.getValue(Order.class);
                                    if (order != null) {
                                        orders.add(order);
                                    }
                                }

                                // Set the orders in the adapter
                                orderAdapter.setOrders(orders);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle error
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void showProductImagesDialog(List<CartItem> cartItems) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_product_images);

        RecyclerView recyclerView = dialog.findViewById(R.id.dialogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        OrderAdapter.ProductImageAdapter productImageAdapter = new OrderAdapter.ProductImageAdapter(this, cartItems);
        recyclerView.setAdapter(productImageAdapter);

        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
