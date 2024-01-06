package com.taas.petsconnect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;
import com.taas.petsconnect.chatwindo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageUserAdapter extends RecyclerView.Adapter<MessageUserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> usersList;

    public MessageUserAdapter(Context context, ArrayList<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=usersList.get(position);
        holder.username.setText(user.getUname());
        holder.userStatus.setText(user.getUprofession());
        Picasso.get().load(user.getUprofile()).into(holder.userImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, chatwindo.class);
                intent.putExtra("Userid",user.getUserID());
                intent.putExtra("name", user.getUname());
                intent.putExtra("receiverImg", user.getUprofile());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView username, userStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
            userStatus = itemView.findViewById(R.id.userstatus);
        }
    }
}
