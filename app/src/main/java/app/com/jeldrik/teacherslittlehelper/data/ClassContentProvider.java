package app.com.jeldrik.teacherslittlehelper.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static app.com.jeldrik.teacherslittlehelper.data.DbContract.*;


/**
 * Created by jeldrik on 06/02/15.
 */
public class ClassContentProvider extends ContentProvider {

    private static final int CLASS=1;
    private static final int CLASS_ID=2;
    private static final int STUDENT=3;
    private static final int STUDENT_ID=4;
    private static final int CLASSCONTENT=5;
    private static final int CLASSCONTENT_ID=6;

    private static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    // The # sign will match numbers
    static {
        mUriMatcher.addURI(AUTHORITY,PATH_CLASS,CLASS);
        mUriMatcher.addURI(AUTHORITY,PATH_CLASS+"/#",CLASS_ID);

        mUriMatcher.addURI(AUTHORITY,PATH_STUDENT,STUDENT);
        mUriMatcher.addURI(AUTHORITY,PATH_STUDENT+"/#",STUDENT_ID);

        mUriMatcher.addURI(AUTHORITY,PATH_CLASSCONTENT,CLASSCONTENT);
        mUriMatcher.addURI(AUTHORITY,PATH_CLASSCONTENT+"/#",CLASSCONTENT_ID);
    }

    SQLiteDatabase mdataBase;
    @Override
    public boolean onCreate() {
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
        ContentValues vals=values;

        if(vals==null)
            return null;

        String table="";
        mdataBase=new DbHelper(getContext()).getWritableDatabase();

        switch(mUriMatcher.match(uri)) {
            case CLASS:
                Log.v("ContentProvider","Insert data into class table");
                table=ClassEntry.TABLE_NAME;
                break;
            case STUDENT:
                Log.v("ContentProvider","Insert data into student table");
                table=StudentEntry.TABLE_NAME;
                break;
            case CLASSCONTENT:
                Log.v("ContentProvider","Insert data into classcontent table");
                table=ClassContentEntry.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }
        long rowId=mdataBase.insert(table,null,values);
        Uri noteUri=null;
        if(rowId>0)
            noteUri= ContentUris.withAppendedId(uri,rowId);

        return noteUri;
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
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(mUriMatcher.match(uri)) {
            case CLASS:
                break;
            case STUDENT:
                break;
            case CLASSCONTENT:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch(mUriMatcher.match(uri)) {
            case CLASS:
                break;
            case STUDENT:
                break;
            case CLASSCONTENT:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return 0;
    }
}
