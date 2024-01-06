package com.taas.petsconnect.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.CartItem;
import com.taas.petsconnect.Model.Order;
import com.taas.petsconnect.R;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnShowAllProductsClickListener showAllProductsClickListener;

    public OrderAdapter(Context context) {
        this.context = context;
        this.orders = new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    public void setOnShowAllProductsClickListener(OnShowAllProductsClickListener listener) {
        this.showAllProductsClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Bind order details to the view
        holder.textViewUserName.setText(order.getName());
        holder.textViewPhoneNumber.setText(order.getPhone());
        holder.textViewEmail.setText(order.getEmail());
        holder.textViewTotalPrice.setText(String.valueOf(order.getTotal()));
        holder.textViewAddress.setText(order.getAddress());
        holder.textViewDateTime.setText(order.getDate());

        // Set other order details...

        // Set click listener for showAllProductsButton
        holder.showAllProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show product images dialog
                if (showAllProductsClickListener != null) {
                    showAllProductsClickListener.onShowAllProductsClick(order.getCartItems());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private void showProductImagesDialog(List<CartItem> cartItems) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_product_images);

        RecyclerView recyclerView = dialog.findViewById(R.id.dialogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        ProductImageAdapter productImageAdapter = new ProductImageAdapter(context, cartItems);
        recyclerView.setAdapter(productImageAdapter);

        dialog.show();
    }

    public interface OnShowAllProductsClickListener {
        void onShowAllProductsClick(List<CartItem> cartItems);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        TextView textViewPhoneNumber;
        TextView textViewEmail;
        TextView textViewTotalPrice;
        TextView textViewAddress;
        TextView textViewDateTime;
        Button showAllProductsButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.order_user_name);
            textViewPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            textViewEmail = itemView.findViewById(R.id.order_email);
            textViewTotalPrice = itemView.findViewById(R.id.order_total_price);
            textViewAddress = itemView.findViewById(R.id.order_address);
            textViewDateTime = itemView.findViewById(R.id.order_date_time);
            showAllProductsButton = itemView.findViewById(R.id.show_all_products_btn);
        }
    }

    public static class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ProductImageViewHolder> {

        private Context context;
        private List<CartItem> cartItems;

        public ProductImageAdapter(Context context, List<CartItem> cartItems) {
            this.context = context;
            this.cartItems = cartItems;
        }

        @NonNull
        @Override
        public ProductImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product_image, parent, false);
            return new ProductImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductImageViewHolder holder, int position) {
            CartItem cartItem = cartItems.get(position);

            // Load product image using Picasso
            Picasso.get()
                    .load(cartItem.getProductimg())
                    .into(holder.productImageView);
        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        public static class ProductImageViewHolder extends RecyclerView.ViewHolder {
            ImageView productImageView;

            public ProductImageViewHolder(@NonNull View itemView) {
                super(itemView);
                productImageView = itemView.findViewById(R.id.productImageView);
            }
        }
    }
}
