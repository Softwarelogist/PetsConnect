package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.R;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private List<String> imageUrlList; // Change the type to String

    public SliderAdapter(Context context, List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        String imageUrl = imageUrlList.get(position); // Change the type to String

        // Use an image loading library like Picasso or Glide to load the image
        Picasso.get().load(imageUrl).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return imageUrlList.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        ImageView imageView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
