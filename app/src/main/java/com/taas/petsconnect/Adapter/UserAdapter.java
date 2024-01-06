package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.FollowModel;
import com.taas.petsconnect.Model.NotificationModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;

import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private ArrayList<User> userList;
    private Context context;

    public UserAdapter(ArrayList<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        if (user != null) {
            Picasso.get().load(user.getUprofile()).placeholder(R.drawable.placeholder).into(holder.uprofile);
            holder.uname.setText(user.getUname());
            holder.uprofession.setText(user.getUprofession());

            DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(user.getUserID())
                    .child("followers")
                    .child(FirebaseAuth.getInstance().getUid());

            followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        updateFollowButton(holder, true);
                    } else {
                        updateFollowButton(holder, false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            });

            holder.followbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.followbtn.getText().equals("Follow")) {
                        followUser(user, holder);
                    } else {
                        unfollowUser(user, holder);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button followbtn;
        ImageView uprofile;
        TextView uname;
        TextView uprofession;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uprofile = itemView.findViewById(R.id.uprofile);
            uname = itemView.findViewById(R.id.uname);
            uprofession = itemView.findViewById(R.id.uprofeesion);
            followbtn = itemView.findViewById(R.id.followbtn);
        }
    }

    private void followUser(User user, ViewHolder holder) {
        FollowModel follow = new FollowModel();
        follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
        follow.setFollowedAt(new Date().getTime());

        DatabaseReference userFollowersRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(user.getUserID())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid());

        userFollowersRef.setValue(follow)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateFollowButton(holder, true);
                        updateFollowerCounts(user.getUserID(), true);
                        updateFollowingCounts(FirebaseAuth.getInstance().getUid(), true);
                        Toast.makeText(context, "You followed " + user.getUname(), Toast.LENGTH_SHORT).show();

                        NotificationModel notificationModel=new NotificationModel();
                        notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                        notificationModel.setNotificationAt(new Date().getTime());
                        notificationModel.setTypes("follow");
                        FirebaseDatabase.getInstance().getReference()
                                .child("notification")
                                .child(user.getUserID())
                                .push()
                                .setValue(notificationModel);
                    }
                });
    }

    private void unfollowUser(User user, ViewHolder holder) {
        DatabaseReference userFollowersRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(user.getUserID())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid());

        userFollowersRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateFollowButton(holder, false);
                        updateFollowerCounts(user.getUserID(), false);
                        updateFollowingCounts(FirebaseAuth.getInstance().getUid(), false);
                        Toast.makeText(context, "You unfollowed " + user.getUname(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateFollowButton(ViewHolder holder, boolean following) {
        if (following) {
            holder.followbtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_btn_bg));
            holder.followbtn.setText("UnFollow");
            holder.followbtn.setTextColor(context.getResources().getColor(R.color.black));
            holder.followbtn.setEnabled(true);
        } else {
            holder.followbtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.buttondesign));
            holder.followbtn.setText("Follow");
            holder.followbtn.setTextColor(context.getResources().getColor(R.color.white));
            holder.followbtn.setEnabled(true);
        }
    }

    private void updateFollowerCounts(String userID, boolean increment) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userID);

        userRef.child("followerCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long followerCount = snapshot.getValue(Long.class);
                    if (increment) {
                        followerCount++;
                    } else {
                        followerCount--;
                        if (followerCount < 0) {
                            followerCount = 0;
                        }
                    }
                    userRef.child("followerCount").setValue(followerCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void updateFollowingCounts(String userID, boolean increment) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userID);

        userRef.child("followingCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long followingCount = snapshot.getValue(Long.class);
                    if (increment) {
                        followingCount++;
                    } else {
                        followingCount--;
                        if (followingCount < 0) {
                            followingCount = 0;
                        }
                    }
                    userRef.child("followingCount").setValue(followingCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }
}
