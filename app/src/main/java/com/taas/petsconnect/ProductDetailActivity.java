package com.taas.petsconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.CartActivity;
import com.taas.petsconnect.Model.Product;
import com.taas.petsconnect.R;

public class ProductDetailActivity extends AppCompatActivity {

    private DatabaseReference cartRef;
    private FirebaseAuth auth;
    private Button addToCartButton;
    private String productId; // Declare productId as a class-level variable

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Product Detail");

        // Get data from Intent
        String productName = getIntent().getStringExtra("name");
        String productImage = getIntent().getStringExtra("image");
        String productDescription = getIntent().getStringExtra("productdescription");
        double discount = getIntent().getDoubleExtra("discount", 0.0);
        double discountPercentage = getIntent().getDoubleExtra("discountprice", 0.0);
        productId = getIntent().getStringExtra("id");
        double productPrice = getIntent().getDoubleExtra("price", 0.0);


        // Initialize Firebase
        cartRef = FirebaseDatabase.getInstance().getReference("carts");
        auth = FirebaseAuth.getInstance();

        // Initialize views
        ImageView productImageView = findViewById(R.id.productImage);
        TextView productNameTextView = findViewById(R.id.productlabel);
        TextView productDescriptionTextView = findViewById(R.id.productDescription);
        TextView productPriceTextView = findViewById(R.id.productactuallprice);
        TextView discountPercentageTextView = findViewById(R.id.productdiscount);
        addToCartButton = findViewById(R.id.addToCartBtn);

        // Load product details into views
        Picasso.get().load(productImage).into(productImageView);
        productNameTextView.setText(productName);
        productDescriptionTextView.setText(productDescription);
        // Display original and discounted prices
        productPriceTextView.setText(Html.fromHtml("<b>Original Price: PKR " + productPrice));
        if (discount > 0) {
            double discountedPrice = calculateDiscountedPrice(productPrice, discount);
            discountPercentageTextView.setText(Html.fromHtml("<b>Discount: " + discount + "%"));
            productPriceTextView.append(Html.fromHtml("<br>Discounted Price: PKR " + discountedPrice + "</b>"));
            discountPercentageTextView.setVisibility(View.VISIBLE);
        } else {
            discountPercentageTextView.setVisibility(View.GONE);
        }

        // Add to Cart button click listener
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(productName, productPrice, discount);
                checkProductInCart();
            }

        });


    }

    private void checkProductInCart() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Check if the product ID exists in the user's cart
            cartRef.child(userId).orderByChild("productid").equalTo(productId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Enable or disable the "Add to Cart" button based on whether the product is in the cart
                            boolean productInCart = snapshot.exists();
                            addToCartButton.setEnabled(!productInCart);

                            // Change button color based on whether the product is in the cart
                            int buttonColor = productInCart
                                    ? ContextCompat.getColor(ProductDetailActivity.this, R.color.colorButtonDisabled)
                                    : ContextCompat.getColor(ProductDetailActivity.this, R.color.blue);
                            addToCartButton.setBackgroundColor(buttonColor);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
        }
    }

    private void addToCart(String productName, double productPrice, double discount) {
        // Check if the user is authenticated
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // User is authenticated, add the product to the cart in Firebase
            String userId = user.getUid();
            String cartItemId = cartRef.child(userId).push().getKey();

            if (cartItemId != null) {
                Product product = new Product();
                product.setPrductname(productName);
                product.setProductimg(getIntent().getStringExtra("image"));
                product.setProductdescription(getIntent().getStringExtra("productdescription"));
                product.setPrice(productPrice);
                product.setDiscount(discount);
                product.setId(productId);

                cartRef.child(userId).child(cartItemId).setValue(product)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ProductDetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                            checkProductInCart(); // Update button state after adding to cart
                        })
                        .addOnFailureListener(e -> Toast.makeText(ProductDetailActivity.this, "Failed to add to Cart", Toast.LENGTH_SHORT).show());
            }
        } else {
            // User is not authenticated, redirect to login or handle accordingly
            Toast.makeText(ProductDetailActivity.this, "Please log in to add to Cart", Toast.LENGTH_SHORT).show();
        }
    }

    // Calculate discounted price
    private double calculateDiscountedPrice(double originalPrice, double discountPercentage) {
        return originalPrice - (originalPrice * (discountPercentage / 100));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart) {
            startActivity(new Intent(this, CartActivity.class));
        }
        finish();
        return super.onOptionsItemSelected(item);
    }
}
