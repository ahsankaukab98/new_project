package com.example.user1.service_provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class database_fav_services extends SQLiteOpenHelper
{
    public static final String col_1="image";
    public static final String col_2="des_short";
    public static final String col_3="des_long";
    public static final String col_4="uri";
    public static final String col_7="name";
    public static final String col_8="email";
    public static final String col_9="mobile";
    public static final String ID="ID";
    public static final String DataBase_NAME="fav_services.db";

    public static final String TABLE_NAME="fav_service_table";
    private static final String TAG = "DatabaseHelper";

    public database_fav_services(Context context)
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
                        col_1 + " TEXT," +
                        col_2 + " TEXT," +
                        col_3 + " TEXT," +
                        col_4 + " TEXT," +
                        col_7 + " TEXT," +
                        col_8 + " TEXT," +
                        col_9 + " TEXT )";

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }


    public boolean add_data(String image,String des_short,String des_long,String uri,String name,String email,String mobile)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content=new ContentValues();
        content.put(col_1,image);
        content.put(col_2,des_short);
        content.put(col_3,des_long);
        content.put(col_4,uri);
        content.put(col_7,name);
        content.put(col_8,email);
        content.put(col_9,mobile);

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
                col_2           // The sort order
        );
        return cursor;
    }
    public void delete_data(String des_short)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME,"name=?",new String[]{des_short});
    }
    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+TABLE_NAME;
        db.execSQL(clearDBQuery);
    }

}