package com.mohammadbahrami.border;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {
  public DatabaseHelper(Context c){super(c,"border_manager.db",null,3);}
  public void onCreate(SQLiteDatabase d){
    d.execSQL("CREATE TABLE owners(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL UNIQUE)");
    d.execSQL("CREATE TABLE cargos(id INTEGER PRIMARY KEY AUTOINCREMENT,owner_id INTEGER,name TEXT NOT NULL,iraqi_owner TEXT,broker TEXT,origin TEXT)");
    d.execSQL("CREATE TABLE trucks(id INTEGER PRIMARY KEY AUTOINCREMENT,cargo_id INTEGER,plate TEXT NOT NULL,driver TEXT,phone TEXT,weight TEXT,size TEXT,entry_at INTEGER,exit_at INTEGER,border_status TEXT,created_at INTEGER)");
  }
  public void onUpgrade(SQLiteDatabase d,int o,int n){
    if(o<2){ d.execSQL("ALTER TABLE trucks RENAME TO trucks_old");
      onCreate(d);
      try{d.execSQL("INSERT OR IGNORE INTO owners(name) SELECT DISTINCT CASE WHEN owner IS NULL OR owner='' THEN 'بدون نام' ELSE owner END FROM trucks_old");
        d.execSQL("INSERT INTO cargos(owner_id,name,iraqi_owner,broker,origin) SELECT o.id,CASE WHEN t.cargo IS NULL OR t.cargo='' THEN 'متفرقه' ELSE t.cargo END,'',MAX(t.broker),MAX(t.origin) FROM trucks_old t JOIN owners o ON o.name=CASE WHEN t.owner IS NULL OR t.owner='' THEN 'بدون نام' ELSE t.owner END GROUP BY o.id,t.cargo");
        d.execSQL("INSERT INTO trucks(cargo_id,plate,driver,phone,weight,size,created_at) SELECT c.id,t.plate,t.driver,t.phone,t.weight,'',t.created_at FROM trucks_old t JOIN owners o ON o.name=CASE WHEN t.owner IS NULL OR t.owner='' THEN 'بدون نام' ELSE t.owner END JOIN cargos c ON c.owner_id=o.id AND c.name=CASE WHEN t.cargo IS NULL OR t.cargo='' THEN 'متفرقه' ELSE t.cargo END");
      }catch(Exception ignored){} d.execSQL("DROP TABLE IF EXISTS trucks_old"); } if(o<3){try{d.execSQL("ALTER TABLE trucks ADD COLUMN entry_at INTEGER");}catch(Exception ignored){} try{d.execSQL("ALTER TABLE trucks ADD COLUMN exit_at INTEGER");}catch(Exception ignored){} try{d.execSQL("ALTER TABLE trucks ADD COLUMN border_status TEXT");}catch(Exception ignored){}}
  }
  long addOwner(String n){ContentValues v=new ContentValues();v.put("name",n);return getWritableDatabase().insert("owners",null,v);}
  long addCargo(long oid,String n,String io,String b,String or){ContentValues v=new ContentValues();v.put("owner_id",oid);v.put("name",n);v.put("iraqi_owner",io);v.put("broker",b);v.put("origin",or);return getWritableDatabase().insert("cargos",null,v);}
  long addTruck(long cid,String p,String d,String ph,String w,String s){ContentValues v=new ContentValues();v.put("cargo_id",cid);v.put("plate",p);v.put("driver",d);v.put("phone",ph);v.put("weight",w);v.put("size",s);v.put("entry_at",System.currentTimeMillis());v.put("border_status","پارکینگ");v.put("created_at",System.currentTimeMillis());return getWritableDatabase().insert("trucks",null,v);}  int updateTruck(long id,String p,String d,String ph,String w,String s,long entry,long exit,String status){ContentValues v=new ContentValues();v.put("plate",p);v.put("driver",d);v.put("phone",ph);v.put("weight",w);v.put("size",s);v.put("entry_at",entry);if(exit>0)v.put("exit_at",exit);else v.putNull("exit_at");v.put("border_status",status);return getWritableDatabase().update("trucks",v,"id=?",new String[]{""+id});}  int deleteTruck(long id){return getWritableDatabase().delete("trucks","id=?",new String[]{""+id});}  int deleteCargo(long id){getWritableDatabase().delete("trucks","cargo_id=?",new String[]{""+id});return getWritableDatabase().delete("cargos","id=?",new String[]{""+id});}  int updateCargo(long id,String n,String io,String b,String or){ContentValues v=new ContentValues();v.put("name",n);v.put("iraqi_owner",io);v.put("broker",b);v.put("origin",or);return getWritableDatabase().update("cargos",v,"id=?",new String[]{""+id});}
  Cursor owners(){return getReadableDatabase().rawQuery("SELECT o.id,o.name,COUNT(t.id) n FROM owners o LEFT JOIN cargos c ON c.owner_id=o.id LEFT JOIN trucks t ON t.cargo_id=c.id GROUP BY o.id ORDER BY o.id DESC",null);}
  Cursor cargos(long oid){return getReadableDatabase().rawQuery("SELECT c.id,c.name,c.iraqi_owner,c.broker,c.origin,COUNT(t.id) n FROM cargos c LEFT JOIN trucks t ON t.cargo_id=c.id WHERE c.owner_id=? GROUP BY c.id ORDER BY c.id DESC",new String[]{""+oid});}
  Cursor trucks(long cid){return getReadableDatabase().rawQuery("SELECT id,plate,driver,phone,weight,size,created_at,entry_at,exit_at,border_status FROM trucks WHERE cargo_id=? ORDER BY id DESC",new String[]{""+cid});}
  Cursor monthly(int jy,int jm){long[] r=Jalali.range(jy,jm);return getReadableDatabase().rawQuery("SELECT o.name,c.name,t.plate,t.driver,t.phone,t.weight,t.size,t.created_at FROM trucks t JOIN cargos c ON c.id=t.cargo_id JOIN owners o ON o.id=c.owner_id WHERE t.created_at>=? AND t.created_at<? ORDER BY o.name,c.name,t.id",new String[]{""+r[0],""+r[1]});}
  static class Jalali {
    static long[] range(int y,int m){int[] a=toGregorian(y,m,1),b=m==12?toGregorian(y+1,1,1):toGregorian(y,m+1,1);Calendar c=Calendar.getInstance();c.clear();c.set(a[0],a[1]-1,a[2]);long s=c.getTimeInMillis();c.clear();c.set(b[0],b[1]-1,b[2]);return new long[]{s,c.getTimeInMillis()};}
    static int[] today(){Calendar c=Calendar.getInstance();return toJalali(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH));}
    static int[] toGregorian(int jy,int jm,int jd){jy+=1595;int days=-355668+365*jy+(jy/33)*8+((jy%33+3)/4)+jd+(jm<7?(jm-1)*31:(jm-7)*30+186);int gy=400*(days/146097);days%=146097;if(days>36524){gy+=100*(--days/36524);days%=36524;if(days>=365)days++;}gy+=4*(days/1461);days%=1461;if(days>365){gy+=(days-1)/365;days=(days-1)%365;}int gd=days+1;int[] sal={0,31,((gy%4==0&&gy%100!=0)||gy%400==0)?29:28,31,30,31,30,31,31,30,31,30,31};int gm=1;while(gm<=12&&gd>sal[gm])gd-=sal[gm++];return new int[]{gy,gm,gd};}
    static int[] toJalali(int gy,int gm,int gd){int[] gdm={0,31,59,90,120,151,181,212,243,273,304,334};int gy2=gm>2?gy+1:gy;int days=355666+365*gy+(gy2+3)/4-(gy2+99)/100+(gy2+399)/400+gd+gdm[gm-1];int jy=-1595+33*(days/12053);days%=12053;jy+=4*(days/1461);days%=1461;if(days>365){jy+=(days-1)/365;days=(days-1)%365;}int jm=days<186?1+days/31:7+(days-186)/30;int jd=1+(days<186?days%31:(days-186)%30);return new int[]{jy,jm,jd};}
  }
}