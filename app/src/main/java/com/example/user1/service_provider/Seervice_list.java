package com.example.user1.service_provider;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Seervice_list extends AppCompatActivity {

    SearchView searchView;
    ArrayList<HashMap<String,String>> newlist;
    ArrayList<HashMap<String,String>>  arraylist;
    ListView list;
    String TAG_DES_SHORT="short";
    String TAG_DES_LONG="long";
    String TAG_IMAGE="image";
    String TAG_URL="url";
    String TAG_NAME="name";
    String TAG_MOBILE="mobile";
    String TAG_EMAIL="email";
    private DatabaseReference selector;
    String TAG_SELECT="select";
    boolean verify=false;
    Bundle bundle;
    int count=0;
    ArrayList<HashMap<String,String>>  temp_list;
    database_fav_services mfav_data;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seervice_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Service Detail List");
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Seervice_list.this,services_list_ind.class));
            }
        });
        init();
        list_data();
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
    void init()
    {
        list=findViewById(R.id.lv_services);
        arraylist=new ArrayList<>();
        newlist=new ArrayList<>();
        bundle=new Bundle();
        bundle=getIntent().getExtras();
        selector = FirebaseDatabase.getInstance().getReference("list_services")
                .child(bundle.getString(TAG_SELECT));
        temp_list=new ArrayList<>();
        mfav_data=new database_fav_services(Seervice_list.this);

    }
    void updatedata(String name)
    {
        newlist=new ArrayList<>();
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
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        SimpleAdapter simpleAdapter=new SimpleAdapter(Seervice_list.this,newlist,R.layout.item_acitvity_your_services, from, to);
        list.setAdapter(simpleAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent=new Intent(Seervice_list.this,services_list_ind.class);
                intent.putExtra(TAG_URL,newlist.get(position).get(TAG_URL));
                intent.putExtra(TAG_DES_LONG,newlist.get(position).get(TAG_DES_LONG));
                intent.putExtra(TAG_DES_SHORT,newlist.get(position).get(TAG_DES_SHORT));
                intent.putExtra(TAG_MOBILE,newlist.get(position).get(TAG_MOBILE));
                intent.putExtra(TAG_NAME,newlist.get(position).get(TAG_NAME));
                intent.putExtra(TAG_EMAIL,newlist.get(position).get(TAG_EMAIL));
                startActivity(intent);

            }
        });
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {
                count=count+1;
                mode.setTitle(count+" itmes Selected");
                temp_list.add(newlist.get(position));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                MenuInflater inf=mode.getMenuInflater();
                inf.inflate(R.menu.menu_fav,menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                if(item.getItemId()==R.id.btn_fav)
                {
                    for(HashMap<String,String> fav:temp_list)
                    {
                        mfav_data= new database_fav_services(Seervice_list.this);
                        Cursor cu=mfav_data.getdata();
                        boolean check=false;
                        //Toast.makeText(Messages.this,"hello",Toast.LENGTH_SHORT).show();
                            cu=mfav_data.getdata();
                            while(cu.moveToNext())
                            {
                                if(cu.getString(2).equals(fav.get(TAG_DES_SHORT)))
                                    check=true;
                            }
                            if(!check)
                            {
                                Toast.makeText(Seervice_list.this,"Service Added to the Favourite List",Toast.LENGTH_SHORT).show();
                               mfav_data.add_data(fav.get(TAG_IMAGE),fav.get(TAG_DES_SHORT),fav.get(TAG_DES_LONG)
                                       ,fav.get(TAG_URL),fav.get(TAG_NAME),fav.get(TAG_EMAIL),fav.get(TAG_MOBILE));
                            }
                            else
                                Toast.makeText(Seervice_list.this,"Service is already in your Favourite List",Toast.LENGTH_SHORT).show();



                    }

                }

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {

            }
        });
    }
    void list_data()
    {
        selector.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    for(HashMap<String,String> hash:arraylist)
                    {
                        if(hash.get(TAG_DES_SHORT).equals(data.child("short_description").getValue().toString()))
                        verify=true;
                        break;
                    }
                    if(verify==false)
                    {
                        HashMap<String, String> hash;
                        hash = new HashMap<String, String>();
                        hash.put(TAG_DES_SHORT, data.child("short_description").getValue().toString());
                        hash.put(TAG_DES_LONG, data.child("Long_description").getValue().toString());
                        hash.put(TAG_NAME, data.child("name").getValue().toString());
                        hash.put(TAG_MOBILE, data.child("mobile").getValue().toString());
                        hash.put(TAG_EMAIL, data.child("email").getValue().toString());

                        if (data.child("image_uri").getValue() == null) {
                            hash.put(TAG_IMAGE, R.mipmap.ic_launcher + "");
                            hash.put(TAG_URL, null);
                        } else {
                            hash.put(TAG_IMAGE, R.mipmap.ic_launcher_round + "");
                            hash.put(TAG_URL, data.child("image_uri").getValue().toString());
                        }

                        arraylist.add(hash);
                    }
                    verify=false;
                    updatedata("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }


}
