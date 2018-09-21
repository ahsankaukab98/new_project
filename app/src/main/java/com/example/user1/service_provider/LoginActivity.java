package com.example.user1.service_provider;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    Button btn_signin;
    EditText ed_name;
    EditText ed_passward;
    TextView forget_passward;
    private FirebaseAuth mAuth;
    private DatabaseReference userdata;
    private Shared_pref_Login mshared_pref_login;
    LoginButton loginButton;
    FirebaseUser user;
    CallbackManager mcallbackManager;
    String TAG_NAME="name";
    String TAG_EMAIL="email";
    String TAG_MOBILE="mobile";
    String TAG_IAMGE="image";
    boolean tell=false;
    private static final String EMAIL = "email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mcallbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        user = mAuth.getCurrentUser();

        loginButton.registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                mAuth = FirebaseAuth.getInstance();
                handleFacebookAccessToken(loginResult.getAccessToken());
                // App code
            }

            @Override
            public void onCancel()
            {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        btn_login=findViewById(R.id.btn_login);
        btn_signin=findViewById(R.id.btn_signin);
        ed_name=findViewById(R.id.ed_name);
        ed_passward=findViewById(R.id.ed_password);
        TextView forget_passward=findViewById(R.id.forget_passward);
        mAuth = FirebaseAuth.getInstance();
        userdata = FirebaseDatabase.getInstance().getReference("service_providers");
        mshared_pref_login=new Shared_pref_Login(LoginActivity.this);
        btn_signin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this,SignIn_Activity.class));

            }
        });
        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAuth.signInWithEmailAndPassword(ed_name.getText().toString(),ed_passward.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    user = mAuth.getCurrentUser();
                                    store_user_data();
                                   startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                   finish();

                                } else
                                    {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });

            }
        });
        forget_passward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this,forget_passward.class));

            }
        });
    }
    void store_user_data()
    {

        userdata.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    if(!Objects.requireNonNull(data.getKey()).equals("count"))
                    {
                        if(data.child("email").getValue().toString().equals(ed_name.getText().toString()))
                        {
                            String image=null;
                            if(data.hasChild("image"))
                                image= Objects.requireNonNull(data.child("image").getValue()).toString();


                            mshared_pref_login.saveLoginDetails(data.child("email").getValue().toString(),
                                    data.child("passward").getValue().toString(),
                                    data.child("name").getValue().toString(),  data.child("mobile").getValue().toString(),
                                    data.getKey().toString(),image);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mcallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this,"Login with fb is successful",Toast.LENGTH_SHORT).show();
                            user = mAuth.getCurrentUser();
                            check_user();


                        } else
                            {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,  task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    void check_user()
    {

        userdata.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    if(!Objects.requireNonNull(data.getKey()).equals("count"))
                    {
                        //Toast.makeText(LoginActivity.this,data.child("email").getValue().toString(),Toast.LENGTH_SHORT).show();
                        if(Objects.requireNonNull(data.child("email").getValue()).toString().equals(user.getEmail()))
                        {
                            String image=null;
                            if(data.hasChild("image"))
                                image= Objects.requireNonNull(data.child("image").getValue()).toString();


                            mshared_pref_login.saveLoginDetails(data.child("email").getValue().toString(),
                                    data.child("passward").getValue().toString(),
                                    data.child("name").getValue().toString(),  data.child("mobile").getValue().toString(),
                                    data.getKey().toString(),image);
                            tell=true;

                            break;
                        }
                    }
                }
                if(!tell)
                {
                    Intent intent = new Intent(LoginActivity.this, SignIn_Activity.class);
                    intent.putExtra(TAG_NAME, user.getDisplayName());
                    intent.putExtra(TAG_EMAIL, user.getEmail());
                    intent.putExtra(TAG_IAMGE, Objects.requireNonNull(user.getPhotoUrl()).toString());
                    intent.putExtra(TAG_MOBILE, user.getPhoneNumber());
                    startActivity(intent);
                }
                else
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }

        });

    }


}
