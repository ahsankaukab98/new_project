package com.example.user1.service_provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class your_service_show_des extends AppCompatActivity {
    TextView ed_des_long;
    TextView ed_des_short;
    ImageView img_services;
    Bundle bundle;
    String TAG_DES_SHORT="short";
    String TAG_DES_LONG="long";
    String TAG_IMAGE="image";
    String TAG_URL="url";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference httpsReference;
    File localFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_service_show_des);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Your Services detail View");
        }
        init();
        ed_des_long.setText(bundle.getString(TAG_DES_LONG));
        ed_des_short.setText(bundle.getString(TAG_DES_SHORT));
       if(bundle.getString(TAG_URL)!=null)
        load_image();

    }
    void load_image()
    {
        httpsReference=storage.getReferenceFromUrl(bundle.getString(TAG_URL));
        try
        {
            localFile = File.createTempFile("images", "jpg");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(your_service_show_des.this,"file created Successfully",Toast.LENGTH_SHORT).show();
                // Local temp file has been created
                Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                img_services.setImageBitmap(myBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(your_service_show_des.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    void init()
    {
        ed_des_long=findViewById(R.id.ed_des_long);
        ed_des_short=findViewById(R.id.ed_des_short);
        img_services=findViewById(R.id.img_servicess);
        bundle=getIntent().getExtras();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
