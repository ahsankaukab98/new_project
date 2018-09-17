package com.example.user1.service_provider;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    int count=0;
    private DatabaseReference counter;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    boolean check=false;

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
            public void onClick(View v)
            {
                if(ed_passward.getText().toString().equals(ed_retype_passward.getText().toString()))
                {
                    usersignin();
                    userdata=FirebaseDatabase.getInstance().getReference("service_providers").child("user "+count);
                    counter.setValue(count);
                    save_user();
                }
                else
                {
                    Toast.makeText(SignIn_Activity.this, "Passward does not match", Toast.LENGTH_SHORT).show();
                    ed_passward.setText(null);
                    ed_retype_passward.setText(null);
                }


//                PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                        ed_mobille.getText().toString(),        // Phone number to verify
//                        60,                 // Timeout duration
//                        TimeUnit.SECONDS,   // Unit of timeout
//                        SignIn_Activity.this,               // Activity (for callback binding)
//                        mCallbacks);        // OnVerificationStateChangedCallbacks
//                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                    @Override
//                    public void onVerificationCompleted(PhoneAuthCredential credential) {
//                        // This callback will be invoked in two situations:
//                        // 1 - Instant verification. In some cases the phone number can be instantly
//                        //     verified without needing to send or enter a verification code.
//                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
//                        //     detect the incoming verification SMS and perform verification without
//                        //     user action.
//
//                        //signInWithPhoneAuthCredential(credential);
//                    }
//
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//                        // This callback is invoked in an invalid request for verification is made,
//                        // for instance if the the phone number format is not valid.
//
//
//                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                            // Invalid request
//                            // ...
//                            Toast.makeText(SignIn_Activity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        } else if (e instanceof FirebaseTooManyRequestsException) {
//                            // The SMS quota for the project has been exceeded
//                            // ...
//                            Toast.makeText(SignIn_Activity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//
//                        // Show a message and update the UI
//                        // ...
//                    }
//
//                    @Override
//                    public void onCodeSent(String verificationId,
//                                           PhoneAuthProvider.ForceResendingToken token) {
//                        // The SMS verification code has been sent to the provided phone number, we
//                        // now need to ask the user to enter the code and then construct a credential
//                        // by combining the code with a verification ID.
//                        Toast.makeText(SignIn_Activity.this, "Code Send Successful", Toast.LENGTH_SHORT).show();
//                        // Save verification ID and resending token so we can use them later
//                        mVerificationId = verificationId;
//                        mResendToken = token;
//
//                        // ...
//                    }
//                };
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
        if (result instanceof Bitmap) {
            img_person.setImageBitmap((Bitmap) result);
        } else {
            Glide.with(this)
                    .load(result) // works for File or Uri
                    .crossFade()
                    .into(img_person);
        }
        mAuth.setFirebaseUIVersion(img_person+"");
    }

    private File createTempFile() {
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
                    userdata = userdata.child("user "+count);
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

  }
  void init()
  {
      fab_pick_camera=findViewById(R.id.fab_pick_camera);
      fab_pick_gallery=findViewById(R.id.fab_pick_gallery);
      img_person=findViewById(R.id.img_person);
      sign_in=findViewById(R.id.sign_in);
      ed_name=findViewById(R.id.ed_name);
      ed_email=findViewById(R.id.ed_email);
      ed_mobille=findViewById(R.id.ed_mobile);
      ed_username=findViewById(R.id.ed_user_name);
      ed_retype_passward=findViewById(R.id.ed_retypepass);
      ed_passward=findViewById(R.id.ed_passward);
      mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
          @Override
          public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

          }

          @Override
          public void onVerificationFailed(FirebaseException e) {

          }
      };


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

                          FirebaseUser user = mAuth.getCurrentUser();
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



}


