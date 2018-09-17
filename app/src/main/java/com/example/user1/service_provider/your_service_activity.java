package com.example.user1.service_provider;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class your_service_activity extends AppCompatActivity {
    ArrayList<HashMap<String,String>> newlist;
    ArrayList<HashMap<String,String>>  arraylist;
    ListView list;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    }
    void list_data()
    {
        arraylist=new ArrayList<>();
        list=findViewById(R.id.lv_your_services);
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
    }
    void updatedata(String name)
    {
        newlist=new ArrayList<>();
        list=new ListView(your_service_activity.this);
        list=findViewById(R.id.list_home);
        if(searchView!=null)
        {
//            for(String service:arraylist)
//            {
//                if(service.toLowerCase().contains(name))
//                {
//                    newlist.add(service);
//                }
//
//            }
        }
        else
            newlist=arraylist;
//
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent=new Intent(your_service_activity.this,Seervice_list.class);
                intent.putExtra(name, newlist.get(position));
                startActivity(intent);
            }
        });
        //String []from={TAG_RECIEVE,TAG_SEND};
        //int [] to={R.id.textview_list_message_body_recieve,R.id.textview_list_message_body_send};
      //  SimpleAdapter simpleAdapter=new SimpleAdapter(MessageBody.this,newlist,R.layout.list_item_messagebody,from,to);
//        list.setAdapter(simpleAdapter);
//        ArrayAdapter arrayAdapter=new ArrayAdapter(your_service_activity.this, R.layout.item_activity_home, R.id.tv_items_name,newlist);
//        list.setAdapter(arrayAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.home,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
       // MenuItem menuItem=menu.findItem(R.id.action_search);
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

}
