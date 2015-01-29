package app.com.jeldrik.teacherslittlehelper;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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

        public ViewHolder(){
            weekdays=new LinearLayout[7];
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

                    ListView listView=null;

                    if (i == 0) {
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.monday);
                        listView= (ListView)rootView.findViewById(R.id.mondayListView);
                    }
                    else if (i == 1) {
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.tuesday);
                        listView = (ListView) rootView.findViewById(R.id.tuesdayListView);
                    }
                    else if (i == 2) {
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.wednesday);
                        listView = (ListView) rootView.findViewById(R.id.wednesdayListView);
                    }
                    else if (i == 3) {
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.thursday);
                        listView = (ListView) rootView.findViewById(R.id.thursdayListView);
                    }
                    else if (i == 4){
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.friday);
                        listView = (ListView) rootView.findViewById(R.id.fridayListView);
                    }
                    else if (i == 5){
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.saturday);
                        listView = (ListView) rootView.findViewById(R.id.saturdayListView);
                    }
                    else if (i == 6){
                        mViewHolder.weekdays[i] = (LinearLayout) rootView.findViewById(R.id.sunday);
                        listView = (ListView) rootView.findViewById(R.id.sundayListView);
                    }

                    mViewHolder.weekdays[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), v.getResources().getResourceName(v.getId()), Toast.LENGTH_LONG).show();
                        }
                    });

                    String[] testData={"Class 1","Class 2", "Class 3"};
                    ArrayList <String> list = new ArrayList<String>();
                    for (int u = 0; u < testData.length; ++u) {
                        list.add(testData[u]);
                    }


                    ArrayAdapter adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,list);

                    listView.setOnTouchListener(new View.OnTouchListener() {
                        // Setting on Touch Listener for handling the touch inside ScrollView
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // Disallow the touch request for parent scroll on touch of child view
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                    listView.setAdapter(adapter);
                }
                rootView.setTag(mViewHolder);
            }
            else {
                mViewHolder=(ViewHolder)rootView.getTag();
                Toast.makeText(getActivity(),"gettag called",Toast.LENGTH_LONG).show();
            }

            return rootView;
        }

    }
}
