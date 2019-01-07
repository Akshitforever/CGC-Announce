package com.example.akshi.cgcradio;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class SeeAnnouncements extends AppCompatActivity{

    DatabaseReference databaseReference;
    Button add;
    RecyclerView recyclerView;
    ArrayList<String> name,keys;
    ArrayList<Integer> image;
    ArrayList<String> url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_announcements);
        name = new ArrayList<>();
        image = new ArrayList<>();
        url = new ArrayList<>();
        keys = new ArrayList<>();
        add = findViewById(R.id.add);
        Toast.makeText(this,"Retrieving",Toast.LENGTH_LONG).show();
        String username = "";
        try{username = FirebaseAuth.getInstance().getCurrentUser().getEmail();}
        catch(NullPointerException npe){
            Toast.makeText(this,"Can't retrieve your id. Please Logout.",Toast.LENGTH_LONG).show();
        }
        Toast.makeText(this,username,Toast.LENGTH_LONG).show();
        if(username.equals("akshitbansal2828@gmail.com"))
            add.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeeAnnouncements.this,ShareIt.class));
            }
        });
        recyclerView = findViewById(R.id.seeAnnouncementList);
        databaseReference = FirebaseDatabase.getInstance().getReference("announcements");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    Announcement an = ds.getValue(Announcement.class);
                    name.add(an.getName());
                    image.add(R.drawable.ic_music_24dp);
                    url.add(an.getUrl());
                    keys.add(ds.getKey());
                }
                AnnouncementAdapter recyclerAdapter=new AnnouncementAdapter(SeeAnnouncements.this,name,image){
                    @Override
                    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
                        super.onBindViewHolder(holder, position);
                        final String pos = url.get(position);
                        final int cur = position;
                        holder.layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isNetworkAvailable()) {
                                    Intent i = new Intent(getApplicationContext(),Music.class);
                                    i.putExtra("name",name.get(cur));
                                    i.putExtra("url",pos);
                                    i.putExtra("ref",keys.get(cur));
                                    startActivity(i);
                                    finish();
                                } else {
                                    final Snackbar snackbar = Snackbar.make(v, "No Internet Connection", Snackbar.LENGTH_LONG);
                                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) { snackbar.dismiss(); }}).show();
                                }
                            }
                        });
                }};
                recyclerView.setLayoutManager(new LinearLayoutManager(SeeAnnouncements.this));
                recyclerView.setAdapter(recyclerAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
        }
        return activeNetworkInfo != null;
    }

}
