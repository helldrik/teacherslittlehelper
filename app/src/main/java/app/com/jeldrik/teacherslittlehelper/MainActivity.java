package app.com.jeldrik.teacherslittlehelper;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements NewClassFragment.OnAddNewClassListener, ClassFragment.OnDeleteListener,
        UpdateStudentFragment.OnStudentUpdatedListener,NewClassContentFragment.OnNewClassContentListener,
        NewStudentFragment.OnStudentAddedListener,UpdateClassFragment.OnClassUpdatedListener {

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
    public void forwardDatetoNewClassContentFragment(String date){
        Toast.makeText(this,date,Toast.LENGTH_LONG).show();
        NewClassContentFragment frag=(NewClassContentFragment)getSupportFragmentManager().findFragmentByTag(NewClassContentFragment.TAG);
        frag.setDate(date);
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
        frag.updateClassContent(values);
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
