package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.CommentModel;
import com.taas.petsconnect.Model.NotificationModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;
import com.taas.petsconnect.ReplyCommentActivity;

import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CommentModel> commentList;
    private String postId;


    public CommentAdapter(Context context, ArrayList<CommentModel> commentList, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentModel commentModel = commentList.get(position);
        holder.comment.setText(commentModel.getCommmentBody());
        String time = TimeAgo.using(commentModel.getCommentAt());
        holder.time.setText(time);

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(commentModel.getCommentBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            Picasso.get().load(user.getUprofile()).placeholder(R.drawable.placeholder)
                                    .into(holder.uprofile);
                            holder.comment.setText(Html.fromHtml("<b>" + user.getUname() + "</b>" + "   " + commentModel.getCommmentBody()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ReplyCommentActivity.class);
                intent.putExtra("postId",postId);
                intent.putExtra("commentId",commentModel.getCommentId());
                intent.putExtra("commentBy",commentModel.getCommentBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(postId)
                .child("comments")
                .child(commentModel.getCommentId())
                .child("CommentLikes")
                .child(FirebaseAuth.getInstance().getUid());
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.likecomment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like_red, 0, 0, 0);
                    holder.likecomment.setTag("liked"); // Set a tag to indicate that the user has liked this post
                } else {
                    holder.likecomment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like, 0, 0, 0);
                    holder.likecomment.setTag("notLiked"); // Set a tag to indicate that the user has not liked this post
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.likecomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.likecomment.getTag().equals("liked")) {
                    // User has already liked the post, so unlike it
                    likeRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            int updatedLikes = commentModel.getCommentLike()- 1;
                            if (updatedLikes < 0) {
                                updatedLikes = 0; // Ensure like count is never negative
                            }
                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(postId)
                                    .child("comments")
                                    .child(commentModel.getCommentId())
                                    .child("CommentLikes")
                                    .setValue(updatedLikes);
                            holder.likecomment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like, 0, 0, 0);
                            holder.likecomment.setTag("notLiked");
                            holder.likecomment.setText(String.valueOf(updatedLikes));
                        }
                    });
                } else {
                    // User has not liked the post, so like it
                    likeRef.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            int updatedLikes = commentModel.getCommentLike() + 1;
                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(postId)
                                    .child("comments")
                                    .child(commentModel.getCommentId())
                                    .child("CommentLikes")
                                    .setValue(updatedLikes);
                            holder.likecomment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like_red, 0, 0, 0);
                            holder.likecomment.setTag("liked");
                            holder.likecomment.setText(String.valueOf(updatedLikes));

                            NotificationModel notificationModel=new NotificationModel();
                            notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                            notificationModel.setNotificationAt(new Date().getTime());
                            notificationModel.setPostId(postId);
                            notificationModel.setCommentId(commentModel.getCommentId());
                            notificationModel.setCommentBy(commentModel.getCommentBy());
                            notificationModel.setTypes("like");
                            FirebaseDatabase.getInstance().getReference()
                                    .child("notification")
                                    .child(commentModel.getCommentBy())
                                    .push()
                                    .setValue(notificationModel);
                        }
                    });
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView uname, comment, time, reply,likecomment;
        ImageView uprofile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uprofile = itemView.findViewById(R.id.uprofile);
            uname = itemView.findViewById(R.id.uname);
            comment = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.time);
            reply = itemView.findViewById(R.id.reply);
            likecomment=itemView.findViewById(R.id.likecomment);

        }
    }
}