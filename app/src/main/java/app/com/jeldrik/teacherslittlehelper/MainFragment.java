package app.com.jeldrik.teacherslittlehelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

/**
 * Created by jeldrik on 03/02/15.
 */
public class MainFragment extends Fragment {

    private final String TAG="MainFragment";
    View mRootView;
    MyAdapter[] mAdapter;
    LinearLayout[] mWeekdays;
    TwoWayView[] mListView;
    Button[] mAddClassBtn;


    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.createAdapters(savedInstanceState);
        mWeekdays=new LinearLayout[7];
        mListView=new TwoWayView[7];
        mAddClassBtn=new Button[7];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        for (int i = 0; i < 7; i++) {

            if (i == 0) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.monday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.mondayListView);
                mAddClassBtn[i] = (Button) mRootView.findViewById(R.id.addMondayClass);
            } else if (i == 1) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.tuesday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.tuesdayListView);
                mAddClassBtn[i] = (Button) mRootView.findViewById(R.id.addTuesdayClass);
            } else if (i == 2) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.wednesday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.wednesdayListView);
                mAddClassBtn[i] = (Button) mRootView.findViewById(R.id.addWednesdayClass);
            } else if (i == 3) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.thursday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.thursdayListView);
                mAddClassBtn[i] = (Button) mRootView.findViewById(R.id.addThursdayClass);
            } else if (i == 4) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.friday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.fridayListView);
                mAddClassBtn[i] = (Button) mRootView.findViewById(R.id.addFridayClass);
            } else if (i == 5) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.saturday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.saturdayListView);
                mAddClassBtn[i] = (Button) mRootView.findViewById(R.id.addSaturdayClass);
            } else if (i == 6) {
                mWeekdays[i] = (LinearLayout) mRootView.findViewById(R.id.sunday);
                mListView[i] = (TwoWayView) mRootView.findViewById(R.id.sundayListView);
                mAddClassBtn[i] = (Button) mRootView.findViewById(R.id.addSundayClass);
            }
           
        }
        this.setListeners();
        return mRootView;
    }

    public void onSaveInstanceState(Bundle savedState){
        super.onSaveInstanceState(savedState);
        
        for (int i = 0; i < 7; i++) {
            MyAdapter adapter=(MyAdapter)mListView[i].getAdapter();
            ArrayList<String> values=new ArrayList<String>();
            for (int u = 0; u < adapter.getCount(); u++)
                values.add((String) adapter.getItem(u));
            savedState.putStringArrayList("Day"+i,values);
        }
    }
    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void createAdapters(Bundle savedInstanceState){
        if(mAdapter==null) {
            Log.v(TAG,"mAdapter  created");
            mAdapter = new MyAdapter[7];
            for (int i = 0; i < 7; i++) {
                ArrayList<String> list = null;
                if (savedInstanceState != null) {
                    list = savedInstanceState.getStringArrayList("Day" + i);
                    if (list == null) {
                        String[] testData = {"Class 1", "Class 2", "Class 3", "Class 1", "Class 2", "Class 3"};
                        list = new ArrayList<String>();
                        for (int u = 0; u < testData.length; ++u) {
                            list.add(testData[u]);
                        }
                    }
                } else {
                    String[] testData = {"Class 1", "Class 2", "Class 3", "Class 1", "Class 2", "Class 3"};
                    list = new ArrayList<String>();
                    for (int u = 0; u < testData.length; ++u) {
                        list.add(testData[u]);
                    }
                }
                //ArrayAdapter adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,list);
                mAdapter[i] = new MyAdapter(getActivity(), list);
            }
        }
        else{
            Log.v(TAG,"mAdapter  already exists");
        }
    }
    private void setListeners(){
        for (int i = 0; i < 7; i++) {

            mListView[i].setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            mListView[i].setAdapter(mAdapter[i]);


            mAddClassBtn[i].setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), v.getResources().getResourceName(v.getId()), Toast.LENGTH_LONG).show();
                    TwoWayView listView=null;
                    String viewName=v.getResources().getResourceName(v.getId());
                    if(viewName.contains("Monday")){
                        listView = (TwoWayView) getActivity().findViewById(R.id.mondayListView);
                    }
                    else if(viewName.contains("Tuesday")){
                        listView = (TwoWayView) getActivity().findViewById(R.id.tuesdayListView);
                    }
                    else if(viewName.contains("Wednesday")){
                        listView = (TwoWayView) getActivity().findViewById(R.id.wednesdayListView);
                    }
                    else if(viewName.contains("Thursday")){
                        listView = (TwoWayView) getActivity().findViewById(R.id.thursdayListView);
                    }
                    else if(viewName.contains("Friday")){
                        listView = (TwoWayView) getActivity().findViewById(R.id.fridayListView);
                    }
                    else if(viewName.contains("Saturday")){
                        listView = (TwoWayView) getActivity().findViewById(R.id.saturdayListView);
                    }
                    else if(viewName.contains("Sunday")){
                        listView = (TwoWayView) getActivity().findViewById(R.id.sundayListView);
                    }
                    MyAdapter adapter = (MyAdapter) listView.getAdapter();
                    if (adapter != null) {
                        adapter.add("New Class");
                        //TODO: implementing newClassFragment
                        Fragment newClassFragment= new NewClassFragment();
                        FragmentTransaction transaction=getFragmentManager().beginTransaction();
                        transaction.replace(R.id.mainFragment,newClassFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        //Log.v("MainActivity", "Count: " + Integer.toString(adapter.getCount()));
                    }
                }
            });

            //clicking on a particular class
            mListView[i].setOnItemClickListener(new TwoWayView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO: implementing classFragment
                    Toast.makeText(getActivity(), Integer.toString(position) + " " + parent.getResources().getResourceName(parent.getId()), Toast.LENGTH_LONG).show();

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
}

