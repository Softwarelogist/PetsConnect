package com.taas.petsconnect.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.CartItem;
import com.taas.petsconnect.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private ArrayList<CartItem> cartItems;
    private CartListener cartListener;
    private String userId;

    public interface CartListener {
        void onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<CartItem> cartItems, CartListener cartListener, String userId) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartListener = cartListener;
        this.userId = userId;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        Picasso.get()
                .load(cartItem.getProductimg())
                .into(holder.image);

        holder.name.setText(cartItem.getProductName());

        // Display original and discounted prices
        holder.price.setText(Html.fromHtml("<b>OP: PKR " + cartItem.getPrice()));
        if (cartItem.getDiscount() > 0) {
            double discountedPrice = calculateDiscountedPrice(cartItem.getPrice(), cartItem.getDiscount());
            holder.discount.setText(Html.fromHtml("<b>DP: PKR " + discountedPrice));
            holder.discount.setVisibility(View.VISIBLE);
        } else {
            holder.discount.setVisibility(View.GONE);
        }

        holder.quantity.setText(String.valueOf(cartItem.getQuantity()) + " item(s)");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                View dialogView = LayoutInflater.from(context).inflate(R.layout.quantity_dialog, null);

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setView(dialogView)
                        .create();
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                TextView productNameTextView = dialogView.findViewById(R.id.productName);
                TextView quantityTextView = dialogView.findViewById(R.id.quantity);
                Button plusBtn = dialogView.findViewById(R.id.plusBtn);
                Button minusBtn = dialogView.findViewById(R.id.minusBtn);
                Button saveBtn = dialogView.findViewById(R.id.saveBtn);
                Button deleteBtn = dialogView.findViewById(R.id.deletebtn);

                productNameTextView.setText(cartItem.getProductName());
                final int[] currentQuantity = {cartItem.getQuantity()}; // Use an array to make it effectively final
                quantityTextView.setText(String.valueOf(currentQuantity[0]));

                plusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Increment the local quantity
                        currentQuantity[0]++;
                        quantityTextView.setText(String.valueOf(currentQuantity[0]));
                    }
                });

                minusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Decrement the local quantity, ensuring it doesn't go below 1
                        if (currentQuantity[0] > 1) {
                            currentQuantity[0]--;
                            quantityTextView.setText(String.valueOf(currentQuantity[0]));
                        }
                    }
                });

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle save button click
                        // Update the quantity in the CartItem object
                        cartItem.setQuantity(currentQuantity[0]);

                        // Notify the listener (CartActivity) about the quantity change
                        if (cartListener != null) {
                            cartListener.onQuantityChanged();
                        }
                        updateFirebaseQuantity(cartItem.getId(), currentQuantity[0]);
                        // Dismiss the dialog
                        alertDialog.dismiss();
                    }
                });

                // Set an onClickListener for the delete button
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmationDialog(cartItem);
                        alertDialog.dismiss(); // Dismiss the dialog after showing the delete confirmation
                    }
                });

                // Show the AlertDialog
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, discount, quantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            discount = itemView.findViewById(R.id.discount);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }

    private void updateFirebaseQuantity(String cartItemId, int quantity) {
        // Update the quantity in Firebase
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("carts")
                .child(userId)
                .child(cartItemId);

        // Update the quantity field in Firebase
        cartItemRef.child("quantity").setValue(quantity);
    }

    private void showDeleteConfirmationDialog(CartItem cartItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item from the cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Delete the item
                    removeItemFromCart(cartItem);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                })
                .show();
    }

    private void removeItemFromCart(CartItem cartItem) {
        // Remove the item from the dataset
        cartItems.remove(cartItem);
        notifyDataSetChanged();

        // Update Firebase to remove the item
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("carts")
                .child(userId)
                .child(cartItem.getId());

        cartItemRef.removeValue();

        // Notify the listener (CartActivity) about the quantity change
        if (cartListener != null) {
            cartListener.onQuantityChanged();
        }
    }

    private double calculateDiscountedPrice(double originalPrice, double discountPercentage) {
        return originalPrice - (originalPrice * (discountPercentage / 100));
    }
}
