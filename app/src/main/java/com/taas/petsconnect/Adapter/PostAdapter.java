package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.CommentActivity;
import com.taas.petsconnect.Model.NotificationModel;
import com.taas.petsconnect.Model.PostModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;
import java.util.ArrayList;
import java.util.Date;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    private ArrayList<PostModel> list;
    private Context context;

    public PostAdapter(ArrayList<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public PostAdapter() {
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_rv_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostModel postModel = list.get(position);
        if (postModel.getPostimage() != null && !postModel.getPostimage().isEmpty()) {
            Picasso.get().load(postModel.getPostimage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.postimg);
            holder.postimg.setVisibility(View.VISIBLE);
        } else {
            // If postimage is empty or null, hide the ImageView
            holder.postimg.setVisibility(View.GONE);
        }

        if (postModel.getPostvideo() != null && !postModel.getPostvideo().isEmpty()) {
            holder.postvideo.setVideoURI(Uri.parse(postModel.getPostvideo()));
            holder.postvideo.setVisibility(View.VISIBLE);

            // Enable default media controller
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.postvideo);

            // Set media controller for the VideoView
            holder.postvideo.setMediaController(mediaController);

            // Set error listener for the VideoView
            holder.postvideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // Handle errors here
                    // You can log the error, show a message to the user, or take other actions.
                    return true; // Return true to indicate that you've handled the error
                }
            });

            holder.postvideo.start();
        } else {
            // If there's no video URL, hide the video view
            holder.postvideo.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(postModel.getPostedBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null /*&& user.getUprofile() != null*/) {
                            Picasso.get().load(user.getUprofile())
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.uprofile);
                            holder.uname.setText(user.getUname());
                            holder.uprofeesion.setText(user.getUprofession());
                            holder.postdes.setText(postModel.getPostdes());
                            holder.comment.setText(postModel.getCommentCount()+"");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(postModel.getPostId())
                .child("Likes")
                .child(FirebaseAuth.getInstance().getUid());

        // Set the initial like button state based on whether the user has liked the post
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like_red, 0, 0, 0);
                    holder.like.setTag("liked"); // Set a tag to indicate that the user has liked this post
                } else {
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like, 0, 0, 0);
                    holder.like.setTag("notLiked"); // Set a tag to indicate that the user has not liked this post
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("liked")) {
                    // User has already liked the post, so unlike it
                    likeRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            int updatedLikes = postModel.getPostLike() - 1;
                            if (updatedLikes < 0) {
                                updatedLikes = 0; // Ensure like count is never negative
                            }
                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(postModel.getPostId())
                                    .child("postLike")
                                    .setValue(updatedLikes);
                            holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like, 0, 0, 0);
                            holder.like.setTag("notLiked");
                            holder.like.setText(String.valueOf(updatedLikes));
                        }
                    });
                } else {
                    // User has not liked the post, so like it
                    likeRef.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            int updatedLikes = postModel.getPostLike() + 1;
                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(postModel.getPostId())
                                    .child("postLike")
                                    .setValue(updatedLikes);
                            holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like_red, 0, 0, 0);
                            holder.like.setTag("liked");
                            holder.like.setText(String.valueOf(updatedLikes));

                            NotificationModel notificationModel=new NotificationModel();
                            notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                            notificationModel.setNotificationAt(new Date().getTime());
                            notificationModel.setPostId(postModel.getPostId());
                            notificationModel.setPostedBy(postModel.getPostedBy());
                            notificationModel.setTypes("like");
                            FirebaseDatabase.getInstance().getReference()
                                    .child("notification")
                                    .child(postModel.getPostedBy())
                                    .push()
                                    .setValue(notificationModel);
                        }
                    });
                }
            }
        });

        // Set the like count text
        holder.like.setText(String.valueOf(postModel.getPostLike()));
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", postModel.getPostId());
                intent.putExtra("postedBy", postModel.getPostedBy()); // Corrected key
                intent.putExtra("postLike",postModel.getPostLike());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Corrected flag
                context.startActivity(intent);
            }
        });
    }

        @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView uprofile, postimg, saveImg;
        VideoView postvideo;
        TextView uname, uprofeesion, like, comment, share, postdes;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            uprofile = itemView.findViewById(R.id.uprofile);
            postimg = itemView.findViewById(R.id.postimage);
            postvideo = itemView.findViewById(R.id.postvideo);
            saveImg = itemView.findViewById(R.id.save);
            uname = itemView.findViewById(R.id.uname);
            uprofeesion = itemView.findViewById(R.id.uprofeesion);
            like = itemView.findViewById(R.id.likecomment);
            comment = itemView.findViewById(R.id.commentText);
            share = itemView.findViewById(R.id.share);
            postdes = itemView.findViewById(R.id.postdes);
        }
    }
}
