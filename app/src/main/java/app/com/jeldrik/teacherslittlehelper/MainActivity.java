package app.com.jeldrik.teacherslittlehelper;

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


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class ViewHolder {
        LinearLayout[] weekdays;
        TwoWayView[] listView;
        Button[] addClassBtn;

        public ViewHolder(){
            weekdays=new LinearLayout[7];
            listView=new TwoWayView[7];
            addClassBtn=new Button[7];
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        ViewHolder mViewHolder;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);


            if(mViewHolder==null) {
                mViewHolder = new ViewHolder();
                for (int i = 0; i < 7; i++) {

                    if (i == 0) {
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.monday);
                        mViewHolder.listView[i]= (TwoWayView)rootView.findViewById(R.id.mondayListView);
                        mViewHolder.addClassBtn[i]=(Button)rootView.findViewById(R.id.addMondayClass);
                    }
                    else if (i == 1) {
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.tuesday);
                        mViewHolder.listView[i] = (TwoWayView) rootView.findViewById(R.id.tuesdayListView);
                        mViewHolder.addClassBtn[i]=(Button)rootView.findViewById(R.id.addTuesdayClass);
                    }
                    else if (i == 2) {
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.wednesday);
                        mViewHolder.listView[i] = (TwoWayView) rootView.findViewById(R.id.wednesdayListView);
                        mViewHolder.addClassBtn[i]=(Button)rootView.findViewById(R.id.addWednesdayClass);
                    }
                    else if (i == 3) {
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.thursday);
                        mViewHolder.listView[i] = (TwoWayView) rootView.findViewById(R.id.thursdayListView);
                        mViewHolder.addClassBtn[i]=(Button)rootView.findViewById(R.id.addThursdayClass);
                    }
                    else if (i == 4){
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.friday);
                        mViewHolder.listView[i] = (TwoWayView) rootView.findViewById(R.id.fridayListView);
                        mViewHolder.addClassBtn[i]=(Button)rootView.findViewById(R.id.addFridayClass);
                    }
                    else if (i == 5){
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.saturday);
                        mViewHolder.listView[i] = (TwoWayView) rootView.findViewById(R.id.saturdayListView);
                        mViewHolder.addClassBtn[i]=(Button)rootView.findViewById(R.id.addSaturdayClass);
                    }
                    else if (i == 6){
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.sunday);
                        mViewHolder.listView[i] = (TwoWayView) rootView.findViewById(R.id.sundayListView);
                        mViewHolder.addClassBtn[i]=(Button)rootView.findViewById(R.id.addSundayClass);
                    }

                    ArrayList <String> list=null;
                    if (savedInstanceState != null) {
                        list = savedInstanceState.getStringArrayList("Day" + i);
                        if (list == null) {
                            String[] testData = {"Class 1", "Class 2", "Class 3", "Class 1", "Class 2", "Class 3"};
                            list = new ArrayList<String>();
                            for (int u = 0; u < testData.length; ++u) {
                                list.add(testData[u]);
                            }
                        }
                    }
                    else{
                        String[] testData = {"Class 1", "Class 2", "Class 3", "Class 1", "Class 2", "Class 3"};
                        list = new ArrayList<String>();
                        for (int u = 0; u < testData.length; ++u) {
                            list.add(testData[u]);
                        }
                    }
                    //ArrayAdapter adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,list);
                    MyAdapter adapter=new MyAdapter(getActivity(),list);


                    mViewHolder.listView[i].setOnTouchListener(new View.OnTouchListener() {
                        // Setting on Touch Listener for handling the touch inside ScrollView
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // Disallow the touch request for parent scroll on touch of child view
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });

                    mViewHolder.listView[i].setAdapter(adapter);


                    mViewHolder.addClassBtn[i].setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getActivity(), v.getResources().getResourceName(v.getId()), Toast.LENGTH_LONG).show();
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
                                //Log.v("MainActivity", "Count: " + Integer.toString(adapter.getCount()));
                            }
                        }
                    });

                    //clicking on a particular class
                    mViewHolder.listView[i].setOnItemClickListener(new TwoWayView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //TODO: implementing classFragment
                            Toast.makeText(getActivity(), Integer.toString(position) + " " + parent.getResources().getResourceName(parent.getId()), Toast.LENGTH_LONG).show();

                        }
                    });

                    //ClickListener for weekdays
                    mViewHolder.weekdays[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), v.getResources().getResourceName(v.getId()), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                container.setTag(mViewHolder);
            }
            else {
                mViewHolder=(ViewHolder)container.getTag();
                Toast.makeText(getActivity(),"gettag called",Toast.LENGTH_LONG).show();
            }

            return rootView;
        }

        public void onSaveInstanceState(Bundle savedState){
            super.onSaveInstanceState(savedState);
            if(mViewHolder!=null) {
                for (int i = 0; i < 7; i++) {
                    MyAdapter adapter=(MyAdapter)mViewHolder.listView[i].getAdapter();
                    ArrayList<String> values=new ArrayList<String>();
                    for (int u = 0; u < adapter.getCount(); u++)
                        values.add((String) adapter.getItem(u));

                    savedState.putStringArrayList("Day"+i,values);
                }

            }
        }

    }

}
