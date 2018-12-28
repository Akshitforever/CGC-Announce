package com.example.akshi.cgcradio;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Events extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    RecyclerView list;
    ArrayList<String> names=new ArrayList<>();
    ArrayList<Integer> images=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        if (actionbar != null) {
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        list= findViewById(R.id.recycler_semester_contents);
        list.setHasFixedSize(true);
        names.add("Akshit");
        images.add(R.drawable.ic_launcher_background);
        RecyclerAdapter recyclerAdapter=new RecyclerAdapter(this,names,images){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                final int i=position;
                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNetworkAvailable()) {


                        } else {
                            final Snackbar snackbar = Snackbar.make(v, "No Internet Connection", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                        }
                                    }).show();
                        }
                    }
                });
            }
        };

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(recyclerAdapter);

//        if(!checkPermissionFromDevice())
//            requestPermission();
//        storageReference = FirebaseStorage.getInstance().getReference();
//        progressDialog = new ProgressDialog(this);
//        upload = findViewById(R.id.upload);
//        btnRecord = findViewById(R.id.record);
//        btnStopRecord = findViewById(R.id.stoprecord);
//        btnPlay = findViewById(R.id.playrecord);
//        btnStop = findViewById(R.id.stopPlayingrecord);
//        btnRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(checkPermissionFromDevice()) {
//                    path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
//                            UUID.randomUUID().toString() + "_audio_record.3gp";
//                    setUpMediaRecorder();
//                    try {
//                        mediaRecorder.prepare();
//                        mediaRecorder.start();
//                    } catch (IOException io) {
//                        io.printStackTrace();
//                    }
//                    Toast.makeText(Events.this, "Recording....", Toast.LENGTH_LONG).show();
//                    btnPlay.setEnabled(false);
//                    btnStopRecord.setEnabled(true);
//                }
//                else requestPermission();
//            }
//        });
//        btnStopRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnStopRecord.setEnabled(true);
//                btnPlay.setEnabled(true);
//                btnRecord.setEnabled(true);
//                btnStop.setEnabled(false);
//                mediaRecorder.stop();
//                btnStopRecord.setEnabled(false);
//                btnStopRecord.setEnabled(true);
//            }
//        });
//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnStop.setEnabled(true);
//                btnStopRecord.setEnabled(false);
//                btnPlay.setEnabled(false);
//                mediaPlayer = new MediaPlayer();
//                try{
//                    mediaPlayer.setDataSource(path);
//                    mediaPlayer.prepare();
//                }
//                catch(IOException e){
//                    e.printStackTrace();
//                }
//                mediaPlayer.start();
//                Toast.makeText(Events.this,"Playing...",Toast.LENGTH_LONG).show();
//            }
//        });
//        btnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnStopRecord.setEnabled(false);
//                btnStop.setEnabled(false);
//                btnRecord.setEnabled(true);
//                btnPlay.setEnabled(true);
//                if(mediaPlayer!=null) {
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                    setUpMediaRecorder();
//                }
//            }
//        });
//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadAudio();
//            }
//        });
//    }
//
//    private void setUpMediaRecorder() {
//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//        mediaRecorder.setOutputFile(path);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case REQUEST_PERMISSION_CODE:{
//                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
//                    Toast.makeText(this,"Granted",Toast.LENGTH_LONG).show();
//                else
//                    Toast.makeText(this,"Denied",Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},REQUEST_PERMISSION_CODE) ;
//    }
//
//    private boolean checkPermissionFromDevice() {
//        int write_external_storage = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int record_audio_result = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
//        return write_external_storage==PackageManager.PERMISSION_GRANTED && record_audio_result==PackageManager.PERMISSION_GRANTED;
//    }
//    public void uploadAudio(){
//        progressDialog.setMessage("Uploading...");
//        progressDialog.show();
//        StorageReference mpath = storageReference.child("Audio").child("new_audio.3gp");
//        Uri uri = Uri.fromFile(new File(path));
//        mpath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(Events.this,"Uploaded",Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
//            }
//        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                switch (id)
                {
                    case R.id.logout: {
                        if (checkNet()) {
                            final Snackbar snackbar = Snackbar.make(mDrawerLayout, "No Internet Connection", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                        }
                                    }).show();
                            return false;
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();
                            break;
                        }
                    }
                }
                mDrawerLayout.closeDrawer(Gravity.START,true);
                return true;
            }

        });
    }
    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
        }
        return activeNetworkInfo != null;
    }
    public boolean checkNet()
    {
        return !isNetworkAvailable();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
