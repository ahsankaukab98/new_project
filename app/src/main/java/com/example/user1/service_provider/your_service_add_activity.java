package com.example.user1.service_provider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.user1.service_provider.R.layout.activity_your_service_add_activity;

public class your_service_add_activity extends AppCompatActivity {

    ImageView img_service;

    FloatingActionButton fab_pick_camera;
    FloatingActionButton fab_pick_gallery;
    Spinner spinner_services;
    EditText ed_add_service_short_des;
    EditText ed_add_service_Description;
    Button btn_add_service;
    private DatabaseReference counter;
    private DatabaseReference counter2;
    private DatabaseReference userdata;
    private DatabaseReference selector;
    ArrayList<String> array;
    services_list_database mservice_databae;
    Shared_pref_Login msharedpref;
    Uri downloadUri;
    ProgressBar progress_bar;
    boolean check=false;
    boolean flag=false;
    int count=0;
    Cursor cur;
    int count2=0;
    List<String> spinnerArray = new ArrayList<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    // Create a reference to "mountains.jpg"
    // Create a reference to 'images/mountains.jpg'
    StorageReference firebaseImagesRef = storageRef.child("images/"+ UUID.randomUUID()+".jpg");
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(activity_your_service_add_activity);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Add New Service");
        }
        init();
        while(cur.moveToNext())
        {
            array.add(cur.getString(1));
            spinnerArray.add(cur.getString(1));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                your_service_add_activity.this,
                android.R.layout.simple_spinner_item,
                spinnerArray
        );

        spinner_services.setAdapter(adapter);
        fab_pick_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromSource(Sources.CAMERA);
            }
        });
        fab_pick_gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pickImageFromSource(Sources.GALLERY);
            }
        });
        count_service();

    }


    void count_service()
    {

        counter.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue(Integer.class)!=null)
                {
                    count=dataSnapshot.getValue(Integer.class);
                    count=count+1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
        counter2.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue(Integer.class)!=null)
                {
                    count2 = dataSnapshot.getValue(Integer.class);
                    count2 = count2 + 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
//
        btn_add_service.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selector = FirebaseDatabase.getInstance().getReference("list_services").child(spinner_services.getSelectedItem().toString());
                            userdata= FirebaseDatabase.getInstance().getReference("service_providers")
                                    .child(msharedpref.getData("Firebase")).child("my_services").child("service "+count);
                            userdata.child("short_description").setValue(ed_add_service_short_des.getText().toString());
                            userdata.child("Long_description").setValue(ed_add_service_Description.getText().toString());
                            if(downloadUri!=null)
                            userdata.child("image_uri").setValue(downloadUri.toString());

                            selector=FirebaseDatabase.getInstance().getReference("list_services")
                                    .child(spinner_services.getSelectedItem().toString()).child("service "+count2);
                            selector.child("name").setValue(msharedpref.getData("Name").toString());
                            selector.child("mobile").setValue(msharedpref.getData("Mobile").toString());
                            selector.child("email").setValue(msharedpref.getData("Email").toString());
                            if(downloadUri!=null)
                            selector.child("image_uri").setValue(downloadUri.toString());
                            selector.child("short_description").setValue(ed_add_service_short_des.getText().toString());
                            selector.child("Long_description").setValue(ed_add_service_Description.getText().toString());
                            //Toast.makeText(your_service_add_activity.this,"Service Added Successfully", Toast.LENGTH_SHORT).show();
                            counter.setValue(count);
                            counter2.setValue(count2);
                            Toast.makeText(your_service_add_activity.this,"Service Added Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(your_service_add_activity.this,your_service_activity.class);
                        startActivity(intent);
                       // finish();


            }
        });
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

    void init()
    {
        img_service=findViewById(R.id.img_service);
        fab_pick_camera=findViewById(R.id.fab_pick_camera);
        fab_pick_gallery=findViewById(R.id.fab_pick_gallery);
        spinner_services=findViewById(R.id.spinner_services);
        msharedpref=new Shared_pref_Login(your_service_add_activity.this);
        ed_add_service_short_des=findViewById(R.id.ed_add_service_short_des);
        ed_add_service_Description=findViewById(R.id.ed_add_service_Description);
        btn_add_service=findViewById(R.id.btn_add_service);
        userdata = FirebaseDatabase.getInstance().getReference("service_providers")
                .child(msharedpref.getData("Firebase")).child("my_services");
        counter=userdata.child("count");
         counter2=FirebaseDatabase.getInstance().getReference("list_services").child("count");
        mservice_databae= new services_list_database(your_service_add_activity.this);
        array=new ArrayList<>();
        cur=mservice_databae.getdata();
        progress_bar=findViewById(R.id.progress_bar);
        store_file();

    }
    @SuppressLint("CheckResult")
    private void pickImageFromSource(Sources source)
    {
        RxImagePicker.with(getFragmentManager()).requestImage(source)
                .flatMap(uri -> {

                    return RxImageConverters.uriToFile(your_service_add_activity.this, uri, createTempFile());

                })
                .subscribe(this::onImagePicked, throwable -> {
//                    return Toast.makeText(SignIn_Activity.this, String.format("Error: %s", throwable), Toast.LENGTH_LONG).show();
                });
    }

    private void onImagePicked(Object result) throws FileNotFoundException {
//        Toast.makeText(this, String.format("Result: %s", result), Toast.LENGTH_LONG).show();

        if (result instanceof Bitmap) {
            img_service.setImageBitmap((Bitmap) result);
            img_service.setDrawingCacheEnabled(true);
            img_service.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) img_service.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            img_service.setDrawingCacheEnabled(true);
            StorageMetadata metadata=new StorageMetadata.Builder()
                    .setCustomMetadata("text",ed_add_service_short_des.getText().toString())
                    .build();
            progress_bar.setVisibility(View.VISIBLE);
            UploadTask uploadtask=firebaseImagesRef.putBytes(data,metadata);
            uploadtask.addOnSuccessListener(your_service_add_activity.this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progress_bar.setVisibility(View.GONE);
                    downloadUri=taskSnapshot.getUploadSessionUri();
                }



            });

        } else {
            Glide.with(this)
                    .load(result) // works for File or Uri
                    .crossFade()
                    .into(img_service);


            Uri file = Uri.fromFile((File) result);
            StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);
            progress_bar.setVisibility(View.VISIBLE);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    progress_bar.setVisibility(View.GONE);
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUri =uri;
                            Toast.makeText(your_service_add_activity.this,uri.toString(),Toast.LENGTH_SHORT).show();
                        }

                    });

                }
            });
            // Observe state change events such as progress, pause, and resume
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    Toast.makeText(your_service_add_activity.this,"Upload is " + progress + "% done",Toast.LENGTH_SHORT).show();
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(your_service_add_activity.this,"Upload is paused",Toast.LENGTH_SHORT).show();
                }
            });


            



        }

    }
    void store_file()
    {


// While the file names are the same, the references point to different files

    }


    private File createTempFile() {
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "_image.jpeg");
    }
}
