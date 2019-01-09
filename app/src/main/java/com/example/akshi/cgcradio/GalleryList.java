package com.example.akshi.cgcradio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryList extends AppCompatActivity {

    DatabaseReference databaseReference;
    Button add;
    RecyclerView recyclerView;
    ArrayList<String> name,keys;
    ArrayList<Integer> image;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_list);
        name = new ArrayList<>();
        image = new ArrayList<>();
        keys = new ArrayList<>();
        add = findViewById(R.id.addPhotos);
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
                startActivity(new Intent(GalleryList.this,PickPhotos.class));
            }
        });
        recyclerView = findViewById(R.id.seeGalleryList);
        databaseReference = FirebaseDatabase.getInstance().getReference("gallery");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    DataSnapshot d = dataSnapshot.child(ds.getKey());
                    for (DataSnapshot val : d.getChildren()) {
                        PhotoBean pv = val.getValue(PhotoBean.class);
                        name.add(pv.getEvent());
                        image.add(R.drawable.ic_music_24dp);
                        keys.add(val.getKey());
                    }
                }
                AnnouncementAdapter recyclerAdapter=new AnnouncementAdapter(GalleryList.this,name,image){
                    @Override
                    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
                        super.onBindViewHolder(holder, position);
                        final int cur = position;
                        holder.layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isNetworkAvailable()) {
                                    Intent i = new Intent(getApplicationContext(),Music.class);
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
                recyclerView.setLayoutManager(new LinearLayoutManager(GalleryList.this));
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
    private class PhotoBean{
        String event,url;

        public PhotoBean(){}
        public PhotoBean(String event, String url) {
            this.event = event;
            this.url = url;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

