package com.taas.petsconnect.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.taas.petsconnect.Adapter.PostAdapter;
import com.taas.petsconnect.Adapter.StoryAdapter;
import com.taas.petsconnect.ArticlesActivity;
import com.taas.petsconnect.MainActivity;
import com.taas.petsconnect.MessageActivity;
import com.taas.petsconnect.Model.PostModel;
import com.taas.petsconnect.Model.StoryModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.Model.UserStories;
import com.taas.petsconnect.R;

import java.util.ArrayList;
import java.util.Date;

public class home extends Fragment {
    private ImageView uprofile;
    RecyclerView storyRV, postRV;
    ArrayList<StoryModel> storylist;
    ArrayList<PostModel> dashboardlist;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    RoundedImageView addStoryImage;

    ActivityResultLauncher<String> gallaryLauncher;
    ProgressDialog dialog;

    public home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getContext());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);

        //Story Section Code Start
        storyRV = view.findViewById(R.id.storyRV);
        storylist = new ArrayList<>();
        StoryAdapter adapter = new StoryAdapter(storylist, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        storyRV.setLayoutManager(layoutManager);
        storyRV.setNestedScrollingEnabled(false);
        storyRV.setAdapter(adapter);
        database.getReference()
                .child("stories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            storylist.clear();
                            for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                                StoryModel storyModel = new StoryModel();
                                storyModel.setStoryBy(storySnapshot.getKey());
                                storyModel.setStoryAt(storySnapshot.child("storyBy").getValue(Long.class));
                                ArrayList<UserStories> stories = new ArrayList<>();
                                for (DataSnapshot snapshot1 : storySnapshot.child("userstories").getChildren()) {
                                    UserStories userStories = snapshot1.getValue(UserStories.class);
                                    stories.add(userStories);
                                }
                                storyModel.setStories(stories);
                                storylist.add(storyModel);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //Story Section Code End

        postRV = view.findViewById(R.id.postRV);
        dashboardlist = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(dashboardlist, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postRV.setLayoutManager(linearLayoutManager);
        postRV.addItemDecoration(new DividerItemDecoration(postRV.getContext(), DividerItemDecoration.VERTICAL));
        postRV.setAdapter(postAdapter);
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dashboardlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostModel postModel = dataSnapshot.getValue(PostModel.class);
                    postModel.setPostId(dataSnapshot.getKey());
                    dashboardlist.add(postModel);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        addStoryImage = view.findViewById(R.id.addStoryImage);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallaryLauncher.launch("image/*");
            }
        });
        gallaryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                addStoryImage.setImageURI(result);
                dialog.show();
                final StorageReference reference = storage.getReference()
                        .child("stories")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime() + "");
                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                StoryModel storyModel = new StoryModel();
                                storyModel.setStoryAt(new Date().getTime());
                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("storyBy")
                                        .setValue(storyModel.getStoryAt())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                UserStories userStories = new UserStories(uri.toString(), storyModel.getStoryAt());
                                                database.getReference()
                                                        .child("stories")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("userstories")
                                                        .push()
                                                        .setValue(userStories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.dismiss();
                                                            }
                                                        });

                                            }
                                        });
                            }
                        });
                    }
                });

            }
        });
        CardView info;
        info = view.findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ArticlesActivity.class);
                startActivity(intent);
            }
        });
        CardView message;
        message = view.findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                startActivity(intent);

            }
        });
        uprofile = view.findViewById(R.id.uprofile);
        if (database != null && auth != null && auth.getUid() != null) {
            database.getReference().child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
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
        uprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile profileFragment = new profile();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainConstraint,profileFragment );
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
