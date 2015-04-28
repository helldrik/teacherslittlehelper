package app.com.jeldrik.teacherslittlehelper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
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
    public long timestamp;
    public MyContentObserver myContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null) {
            mainFragment = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mainFragment");
            timestamp=savedInstanceState.getLong("timestamp");
            userEmail=savedInstanceState.getString("userEmail");
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        getContentResolver().unregisterContentObserver(myContentObserver);
        Log.v("MainActivity", "ttt MyContentObserver unregistered");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myContentObserver=new MyContentObserver(null);
        getContentResolver().registerContentObserver(DbContract.BASE_CONTENT_URI, true,myContentObserver);
        sync();
        //SetUserData();
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
        File file = new File((getFileStreamPath("UserData.txt").getPath()));
       // File file= new File(getExternalFilesDir(null),"UserData.txt");
        if(file.exists()) {
            try {
                FileInputStream fIn = new FileInputStream(file);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                String aDataRow = "";
                String aBuffer = "";
                while ((aDataRow = myReader.readLine()) != null) {
                    aBuffer += aDataRow + "\n";
                }
                myReader.close();
                JSONArray jarr=new JSONArray(aBuffer);
                JSONObject json=jarr.getJSONObject(1);
                userEmail=json.getString("email");
                json=jarr.getJSONObject(0);
                timestamp=json.getLong("timestamp");
                sync();
                Toast.makeText(getBaseContext(),"Done reading file "+userEmail+" "+timestamp,Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "TTT: "+ e.getMessage());
            }
        }
        else {
            SetUserData();
        }
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
                //File file= new File(getExternalFilesDir(null),"UserData.txt");
                FileOutputStream fOut=null;
                try {
                    fOut = openFileOutput("UserData.txt", MODE_PRIVATE);
                }catch(FileNotFoundException e){Log.e("MAinActivity", "Could not create new File: "+e);}

                try {
                    Date date= new Date();
                    //getTime() returns current time in milliseconds
                    //timestamp = date.getTime();
                    timestamp=0;

                    String identifyer = "[{\"timestamp\":\""+timestamp+"\"},{\"email\":\""+userEmail+"\"}]";

                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(identifyer);
                    myOutWriter.close();
                    fOut.close();
                    //Log.v("MainFragment",identifyer);
                    sync();

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("MainActivity", "Error in MAinActivity of Teacherslittlehelper: "+ e.getMessage());
                }
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
        mAccount = CreateSyncAccount(this);
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
        settingsBundle.putString("useremail", userEmail);
        settingsBundle.putLong("timestamp", timestamp);

        ContentResolver.requestSync(mAccount, DbContract.AUTHORITY, settingsBundle);
        Toast.makeText(this, "Syncing", Toast.LENGTH_LONG).show();
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

            //File file= new File(getExternalFilesDir(null),"UserData.txt");
            File file = new File((getFileStreamPath("UserData.txt").getPath()));
            if(file.exists()) {
                try {
                    Date date= new Date();
                    timestamp = date.getTime();
                    String identifyer = "[{\"timestamp\":\"" + timestamp + "\"},{\"email\":\"" + userEmail + "\"}]";

                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(identifyer);
                    myOutWriter.close();
                    fOut.close();
                    Log.v("MainFragment","ttt Timestamp updated "+identifyer);
                    //sync();

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            else
                Log.e("MainActivity", "ttt File  "+getFileStreamPath("UserData.txt").getPath()+" does not exist");

        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // depending on the handler you might be on the UI
            // thread, so be cautious!
            Log.v("MainActivity", "ttt Changing data in ContentProvider");
            //File file= new File(getExternalFilesDir(null),"UserData.txt");
            File file = new File((getFileStreamPath("UserData.txt").getPath()));
            if(file.exists()) {
                try {
                    Date date= new Date();
                    timestamp = date.getTime();
                    String identifyer = "[{\"timestamp\":\"" + timestamp + "\"},{\"email\":\"" + userEmail + "\"}]";

                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(identifyer);
                    myOutWriter.close();
                    fOut.close();
                    Log.v("MainFragment","ttt Timestamp updated "+identifyer);
                    //sync();

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            else
                Log.e("MainActivity", "ttt File  "+getFileStreamPath("UserData.txt").getPath()+" does not exist");
        }
    }
}
