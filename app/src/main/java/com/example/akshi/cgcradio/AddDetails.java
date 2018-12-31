package com.example.akshi.cgcradio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddDetails extends AppCompatActivity {

    TextView event,org,venue,desc;
    Button submit;
    DatabaseReference databaseReference;
    CircleImageView imageView;
    public final int FILE_SELECT_CODE = 1;
    StorageReference storageReference;
    Uri uri = null;
    String url = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        initialize();
    }
    public void initialize(){
        event = findViewById(R.id.eventName);
        org = findViewById(R.id.eventOrg);
        venue = findViewById(R.id.eventVenue);
        desc = findViewById(R.id.eventDesc);
        submit = findViewById(R.id.submit);
        imageView = findViewById(R.id.eventImage);
        storageReference = FirebaseStorage.getInstance().getReference("events/images/");
        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                try {
                    startActivityForResult(Intent.createChooser(intent,"Choose File"),FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AddDetails.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri!=null)
                    uploadImage(uri);
                else
                    Toast.makeText(AddDetails.this,"Null",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void uploadImage(Uri uri) {
        if(uri!=null && !(TextUtils.isEmpty(event.getText().toString()) || TextUtils.isEmpty(venue.getText().toString()) ||
                TextUtils.isEmpty(desc.getText().toString()) || TextUtils.isEmpty(org.getText().toString()))){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();
            storageReference = storageReference.child(Objects.requireNonNull(uri.getLastPathSegment()));
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            {
                                EventDetailsBean ev = new EventDetailsBean(event.getText().toString(),venue.getText().toString()
                                        ,org.getText().toString(),desc.getText().toString(),task.getResult().toString());
                                databaseReference.push().setValue(ev);
                            }
                        }
                    });
                    Toast.makeText(AddDetails.this,"Uploaded",Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(AddDetails.this,Events.class));
                            finish();
                        }
                    },2000);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddDetails.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    dialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });
        }
        else{
            Toast.makeText(this,"Fill details",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (data != null) {
                    if(!data.toString().contains("image")){
                        Toast.makeText(this,"Only Image is allowed",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if(resultCode==RESULT_OK && data!=null && data.getData()!=null){
                    try {
                        uri = data.getData();
                        InputStream inputStream;
                        inputStream = getContentResolver().openInputStream(uri);
                        final Bitmap imageMap = BitmapFactory.decodeStream(inputStream);
                        imageView.setImageBitmap(imageMap);
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else Toast.makeText(this,"Error Importing Image",Toast.LENGTH_LONG).show();
        }
    }
}
