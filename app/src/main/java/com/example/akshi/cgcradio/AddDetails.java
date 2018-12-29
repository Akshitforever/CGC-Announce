package com.example.akshi.cgcradio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDetails extends AppCompatActivity {

    TextView event,org,venue,desc;
    Button submit;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        event = findViewById(R.id.eventName);
        org = findViewById(R.id.eventOrg);
        venue = findViewById(R.id.eventVenue);
        desc = findViewById(R.id.eventDesc);
        submit = findViewById(R.id.submit);
        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(event.getText().toString()) || TextUtils.isEmpty(venue.getText().toString()) ||
                        TextUtils.isEmpty(desc.getText().toString()) || TextUtils.isEmpty(org.getText().toString())) {
                    Toast.makeText(AddDetails.this,"Please add all details",Toast.LENGTH_LONG).show();
                }
                else{
                    databaseReference.push().setValue(new EventDetailsBean(event.getText().toString(),venue.getText().toString()
                    ,org.getText().toString(),desc.getText().toString()));
                    startActivity(new Intent(AddDetails.this,Events.class));
                    finish();
                }
            }
        });
    }
}
