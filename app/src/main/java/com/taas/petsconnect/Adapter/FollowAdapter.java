package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.FollowModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    private static final int MAX_ITEMS = 5;
    private List<FollowModel> list;
    private Context context;
    private FirebaseDatabase database;




    public FollowAdapter(List<FollowModel> list, Context context) {
        this.list = list;
        this.context = context;
        this.database = FirebaseDatabase.getInstance();

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.followers_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowModel followModel = list.get(position);
        String followedBy = followModel.getFollowedBy();
        if (followedBy != null){
        FirebaseDatabase.getInstance().getReference().child("users").child(followModel.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Picasso.get()
                            .load(user.getUprofile())
                            .placeholder(R.drawable.placeholder)
                            .into((ImageView) holder.itemView.findViewById(R.id.uprofile));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }}

    @Override
    public int getItemCount() {
        return Math.min(list.size(), MAX_ITEMS);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.followers);
        }
    }
}
