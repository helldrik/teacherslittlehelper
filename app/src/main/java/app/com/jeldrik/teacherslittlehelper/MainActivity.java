package app.com.jeldrik.teacherslittlehelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;
import app.com.jeldrik.teacherslittlehelper.data.DbHelper;


public class MainActivity extends ActionBarActivity implements NewClassFragment.OnAddNewClassListener, ClassFragment.OnDeleteListener,
        UpdateStudentFragment.OnStudentUpdatedListener,NewClassContentFragment.OnNewClassContentListener,
        NewStudentFragment.OnStudentAddedListener,UpdateClassFragment.OnClassUpdatedListener,
        UpdateClassContentFragment.OnUpdateClassContentListener {

    MainFragment mainFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment=new MainFragment();
        if(savedInstanceState!=null)
            mainFragment=(MainFragment)getSupportFragmentManager().getFragment(savedInstanceState,"mainFragment");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FragmentContainer, mainFragment)
                    .commit();
        }
     /*    SQLiteDatabase myDataBase=new DbHelper(this).getReadableDatabase();

       Cursor cursor=myDataBase.query(DbContract.ClassEntry.TABLE_NAME,new String[]{DbContract.ClassEntry.COLUMN_TITLE,DbContract.ClassEntry.COLUMN_LOCATION},null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Log.v("MYCURSOR",cursor.getString(0)+" location: "+cursor.getString(1));
            cursor.moveToNext();
        }
        String q="Select student.studentName,class.title from student inner join class on student.classID=class._id;";
        Cursor cursor=myDataBase.rawQuery(q,null);
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Log.v("MYCURSOR", cursor.getString(0) + " location: " + cursor.getString(1));
                cursor.moveToNext();
            }
        }

        myDataBase.close();

        ContentValues vals=new ContentValues(7);
        vals.put(DbContract.ClassEntry.COLUMN_TITLE,"the Title2");
        vals.put(DbContract.ClassEntry.COLUMN_TIME,"19:00");
        vals.put(DbContract.ClassEntry.COLUMN_DATE,"Monday");
        vals.put(DbContract.ClassEntry.COLUMN_DURATION, 60);
        vals.put(DbContract.ClassEntry.COLUMN_LOCATION,"Madrid");
        vals.put(DbContract.ClassEntry.COLUMN_LEVEL,"A2");
        vals.put(DbContract.ClassEntry.COLUMN_EXTRA_INFO,"no extra infos");

        ContentResolver resolver=getContentResolver();
        Uri returnUri=resolver.insert(DbContract.ClassEntry.CONTENT_URI,vals);
        Log.v("MainActivity",returnUri.toString());

        ContentValues vals=new ContentValues();
        vals.put(DbContract.StudentAttendanceEntry.COLUMN_FOREIGN_KEY_CLASSCONTENT,2);
        vals.put(DbContract.StudentAttendanceEntry.COLUMN_FOREIGN_KEY_STUDENT,3);
        vals.put(DbContract.StudentAttendanceEntry.COLUMN_STATUS,"ill");
        ContentResolver resolver=getContentResolver();
        Uri returnUri=resolver.insert(DbContract.StudentAttendanceEntry.CONTENT_URI,vals);
        Log.v("MainActivity","return uri: "+returnUri.toString());
/*
        ContentValues vals=new ContentValues();
        vals.put(DbContract.StudentEntry.COLUMN_STUDENT_NAME, "Emmir");
        vals.put(DbContract.StudentEntry.COLUMN_FOREIGN_KEY_CLASS,2);
        ContentResolver resolver=getContentResolver();
        Uri returnUri=resolver.insert(DbContract.StudentEntry.CONTENT_URI,vals);
/*
        vals=new ContentValues();
        vals.put(DbContract.ClassContentEntry.COLUMN_DATE, "ayer");
        vals.put(DbContract.ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS,2);
        resolver=getContentResolver();
        returnUri=resolver.insert(DbContract.ClassContentEntry.CONTENT_URI,vals);
*/
        /*
        resolver = this.getContentResolver();
        Uri uri2= DbContract.StudentAttendanceEntry.CONTENT_URI_WITH_STUDENTKEY.buildUpon().appendPath("2").build();
        resolver = this.getContentResolver();
        resolver.delete(uri2,null,null);

        uri2= DbContract.StudentAttendanceEntry.CONTENT_URI_WITH_CLASSCONTENTKEY.buildUpon().appendPath("2").build();
        resolver = this.getContentResolver();
        resolver.delete(uri2,null,null);

        resolver = this.getContentResolver();
        Uri uri= DbContract.StudentAttendanceEntry.CONTENT_URI_WITH_CLASSCONTENTKEY.buildUpon().appendPath("1").build();
        Cursor cursor=resolver.query(uri,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Log.v("MYCURSOR","ID: "+cursor.getString(0)+" Foreign Key Student: "+cursor.getString(1)+" Status: "+cursor.getString(2)+" Student Name: "+cursor.getString(3));
            cursor.moveToNext();
        }
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState,"mainFragment",mainFragment);
    }
    //---------------------------------------------------------------------------------------------
    //LISTENER METHODS
    //---------------------------------------------------------------------------------------------
    @Override
    public void onAddNewClass(ArrayList days,String title, String startTime,String endTime, int id) {
        //Toast.makeText(this,day+" "+msg,Toast.LENGTH_LONG).show();
        mainFragment.addNewClassToAdapter(days,title,startTime,endTime,id);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onDelete(int id) {
        try {
            mainFragment.deleteClassfromAdapter(id);
        }catch(Exception e){
            Log.e("MainActivity","Something went wrong her: "+e);
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    //---------------------------------------------------------------------------------------------
    //Methods to forward Results from DialogFragments to fragment that called it
    //---------------------------------------------------------------------------------------------
    public void forwardDataFromDialogFragmentToFragment(String TAG, ArrayList data){
        switch (TAG){
            case NewClassFragment.TAG:
                NewClassFragment frag=(NewClassFragment)getSupportFragmentManager().findFragmentByTag(TAG);
                frag.setSelectedWeekdays(data);
                break;
            case UpdateClassFragment.TAG:
                UpdateClassFragment updateFrag=(UpdateClassFragment)getSupportFragmentManager().findFragmentByTag(TAG);
                updateFrag.setSelectedWeekdays(data);
            default:
                Log.e("MainActivity","could not find Fragment "+TAG);
        }
    }
    //---------------------------------------------------------------------------------------------
    public void forwardTimeFromDialogFragmentToFragment(String TAG, String time){
        switch (TAG){
            case NewClassFragment.TAG:
            case NewClassFragment.TAG+"END":
                NewClassFragment frag=(NewClassFragment)getSupportFragmentManager().findFragmentByTag(NewClassFragment.TAG);
                frag.setSelectedTime(TAG,time);
                break;
            case UpdateClassFragment.TAG:
            case UpdateClassFragment.TAG+"END":
                UpdateClassFragment upFrag=(UpdateClassFragment)getSupportFragmentManager().findFragmentByTag(UpdateClassFragment.TAG);
                upFrag.setSelectedTime(TAG,time);
                break;
            default:
                Log.e("MainActivity","could not find Fragment "+TAG);
        }
    }
    //---------------------------------------------------------------------------------------------
    public void forwardDatetoNewClassContentFragment(String date, int timestamp){
        //Toast.makeText(this,date,Toast.LENGTH_LONG).show();
        NewClassContentFragment frag=(NewClassContentFragment)getSupportFragmentManager().findFragmentByTag(NewClassContentFragment.TAG);
        frag.setDate(date,timestamp);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStudentUpdated(StudentAdapter.StudentAdapterValues vals,int position) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.updateStudents(vals, position);

    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void OnNewClassContent(ClassContentAdapter.ClassContentAdapterValues values) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.newClassContent(values);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void OnUpdateClassContent(ClassContentAdapter.ClassContentAdapterValues values) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.upDateClassContent(values);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onDeleteClassContent(ClassContentAdapter.ClassContentAdapterValues deletedObj) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.deleteClassContent(deletedObj);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStudentAdded(StudentAdapter.StudentAdapterValues newStudent) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.updateStudents(newStudent,-1);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void OnClassUpdated(int id, String title, String days, String location, String startTime, String endTime, String level, String info) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.updateMemberVars(title,days,location,startTime,endTime,level,info);
        mainFragment.updateClassinAdapter(days,title,startTime,endTime,id);
    }
}
