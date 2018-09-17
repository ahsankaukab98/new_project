package com.example.user1.service_provider;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
    private FirebaseAuth mAuth;
    private DatabaseReference userdata;
    private Shared_pref_Login mshared_pref_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        btn_login=findViewById(R.id.btn_login);
        btn_signin=findViewById(R.id.btn_signin);
        ed_name=findViewById(R.id.ed_name);
        ed_passward=findViewById(R.id.ed_password);
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
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    store_user_data();
                                   startActivity(new Intent(LoginActivity.this,HomeActivity.class));

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
                            mshared_pref_login.saveLoginDetails(data.child("email").getValue().toString(),
                                    data.child("passward").getValue().toString(),
                                    data.child("name").getValue().toString(),data.child("mobile").getValue().toString(),
                                    data.getKey().toString());
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

}
