package app.com.jeldrik.teacherslittlehelper.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by jeldrik on 06/02/15.
 */
public class ClassContentProvider extends ContentProvider {

    DbHelper mdataBase;
    @Override
    public boolean onCreate() {
        mdataBase=new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        ContentValues vals=new ContentValues(7);
/*
        vals.put(DbContract.ClassEntry.COLUMN_TITLE,"the Title");
        vals.put(DbContract.ClassEntry.COLUMN_TIME,"17:00");
        vals.put(DbContract.ClassEntry.COLUMN_DATE,"21.02.2015");
        vals.put(DbContract.ClassEntry.COLUMN_DURATION, 90);
        vals.put(DbContract.ClassEntry.COLUMN_LOCATION,"Madrid");
        vals.put(DbContract.ClassEntry.COLUMN_LEVEL,"A1");
        vals.put(DbContract.ClassEntry.COLUMN_EXTRA_INFO,"no extra info");
        myDataBase.insert(DbContract.ClassEntry.TABLE_NAME,null,vals);

        vals=new ContentValues(4);
        vals.put(DbContract.ClassContentEntry.COLUMN_BOOK, "no Book");
        vals.put(DbContract.ClassContentEntry.COLUMN_PAGE, "15-20");
        vals.put(DbContract.ClassContentEntry.COLUMN_INFO, "no info");
        vals.put(DbContract.ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS, 1);
        myDataBase.insert(DbContract.ClassContentEntry.TABLE_NAME,null,vals);

        vals=new ContentValues(4);
        vals.put(DbContract.StudentEntry.COLUMN_STUDENT_NAME, "Petro");
        vals.put(DbContract.StudentEntry.COLUMN_EMAIL, "petro@email.com");
        vals.put(DbContract.StudentEntry.COLUMN_PHONE, "9111234532");
        vals.put(DbContract.StudentEntry.COLUMN_FOREIGN_KEY_CLASS, 1);
        myDataBase.insert(DbContract.StudentEntry.TABLE_NAME,null,vals);
 */
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
