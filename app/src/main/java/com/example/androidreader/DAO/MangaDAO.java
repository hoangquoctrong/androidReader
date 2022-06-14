package com.example.androidreader.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;

import com.example.androidreader.Model.Manga;
import com.example.androidreader.Model.MangaData;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MangaDAO extends SQLiteOpenHelper {

    public static String DB_PATH = "/databases/";
    public static String DB_NAME = "manga.db";
    private SQLiteDatabase myDatabase;


    //init Data
    public MangaDAO(@Nullable Context context) {
        super(context, DB_NAME, null,1);
        if(Build.VERSION.SDK_INT>=17)
        {
            DB_PATH = context.getApplicationInfo().dataDir+"/databases/";
        }
        else {
            DB_PATH = "/data/data/" + context.getPackageName() + DB_PATH;
        }
    }

    //Check if Database exist
    public void checkDatabase()
    {
        SQLiteDatabase TempDB = null;
        try{
            openDatabase();
        } catch (Exception e) {
            this.getReadableDatabase();
        }
        if(TempDB!=null)
            TempDB.close();
    }

    //Open Database
    public void openDatabase(){
        String path = DB_PATH+DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);
    }

    public static final String COLUMN_ID = "ID";
    public static final String MANGA_TABLE = "MANGA_TABLE";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_COVER_URL = "COVERURL";
    public static final String COLUMN_LINK_URL = "LINKRURL";
    public static final String COLUMN_CHAPTER_URL = "CHAPTERURL";
    public static final String COLUMN_CHAPTER_NAME = "CHAPTERNAME";
    public static final String COLUMN_IS_FAVORITE = "ISFAVORITE";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_CHAPTER_INDEX = "CHAPTERINDEX";

    //close database
    @Override
    public synchronized void close() {
        if(myDatabase!=null)
            myDatabase.close();
        super.close();
    }


    //Create Data table MANGA_TABLE
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + MANGA_TABLE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_COVER_URL + " TEXT,"
                + COLUMN_LINK_URL + " TEXT,"
                + COLUMN_CHAPTER_URL + " TEXT,"
                + COLUMN_CHAPTER_NAME + " TEXT,"
                + COLUMN_IS_FAVORITE + " BOOLEAN,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_CHAPTER_INDEX + " INT)";

        sqLiteDatabase.execSQL(createTableStatement);
    }

    //Add new manga to database
    public void addOne(MangaData mangaData)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE,mangaData.getTitle());
        cv.put(COLUMN_COVER_URL,mangaData.getCoverURL());
        cv.put(COLUMN_LINK_URL,mangaData.getLinkURL());
        cv.put(COLUMN_CHAPTER_URL, mangaData.getChapterURL());
        cv.put(COLUMN_CHAPTER_NAME, mangaData.getChapterName());
        cv.put(COLUMN_IS_FAVORITE,mangaData.isFavorited());
        cv.put(COLUMN_DATE,mangaData.getDate().toString());
        cv.put(COLUMN_CHAPTER_INDEX,mangaData.getChapterIndex());
        db.insert(MANGA_TABLE,null,cv);
    }

    //Edit a specific mangaData for history or favorite
    public void EditManga(MangaData mangaData)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE,mangaData.getTitle());
        cv.put(COLUMN_COVER_URL,mangaData.getCoverURL());
        cv.put(COLUMN_LINK_URL,mangaData.getLinkURL());
        cv.put(COLUMN_CHAPTER_URL,mangaData.getChapterURL());
        cv.put(COLUMN_CHAPTER_NAME,mangaData.getChapterName());
        int favorite = mangaData.isFavorited() ? 1 : 0;
        cv.put(COLUMN_IS_FAVORITE,favorite);
        cv.put(COLUMN_DATE,mangaData.getDate().toString());
        cv.put(COLUMN_CHAPTER_INDEX,mangaData.getChapterIndex());
        String where = COLUMN_LINK_URL + "= '" + mangaData.getLinkURL() + "'";
        System.out.println("where" + where);
        db.update(MANGA_TABLE,cv,where, null);
    }

    //Check if data exist in database
    public boolean checkExist(String MangaURL){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + MANGA_TABLE + " WHERE " + COLUMN_LINK_URL + " = '" + MangaURL + "'";
        Cursor c;
        c = db.rawQuery(query,null);
        if(c.getCount() == 0)
            return false;
        return true;

    }

    //Get all favorite manga
    public List<MangaData> getAllFavorite()
    {
        List<MangaData> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + MANGA_TABLE + " WHERE " + COLUMN_IS_FAVORITE + "= 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        if(cursor.moveToFirst())
        {
            do{
                int ID = cursor.getInt(0);
                String title = cursor.getString(1);
                String CoverURL = cursor.getString(2);
                String LinkURL = cursor.getString(3);
                String chapterURL = cursor.getString(4);
                String chapterName = cursor.getString(5);
                boolean IsFavorite = cursor.getInt(6) > 0;
                String Date = cursor.getString(7);
                Date date1= new Date();
                try {
                    SimpleDateFormat sdf=new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy");
                    date1=sdf.parse(Date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int index = cursor.getInt(8);
                MangaData mangaData = new MangaData(title,CoverURL,LinkURL,chapterURL,chapterName,IsFavorite,date1,index);
                returnList.add(mangaData);
            }while(cursor.moveToNext());
        }
        else
        {

        }

        cursor.close();
        db.close();
        return returnList;
    }

    //Get 1 specific data for mangaDetail
    public MangaData getData(String MangaURL)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        String query = "SELECT * FROM " + MANGA_TABLE + " WHERE " + COLUMN_LINK_URL + " = '" + MangaURL + "'";
        Cursor c;
        c = db.rawQuery(query,null);

        c.moveToFirst();
        boolean favorite = c.getInt(6) > 0;
        System.out.println("Favorite database " + c.getInt(6));
        System.out.println("Favorite: " + favorite);
        String Date = c.getString(7);
        Date date1= new Date();
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy");
            date1=sdf.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        MangaData mangaData = new MangaData(c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),favorite,date1,c.getInt(8));
        System.out.println("Manga data get: " + mangaData.getTitle());
        return mangaData;

    }

    //Get all history data
    public List<MangaData> getHistory(){
        List<MangaData> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + MANGA_TABLE + " ORDER BY " + COLUMN_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        if(cursor.moveToFirst())
        {
            do{
                int ID = cursor.getInt(0);
                String title = cursor.getString(1);
                String CoverURL = cursor.getString(2);
                String LinkURL = cursor.getString(3);
                String chapterURL = cursor.getString(4);
                String chapterName = cursor.getString(5);
                boolean IsFavorite = cursor.getInt(6) > 0;
                String Date = cursor.getString(7);
                Date date1= new Date();
                try {
                    SimpleDateFormat sdf=new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy");
                    date1=sdf.parse(Date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int index = cursor.getInt(8);
                MangaData mangaData = new MangaData(title,CoverURL,LinkURL,chapterURL,chapterName,IsFavorite,date1,index);
                returnList.add(mangaData);
            }while(cursor.moveToNext());
        }
        else
        {

        }

        cursor.close();
        db.close();
        return returnList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
