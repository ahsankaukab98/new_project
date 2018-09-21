package com.example.user1.service_provider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.File;

public class SignIn_Activity extends AppCompatActivity {

    private Button sign_in;
    private FloatingActionButton fab_pick_camera;
    private FloatingActionButton fab_pick_gallery;
    private ImageView img_person;
    private EditText ed_name;
    private EditText ed_email;
    private EditText ed_mobille;
    private EditText ed_passward;
    private EditText ed_username;
    private EditText ed_retype_passward;
    private DatabaseReference userdata;
    String TAG_NAME="name";
    String TAG_EMAIL="email";
    String TAG_MOBILE="mobile";
    String TAG_IAMGE="image";
    ProgressBar progress_bar;
    Shared_pref_Login mshaerd_login;
    Uri downloadUri;
    Object results;
    int count=0;
    private DatabaseReference counter;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    boolean check=false;
    boolean verify=false;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_);

       init();
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Sign In");
        }
        mAuth = FirebaseAuth.getInstance();

        userdata = FirebaseDatabase.getInstance().getReference("service_providers");
        counter=userdata.child("count");

        count=counting_users();

        sign_in.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (ed_mobille != null)
                {
                    counter.setValue(count);
                    if (bundle.getString(TAG_NAME) != null)
                    {
                        save_user();
                        if (verify)
                            pic_upload();
                        startActivity(new Intent(SignIn_Activity.this, HomeActivity.class));

                    }
                    else {
                        if (ed_passward.getText().toString().equals(ed_retype_passward.getText().toString())) {

                                if (verify) {
                                    pic_upload();
                                } else
                                    save_user();

                                usersignin();
//
                        } else {
                            Toast.makeText(SignIn_Activity.this, "Passward does not match", Toast.LENGTH_SHORT).show();
                            ed_passward.setText(null);
                            ed_retype_passward.setText(null);
                        }
                    }

                }
                else
                {
                    Toast.makeText(SignIn_Activity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==android.R.id.home)
        {
            if(bundle.get(TAG_NAME)!=null)
                LoginManager.getInstance().logOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("CheckResult")
    private void pickImageFromSource(Sources source)
    {
        RxImagePicker.with(getFragmentManager()).requestImage(source)
                .flatMap(uri -> {

                            return RxImageConverters.uriToFile(SignIn_Activity.this, uri, createTempFile());

                })
                .subscribe(this::onImagePicked, throwable -> {
//                    return Toast.makeText(SignIn_Activity.this, String.format("Error: %s", throwable), Toast.LENGTH_LONG).show();
                });
    }
    private void onImagePicked(Object result) {
//        Toast.makeText(this, String.format("Result: %s", result), Toast.LENGTH_LONG).show();
        if (result instanceof Bitmap)
        {
            img_person.setImageBitmap((Bitmap) result);
            results=result;
            verify=true;
        } else
            {
            Glide.with(this)
                    .load(result) // works for File or Uri
                    .into(img_person);
                results=result;
                verify=true;
        }

    }

    private File createTempFile()
    {
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "_image.jpeg");
    }
    int counting_users()
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
                    userdata =  userdata = FirebaseDatabase.getInstance().getReference("service_providers").child("user "+count);
                    check=true;
                    //counter.setValue(count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });


        return count;
    }
  //  void save_user(String name,String email,String mobile,String user_name,String passward)
  void save_user()
  {

      userdata.child("name").setValue(ed_name.getText().toString());
      userdata.child("email").setValue(ed_email.getText().toString());
      userdata.child("mobile").setValue(ed_mobille.getText().toString());
      userdata.child("user_name").setValue(ed_username.getText().toString());
      userdata.child("passward").setValue(ed_passward.getText().toString());
      if(bundle.getString(TAG_NAME)!=null)
      {
          userdata.child("image").setValue(bundle.getString(TAG_IAMGE));

          mshaerd_login.saveLoginDetails(ed_email.getText().toString(),
                  ed_passward.getText().toString(),
                  ed_name.getText().toString(), ed_mobille.getText().toString(),
                  "user "+count,bundle.getString(TAG_IAMGE));
      }

  }


  void init()
  {
      fab_pick_camera=findViewById(R.id.fab_pick_camera);
      bundle=getIntent().getExtras();
      fab_pick_gallery=findViewById(R.id.fab_pick_gallery);
      img_person=findViewById(R.id.img_person);
      sign_in=findViewById(R.id.sign_in);
      ed_name=findViewById(R.id.ed_name);
      ed_email=findViewById(R.id.ed_email);
      ed_mobille=findViewById(R.id.ed_mobile);
      ed_username=findViewById(R.id.ed_user_name);
      ed_retype_passward=findViewById(R.id.ed_retypepass);
      ed_passward=findViewById(R.id.ed_passward);
      progress_bar=findViewById(R.id.progress_bar);

     if(bundle.getString(TAG_NAME)!=null)
     {
         ed_name.setText(bundle.getString(TAG_NAME));
         ed_email.setText(bundle.getString(TAG_EMAIL));
         ed_mobille.setText(bundle.getString(TAG_MOBILE));
         ed_username.setText(bundle.getString(TAG_NAME));
         sign_in.setText("Log in");
         fab_pick_gallery.setVisibility(View.GONE);
         fab_pick_camera.setVisibility(View.GONE);
         ed_retype_passward.setVisibility(View.GONE);
         ed_passward.setVisibility(View.GONE);
     }
     mshaerd_login=new Shared_pref_Login(SignIn_Activity.this);
     load_image();
  }
    void load_image() {
        if (bundle.getString(TAG_IAMGE)!=null)
        {
            Glide.with(this)
                    .load(bundle.getString(TAG_IAMGE)) // works for File or Uri
                    .into(img_person);
        }
    }
  void usersignin()
  {
      mAuth.createUserWithEmailAndPassword(ed_email.getText().toString(),ed_passward.getText().toString())
              .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful()) {
                          // Sign in success, update UI with the signed-in user's information
                          Toast.makeText(SignIn_Activity.this,"Sign In Successful",Toast.LENGTH_SHORT).show();

                          user = mAuth.getCurrentUser();
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

                                          }
                                      }
                                  });
                          startActivity(new Intent(SignIn_Activity.this,LoginActivity.class));
                      } else
                          {
                              FirebaseAuthException e = (FirebaseAuthException )task.getException();
                              Toast.makeText(SignIn_Activity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                          // If sign in fails, display a message to the user.
                      }

                      // ...
                  }
              });
  }

  void pic_upload()
  {
      Uri file = Uri.fromFile((File) results);



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
                  public void onSuccess(Uri uri)
                  {
                      downloadUri =uri;
                      Toast.makeText(SignIn_Activity.this,uri.toString(),Toast.LENGTH_SHORT).show();

                      UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                              .setPhotoUri(Uri.parse(downloadUri.toString()))
                              .build();
                      user.updateProfile(profileUpdates)
                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task)
                                  {
                                      if (task.isSuccessful())
                                      {
                                          userdata.child("name").setValue(ed_name.getText().toString());
                                          userdata.child("email").setValue(ed_email.getText().toString());
                                          userdata.child("mobile").setValue(ed_mobille.getText().toString());
                                          userdata.child("user_name").setValue(ed_username.getText().toString());
                                          userdata.child("passward").setValue(ed_passward.getText().toString());
                                          userdata.child("image").setValue(downloadUri.toString());
                                          Toast.makeText(SignIn_Activity.this,"Image Updated Successfully",Toast.LENGTH_SHORT).show();
                                      }
                                  }
                              });
                  }

              });

          }
      });
      // Observe state change events such as progress, pause, and resume
      uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
          {
              double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

              Toast.makeText(SignIn_Activity.this,"Upload is " + progress + "% done",Toast.LENGTH_SHORT).show();
          }
      }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
              Toast.makeText(SignIn_Activity.this,"Upload is paused",Toast.LENGTH_SHORT).show();
          }
      });
  }


}


