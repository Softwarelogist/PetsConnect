package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.text.Html;
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
import com.taas.petsconnect.Model.NotificationModel;
import com.taas.petsconnect.Model.ReplyCommentModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;

import java.util.ArrayList;
import java.util.Date;

public class ReplyCommentAdapter extends RecyclerView.Adapter<ReplyCommentAdapter.viewHolder> {
    Context context;
    ArrayList<ReplyCommentModel> list;
    String postId;
    String commentId;

    public ReplyCommentAdapter(Context context, ArrayList<ReplyCommentModel> list, String postId, String commentId) {
        this.context = context;
        this.list = list;
        this.postId = postId;
        this.commentId = commentId;
    }

    // onCreateViewHolder and getItemCount remain unchanged

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ReplyCommentModel replyCommentModel = list.get(position);
        holder.comment.setText(replyCommentModel.getReplycommmentBody());
        String time = TimeAgo.using(replyCommentModel.getReplycommentAt());
        holder.time.setText(time);

        String userId = replyCommentModel.getReplycommentBy();
        if (userId != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Picasso.get().load(user.getUprofile()).placeholder(R.drawable.placeholder).into(holder.uprofile);
                        holder.comment.setText(Html.fromHtml("<b>" + user.getUname() + "</b>" + "   " + replyCommentModel.getReplycommmentBody()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error
                }
            });
        }

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(postId).child("comments")
                .child(commentId).child("Replies")
                .child(replyCommentModel.getReplyCommentId())
                .child("ReplyCommentLikes").child(FirebaseAuth.getInstance().getUid());
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.likecomment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like_red, 0, 0, 0);
                    holder.likecomment.setTag("liked");
                } else {
                    holder.likecomment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like, 0, 0, 0);
                    holder.likecomment.setTag("notLiked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

        holder.likecomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.likecomment.getTag().equals("liked")) {
                    likeRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            int updatedLikes = replyCommentModel.getReplycommentLike() - 1;
                            if (updatedLikes < 0) {
                                updatedLikes = 0;
                            }
                            FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("comments").child(commentId).child("Replies").child(replyCommentModel.getReplyCommentId()).child("ReplyCommentLikes").child(FirebaseAuth.getInstance().getUid()).setValue(updatedLikes);
                            holder.likecomment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like, 0, 0, 0);
                            holder.likecomment.setTag("notLiked");
                            holder.likecomment.setText(String.valueOf(updatedLikes));
                        }
                    });
                } else {
                    likeRef.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            int updatedLikes = replyCommentModel.getReplycommentLike() + 1;
                            FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("comments").child(commentId).child("Replies").child(replyCommentModel.getReplyCommentId()).child("ReplyCommentLikes").child(FirebaseAuth.getInstance().getUid()).setValue(updatedLikes);
                            holder.likecomment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like_red, 0, 0, 0);
                            holder.likecomment.setTag("liked");
                            holder.likecomment.setText(String.valueOf(updatedLikes));

                            NotificationModel notificationModel = new NotificationModel();
                            notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                            notificationModel.setNotificationAt(new Date().getTime());
                            notificationModel.setPostId(postId);
                            notificationModel.setReplycommentId(replyCommentModel.getReplyCommentId());
                            notificationModel.setCommentBy(replyCommentModel.getReplycommentBy());
                            notificationModel.setTypes("like");
                            FirebaseDatabase.getInstance().getReference().child("notification").child(replyCommentModel.getReplycommentBy()).push().setValue(notificationModel);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView uname, comment, time, reply, likecomment;
        ImageView uprofile;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            uprofile = itemView.findViewById(R.id.uprofile);
            uname = itemView.findViewById(R.id.uname);
            comment = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.time);
            reply = itemView.findViewById(R.id.reply);
            likecomment = itemView.findViewById(R.id.likecomment);
        }
    }
}
