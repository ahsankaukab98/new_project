package com.example.user1.service_provider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class services_list_ind extends AppCompatActivity {

    ImageView img_servicess;
    TextView ed_name;
    TextView ed_des_short;
    TextView ed_des_long;
    Button btn_Mobile;
    Button btn_email;
    Button btn_Mobile_sms;
    Bundle bundle;
    String TAG_DES_SHORT="short";
    String TAG_DES_LONG="long";
    String TAG_URL="url";
    String TAG_NAME="name";
    String TAG_MOBILE="mobile";
    String TAG_EMAIL="email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_list_ind);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Service Detail");
        }
        init();
        btn_email.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, bundle.getString(TAG_EMAIL));

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
        btn_Mobile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" +bundle.getString(TAG_MOBILE)));
                startActivity(intent);
            }
        });
        btn_Mobile_sms.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" +bundle.getString(TAG_MOBILE)));
                startActivity(intent);
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
        img_servicess=findViewById(R.id.img_servicess);
        ed_name=findViewById(R.id.ed_name);
        ed_des_short=findViewById(R.id.ed_des_short);
        ed_des_long=findViewById(R.id.ed_des_long);
        btn_Mobile=findViewById(R.id.btn_Mobile);
        btn_email=findViewById(R.id.btn_email);
        btn_Mobile_sms=findViewById(R.id.btn_Mobile_sms);
        bundle=getIntent().getExtras();
        set_text();
        set_image();
    }
    void set_text()
    {
        ed_name.setText(bundle.getString(TAG_NAME));
        ed_des_short.setText(bundle.getString(TAG_DES_SHORT));
        ed_des_long.setText(bundle.getString(TAG_DES_LONG));
        btn_Mobile.setText("Call : "+bundle.getString(TAG_MOBILE));
        btn_email.setText(bundle.getString(TAG_EMAIL));
        btn_Mobile_sms.setText("Message : "+bundle.getString(TAG_MOBILE));
    }
    void set_image()
    {
        if(bundle.getString(TAG_URL)!=null) {
            Glide.with(this)
                    .load(bundle.getString(TAG_URL)) // works for File or Uri
                    .into(img_servicess);
        }
    }



}
