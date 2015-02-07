package app.com.jeldrik.teacherslittlehelper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import app.com.jeldrik.teacherslittlehelper.data.DbContract.*;

/**
 * Created by jeldrik on 05/02/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME ="teachersLittleHelperDB.db";

    public DbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
        Log.v("dbHelper","Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //student table
        final String SQL_CREATE_STUDENT_TABLE="create table "+StudentEntry.TABLE_NAME+" ("+
               StudentEntry._ID+" integer primary key autoincrement,"+
               StudentEntry.COLUMN_FOREIGN_KEY_CLASS+ " integer not null,"+
               StudentEntry.COLUMN_STUDENT_NAME+" text not null, "+
               StudentEntry.COLUMN_EMAIL+" text, "+
               StudentEntry.COLUMN_PHONE+" text, "+
               " foreign key (" +StudentEntry.COLUMN_FOREIGN_KEY_CLASS+ ") references "+
                ClassEntry.TABLE_NAME+" ("+ClassEntry._ID+"));";

        //classContent table
        final String SQL_CREATE_CLASSCONTENT_TABLE="create table "+ClassContentEntry.TABLE_NAME+" ("+
                ClassContentEntry._ID+" integer primary key autoincrement,"+
                ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS+ " integer not null,"+
                ClassContentEntry.COLUMN_BOOK+" text, "+
                ClassContentEntry.COLUMN_PAGE+" text, "+
                ClassContentEntry.COLUMN_INFO+" text, "+
                " foreign key (" +ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS+ ") references "+
                ClassEntry.TABLE_NAME+" ("+ClassEntry._ID+"));";

        //class table
        final String SQL_CREATE_CLASS_TABLE="create table "+ClassEntry.TABLE_NAME+" ("+
                ClassEntry._ID+" integer primary key autoincrement,"+
                ClassEntry.COLUMN_TITLE+" text not null, "+
                ClassEntry.COLUMN_LOCATION+" text not null, "+
                ClassEntry.COLUMN_DATE+" text not null, "+
                ClassEntry.COLUMN_TIME+" text not null, "+
                ClassEntry.COLUMN_DURATION+" integer not null, "+
                ClassEntry.COLUMN_LEVEL+" text, "+
                ClassEntry.COLUMN_EXTRA_INFO+" text);";

        db.execSQL(SQL_CREATE_CLASS_TABLE);
        db.execSQL(SQL_CREATE_CLASSCONTENT_TABLE);
        db.execSQL(SQL_CREATE_STUDENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ClassEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ClassContentEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StudentEntry.TABLE_NAME);
        onCreate(db);
    }
}
