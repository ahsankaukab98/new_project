package com.example.user1.service_provider;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class your_service_activity extends AppCompatActivity {
    ArrayList<HashMap<String,String>> newlist;
    ArrayList<HashMap<String,String>>  arraylist;
    ListView list;
    SearchView searchView;
    String TAG_DES_SHORT="short";
    String TAG_DES_LONG="long";
    String TAG_IMAGE="image";
    String TAG_URL="url";
    ImageView img;
    // Create a storage reference from our app
    private DatabaseReference userdata;
    Shared_pref_Login msharedpref;
    database_activity_your_services mdatabaase_your_services;
    Cursor cur;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    boolean check=false;
    File localFile=null;
    StorageReference httpsReference;
    Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_service_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Your Services");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(your_service_activity.this,your_service_add_activity.class));
            }
        });
        init();
        list_data();

    }
    void list_data()
    {

       img=findViewById(R.id.img_sample);
        Integer resource = (Integer)img.getTag();

        userdata.addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    if(!data.getKey().toString().equals("count"))
                    {
//                        Toast.makeText(your_service_activity.this,data.getKey().toString()
//                            ,Toast.LENGTH_SHORT).show();
                        mdatabaase_your_services=new database_activity_your_services(your_service_activity.this);
                        cur=mdatabaase_your_services.getdata();
                        check=false;
                        while(cur.moveToNext())
                        {
                            if(cur.getString(2).equals(data.child("short_description").getValue().toString()))
                            {
                                check=true;
                                break;
                            }
                        }
                        if(check==false)
                        {
                            if(data.child("image_uri").getValue()==null)
                            {
                                mdatabaase_your_services.add_data(R.mipmap.ic_launcher + "",
                                        data.child("short_description").getValue().toString()
                                        , data.child("Long_description").getValue().toString(),null,
                                        data.getKey());
                            }
                            else
                            {
                                img.setTag(R.mipmap.ic_launcher_round);
                              //  load_image(data);
//                                Integer resource = (Integer)img.getTag();
                                //int resourceId = img.getResources().getIdentifier("flag_"+data.child("image_uri").getValue(), "drawable", "net.labasland.big.t3");
                                mdatabaase_your_services.add_data(R.mipmap.ic_launcher_round+"",
                                        data.child("short_description").getValue().toString()
                                        , data.child("Long_description").getValue().toString()
                                        ,data.child("image_uri").getValue().toString(),
                                        data.getKey());
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       mdatabaase_your_services=new database_activity_your_services(your_service_activity.this);
        cur=mdatabaase_your_services.getdata();
        while(cur.moveToNext())
        {
            HashMap<String,String> hash;
            hash=new HashMap<String,String>();
            hash.put(TAG_DES_SHORT,cur.getString(2));
            hash.put(TAG_DES_LONG,cur.getString(3));
            hash.put(TAG_IMAGE,cur.getString(1));
            hash.put(TAG_URL,cur.getString(4));
                          arraylist.add(hash);

        }
        updatedata("");

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
        list=findViewById(R.id.lv_your_services);
        arraylist=new ArrayList<>();
        newlist=new ArrayList<>();
       msharedpref=new Shared_pref_Login(your_service_activity.this);
        userdata= FirebaseDatabase.getInstance().getReference("service_providers")
               .child(msharedpref.getData("Firebase")).child("my_services");
        mdatabaase_your_services =new database_activity_your_services(your_service_activity.this);
    }
    void updatedata(String name)
    {
        newlist=new ArrayList<>();
        list=findViewById(R.id.lv_your_services);
        if(searchView!=null)
        {
            for(HashMap<String,String> service:arraylist)
            {
                if(service.get(TAG_DES_SHORT).toLowerCase().contains(name))
                {
                    newlist.add(service);
                }

            }
        }
        else
            newlist=arraylist;
//
        String []from={TAG_IMAGE,TAG_DES_SHORT};

        int [] to={R.id.img_services,R.id.tv_items_des_short};
        SimpleAdapter simpleAdapter=new SimpleAdapter(your_service_activity.this,newlist,R.layout.item_acitvity_your_services, from, to);
        list.setAdapter(simpleAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent=new Intent(your_service_activity.this,your_service_show_des.class);
                intent.putExtra(TAG_URL,newlist.get(position).get(TAG_URL));
                intent.putExtra(TAG_DES_LONG,newlist.get(position).get(TAG_DES_LONG));
                intent.putExtra(TAG_DES_SHORT,newlist.get(position).get(TAG_DES_SHORT));
                startActivity(intent);

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        searchView=(SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updatedata(newText);
                return false;
            }
        });

        return true;
    }
    void load_image(DataSnapshot data)
    {
//        httpsReference=storage.getReferenceFromUrl(data.child("image_uri").getValue().toString());
//        try
//        {
//            localFile = File.createTempFile("images", "jpg");
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

//        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(your_service_activity.this,"file created Successfully", Toast.LENGTH_SHORT).show();
//                // Local temp file has been created
//                Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                img.setImageBitmap(myBitmap);
//                //img.setTag(R.mipmap.ic_launcher_round);
//
//                Integer resource = (Integer)img.getTag();
//                //int resourceId = img.getResources().getIdentifier("flag_"+data.child("image_uri").getValue(), "drawable", "net.labasland.big.t3");
//                mdatabaase_your_services.add_data(resource+"",
//                        data.child("short_description").getValue().toString()
//                        , data.child("Long_description").getValue().toString()
//                        ,data.child("image_uri").getValue().toString());
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//                Toast.makeText(your_service_activity.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        });



    }
}

// if(item.getItemId()==R.id.btn_delete)
//         {
//         mdatabsehelper4=new DataBaseHelper4(MessageBody.this);
//         AlertDialog.Builder adb=new AlertDialog.Builder(MessageBody.this);
//         adb.setIcon(R.mipmap.person);
//         adb.setMessage("Do you want to delete this message");
//         adb.setTitle("Delete Message");
//         adb.setCancelable(true);
//         adb.setNegativeButton("Delete For Every One", new DialogInterface.OnClickListener() {
//
//public void onClick(DialogInterface dialog, int which)
//        {
//
//        }
//        });
//        adb.setPositiveButton("Delete for me only", new DialogInterface.OnClickListener() {
//
//public void onClick(DialogInterface dialog, int which)
//        {
//        for(HashMap<String,String> del:arrayList_delete)
//        {
//
//        // newlist.remove(del);
//
//        }
//
//        }
//        });
//        adb.show();
//
//        String []from={TAG_RECIEVE,TAG_SEND};
//        int [] to={R.id.textview_list_message_body_recieve,R.id.textview_list_message_body_send};
//        SimpleAdapter simpleAdapter=new SimpleAdapter(MessageBody.this,newlist,R.layout.list_item_messagebody,from,to);
//        list.setAdapter(simpleAdapter);
//        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        }
