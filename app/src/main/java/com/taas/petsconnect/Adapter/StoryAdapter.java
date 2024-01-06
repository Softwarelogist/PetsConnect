package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.StoryModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.Model.UserStories;
import com.taas.petsconnect.R;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private ArrayList<StoryModel> list;
    private Context context;

    public StoryAdapter(ArrayList<StoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stroy_vr_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoryModel storyModel = list.get(position);
        ArrayList<UserStories> stories = storyModel.getStories();
        if (!stories.isEmpty()) {
            UserStories lastStory = stories.get(stories.size() - 1);

            // Check if story is older than 24 hours
            long currentTime = System.currentTimeMillis();
            long storyTimestamp = lastStory.getStoryAt();
            if (currentTime - storyTimestamp <= 24 * 60 * 60 * 1000) {
                // Show the story
                Picasso.get()
                        .load(lastStory.getImage())
                        .into(holder.addStoryImage);
                holder.statuscircle.setPortionsCount(stories.size());
                holder.addStoryImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<MyStory> myStories = new ArrayList<>();
                        for(UserStories stories: storyModel.getStories()){
                            myStories.add(new MyStory(
                                    stories.getImage()
                            ));
                        }
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(storyModel.getStoryBy()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user=snapshot.getValue(User.class);
                                        if (user != null) {
                                            user.setUname(snapshot.child("uname").getValue(String.class));
                                            user.setUprofession(snapshot.child("uprofession").getValue(String.class));
                                            user.setUprofile(snapshot.child("uprofile").getValue(String.class));
                                            showStoryView(user, myStories);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle errors
                                    }
                                });
                    }
                });

            } else {
                // Story is older than 24 hours, hide it
                holder.itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView addStoryImage, uprofile;
        TextView uname;
        CircularStatusView statuscircle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addStoryImage = itemView.findViewById(R.id.addStoryImage);
            uprofile = itemView.findViewById(R.id.uprofile);
            uname = itemView.findViewById(R.id.uname);
            statuscircle = itemView.findViewById(R.id.statuscircle);
        }
    }

    private void showStoryView(User user, ArrayList<MyStory> myStories) {
        new StoryView.Builder(((AppCompatActivity)context).getSupportFragmentManager())
                .setStoriesList(myStories)
                .setStoryDuration(5000)
                .setTitleText(user.getUname())
                .setSubtitleText(user.getUprofession())
                .setTitleLogoUrl(user.getUprofile())
                .setStoryClickListeners(new StoryClickListeners() {
                    @Override
                    public void onDescriptionClickListener(int position) {
                        // your action
                    }

                    @Override
                    public void onTitleIconClickListener(int position) {
                        // your action
                    }
                })
                .build()
                .show();
    }
}
