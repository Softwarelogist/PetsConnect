package com.taas.petsconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Adapter.CommentAdapter;
import com.taas.petsconnect.Model.CommentModel;
import com.taas.petsconnect.Model.NotificationModel;
import com.taas.petsconnect.Model.PostModel;
import com.taas.petsconnect.Model.User;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    String postId;
    String postedBy;

    private int postLike;
    ImageView postimage;
    ImageView uprofile, btncommentpost;
    TextView postdes, like, comment, uname, uprofeesion;
    EditText commentet;
    FirebaseDatabase database;
    FirebaseAuth auth;
    VideoView postvideo;
    RecyclerView recyclerView;
    ArrayList<CommentModel> list = new ArrayList<>();
    private String commentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CommentActivity.this.setTitle("Comments");

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        uprofile = findViewById(R.id.uprofile);
        uname = findViewById(R.id.uname);
        uprofeesion = findViewById(R.id.uprofeesion);
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        postimage = findViewById(R.id.postimage);
        postvideo = findViewById(R.id.postvideo);
        postdes = findViewById(R.id.postdes);
        like = findViewById(R.id.like);
        btncommentpost = findViewById(R.id.btncommentpost);
        commentet = findViewById(R.id.commentet);
        comment = findViewById(R.id.commentText);
        recyclerView = findViewById(R.id.commentRV);

        postimage.setVisibility(View.GONE);
        postvideo.setVisibility(View.GONE);
        postdes.setVisibility(View.GONE);

        if (postId != null) {
            database.getReference().child("posts").child(postId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    PostModel postModel = snapshot.getValue(PostModel.class);

                    if (postModel != null) {
                        if (postModel.getPostimage() != null && !postModel.getPostimage().isEmpty()) {
                            postimage.setVisibility(View.VISIBLE);
                            Picasso.get().load(postModel.getPostimage()).placeholder(R.drawable.placeholder)
                                    .into(postimage);
                        }
                        if (postModel.getPostvideo() != null && !postModel.getPostvideo().isEmpty()) {
                            postvideo.setVisibility(View.VISIBLE);
                            // Set up VideoView with postModel.getPostvideo()
                            // Assuming you have the code to handle video loading here
                        }
                        if (postModel.getPostdes() != null && !postModel.getPostdes().isEmpty()) {
                            postdes.setVisibility(View.VISIBLE);
                            postdes.setText(postModel.getPostdes());
                        }

                        // Update postLike
                        postLike = postModel.getPostLike();
                        like.setText(String.valueOf(postLike));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to retrieve post details: " + error.getMessage());
                }
            });
        }
        database.getReference()
                .child("users").child(postedBy)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.d("UserData", snapshot.getValue().toString());
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                if (user.getUprofile() != null) {
                                    Picasso.get()
                                            .load(user.getUprofile())
                                            .placeholder(R.drawable.placeholder)
                                            .into(uprofile);
                                }
                                if (user.getUname() != null) {
                                    uname.setText(user.getUname());
                                }
                                if (user.getUprofession() != null) {
                                    uprofeesion.setText(user.getUprofession());
                                }
                            }
                        } else {
                            Log.d("UserData", "User data not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to retrieve user details: " + error.getMessage());
                    }
                });

        // Adjust constraints based on visibility of postimage and postvideo
        if (postimage.getVisibility() == View.VISIBLE) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) postdes.getLayoutParams();
            params.topToBottom = R.id.postimage;
            postdes.setLayoutParams(params);
        } else if (postvideo.getVisibility() == View.VISIBLE) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) postdes.getLayoutParams();
            params.topToBottom = R.id.postvideo;
            postdes.setLayoutParams(params);
        } else {
            // If both postimage and postvideo are gone, adjust constraints here
            // ...
        }

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(postId)
                .child("Likes")
                .child(FirebaseAuth.getInstance().getUid());

        // Set the initial like button state based on whether the user has liked the post
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like_red, 0, 0, 0);
                    like.setTag("liked"); // Set a tag to indicate that the user has liked this post
                } else {
                    like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like, 0, 0, 0);
                    like.setTag("notLiked"); // Set a tag to indicate that the user has not liked this post
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to retrieve like status: " + error.getMessage());
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (like.getTag().equals("liked")) {
                    // User has already liked the post, so unlike it
                    likeRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            postLike--;
                            if (postLike < 0) {
                                postLike = 0; // Ensure like count is never negative
                            }
                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(postId)
                                    .child("postLike")
                                    .setValue(postLike);
                            like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like, 0, 0, 0);
                            like.setTag("notLiked");
                            like.setText(String.valueOf(postLike));
                        }
                    });
                } else {
                    // User has not liked the post, so like it
                    likeRef.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            postLike++;
                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(postId)
                                    .child("postLike")
                                    .setValue(postLike);
                            like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__like_red, 0, 0, 0);
                            like.setTag("liked");
                            like.setText(String.valueOf(postLike));

                            NotificationModel notificationModel = new NotificationModel();
                            notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                            notificationModel.setNotificationAt(new Date().getTime());
                            notificationModel.setPostId(postId);
                            notificationModel.setPostedBy(postedBy);
                            notificationModel.setTypes("like");
                            FirebaseDatabase.getInstance().getReference()
                                    .child("notification")
                                    .child(postedBy)
                                    .push()
                                    .setValue(notificationModel);
                        }
                    });
                }
            }
        });
        btncommentpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentModel commentModel = new CommentModel();
                commentModel.setCommmentBody(commentet.getText().toString());
                commentModel.setCommentAt(new Date().getTime());
                commentModel.setCommentBy(auth.getUid());

                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(commentModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentCount")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentCount = 0;
                                                if (snapshot.exists()) {
                                                    commentCount = snapshot.getValue(Integer.class);
                                                }
                                                database.getReference()
                                                        .child("posts")
                                                        .child(postId)
                                                        .child("commentCount")
                                                        .setValue(commentCount + 1)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                commentet.setText("");
                                                                Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();

                                                                NotificationModel notificationModel = new NotificationModel();
                                                                notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                notificationModel.setNotificationAt(new Date().getTime());
                                                                notificationModel.setPostId(postId);
                                                                notificationModel.setPostedBy(postedBy);
                                                                notificationModel.setTypes("comment");
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("notification")
                                                                        .child(postedBy)
                                                                        .push()
                                                                        .setValue(notificationModel);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        });
            }
        });


        CommentAdapter adapter = new CommentAdapter(this, list, postId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CommentModel commentModel = dataSnapshot.getValue(CommentModel.class);
                            commentModel.setCommentId(dataSnapshot.getKey());
                            list.add(commentModel);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to retrieve comments: " + error.getMessage());
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}