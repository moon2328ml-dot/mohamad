package com.mohammadbahrami.border;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String[] STATUSES = {"لیست شرکت", "پارکینگ پایین", "باسکول", "پارکینگ بالا", "ورود عراق"};

    public DatabaseHelper(Context context) { super(context, "border_manager.db", null, 1); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE trucks (id INTEGER PRIMARY KEY AUTOINCREMENT, plate TEXT NOT NULL, driver TEXT, phone TEXT, cargo TEXT, owner TEXT, broker TEXT, origin TEXT, parking TEXT, weight TEXT, status INTEGER DEFAULT 0, created_at INTEGER)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public long add(String plate, String driver, String phone, String cargo, String owner, String broker, String origin, String parking, String weight) {
        ContentValues v = new ContentValues();
        v.put("plate", plate); v.put("driver", driver); v.put("phone", phone); v.put("cargo", cargo);
        v.put("owner", owner); v.put("broker", broker); v.put("origin", origin); v.put("parking", parking);
        v.put("weight", weight); v.put("status", 0); v.put("created_at", System.currentTimeMillis());
        return getWritableDatabase().insert("trucks", null, v);
    }

    public void advance(long id, int status) {
        ContentValues v = new ContentValues(); v.put("status", Math.min(status + 1, STATUSES.length - 1));
        getWritableDatabase().update("trucks", v, "id=?", new String[]{String.valueOf(id)});
    }

    public List<Truck> all(String query) {
        List<Truck> out = new ArrayList<>();
        String q = "%" + query + "%";
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM trucks WHERE plate LIKE ? OR driver LIKE ? OR owner LIKE ? ORDER BY id DESC", new String[]{q,q,q});
        while (c.moveToNext()) out.add(new Truck(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getInt(10)));
        c.close(); return out;
    }

    public int countStatus(int status) {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM trucks WHERE status=?", new String[]{String.valueOf(status)});
        int n = c.moveToFirst() ? c.getInt(0) : 0; c.close(); return n;
    }
}
