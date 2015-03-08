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
    private static final int CLASS_DAY_TITLE_HOUR_ID=101;
    private static final int STUDENT=3;
    private static final int STUDENT_ID=4;
    private static final int STUDENT_CLASS_ID=401;
    private static final int CLASSCONTENT=5;
    private static final int CLASSCONTENT_ID=6;
    private static final int CLASSCONTENT_CLASS_ID=601;
    private static final int STUDENTATTENDANCE=7;
    private static final int STUDENTATTENDANCE_ID=8;
    private static final int STUDENTATTENDANCE_STUDENT_ID=801;
    private static final int STUDENTATTENDANCE_CLASSCONTENT_ID=802;

    private static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    // The # sign will match numbers
    static {
        mUriMatcher.addURI(AUTHORITY,PATH_CLASS,CLASS);
        mUriMatcher.addURI(AUTHORITY,PATH_CLASS+"/#",CLASS_ID);

        mUriMatcher.addURI(AUTHORITY,"CLASS_DAY_TITLE_HOUR_ID",CLASS_DAY_TITLE_HOUR_ID);

        mUriMatcher.addURI(AUTHORITY,PATH_STUDENT,STUDENT);
        mUriMatcher.addURI(AUTHORITY,PATH_STUDENT+"/#",STUDENT_ID);
        mUriMatcher.addURI(AUTHORITY,PATH_STUDENT_WITH_FOREIGNKEY+"/#",STUDENT_CLASS_ID);

        mUriMatcher.addURI(AUTHORITY,PATH_CLASSCONTENT,CLASSCONTENT);
        mUriMatcher.addURI(AUTHORITY,PATH_CLASSCONTENT+"/#",CLASSCONTENT_ID);
        mUriMatcher.addURI(AUTHORITY,PATH_CLASSCONTENT_WITH_FOREIGNKEY+"/#",CLASSCONTENT_CLASS_ID);

        mUriMatcher.addURI(AUTHORITY,PATH_STUDENT_ATTENDANCE,STUDENTATTENDANCE);
        mUriMatcher.addURI(AUTHORITY,PATH_STUDENT_ATTENDANCE_WITH_CLASSCONTENT_ID+"/#",STUDENTATTENDANCE_CLASSCONTENT_ID);
        mUriMatcher.addURI(AUTHORITY,PATH_STUDENT_ATTENDANCE_WITH_STUDENT_ID+"/#",STUDENTATTENDANCE_STUDENT_ID);
    }

    SQLiteDatabase mdataBase;
    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mdataBase=new DbHelper(getContext()).getReadableDatabase();
        Cursor cursor=null;
        switch(mUriMatcher.match(uri)) {
            case CLASS:
                cursor=mdataBase.query(DbContract.ClassEntry.TABLE_NAME, new String[]{ClassEntry._ID,
                        ClassEntry.COLUMN_TITLE},null,null, null, null, null);
                break;
            case CLASS_ID:
                String id=uri.getLastPathSegment();
                cursor=mdataBase.query(DbContract.ClassEntry.TABLE_NAME, new String[]{ClassEntry.COLUMN_DATE,
                        ClassEntry.COLUMN_DURATION,
                        ClassEntry.COLUMN_EXTRA_INFO,
                        ClassEntry.COLUMN_LEVEL,
                        ClassEntry.COLUMN_LOCATION,
                        ClassEntry.COLUMN_TIME,
                        ClassEntry.COLUMN_TITLE},ClassEntry._ID+" =?", new String[]{id}, null, null, null);
                break;
            case CLASS_DAY_TITLE_HOUR_ID:
                cursor = mdataBase.query(DbContract.ClassEntry.TABLE_NAME, new String[]{
                        ClassEntry._ID, DbContract.ClassEntry.COLUMN_TITLE,
                        ClassEntry.COLUMN_TIME,
                        ClassEntry.COLUMN_DURATION,
                        ClassEntry.COLUMN_DATE}, null, null, null, null, null);
                break;
            case STUDENT_CLASS_ID:
                String classId=uri.getLastPathSegment();
                cursor=mdataBase.query(StudentEntry.TABLE_NAME, new String[]{
                        StudentEntry._ID,
                        StudentEntry.COLUMN_STUDENT_NAME,
                        StudentEntry.COLUMN_EMAIL,
                        StudentEntry.COLUMN_PHONE
                },StudentEntry.COLUMN_FOREIGN_KEY_CLASS+" =?", new String[]{classId}, null, null, null);

               /* String query="Select "+StudentEntry.TABLE_NAME+"."+StudentEntry._ID+","
                                                   +StudentEntry.TABLE_NAME+"."+StudentEntry.COLUMN_STUDENT_NAME+","
                                                   +StudentEntry.TABLE_NAME+"."+StudentEntry.COLUMN_EMAIL+","
                                                   +StudentEntry.TABLE_NAME+"."+StudentEntry.COLUMN_PHONE+","
                                                   +ClassEntry.TABLE_NAME+"."+ClassEntry._ID
                                                   +"from "+ StudentEntry.TABLE_NAME+","+ClassEntry.TABLE_NAME
                                                   +" where "
                                                   +StudentEntry.TABLE_NAME+"."+StudentEntry._ID+" = "+ClassEntry.TABLE_NAME+"."+ClassEntry._ID;
                Log.v("ClassContentProvider",query);
                cursor=mdataBase.rawQuery(query,null);*/



                break;
            case CLASSCONTENT_CLASS_ID:
                classId=uri.getLastPathSegment();
                cursor=mdataBase.query(ClassContentEntry.TABLE_NAME,new String[]{
                        ClassContentEntry.COLUMN_BOOK,
                        ClassContentEntry.COLUMN_DATE,
                        ClassContentEntry.COLUMN_TIMESTAMP,
                        ClassContentEntry.COLUMN_PAGE,
                        ClassContentEntry.COLUMN_INFO,
                        ClassContentEntry._ID},ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS+" =?",new String[]{classId},null,null,ClassContentEntry.COLUMN_TIMESTAMP+" desc");
                break;
            case STUDENTATTENDANCE_CLASSCONTENT_ID:
                String classContentId=uri.getLastPathSegment();
                String query="Select "+StudentAttendanceEntry.TABLE_NAME+"."+StudentAttendanceEntry._ID+","
                        +StudentAttendanceEntry.TABLE_NAME+"."+StudentAttendanceEntry.COLUMN_FOREIGN_KEY_STUDENT+","
                        +StudentAttendanceEntry.TABLE_NAME+"."+StudentAttendanceEntry.COLUMN_STATUS+","
                        +StudentEntry.TABLE_NAME+"."+StudentEntry.COLUMN_STUDENT_NAME

                        +" from "+StudentAttendanceEntry.TABLE_NAME+" inner join "+StudentEntry.TABLE_NAME+" on "
                        +StudentAttendanceEntry.TABLE_NAME+"."+StudentAttendanceEntry.COLUMN_FOREIGN_KEY_STUDENT+" = "
                        +StudentEntry.TABLE_NAME+"."+StudentEntry._ID+" where "
                        +StudentAttendanceEntry.TABLE_NAME+"."+StudentAttendanceEntry.COLUMN_FOREIGN_KEY_CLASSCONTENT+" = "+classContentId+";";

                Log.v("MYContentProvider"," QUERY: "+query);
                cursor=mdataBase.rawQuery(query,null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return cursor;
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
            case STUDENTATTENDANCE:
                table=StudentAttendanceEntry.TABLE_NAME;
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
        int affectedRows=0;
        switch(mUriMatcher.match(uri)) {
            case CLASS_ID:
                String id=uri.getLastPathSegment();
                mdataBase=new DbHelper(getContext()).getWritableDatabase();
                affectedRows=mdataBase.delete(ClassEntry.TABLE_NAME,ClassEntry._ID+"=?",new String[]{id});
                break;
            case STUDENT:
                break;
            case STUDENT_ID:
                id=uri.getLastPathSegment();
                mdataBase=new DbHelper(getContext()).getWritableDatabase();
                affectedRows=mdataBase.delete(StudentEntry.TABLE_NAME,StudentEntry._ID+"=?",new String[]{id});
                break;
            case CLASSCONTENT:
                break;
            case CLASSCONTENT_ID:
                String classId=uri.getLastPathSegment();
                mdataBase=new DbHelper(getContext()).getWritableDatabase();
                affectedRows=mdataBase.delete(ClassContentEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case STUDENTATTENDANCE_STUDENT_ID:
                String studentId=uri.getLastPathSegment();
                mdataBase=new DbHelper(getContext()).getWritableDatabase();
                affectedRows=mdataBase.delete(StudentAttendanceEntry.TABLE_NAME,StudentAttendanceEntry.COLUMN_FOREIGN_KEY_STUDENT+"=?",new String[]{studentId});
                break;
            case STUDENTATTENDANCE_CLASSCONTENT_ID:
                String classContentId=uri.getLastPathSegment();
                mdataBase=new DbHelper(getContext()).getWritableDatabase();
                affectedRows=mdataBase.delete(StudentAttendanceEntry.TABLE_NAME,StudentAttendanceEntry.COLUMN_FOREIGN_KEY_CLASSCONTENT+"=?",new String[]{classContentId});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String id="";
        int affectedRows=0;
        switch(mUriMatcher.match(uri)) {
            case CLASS_ID:
                id=uri.getLastPathSegment();
                mdataBase=new DbHelper(getContext()).getWritableDatabase();
                affectedRows=mdataBase.update(ClassEntry.TABLE_NAME,values,ClassEntry._ID+" = ?",new String[]{id});
                break;
            case STUDENT:
                break;
            case STUDENT_ID:
                id=uri.getLastPathSegment();
                mdataBase=new DbHelper(getContext()).getWritableDatabase();
                affectedRows=mdataBase.update(StudentEntry.TABLE_NAME,values,StudentEntry._ID+"=?",new String[]{id});
                break;
            case CLASSCONTENT:
                break;
            case CLASSCONTENT_ID:
                String classId=uri.getLastPathSegment();
                mdataBase=new DbHelper(getContext()).getWritableDatabase();
                affectedRows=mdataBase.update(ClassContentEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case STUDENTATTENDANCE_STUDENT_ID:
                mdataBase=new DbHelper(getContext()).getWritableDatabase();
                affectedRows=mdataBase.update(StudentAttendanceEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return affectedRows;
    }
}
