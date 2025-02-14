package com.avagr.crypt;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "EncryptionData";
    private static final int VERSION = 1;
    private static final String CREATE = "create table if not exists encres (_id integer primary key autoincrement," +
                                            "title text not null," +
                                            "enctext text not null," +
                                            "keytext text not null," +
                                            "cipher text not null," +
                                            "time text not null)";


    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    public ArrayList<HashMap<String, String>> getEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> entryList = new ArrayList<>();
        String query = "select title, enctext, keytext, time, cipher from encres";
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()) {
            HashMap<String, String> entry = new HashMap<>();
            entry.put("title", cursor.getString(cursor.getColumnIndex("title")));
            entry.put("enctext", cursor.getString(cursor.getColumnIndex("enctext")));
            entry.put("keytext", cursor.getString(cursor.getColumnIndex("keytext")));
            entry.put("time", cursor.getString(cursor.getColumnIndex("time")));
            entry.put("cipher", cursor.getString(cursor.getColumnIndex("cipher")));
            entryList.add(entry);
        }
        return entryList;
    }

    public void DeleteEntry(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("encres", "title=?", new String[]{title});
        db.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
