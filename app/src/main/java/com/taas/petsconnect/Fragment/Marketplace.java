package com.taas.petsconnect.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.smarteist.autoimageslider.SliderView;
import com.taas.petsconnect.Adapter.CategoryAdapter;
import com.taas.petsconnect.Adapter.ProductAdapter;
import com.taas.petsconnect.Adapter.SliderAdapter;
import com.taas.petsconnect.Model.Category;
import com.taas.petsconnect.Model.Product;
import com.taas.petsconnect.Productadd;
import com.taas.petsconnect.R;
import com.taas.petsconnect.SearchActivity;
import com.taas.petsconnect.ShopRegistrationActivity;
import com.taas.petsconnect.ShopStatusActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Marketplace extends Fragment implements CategoryAdapter.OnCategorySelectedListener {

    RecyclerView categoriesListRV;
    RecyclerView productListRV;
    MaterialSearchBar searchBar;
    SliderView sliderView;

    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;

    ProductAdapter productAdapter;

    ArrayList<Product> products;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;

    public Marketplace() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(requireActivity(), SearchActivity.class);
                intent.putExtra("query", text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });

        categoriesListRV = view.findViewById(R.id.categoriesListRV);
        productListRV = view.findViewById(R.id.productListRV);

        sliderView = view.findViewById(R.id.imageSlider);
        initSlider();
        initCategories();
        initProducts();

        Button openshopbtn = view.findViewById(R.id.openshopbtn);
        if (auth.getCurrentUser() != null) {
            final String userId = auth.getCurrentUser().getUid();

            openshopbtn = view.findViewById(R.id.openshopbtn);

            // Declare openshopbtn as final
            final Button finalOpenshopbtn = openshopbtn;

            database.getReference("shops").child("verificationRequests").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Navigate to the specific shop node
                        DataSnapshot shopSnapshot = dataSnapshot.child(userId);

                        if (dataSnapshot.exists()) {
                            // Retrieve the "status" field
                            String shopStatus = dataSnapshot.child("status").getValue(String.class);


                            if ("approved".equals(shopStatus)) {
                                // Use finalOpenshopbtn instead of openshopbtn
                                finalOpenshopbtn.setText("Add Product");
                                finalOpenshopbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openProductAddActivity();
                                       /* Intent intent = new Intent(getActivity(), Productadd.class);
                                        startActivity(intent);*/
                                    }
                                });
                            } else if ("rejected".equals(shopStatus)) {
                                // Use finalOpenshopbtn instead of openshopbtn
                                finalOpenshopbtn.setText("Open Shop");
                                finalOpenshopbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), ShopRegistrationActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                // Use finalOpenshopbtn instead of openshopbtn
                                finalOpenshopbtn.setText("Check Status");
                                finalOpenshopbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), ShopStatusActivity.class);
                                        startActivity(intent);
                                    }
                                });

                                // Display a Toast with the current shop status
                                Toast.makeText(requireActivity(), "Current shop status: " + shopStatus, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // "status" field does not exist in the shopSnapshot
                            Log.d("ShopData", "Status field does not exist");

                            // Use finalOpenshopbtn instead of openshopbtn
                            finalOpenshopbtn.setText("Open Shop");
                            finalOpenshopbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), ShopRegistrationActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    } else {
                        // Shop node does not exist
                        Log.d("ShopData", "Shop node does not exist");

                        // Use finalOpenshopbtn instead of openshopbtn
                        finalOpenshopbtn.setText("Open Shop");
                        finalOpenshopbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), ShopRegistrationActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        } else {
            openshopbtn.setText("Open Shop");
            openshopbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ShopRegistrationActivity.class);
                }
            });
        }
        return view;
    }

    private void initSlider() {
        // Reference to the "sliders" node in your Firebase database
        DatabaseReference slidersReference = database.getReference("sliders");

        slidersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> imageUrls = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Assuming you have a field called "imageUrl" in your slider node
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    if (imageUrl != null) {
                        imageUrls.add(imageUrl);
                    }
                }

                SliderAdapter adapter = new SliderAdapter(requireContext(), imageUrls);
                sliderView.setSliderAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    private void initCategories() {
        categories = new ArrayList<>();

        categories.add(new Category("Cat", R.drawable.caticon, 0));
        categories.add(new Category("Dog", R.drawable.icondog, 1));
        categories.add(new Category("Rabbit", R.drawable.iconrabbit, 2));
        categories.add(new Category("Birds", R.drawable.iconbird, 3));
        categories.add(new Category("Goat", R.drawable.icongot, 4));
        categories.add(new Category("Food", R.drawable.iconfood, 5));
        categories.add(new Category("Toy", R.drawable.petstoyicon, 6));
        categories.add(new Category("Other", R.drawable.foodicon2, 7));
        categoryAdapter = new CategoryAdapter(requireContext(), categories, this);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 4);
        categoriesListRV.setLayoutManager(layoutManager);
        categoriesListRV.setAdapter(categoryAdapter);
    }


    private void initProducts() {
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), products);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        productListRV.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
        productListRV.setAdapter(productAdapter);
        fetchProducts();
    }
    private void fetchProducts() {
        // Fetch products from the common "products" node
        DatabaseReference commonProductsReference = database.getReference().child("products");
        commonProductsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Add products from the common "products" node
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    product.setId(dataSnapshot.getKey());
                    // Add the product only if it's not already added (avoid duplicates)
                    if (!products.contains(product)) {
                        products.add(product);
                    }
                }
                // Notify the adapter about the data change
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

    }

    @Override
    public void onCategorySelected(Category category) {

        filterProductsByCategory(category);
    }

    private void filterProductsByCategory(Category selectedCategory) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategoryId() == selectedCategory.getId()) {
                filteredProducts.add(product);
            }
        }
        productAdapter.setProducts(filteredProducts);
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }


    private void openProductAddActivity() {
        Intent intent = new Intent(getActivity(), Productadd.class);

        ArrayList<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }
        intent.putStringArrayListExtra("categoryNames", categoryNames);

        startActivity(intent);
    }

}