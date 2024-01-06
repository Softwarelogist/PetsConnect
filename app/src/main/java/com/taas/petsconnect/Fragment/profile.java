package com.taas.petsconnect.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Adapter.FollowAdapter;
import com.taas.petsconnect.Adapter.PostAdapter;
import com.taas.petsconnect.ArticlesActivity;
import com.taas.petsconnect.MessageActivity;
import com.taas.petsconnect.Model.FollowModel;
import com.taas.petsconnect.Model.PostModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;
import com.taas.petsconnect.postarticle;

import java.util.ArrayList;
import java.util.List;

public class profile extends Fragment {

    private TextView btnsee;
    private  ImageView  btnccp,message;
    private ImageView btneditprofile;
    private ImageView cover_Pic;
    private ImageView uprofile;
    private TextView Uname;
    private TextView Uprofession;
    private TextView followers,following,post;
    private FollowAdapter friendsAdapter;
    private List<FollowModel> followerList;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;

    private RecyclerView postRV;
    private ArrayList<FollowModel> list;
    private ArrayList<PostModel> postlist;
    private RecyclerView followRV;

    public profile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        followerList = new ArrayList<>();
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Uname = view.findViewById(R.id.uname);
        Uprofession = view.findViewById(R.id.uprofeesion);
        post=view.findViewById(R.id.post);
        cover_Pic = view.findViewById(R.id.cover_Pic);
        uprofile = view.findViewById(R.id.uprofile);
        followers = view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        if (database != null && auth != null && auth.getUid() != null) {
            database.getReference().child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            Uname.setText(user.getUname());
                            Uprofession.setText(user.getUprofession());
                            followers.setText(user.getFollowerCount() + "");
                            following.setText(user.getFollowingCount()+"");
                            post.setText(user.getPostCount()+"");
                            Picasso.get().load(user.getCover_Pic()).placeholder(R.drawable.placeholder).into(cover_Pic);
                            Picasso.get().load(user.getUprofile()).placeholder(R.drawable.placeholder).into(uprofile);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error in loading user data from the database
                }
            });
        }


        btnsee = view.findViewById(R.id.btnsee);
        btneditprofile = view.findViewById(R.id.btneditprofile);
        btnccp = view.findViewById(R.id.btnccp);
        cover_Pic = view.findViewById(R.id.cover_Pic);
        uprofile = view.findViewById(R.id.uprofile);
        Uname = view.findViewById(R.id.uname);
        Uprofession = view.findViewById(R.id.uprofeesion);
        message=view.findViewById(R.id.message);
        btnsee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment friendFragment = new FriendFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainConstraint, friendFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnccp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 11);
            }
        });

        btneditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 22);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                startActivity(intent);

            }
        });

        followRV = view.findViewById(R.id.followRV);
        list = new ArrayList<>();
        FollowAdapter adapter = new FollowAdapter(list, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        followRV.setLayoutManager(layoutManager);
        followRV.setAdapter(adapter);
        database.getReference()
                .child("users")
                .child(auth.getUid())
                .child("followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FollowModel followModel = dataSnapshot.getValue(FollowModel.class);
                            list.add(followModel);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        postRV = view.findViewById(R.id.postRV);
        postlist = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(postlist, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postRV.setLayoutManager(new LinearLayoutManager(getContext()));
        postRV.addItemDecoration(new DividerItemDecoration(postRV.getContext(), DividerItemDecoration.VERTICAL));
        postRV.setAdapter(postAdapter);

        database = FirebaseDatabase.getInstance();

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostModel postmodel = dataSnapshot.getValue(PostModel.class);
                    postmodel.setPostId(dataSnapshot.getKey());
                    postlist.add(postmodel);
                }
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                cover_Pic.setImageURI(selectedImageUri);
                final StorageReference reference = storage.getReference().child("cover_pic").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Cover Picture changed", Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("cover_Pic").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        } else if (requestCode == 22) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                uprofile.setImageURI(selectedImageUri);
                final StorageReference reference = storage.getReference().child("uprofile").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Profile Picture changed", Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uprofile").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }
    }
}