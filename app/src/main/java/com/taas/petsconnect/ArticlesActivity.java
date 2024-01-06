package com.taas.petsconnect;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taas.petsconnect.Adapter.ArticleAdapter;
import com.taas.petsconnect.Model.ArticleModel;

import java.util.ArrayList;
import java.util.List;

public class ArticlesActivity extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseAuth auth;
    Button btnpostarticle;
    RecyclerView articleRV;

    List<ArticleModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Articles");

        btnpostarticle = findViewById(R.id.btnpostarticle);
        btnpostarticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticlesActivity.this, postarticle.class);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        articleRV = findViewById(R.id.articleRV);
        list = new ArrayList<>();




        ArticleAdapter articleAdapter = new ArticleAdapter(list, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        articleRV.setLayoutManager(layoutManager);
        articleRV.setAdapter(articleAdapter);
        database.getReference()
                .child("Article")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ArticleModel articleModel = dataSnapshot.getValue(ArticleModel.class);
                            list.add(articleModel);
                        }
                        articleAdapter.notifyDataSetChanged();
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
