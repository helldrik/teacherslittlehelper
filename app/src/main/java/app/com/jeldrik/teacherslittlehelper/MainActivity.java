package app.com.jeldrik.teacherslittlehelper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;
import app.com.jeldrik.teacherslittlehelper.data.DbHelper;


public class MainActivity extends ActionBarActivity implements NewClassFragment.OnAddNewClassListener, ClassFragment.OnDeleteListener,
        UpdateStudentFragment.OnStudentUpdatedListener,NewClassContentFragment.OnNewClassContentListener,
        NewStudentFragment.OnStudentAddedListener,UpdateClassFragment.OnClassUpdatedListener,
        UpdateClassContentFragment.OnUpdateClassContentListener {

    MainFragment mainFragment;
    public String userEmail;
    public boolean startSyncing;
    public long timestamp;
    public MyContentObserver myContentObserver;
    public SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null) {
            mainFragment = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mainFragment");
            timestamp=savedInstanceState.getLong("timestamp");
            userEmail=savedInstanceState.getString("userEmail");
            startSyncing=savedInstanceState.getBoolean("syncing");
        }
        else {
            mainFragment=new MainFragment();
            getUserData();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FragmentContainer, mainFragment)
                    .commit();
        }

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
        if(id==R.id.sync){
            SetUserData();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState,"mainFragment",mainFragment);
        outState.putLong("timestamp",timestamp);
        outState.putString("userEmail",userEmail);
        outState.putBoolean("syncing",startSyncing);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(myContentObserver);
        Log.v("MainActivity", "ttt MyContentObserver unregistered");

        mSettings=getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor=mSettings.edit();

        editor.putBoolean("syncing",startSyncing);
        editor.commit();

        //TODO: delete this
        sync();

    }

    @Override
    protected void onResume() {
        super.onResume();
        myContentObserver=new MyContentObserver(new Handler(Looper.getMainLooper()));
        getContentResolver().registerContentObserver(DbContract.BASE_CONTENT_URI, true,myContentObserver);
    }

    private void updateUserData(){
        ContentValues vals = new ContentValues();
        vals.put(DbContract.UserEntry.COLUMN_USER_EMAIL,userEmail);
        vals.put(DbContract.UserEntry.COLUMN_TIMESTAMP,timestamp);
        getContentResolver().update(DbContract.UserEntry.CONTENT_URI,vals,null,null);
        Log.v("MainActivity","timestamp in updateUserData: "+timestamp);
    }

    //---------------------------------------------------------------------------------------------
    //LISTENER METHODS
    //---------------------------------------------------------------------------------------------
    @Override
    public void onAddNewClass(ArrayList days,String title, String startTime,String endTime, int id) {
        //Toast.makeText(this,day+" "+msg,Toast.LENGTH_LONG).show();
        mainFragment.addNewClassToAdapter(days,title,startTime,endTime,id);
        Date date= new Date();
        timestamp = date.getTime();
        startSyncing=true;
        updateUserData();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    //TODO: delete students and content related to this class
    public void onDelete(int id) {
        try {
            mainFragment.deleteClassfromAdapter(id);
            Date date= new Date();
            timestamp = date.getTime();
            startSyncing=true;
            updateUserData();
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
        Date date= new Date();
        timestamp = date.getTime();
        startSyncing=true;
        updateUserData();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void OnNewClassContent(ClassContentAdapter.ClassContentAdapterValues values) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.newClassContent(values);
        Date date= new Date();
        timestamp = date.getTime();
        startSyncing=true;
        updateUserData();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void OnUpdateClassContent(ClassContentAdapter.ClassContentAdapterValues values) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.upDateClassContent(values);
        Date date= new Date();
        timestamp = date.getTime();
        startSyncing=true;
        updateUserData();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onDeleteClassContent(ClassContentAdapter.ClassContentAdapterValues deletedObj) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.deleteClassContent(deletedObj);
        Date date= new Date();
        timestamp = date.getTime();
        startSyncing=true;
        updateUserData();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStudentAdded(StudentAdapter.StudentAdapterValues newStudent) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.updateStudents(newStudent,-1);
        Date date= new Date();
        timestamp = date.getTime();
        startSyncing=true;
        updateUserData();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void OnClassUpdated(int id, String title, String days, String location, String startTime, String endTime, String level, String info) {
        ClassFragment frag=(ClassFragment)getSupportFragmentManager().findFragmentByTag(ClassFragment.TAG);
        frag.updateMemberVars(title,days,location,startTime,endTime,level,info);
        mainFragment.updateClassinAdapter(days,title,startTime,endTime,id);
        Date date= new Date();
        timestamp = date.getTime();
        startSyncing=true;
        updateUserData();
    }

    //---------------------------------------------------------------------------------------------
    //Authenticator for using Syncadapter
    public static final String ACCOUNT_TYPE = "jeldrik.com";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;
    public static Account CreateSyncAccount(Context context){
        Account newAccount = new Account(ACCOUNT,ACCOUNT_TYPE);
        AccountManager accountManager=(AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            //Toast.makeText(context,"New Account",Toast.LENGTH_LONG).show();
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }
    //---------------------------------------------------------------------------------------------
    //Creating persistant storage for saving unique user id and timestamp of when Content was last updated
    public void getUserData(){
        mSettings=getPreferences(MODE_PRIVATE);
        startSyncing=mSettings.getBoolean("syncing",true);
        userEmail="";
        Cursor cursor= getContentResolver().query(DbContract.UserEntry.CONTENT_URI,null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            userEmail=cursor.getString(1);
            timestamp=cursor.getLong(2);
            cursor.moveToNext();
        }
        if(userEmail.equals("")){
            SetUserData();
        }
        else
            sync();
    }
    //---------------------------------------------------------------------------------------------
    //show alert window to input email for syncing
    public void SetUserData(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Login");
        alert.setMessage("Enter valid email address for syncing :");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userEmail=input.getText().toString();
                timestamp=0;
                getContentResolver().delete(DbContract.UserEntry.CONTENT_URI,null,null);
                ContentValues vals = new ContentValues();
                vals.put(DbContract.UserEntry.COLUMN_USER_EMAIL,userEmail);
                vals.put(DbContract.UserEntry.COLUMN_TIMESTAMP,timestamp);
                Uri returnUri=getContentResolver().insert(DbContract.UserEntry.CONTENT_URI,vals);

                sync();
                return;
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                return;
            }
        });
        alert.show();
    }
    public void sync(){
        // Create the dummy account
        if(mAccount==null)
            mAccount = CreateSyncAccount(this);
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, false);

        ContentResolver.requestSync(mAccount, DbContract.AUTHORITY, settingsBundle);

        startSyncing=false;


        Toast.makeText(this,"Syncing",Toast.LENGTH_LONG).show();
    }

    private class MyContentObserver extends ContentObserver{


        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
            Log.v("MainActivity", "ttt MyContentObserver");
        }
        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
            Log.v("MainActivity", "ttt Changing data in ContentProvider ee");
            Cursor cursor= getContentResolver().query(DbContract.UserEntry.CONTENT_URI,null, null, null, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                userEmail=cursor.getString(1);
                timestamp=cursor.getLong(2);
                cursor.moveToNext();
            }
            if(startSyncing)
                sync();

        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // depending on the handler you might be on the UI
            // thread, so be cautious!
            Log.v("MainActivity", "ttt Changing data in ContentProvider "+timestamp+" "+startSyncing);
            Cursor cursor= getContentResolver().query(DbContract.UserEntry.CONTENT_URI,null, null, null, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                userEmail=cursor.getString(1);
                timestamp=cursor.getLong(2);
                cursor.moveToNext();
            }
            if(startSyncing)
                sync();


        }
    }
}
