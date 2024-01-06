package com.taas.petsconnect.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Model.ArticleModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.viewHolder> {
    private List<ArticleModel> list;
    private Context context;

    public ArticleAdapter(List<ArticleModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.articles_rv_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ArticleModel articleModel = list.get(position);
        if (articleModel.getBlogimage() != null && !articleModel.getBlogimage().isEmpty()) {
            Picasso.get().load(articleModel.getBlogimage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.blogimage);
            holder.blogimage.setVisibility(View.VISIBLE);
        } else {
            // If postimage is empty or null, hide the ImageView
            holder.blogimage.setVisibility(View.GONE);
        }
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(articleModel.getArticleBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null ) {
                            holder.uname.setText(user.getUname());
                            holder.blogtitle.setText(articleModel.getBlogtitle());
                            holder.blogcontent.setText(articleModel.getBlogcontent());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView blogimage;
        TextView blogcontent, blogtitle,uname;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            blogimage = itemView.findViewById(R.id.blogimage);
            blogtitle = itemView.findViewById(R.id.blogtitle);
            blogcontent = itemView.findViewById(R.id.blogcontent);
            uname=itemView.findViewById(R.id.uname);
        }
    }
}
