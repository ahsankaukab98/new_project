package com.example.user1.service_provider;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class fav_contact_activity extends AppCompatActivity {
    database_fav_services database_fav_services;
    String TAG_DES_SHORT="short";
    String TAG_DES_LONG="long";
    String TAG_IMAGE="image";
    String TAG_URL="url";
    String TAG_NAME="name";
    String TAG_MOBILE="mobile";
    String TAG_EMAIL="email";
    SearchView searchView;
    ArrayList<HashMap<String,String>> newlist;
    ArrayList<HashMap<String,String>>  arraylist;
    ListView list;
    Cursor cur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_contact_activity);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Favourite Services");
        }
        init();
        list_data();
    }
    void init()
    {
        database_fav_services=new database_fav_services(fav_contact_activity.this);
        list=findViewById(R.id.lv_fav_services);
        arraylist=new ArrayList<>();
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
    void list_data()
    {

        database_fav_services=new database_fav_services(fav_contact_activity.this);
        cur=database_fav_services.getdata();
        arraylist=new ArrayList<>();
        while(cur.moveToNext())
        {
            HashMap<String, String> hash;
            hash = new HashMap<String, String>();
            hash.put(TAG_DES_SHORT,cur.getString(2));
            hash.put(TAG_DES_LONG,cur.getString(3));
            hash.put(TAG_NAME,cur.getString(7));
            hash.put(TAG_EMAIL,cur.getString(8));
            hash.put(TAG_MOBILE,cur.getString(9));

            if (cur.getString(4)== null) {
                hash.put(TAG_IMAGE, R.mipmap.ic_launcher + "");
                hash.put(TAG_URL, null);
            } else {
                hash.put(TAG_IMAGE, R.mipmap.ic_launcher_round + "");
                hash.put(TAG_URL,cur.getString(4));
            }

            arraylist.add(hash);
        }
        updatedata("");

    }
    void updatedata(String name)
    {
//
        newlist=new ArrayList<>();

        list=findViewById(R.id.lv_fav_services);
        if(searchView!=null)
        {
            for(HashMap<String,String> service:arraylist)
            {
                if(service.containsValue(name))
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
        SimpleAdapter simpleAdapter=new SimpleAdapter(fav_contact_activity.this,newlist,R.layout.item_acitvity_your_services, from, to);
        list.setAdapter(simpleAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent=new Intent(fav_contact_activity.this,services_list_ind.class);
                intent.putExtra(TAG_URL,newlist.get(position).get(TAG_URL));
                intent.putExtra(TAG_DES_LONG,newlist.get(position).get(TAG_DES_LONG));
                intent.putExtra(TAG_DES_SHORT,newlist.get(position).get(TAG_DES_SHORT));
                intent.putExtra(TAG_MOBILE,newlist.get(position).get(TAG_MOBILE));
                intent.putExtra(TAG_NAME,newlist.get(position).get(TAG_NAME));
                intent.putExtra(TAG_EMAIL,newlist.get(position).get(TAG_EMAIL));
                startActivity(intent);

            }
        });
    }

}
