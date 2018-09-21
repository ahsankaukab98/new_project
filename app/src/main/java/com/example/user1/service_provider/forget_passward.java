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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;

public class forget_passward extends AppCompatActivity {
    Button btn_email_recovery;
    EditText ed_email_recovery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passward);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Service Detail");
        }
        init();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        btn_email_recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mAuth.sendPasswordResetEmail(ed_email_recovery.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(forget_passward.this,"Email Recovery for passward Successfully send",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(forget_passward.this,LoginActivity.class));
                    finish();

                }
                else
                {
                    FirebaseException e= (FirebaseException) task.getException();
                    assert e != null;
                    Toast.makeText(forget_passward.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
            }
        });
//

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
        btn_email_recovery=findViewById(R.id.btn_email_recovery);
        ed_email_recovery=findViewById(R.id.ed_email_recovery);
    }

}
