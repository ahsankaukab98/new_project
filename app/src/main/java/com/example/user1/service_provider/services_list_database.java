package com.example.user1.service_provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class services_list_database extends SQLiteOpenHelper
{
    public static final String col_1="name";
    public static final String ID="ID";
    public static final String DataBase_NAME="list_services_contact.db";

    public static final String TABLE_NAME="Server_contact_table";
    private static final String TAG = "DatabaseHelper";

    public services_list_database(Context context)
    {
        super(context, DataBase_NAME,null,1);
        SQLiteDatabase db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY,"+
                        col_1 + " TEXT)";

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public boolean add_data(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content=new ContentValues();
        content.put(col_1,name);
        db.insert(TABLE_NAME,null,content);

        return true;
    }
    public Cursor getdata()
    {
        SQLiteDatabase db = this.getReadableDatabase();




// How you want the results sorted in the resulting Cursor
        String sortOrder =
                col_1 + " DESC";


        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                col_1           // The sort order
        );
        return cursor;
    }
    public void delete_data(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME,"name=?",new String[]{name});
    }
    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+TABLE_NAME;
        db.execSQL(clearDBQuery);
    }

}