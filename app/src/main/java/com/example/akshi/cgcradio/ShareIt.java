package com.example.akshi.cgcradio;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareIt extends AppCompatActivity {


    TextView type,name;
    EditText desc;
    Button upload,browse;
    CircleImageView image;
    DatabaseReference databaseReference;
    StorageReference reference;
    private static final int FILE_SELECT_CODE = 0;
    Uri imageUri = null, audioUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_it);
        initialize();
    }

    private void handleSendImage(Intent intent,String typeOfFile) throws IOException {
        imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(imageUri!=null){
            String p = imageUri.getPath();
            String nameOfFile = "";
            if (p != null) {
                int cut = p.lastIndexOf('/');
                if(cut!=-1) nameOfFile = p.substring(cut);
            }
            type.setText("Type: "+typeOfFile);
            name.setText("Name: "+nameOfFile);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            image.setImageBitmap(bitmap);
        }
    }

    void initialize(){
        type = findViewById(R.id.contentType);
        name = findViewById(R.id.contentName);
        desc = findViewById(R.id.uploadDesc);
        upload = findViewById(R.id.upload);
        image = findViewById(R.id.circleImageView);
        browse = findViewById(R.id.browse);
        
        databaseReference = FirebaseDatabase.getInstance().getReference("announcements");
        reference = FirebaseStorage.getInstance().getReference("announcements");
        
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                try {
                    startActivityForResult(intent,FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ShareIt.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              uploadImage();
                uploadAudio();
            }
        });
        
        Toast.makeText(this,type,Toast.LENGTH_LONG).show();

        if (intent!=null && Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("audio/")) {
                try {
                    handleSendImage(intent,"audio");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private void uploadImage() {
//        if(imageUri != null && !TextUtils.isEmpty(desc.getText().toString())) {
//            final ProgressDialog dialog = new ProgressDialog(this);
//            dialog.setMessage("Uploading...");
//            dialog.show();
//            reference = reference.child(Objects.requireNonNull(imageUri.getLastPathSegment()));
//            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            {
//                                Announcement an = new Announcement(desc.getText().toString(),task.getResult().toString());
//                                databaseReference.push().setValue(an);
//                            }
//                        }
//                    });
//                    Toast.makeText(ShareIt.this,"Uploaded",Toast.LENGTH_LONG).show();
//                    startActivity(new Intent(ShareIt.this,Events.class));
//                    finish();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(ShareIt.this,e.getMessage(),Toast.LENGTH_LONG).show();
//                    dialog.dismiss();
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress=(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                    dialog.setMessage("Uploaded: "+(int)progress+"%");
//                }
//            });
//        }
//        else
//            Toast.makeText(this,"Fill details",Toast.LENGTH_LONG).show();
//    }

    public  void uploadAudio(){
        if(audioUri != null && !TextUtils.isEmpty(desc.getText().toString())) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();
            reference = reference.child(Objects.requireNonNull(audioUri.getLastPathSegment()));
            reference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            {
                                Announcement an = new Announcement(desc.getText().toString(),task.getResult().toString());
                                databaseReference.push().setValue(an);
                            }
                        }
                    });
                    Toast.makeText(ShareIt.this,"Uploaded",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ShareIt.this,Events.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ShareIt.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    dialog.setMessage("Uploaded: "+(int)progress+"%");
                }
            });
        }
        else
            Toast.makeText(this,"Fill details",Toast.LENGTH_LONG).show();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:

                if (data != null) {
                    if(data.toString().contains("video") || data.toString().contains("image")){
                        Toast.makeText(this,"Upload audio only",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if (resultCode == RESULT_OK) {
                    imageUri = null;
                    if (data != null) {
                        audioUri = data.getData();
//                        imageUri = data.getData();
                    }
//                    try {
//                        image.setImageBitmap(MediaStore.Images.Media.getBitmap (this.getContentResolver(),imageUri));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
        }

    }

}
