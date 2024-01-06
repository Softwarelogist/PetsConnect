package com.taas.petsconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taas.petsconnect.Model.ArticleModel;
import com.taas.petsconnect.Model.PostModel;
import com.taas.petsconnect.Model.User;

import java.util.Date;

public class postarticle extends AppCompatActivity {
    EditText blogtitle, blogcontent;
    ImageView blogimage;
    Button btnpublish, btnaddarticleimage;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postarticle);
        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Publish an Article");
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        blogtitle = findViewById(R.id.blogtitle);
        blogcontent = findViewById(R.id.blogcontent);
        blogimage = findViewById(R.id.blogimage);
        btnpublish = findViewById(R.id.btnpublish);
        btnaddarticleimage = findViewById(R.id.btnaddarticleimage);

        btnaddarticleimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        btnpublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title  = blogtitle.getText().toString().trim();
                String content = blogcontent.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    blogtitle.setError("Title is required");
                } else if (TextUtils.isEmpty(content)) {
                    blogcontent.setError("Add content");
                } else {
                    if(uri !=null){
                    final StorageReference reference = storage.getReference()
                            .child("Article")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child(new Date().getTime() + "");
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                   ArticleModel articleModel=new ArticleModel();
                                    articleModel.setBlogimage(uri != null ?uri.toString():null);
                                    articleModel.setArticleBy(FirebaseAuth.getInstance().getUid());
                                    articleModel.setBlogtitle(title);
                                    articleModel.setBlogcontent(content);
                                    articleModel.setArticleAt(new Date().getTime());
                                    database.getReference()
                                            .child("Article")
                                            .push()
                                            .setValue(articleModel)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(postarticle.this, "Article published Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    });
                }else {
                        // No image selected, save article without image
                        ArticleModel articleModel = new ArticleModel();
                        articleModel.setArticleBy(FirebaseAuth.getInstance().getUid());
                        articleModel.setBlogtitle(title);
                        articleModel.setBlogcontent(content);
                        articleModel.setArticleAt(new Date().getTime());
                        database.getReference()
                                .child("Article")
                                .push()
                                .setValue(articleModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(postarticle.this, "Article published Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null && data.getData() != null) {
                uri = data.getData();
                blogimage.setImageURI(uri);
                blogimage.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
