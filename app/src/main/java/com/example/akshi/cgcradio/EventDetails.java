package com.example.akshi.cgcradio;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetails extends AppCompatActivity {

    DatabaseReference databaseReference;
    TextView event,org,venue,desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i =getIntent();
        Toast.makeText(this,"Retrieving...",Toast.LENGTH_LONG).show();
        Toast.makeText(this,i.getStringExtra("Event"),Toast.LENGTH_LONG).show();
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference("events/"+i.getStringExtra("Event"));
        event = findViewById(R.id.name);
        org = findViewById(R.id.organizer);
        desc = findViewById(R.id.desc);
        venue = findViewById(R.id.venue);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EventDetailsBean ev = dataSnapshot.getValue(EventDetailsBean.class);
                if (ev != null) {
                    event.setText(ev.getEvent());
                    String organizerString = "Organizer: "+ev.getOrg();
                    org.setText(organizerString);
                    desc.setText(ev.getDesc());
                    String venueString = "Venue: "+ev.getVenue();
                    venue.setText(venueString);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
