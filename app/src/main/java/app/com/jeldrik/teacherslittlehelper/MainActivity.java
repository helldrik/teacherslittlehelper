package app.com.jeldrik.teacherslittlehelper;

import android.content.ContentValues;
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
        SQLiteDatabase myDataBase=new DbHelper(this).getWritableDatabase();

        ContentValues vals=new ContentValues(7);

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
