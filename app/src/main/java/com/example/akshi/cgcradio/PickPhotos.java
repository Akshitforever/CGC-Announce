package com.example.akshi.cgcradio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PickPhotos extends AppCompatActivity {
    TextView event;
    Button submit;
    DatabaseReference databaseReference;
    CircleImageView imageView;
    public final int FILE_SELECT_CODE = 1;
    StorageReference storageReference;
    Uri[] uri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_photos);
        initialize();
    }
    public void initialize(){
        event = findViewById(R.id.eventName);
        submit = findViewById(R.id.submit);
        imageView = findViewById(R.id.eventImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), FILE_SELECT_CODE);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri!=null) {
                    storageReference = FirebaseStorage.getInstance().getReference("gallery/"+event.getText().toString()+"/");
                    databaseReference = FirebaseDatabase.getInstance().getReference("gallery/"+event.getText().toString());
                    for (int i=0;i<uri.length;i++) uploadImage(uri[i],i+1);
                }
                else
                    Toast.makeText(PickPhotos.this,"Please Select Photos",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void uploadImage(Uri uri, final int k) {
        if(uri!=null && !(TextUtils.isEmpty(event.getText().toString()))){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.setCancelable(false);
            dialog.show();
            storageReference = storageReference.child(Objects.requireNonNull(uri.getLastPathSegment()));
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            {
                                PhotoBean pv = new PhotoBean(event.getText().toString(),task.getResult().toString());
                                databaseReference.push().setValue(pv);
                            }
                        }
                    });
                    Toast.makeText(PickPhotos.this,"Uploaded",Toast.LENGTH_LONG).show();
                    if(k==PickPhotos.this.uri.length-1)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(PickPhotos.this,Events.class));
                            finish();
                        }
                    },2000);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PickPhotos.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    dialog.setMessage("Uploading images.."+(int)progress+"%");
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
                    Toast.makeText(this,"Data Hai"+resultCode,Toast.LENGTH_SHORT).show();
                    if (resultCode == RESULT_OK) {
                        Toast.makeText(this, "Clip Na", Toast.LENGTH_SHORT).show();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            uri = new Uri[count];
                            for (int i = 0; i < count; i++)
                                uri[i] = data.getClipData().getItemAt(i).getUri();
                            Toast.makeText(this, "Clip", Toast.LENGTH_SHORT).show();
                        } else if (data.getData() != null) {
                            try {
                                uri = new Uri[1];
                                Toast.makeText(this, "Simple", Toast.LENGTH_LONG).show();
                                uri[0] = data.getData();
                                InputStream inputStream;
                                inputStream = getContentResolver().openInputStream(uri[0]);
                                final Bitmap imageMap = BitmapFactory.decodeStream(inputStream);
                                imageView.setImageBitmap(imageMap);
                            } catch (IOException e) {
                                Toast.makeText(this, "Simple ex", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else
                            Toast.makeText(this, "Error Importing Image/es", Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(this,"NULL",Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show();
        }

    }
    private class PhotoBean{
        String event,url;

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
