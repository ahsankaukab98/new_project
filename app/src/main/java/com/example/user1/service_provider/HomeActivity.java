package com.example.user1.service_provider;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<String> arraylist;
    ArrayList<String> newlist;

    ListView list;
    SearchView searchView;
    String TAG_SERVICE="service";
    services_list_database mservice_database;
    Cursor cur;
    private DatabaseReference service_list_firebase;
    boolean check=false;
    Shared_pref_Login msharedpref;
    TextView tv_name;
    TextView tv_mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(HomeActivity.this,Request_new_service_Activity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        list_data();

    }

    void list_data()
    {
        service_list_firebase= FirebaseDatabase.getInstance().getReference("list_services");
        service_list_firebase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    check=false;
                    mservice_database=new services_list_database(HomeActivity.this);
                    cur=mservice_database.getdata();
                    while(cur.moveToNext())
                    {
                        if(cur.getString(1).equals(data.getKey().toString()))
                        {
                            check=true;
                            break;
                        }
                    }
                    if(!check && data.getKey().toString()!="count")
                    {
                     mservice_database.add_data(data.getKey().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mservice_database=new services_list_database(HomeActivity.this);
        cur=mservice_database.getdata();
        arraylist=new ArrayList<>();
        list=findViewById(R.id.list_home);
        while(cur.moveToNext())
        {
            arraylist.add(cur.getString(1));
        }
        updatedata("");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.home,menu);
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
        msharedpref=new Shared_pref_Login(HomeActivity.this);
        tv_name=findViewById(R.id.tv_name);
        tv_mobile=findViewById(R.id.tv_mobile);
        tv_name.setText(msharedpref.getData("Name"));
        tv_mobile.setText(msharedpref.getData("Mobile"));

        return true;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile)
        {
           startActivity(new Intent(HomeActivity.this,ProfileAcivity.class));
        }
        else if (id == R.id.nav_your_services)
        {
            startActivity(new Intent(HomeActivity.this,your_service_activity.class));
            startActivity(new Intent(HomeActivity.this,your_service_activity.class));
        }
        else if (id == R.id.nav_fav_services)
        {

        }
        else if (id == R.id.log_out)
        {
            msharedpref=new Shared_pref_Login(HomeActivity.this);
            msharedpref.logoutsession();
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            finish();
        }
        else if (id == R.id.about_us)
        {

        }
        else if (id == R.id.rate_app)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void updatedata(String name)
    {
//

        newlist=new ArrayList<>();
        list=new ListView(HomeActivity.this);
        list=findViewById(R.id.list_home);
        if(searchView!=null)
        {
                for(String service:arraylist)
               {
                if(service.toLowerCase().contains(name))
                {
                    newlist.add(service);
                }

              }
        }
        else
            newlist=arraylist;
//
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent=new Intent(HomeActivity.this,Seervice_list.class);
                intent.putExtra(name, newlist.get(position));
                startActivity(intent);
            }
        });

        ArrayAdapter arrayAdapter=new ArrayAdapter(HomeActivity.this, R.layout.item_activity_home, R.id.tv_items_name,newlist);
        list.setAdapter(arrayAdapter);
    }



}
