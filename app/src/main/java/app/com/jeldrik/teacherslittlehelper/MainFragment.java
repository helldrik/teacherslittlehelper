package app.com.jeldrik.teacherslittlehelper;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import app.com.jeldrik.teacherslittlehelper.data.ClassContentProvider;
import app.com.jeldrik.teacherslittlehelper.data.DbContract;

/**
 * Created by jeldrik on 03/02/15.
 */
public class MainFragment extends Fragment {

    private final String TAG="MainFragment";
    View mRootView;
    MyAdapter[] mAdapter;
    LinearLayout[] mWeekdays;
    TwoWayView[] mListView;

    private VelocityTracker mVelocityTracker = null;

    //----------------------------------------------------------------------------------------------
    public MainFragment() {
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment,menu);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.addClass_setting){
            Fragment newClassFragment=NewClassFragment.newInstance("day");
            FragmentTransaction transaction=getFragmentManager().beginTransaction();
            transaction.replace(R.id.FragmentContainer,newClassFragment,NewClassFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        this.createAdapters(savedInstanceState);
        mWeekdays=new LinearLayout[7];
        mListView=new TwoWayView[7];

    }
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        for (int i = 0; i < 7; i++) {

            if (i == 0) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.monday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.mondayListView);
            } else if (i == 1) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.tuesday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.tuesdayListView);
            } else if (i == 2) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.wednesday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.wednesdayListView);
            } else if (i == 3) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.thursday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.thursdayListView);
            } else if (i == 4) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.friday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.fridayListView);
            } else if (i == 5) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.saturday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.saturdayListView);
            } else if (i == 6) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.sunday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.sundayListView);
            }
           
        }
        this.setListeners();
        return mRootView;
    }
    //----------------------------------------------------------------------------------------------
    public void onSaveInstanceState(Bundle savedState){
        super.onSaveInstanceState(savedState);
        //TODO:fixing bug that leads to NullPointer Exception
        //http://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack

        for (int i = 0; i < 7; i++) {
            try {
                ArrayList<MyAdapter.ClassAdapterValues> values=new ArrayList<MyAdapter.ClassAdapterValues>();
                for (int u = 0; u < mAdapter[i].getCount(); u++)
                    values.add((MyAdapter.ClassAdapterValues) mAdapter[i].getItem(u));
                savedState.putParcelableArrayList("Day"+i,values);
            }catch(Exception e){Log.e("MainFragment","can not initialize adapter in on SaveInstanceState "+e);}
        }
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void createAdapters(Bundle savedInstanceState){
        if(mAdapter==null) {
            ArrayList<MyAdapter.ClassAdapterValues> list = new ArrayList<MyAdapter.ClassAdapterValues>();
            mAdapter = new MyAdapter[7];
            if (savedInstanceState != null) {
                for (int i = 0; i < 7; i++) {
                    list = savedInstanceState.getParcelableArrayList("Day" + i);
                    mAdapter[i] = new MyAdapter(getActivity(), list);
                }
            }else {
                ContentResolver resolver = getActivity().getContentResolver();
                Cursor cursor= resolver.query(DbContract.CLASS_DAY_TITLE_HOUR_ID, new String[]{DbContract.ClassEntry._ID, DbContract.ClassEntry.COLUMN_TITLE, DbContract.ClassEntry.COLUMN_TIME, DbContract.ClassEntry.COLUMN_DATE}, null, null, null);
                cursor.moveToFirst();
                //the arraylists of every day a saved in a list
                ArrayList<List<MyAdapter.ClassAdapterValues>> listGroup= new ArrayList<List<MyAdapter.ClassAdapterValues>>(7);
                for(int i=0;i<7;i++) {
                    listGroup.add(new ArrayList<MyAdapter.ClassAdapterValues>());
                }

                while(!cursor.isAfterLast()){
                    int id=cursor.getInt(0);
                    String title=cursor.getString(1);
                    String time=cursor.getString(2);
                    try {
                        JSONObject json = new JSONObject(cursor.getString(3));
                        JSONArray jarr=json.optJSONArray("selectedDays");
                        if (jarr != null) {
                            for (int i=0;i<jarr.length();i++){
                                MyAdapter.ClassAdapterValues obj = new MyAdapter.ClassAdapterValues(title,time,id);
                                Log.v("MYCURSOR",id+" "+title+" "+time+" "+jarr.get(i));
                                ArrayList<MyAdapter.ClassAdapterValues> tempList=(ArrayList)listGroup.get(jarr.getInt(i));
                                tempList.add(obj);
                                listGroup.set(jarr.getInt(i),tempList);
                            }
                        }
                    }catch (JSONException e){Log.e("MainFragment","No valid JsonObject or wrong type in createAdapters() "+e);}

                    cursor.moveToNext();
                }
                for (int i=0;i<7;i++) {
                    mAdapter[i] = new MyAdapter(getActivity(), (ArrayList)listGroup.get(i));
                }
            }
        }
        else{
            Log.v(TAG,"mAdapter  already exists");
        }
    }
    //----------------------------------------------------------------------------------------------
    private void setListeners(){
        for (int i = 0; i < 7; i++) {
            //TODO: fix bug that leads to nullpointer exception
            mListView[i].setAdapter(mAdapter[i]);

            //clicking on a particular class
            mListView[i].setOnItemClickListener(new TwoWayView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    MyAdapter.ClassAdapterValues vals=(MyAdapter.ClassAdapterValues)parent.getAdapter().getItem(position);

                    Fragment classFragment=ClassFragment.newInstance(position,vals._id);
                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.FragmentContainer,classFragment,ClassFragment.TAG);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            //ClickListener for mWeekdays
            mWeekdays[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), v.getResources().getResourceName(v.getId()), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    //----------------------------------------------------------------------------------------------
    public void addNewClassToAdapter(ArrayList<Integer>days,String title,String time, int id){
        for(int i=0;i<days.size();i++){
            try {
                MyAdapter adapter = (MyAdapter)mListView[days.get(i)].getAdapter();
                if (adapter != null) {
                    MyAdapter.ClassAdapterValues obj=new MyAdapter.ClassAdapterValues(title,time,id);
                    adapter.add(obj);
                    adapter.notifyDataSetChanged();
                }
            }catch(NullPointerException e){
                Log.e("MainFragment","ListView is null "+e);
            }
        }

    }
    //----------------------------------------------------------------------------------------------
    public void updateClassinAdapter(String days,String title,String time, int id){

        String[]weekDays=getActivity().getResources().getStringArray(R.array.weekDays);

        for (int i = 0; i < weekDays.length; i++) {
            MyAdapter adapter = (MyAdapter) mListView[i].getAdapter();
            if (adapter != null) {
                //found a day where the class should be
                if (days.contains(weekDays[i])) {
                    boolean isOnNewDay = true;
                    for (int u = 0; u < adapter.getCount(); u++) {
                        MyAdapter.ClassAdapterValues obj = (MyAdapter.ClassAdapterValues) adapter.getItem(u);
                        //found the class and update it
                        if (obj._id == id) {
                            obj.title = title;
                            obj.time = time;
                            adapter.remove(u);
                            adapter.add(obj);
                            isOnNewDay = false;
                        }
                    }
                    //class was not found on this day -> we add it
                    if (isOnNewDay) {
                        MyAdapter.ClassAdapterValues obj = new MyAdapter.ClassAdapterValues(title, time, id);
                        adapter.add(obj);
                    }
                    adapter.notifyDataSetChanged();
                }
                //class should not be on this day -> we check if its there and delete it if so
                else{
                    for (int u = 0; u < adapter.getCount(); u++) {
                        MyAdapter.ClassAdapterValues obj = (MyAdapter.ClassAdapterValues) adapter.getItem(u);
                        if (obj._id == id) {
                            adapter.remove(u);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
    //----------------------------------------------------------------------------------------------
    public void deleteClassfromAdapter(int id){
        //Deleting from database
        ContentResolver resolver=getActivity().getContentResolver();
        Uri uri=DbContract.ClassEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        resolver.delete(uri,null,null);
        //deleting from listview
        for (int i=0;i<7;i++) {
            MyAdapter adapter = (MyAdapter) mListView[i].getAdapter();
            for(int u=0;u<adapter.getCount();u++){
                MyAdapter.ClassAdapterValues val = (MyAdapter.ClassAdapterValues) adapter.getItem(u);
                if (val._id == id) {
                    adapter.remove(u);
                }
            }
        }

    }
}

