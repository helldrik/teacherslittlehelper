package app.com.jeldrik.teacherslittlehelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;
import app.com.jeldrik.teacherslittlehelper.data.DbHelper;


public class MainActivity extends ActionBarActivity implements NewClassFragment.OnAddNewClassListener, ClassFragment.OnDeleteListener {

    MainFragment mainFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment=new MainFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainFragment, mainFragment)
                    .commit();
        }
        SQLiteDatabase myDataBase=new DbHelper(this).getReadableDatabase();

      /*  Cursor cursor=myDataBase.query(DbContract.ClassEntry.TABLE_NAME,new String[]{DbContract.ClassEntry.COLUMN_TITLE,DbContract.ClassEntry.COLUMN_LOCATION},null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Log.v("MYCURSOR",cursor.getString(0)+" location: "+cursor.getString(1));
            cursor.moveToNext();
        }*/
        String q="Select student.studentName,class.title from student inner join class on student.classID=class._id;";
        Cursor cursor=myDataBase.rawQuery(q,null);
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Log.v("MYCURSOR", cursor.getString(0) + " location: " + cursor.getString(1));
                cursor.moveToNext();
            }
        }

        myDataBase.close();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddNewClass(String day,String msg) {
        //Toast.makeText(this,day+" "+msg,Toast.LENGTH_LONG).show();
        mainFragment.addNewClassToAdapter(day,msg);
    }

    @Override
    public void onDelete(String day, int position) {
        mainFragment.deleteClassfromAdapter(day,position);
    }
}
