package com.example.user1.service_provider;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.File;

public class ProfileAcivity extends AppCompatActivity {

    ImageView img_person;
    FloatingActionButton fab_pick_camera;
    FloatingActionButton fab_pick_gallery;
    EditText ed_name;
    EditText ed_email;
    EditText ed_mobile;
    EditText ed_new_passward;
    EditText ed_old_passward;
    Button btn_change_name;
    Button btn_change_email;
    Button btn_change_mobile;
    Button btn_change_passward;
    Shared_pref_Login msharedpref;
    private DatabaseReference userdata;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_acivity);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Sign In");
        }
        init();
        sethint();
        userdata = FirebaseDatabase.getInstance().getReference("service_providers").child(msharedpref.getData("Firebase"));
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
        btn_change_name.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(ed_name.getText()!=null)
                {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(ed_name.getText().toString())
//                            .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(ProfileAcivity.this,"Name Updated Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    msharedpref.changeData("Name",ed_name.getText().toString());
                    userdata.child("name").setValue(ed_name.getText().toString());
                }
                else
                    Toast.makeText(ProfileAcivity.this,"Please Enter a name",Toast.LENGTH_SHORT).show();
            }
        });
        btn_change_email.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(ed_email.getText()!=null)
                {
                    user.updateEmail(ed_email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(ProfileAcivity.this,"Email Updated Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                    Toast.makeText(ProfileAcivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    msharedpref.changeData("Email",ed_email.getText().toString());
                    userdata.child("email").setValue(ed_email.getText().toString());
                }
                else
                    Toast.makeText(ProfileAcivity.this,"Please Enter an Email Address",Toast.LENGTH_SHORT).show();
            }
        });
        btn_change_mobile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(ed_mobile.getText()!=null)
                {
                    msharedpref.changeData("Mobile",ed_mobile.getText().toString());
                    userdata.child("mobile").setValue(ed_mobile.getText().toString());
                }
                else
                    Toast.makeText(ProfileAcivity.this,"Please Enter a Mobile Number",Toast.LENGTH_SHORT).show();
            }
        });
        btn_change_passward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(ed_old_passward.getText().toString().equals(msharedpref.getData("Password")))
                {
                    if(ed_new_passward!=null)
                    {
                        String newPassword = ed_new_passward.getText().toString();

                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(ProfileAcivity.this,"Passward Updated Successfully",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                            Toast.makeText(ProfileAcivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                        msharedpref.changeData("Password", ed_new_passward.getText().toString());
                        userdata.child("passward").setValue(ed_mobile.getText().toString());
                    }
                    else
                        Toast.makeText(ProfileAcivity.this,"Please Enter new passward",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(ProfileAcivity.this,"Old Passward is not correct",Toast.LENGTH_SHORT).show();
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
        img_person=findViewById(R.id.img_person);
        fab_pick_camera=findViewById(R.id.fab_pick_camera);
        fab_pick_gallery=findViewById(R.id.fab_pick_gallery);
        ed_name=findViewById(R.id.ed_name);
        ed_email=findViewById(R.id.ed_email);
        ed_mobile=findViewById(R.id.ed_mobile);
        ed_new_passward=findViewById(R.id.ed_new_passward);
        ed_old_passward=findViewById(R.id.ed_old_passward);
        btn_change_name=findViewById(R.id.btn_change_name);
        btn_change_email=findViewById(R.id.btn_change_email);
        btn_change_mobile=findViewById(R.id.btn_change_mobile);
        btn_change_passward=findViewById(R.id.btn_change_passward);
        msharedpref=new Shared_pref_Login(ProfileAcivity.this);
        user = FirebaseAuth.getInstance().getCurrentUser();

    }
    void sethint()
    {
        ed_name.setHint(msharedpref.getData("Name"));
        ed_email.setHint(msharedpref.getData("Email"));
        ed_mobile.setHint(msharedpref.getData("Mobile"));
    }

    @SuppressLint("CheckResult")
    private void pickImageFromSource(Sources source)
    {
        RxImagePicker.with(getFragmentManager()).requestImage(source)
                .flatMap(uri -> {

                    return RxImageConverters.uriToFile(ProfileAcivity.this, uri, createTempFile());

                })
                .subscribe(this::onImagePicked, throwable -> {
//                    return Toast.makeText(SignIn_Activity.this, String.format("Error: %s", throwable), Toast.LENGTH_LONG).show();
                });
    }
    private void onImagePicked(Object result) {
//        Toast.makeText(this, String.format("Result: %s", result), Toast.LENGTH_LONG).show();
        if (result instanceof Bitmap) {
            img_person.setImageBitmap((Bitmap) result);
        } else {
            Glide.with(this)
                    .load(result) // works for File or Uri
                    .crossFade()
                    .into(img_person);
        }
    }
    private File createTempFile() {
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "_image.jpeg");
    }

}
