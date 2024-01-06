package com.taas.petsconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Adapter.ReplyCommentAdapter;
import com.taas.petsconnect.Model.CommentModel;
import com.taas.petsconnect.Model.NotificationModel;
import com.taas.petsconnect.Model.ReplyCommentModel;
import com.taas.petsconnect.Model.User;

import java.util.ArrayList;
import java.util.Date;

public class ReplyCommentActivity extends AppCompatActivity {
    ImageView uprofile, btncommentpost;
    TextView likecomment, comment, uname;
    EditText commentet;
    RecyclerView recyclerView;
    ArrayList<ReplyCommentModel> list = new ArrayList<>();
    String postId;
    String commentId;
    String commentBy;
    FirebaseDatabase database;
    FirebaseAuth auth;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_comment);

        intent = getIntent();
        commentId = intent.getStringExtra("commentId");
        commentBy = intent.getStringExtra("commentBy");
        postId = intent.getStringExtra("postId");

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ReplyCommentActivity.this.setTitle("Reply");

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        uprofile = findViewById(R.id.uprofile);
        uname = findViewById(R.id.uname);
        btncommentpost = findViewById(R.id.btncommentpost);
        likecomment = findViewById(R.id.likecomment);
        commentet = findViewById(R.id.commentet);
        comment = findViewById(R.id.comment);
        recyclerView = findViewById(R.id.reply_RV);

        // Retrieve comment data
        if (commentId != null) {
            database.getReference()
                    .child("posts")
                    .child(postId)
                    .child("comments")
                    .child(commentId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            CommentModel commentModel = snapshot.getValue(CommentModel.class);
                            if (commentModel != null) {
                                comment.setText(commentModel.getCommmentBody());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("ReplyCommentActivity", "Error: " + error.getMessage());
                        }
                    });
        }

        database.getReference().child("users")
                .child(commentBy).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getUprofile())
                                .placeholder(R.drawable.placeholder)
                                .into(uprofile);
                        uname.setText(Html.fromHtml("<b>" + user.getUname() + "</b>"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        btncommentpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReplyCommentModel replyCommentModel = new ReplyCommentModel();
                replyCommentModel.setReplycommmentBody(commentet.getText().toString());
                replyCommentModel.setReplycommentAt(new Date().getTime());
                replyCommentModel.setReplycommentBy(auth.getUid());

                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("comments")
                        .child(commentId)
                        .child("Replies")
                        .push()
                        .setValue(replyCommentModel)
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
                                                                Toast.makeText(ReplyCommentActivity.this, "Replied", Toast.LENGTH_SHORT).show();

                                                                NotificationModel notificationModel = new NotificationModel();
                                                                notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                notificationModel.setNotificationAt(new Date().getTime());
                                                                notificationModel.setPostId(postId);
                                                                notificationModel.setReplycommentId(commentId);
                                                                notificationModel.setCommentBy(commentBy);
                                                                notificationModel.setTypes("reply");
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("notification")
                                                                        .child(commentBy)
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

        ReplyCommentAdapter adapter = new ReplyCommentAdapter(this, list, postId, commentId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments")
                .child(commentId)
                .child("Replies")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ReplyCommentModel replyCommentModel = dataSnapshot.getValue(ReplyCommentModel.class);
                            list.add(replyCommentModel);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}