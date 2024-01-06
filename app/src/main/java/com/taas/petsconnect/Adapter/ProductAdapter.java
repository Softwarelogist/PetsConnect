package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.Product;
import com.taas.petsconnect.ProductDetailActivity;
import com.taas.petsconnect.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private ArrayList<Product> products;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_rv_design, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        // Check for null values
        if (product != null) {
            holder.productlabel.setText(product.getPrductname());
            holder.description.setText(product.getProductdescription());
            Picasso.get()
                    .load(product.getProductimg())
                    .into(holder.productimg);
            // Use double for price
            holder.price.setText(Html.fromHtml("<b>PKR " + product.getPrice()));

            // Display discount information
            if (product.getDiscount() > 0) {
                holder.discount.setText(Html.fromHtml("<b>Discount: " + product.getDiscount() + "%</b>"));
                // Calculate discounted price
                double originalPrice = product.getPrice();
                double discountPercentage = product.getDiscount();
                double discountedPrice = originalPrice - (originalPrice * (discountPercentage / 100));

                holder.discountprice.setText(Html.fromHtml("<b>Discounted Price: PKR " + discountedPrice + "</b>"));
                holder.discount.setVisibility(View.VISIBLE);
                holder.discountprice.setVisibility(View.VISIBLE);
            } else {
                holder.discount.setVisibility(View.GONE);
                holder.discountprice.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("name", product.getPrductname());
                    intent.putExtra("image", product.getProductimg());
                    intent.putExtra("productdescription", product.getProductdescription());
                    intent.putExtra("discount", product.getDiscount());
                    intent.putExtra("discountprice", product.getDiscountprice());
                    intent.putExtra("id", product.getId());
                    intent.putExtra("price", product.getPrice());
                    context.startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productimg;
        TextView productlabel, price, discount, discountprice,description;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productimg = itemView.findViewById(R.id.productimg);
            productlabel = itemView.findViewById(R.id.productlabel);
            price = itemView.findViewById(R.id.productactuallprice);
            discount = itemView.findViewById(R.id.productdiscount);
            discountprice = itemView.findViewById(R.id.productnewprice);
            description=itemView.findViewById(R.id.product_description);

        }
    }
}
