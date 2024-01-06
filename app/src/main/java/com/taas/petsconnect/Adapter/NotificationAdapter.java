package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.CommentActivity;
import com.taas.petsconnect.Model.NotificationModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    ArrayList<NotificationModel> list;
    Context context;

    public NotificationAdapter(ArrayList<NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv_desing, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel notificationModel = list.get(position);
        String type = notificationModel.getTypes();
        String time= TimeAgo.using(notificationModel.getNotificationAt());
        holder.time.setText(time);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(notificationModel.getNotificationBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getUprofile()) // Assuming model.getProfile() returns the image URL or resource
                                .into(holder.uprofile);
                        if (type.equals("like")) {
                            holder.notification.setText(Html.fromHtml("<b>" + user.getUname() + "</b>" + " liked your post"));
                        } else if (type.equals("comment")) {
                            holder.notification.setText(Html.fromHtml("<b>" + user.getUname() + "</b>" + " Commented on your post"));
                        } else if(type.equals("reply")) {
                            holder.notification.setText(Html.fromHtml("<b>" + user.getUname() + "</b>" + " Reply your Comment "));
                        } else {
                            holder.notification.setText(Html.fromHtml("<b>" + user.getUname() + "</b>" + " Start following you"));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!type.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("notification")
                            .child(notificationModel.getPostedBy())
                            .child(notificationModel.getNotificationId())
                            .child("checkOpen")
                            .setValue(true);
                    holder.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", notificationModel.getPostId());
                    intent.putExtra("postedBy", notificationModel.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });

        // Load the image using Picasso
       /*
        holder.notification.setText(Html.fromHtml(model.getNotification()));
        holder.time.setText(model.getTime());*/
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView uprofile;
        TextView notification, time;
        ConstraintLayout openNotification;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            uprofile = itemView.findViewById(R.id.uprofile);
            notification = itemView.findViewById(R.id.notification);
            time = itemView.findViewById(R.id.time);
            openNotification = itemView.findViewById(R.id.openNotification);
        }
    }
}
