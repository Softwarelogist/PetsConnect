package com.taas.petsconnect.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.taas.petsconnect.Model.PostModel;
import com.taas.petsconnect.Model.User;
import com.taas.petsconnect.R;

import java.util.Date;

public class addpost extends Fragment {
    RecyclerView recyclerView;
    private ImageView postimage;
    private VideoView postvideo;
    private ImageView btnaddimage, btnaddvideo, btnaddlocation;
    private Button btnpost;
    private TextView postdes, uname, uprofession;
    private ImageView uprofile;
    private Context context;
    private Uri selectedImageUri, selectedVideoUri;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    ProgressDialog dialog;

    public addpost() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog=new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addpost, container, false);
        uprofile = view.findViewById(R.id.uprofile);
        uname = view.findViewById(R.id.uname);
        uprofession = view.findViewById(R.id.uprofeesion);
        postdes = view.findViewById(R.id.postdes);
        btnpost = view.findViewById(R.id.btnpost);
        postimage = view.findViewById(R.id.postimage);
        postvideo = view.findViewById(R.id.postvideo);
        btnaddimage = view.findViewById(R.id.btnaddimage);
        btnaddvideo = view.findViewById(R.id.btnaddvideo);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Post Uploading");
        dialog.setMessage("Please wait ....");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        database.getReference().child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            Picasso.get()
                                    .load(user.getUprofile())
                                    .placeholder(R.drawable.placeholder)
                                    .into(uprofile);
                            uname.setText(user.getUname());
                            uprofession.setText(user.getUprofession());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        postdes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String description = postdes.getText().toString();
                if (!description.isEmpty()) {
                    btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.buttondesign));
                    btnpost.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    btnpost.setEnabled(true);
                } else {
                    btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btnpost));
                    btnpost.setTextColor(ContextCompat.getColor(getContext(), R.color.derkGrey));
                    btnpost.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnaddimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 15);
            }
        });

        btnaddvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 30);
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postDescription = postdes.getText().toString().trim();
                dialog.show();
                final StorageReference reference = storage.getReference()
                        .child("posts").child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime() + "");
                if (!postDescription.isEmpty() || selectedImageUri != null || selectedVideoUri != null) {
                    if (selectedImageUri != null || selectedVideoUri != null) {
                        Uri fileUri = selectedImageUri != null ? selectedImageUri : selectedVideoUri;

                        if (fileUri != null) {
                            reference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            PostModel post = new PostModel();
                                            post.setPostimage(selectedImageUri != null ? uri.toString() : null);
                                            post.setPostvideo(selectedVideoUri != null ? uri.toString() : null);
                                            post.setPostedBy(FirebaseAuth.getInstance().getUid());
                                            post.setPostdes(postDescription);
                                            post.setPostedAt(new Date().getTime());

                                            database.getReference().child("posts")
                                                    .push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            dialog.dismiss();
                                                            Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Please select a valid image or video", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // No image or video selected, post only text
                        PostModel post = new PostModel();
                        post.setPostedBy(FirebaseAuth.getInstance().getUid());
                        post.setPostdes(postDescription);
                        post.setPostedAt(new Date().getTime());

                        database.getReference().child("posts")
                                .push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter a post description or select an image/video", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 15) {
            if (data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                postimage.setImageURI(selectedImageUri);
                postimage.setVisibility(View.VISIBLE);

                selectedVideoUri = null;
                postvideo.setVisibility(View.GONE);

                btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.buttondesign));
                btnpost.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                btnpost.setEnabled(true);
            }
        } else if (requestCode == 30) {
            if (data != null && data.getData() != null) {
                selectedVideoUri = data.getData();
                postvideo.setVideoURI(selectedVideoUri);
                postvideo.setVisibility(View.VISIBLE);
                postvideo.start();

                selectedImageUri = null;
                postimage.setVisibility(View.GONE);

                btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.buttondesign));
                btnpost.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                btnpost.setEnabled(true);
            }
        }

        if (selectedImageUri != null && selectedVideoUri != null) {
            btnpost.setEnabled(false);
        }
    }
}

