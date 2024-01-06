package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.Category;
import com.taas.petsconnect.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    private Context context;
    private ArrayList<Category> categories;
    private OnCategorySelectedListener categorySelectedListener;

    public CategoryAdapter(Context context, ArrayList<Category> categories, OnCategorySelectedListener listener) {
        this.context = context;
        this.categories = categories;
        this.categorySelectedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categories_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categorylabel.setText(Html.fromHtml(category.getName()));
        Picasso.get().load(category.getIcon()).into(holder.categoryicon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categorySelectedListener != null) {
                    categorySelectedListener.onCategorySelected(category);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categorylabel;
        ImageView categoryicon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categorylabel = itemView.findViewById(R.id.categorylabel);
            categoryicon = itemView.findViewById(R.id.categoryicon);
        }
    }
}
