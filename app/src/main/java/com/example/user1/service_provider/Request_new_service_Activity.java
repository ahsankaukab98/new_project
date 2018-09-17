package com.example.user1.service_provider;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Request_new_service_Activity extends AppCompatActivity {

    EditText ed_new_service_message;
    EditText ed_new_service_request;
    Button btn_service_request;
    Shared_pref_Login msharedpref;
    private DatabaseReference request_firebase;
    boolean check=false;
    int counter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_new_service_);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Request New Service Form");
        }
        init();
        count();
        btn_service_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               requesting_service();
                Toast.makeText(Request_new_service_Activity.this,"Your new service request has been recieve wait for " +
                        "a day for system response",Toast.LENGTH_SHORT).show();
            }
        });

    }
    void init()
    {
        ed_new_service_message=findViewById(R.id.ed_new_service_message);
        ed_new_service_request=findViewById(R.id.ed_new_service_request);
        btn_service_request=findViewById(R.id.btn_service_request);
        msharedpref=new Shared_pref_Login(Request_new_service_Activity.this);
        request_firebase = FirebaseDatabase.getInstance().getReference("list_request");
    }
    void count()
    {
        request_firebase.child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue(Integer.class)!=null)
                {
                    counter=dataSnapshot.getValue(Integer.class);
                    counter=counter+1;
                }
                check=true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void requesting_service()
    {


       if(check)
       {
           FirebaseDatabase.getInstance().getReference("list_request").child("count").setValue(counter);
            request_firebase= FirebaseDatabase.getInstance().getReference("list_request").child("request "+counter);
            request_firebase.child("email").setValue(msharedpref.getData("Email").toString());
            request_firebase.child("name").setValue(ed_new_service_request.getText().toString());
            request_firebase.child("message").setValue(ed_new_service_message.getText().toString());
            check=false;
        }


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
