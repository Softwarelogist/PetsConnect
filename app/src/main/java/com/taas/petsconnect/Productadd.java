package com.taas.petsconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taas.petsconnect.Model.Product;

import java.util.ArrayList;
import java.util.Date;

public class Productadd extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView productImageView;
    private EditText productNameEditText, priceEditText, descriptionEditText, editTextTextdiscount;
    private TextView discountprice;
    private Uri uri;
    private Spinner categorySpinner;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productadd);
        Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Products");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        productImageView = findViewById(R.id.productimg);
        productNameEditText = findViewById(R.id.editTextproductname);
        priceEditText = findViewById(R.id.editTextTextprice);
        editTextTextdiscount = findViewById(R.id.editTextTextdiscount);
        descriptionEditText = findViewById(R.id.editTextTextproductdescription);
        categorySpinner = findViewById(R.id.categorySpinner);
        discountprice = findViewById(R.id.discountprice);

        Button addProductButton = findViewById(R.id.button4);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        Button addImageButton = findViewById(R.id.button3);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to choose an image
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        // Retrieve the list of category names from the intent
        ArrayList<String> categoryNames = getIntent().getStringArrayListExtra("categoryNames");
        if (categoryNames != null) {
            // Populate the category spinner with category names
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
        } else {
            // Handle the case where categoryNames is null
            Toast.makeText(this, "No categories available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.order) {
            startActivity(new Intent(this, OrderActivity.class));
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void addProduct() {
        String productName = productNameEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String discountPercentage = editTextTextdiscount.getText().toString().trim();
        String productDescription = descriptionEditText.getText().toString().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        if (productName.isEmpty() || price.isEmpty() || discountPercentage.isEmpty() || productDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize the dialog before using it
        dialog = new ProgressDialog(this);
        dialog.setMessage("Adding Product...");
        dialog.show();

        // Calculate discounted price
        double originalPrice = Double.parseDouble(price);
        double discount = Double.parseDouble(discountPercentage);
        double discountedPrice = originalPrice - (originalPrice * (discount / 100));

        // Set the discounted price in the TextView
        discountprice.setText(String.format("Discounted Price: $%.2f", discountedPrice));

        // Create a reference to the Firebase Storage where you want to store product images/videos
        final StorageReference productImageRef = storage.getReference().child("products").child(auth.getUid())
                .child(new Date().getTime() + "");

        // Check if an image is selected
        if (uri != null) {
            // Upload the image to Firebase Storage
            productImageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the download URL of the uploaded image
                    productImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri imageUri) {
                            // Create a ProductModel object
                            Product product = new Product();
                            product.setPrductname(productName);
                            product.setProductdescription(productDescription);
                            product.setPrice(Double.parseDouble(price));
                            product.setDiscount(Double.parseDouble(discountPercentage));
                            product.setProductimg(imageUri.toString());  // Set the image URL
                            product.setSelectedCategory(selectedCategory);
                            product.setCategoryId(categorySpinner.getSelectedItemPosition());  // Set the category ID based on the selected position

                            // Save the product to the user's shop
                            String userId = auth.getUid();
                            DatabaseReference userShopReference = database.getReference().child("shops").child(userId);
                            String shopId = userShopReference.child("products").push().getKey();
                            product.setShopId(shopId);
                            userShopReference.child("products").child(shopId).setValue(product);

                            // Save the product to the common "products" node
                            DatabaseReference commonProductsReference = database.getReference().child("products");
                            String productId = commonProductsReference.push().getKey();
                            product.setId(productId);
                            commonProductsReference.child(productId).setValue(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Dismiss the progress dialog
                                            dialog.dismiss();
                                            // Show a success message to the user
                                            Toast.makeText(Productadd.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                            // Finish the activity or navigate to another screen
                                            finish();
                                        }
                                    });
                        }
                    });
                }
            });
        } else {
            Toast.makeText(Productadd.this, "Please Add Product Image", Toast.LENGTH_SHORT).show();
            // If no image is selected, you can handle it accordingly
            // (e.g., show an error message or proceed without an image)
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            // Set the selected image to the ImageView
            productImageView.setImageURI(uri);
        }
    }
}
