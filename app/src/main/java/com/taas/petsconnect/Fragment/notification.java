package com.taas.petsconnect.Fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taas.petsconnect.Adapter.NotificationAdapter;
import com.taas.petsconnect.Model.NotificationModel;
import com.taas.petsconnect.R;

import java.util.ArrayList;


public class notification extends Fragment {

    RecyclerView notificationRV;
    ArrayList<NotificationModel> list;
    FirebaseDatabase database;

    public notification() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=FirebaseDatabase.getInstance();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        notificationRV = view.findViewById(R.id.notificationRV);
        list = new ArrayList<>();
      /*  list.add(new NotificationModel(R.drawable.eaglepro,"<b>Dom</b>  mentioned you in comment","just now"));
        list.add(new NotificationModel(R.drawable.goat,"<b>Mikel</b> like your post","2 mint ago"));
        list.add(new NotificationModel(R.drawable.prrot,"<b>Jack</b> like your post","just now"));
        list.add(new NotificationModel(R.drawable.rabitpro,"<b>Nobody</b> like your post","2 day ago"));
        list.add(new NotificationModel(R.drawable.dd,"<b>Everybody</b> mentioned you in comment","5 mint ago"));
        list.add(new NotificationModel(R.drawable.eagle,"<b>Anyone</b>  comment on your post","3 day ago"));
        list.add(new NotificationModel(R.drawable.cc,"<b>Everyone</b> like your profile ","just now"));
        list.add(new NotificationModel(R.drawable.dog,"<b>Goat</b> like your post","2 mint ago"));
        list.add(new NotificationModel(R.drawable.prrot,"<b>Parrot</b> like your post","just now"));
        list.add(new NotificationModel(R.drawable.rabitpro,"<b>Rabit</b> like your post","2 day ago"));
        list.add(new NotificationModel(R.drawable.ddpro,"<b>Mr.ASK</b> mentioned you in comment","5 mint ago"));
        list.add(new NotificationModel(R.drawable.eagle,"<b>MR.TA</b>  comment on your post","3 day ago"));
        list.add(new NotificationModel(R.drawable.catpro,"<b>Mr.AB</b> like your profile ","just now"));
        list.add(new NotificationModel(R.drawable.rabitpro,"<b>Nobody</b> like your post","2 day ago"));
        list.add(new NotificationModel(R.drawable.dd,"<b>Everybody</b> mentioned you in comment","5 mint ago"));
        list.add(new NotificationModel(R.drawable.eagle,"<b>Anyone</b>  comment on your post","3 day ago"));
        list.add(new NotificationModel(R.drawable.cc,"<b>Everyone</b> like your profile ","just now"));
        list.add(new NotificationModel(R.drawable.dog,"<b>Goat</b> like your post","2 mint ago"));
        list.add(new NotificationModel(R.drawable.prrot,"<b>Parrot</b> like your post","just now"));
        list.add(new NotificationModel(R.drawable.rabitpro,"<b>Rabit</b> like your post","2 day ago"));*/


        NotificationAdapter adapter = new NotificationAdapter(list, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        notificationRV.setLayoutManager(layoutManager);
        notificationRV.setNestedScrollingEnabled(false);
        notificationRV.setAdapter(adapter);
        database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                            NotificationModel notificationModel=dataSnapshot.getValue(NotificationModel.class);
                            notificationModel.setNotificationId(dataSnapshot.getKey());
                            list.add(notificationModel);

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return view;
    }
}
