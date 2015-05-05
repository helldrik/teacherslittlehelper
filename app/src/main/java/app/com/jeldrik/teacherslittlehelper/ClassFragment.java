package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.Locale;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;


public class ClassFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 ="id";


    public static final String TAG="ClassFragment";
    private int position;
    private int mID;
    private long mTimestamp;

    private View rootView;

    private String mTitle;
    private String mDays;
    private String mLocation;
    private String mHour;
    private String mEndTime;
    private String mLevel;
    private String mInfo;

    private ArrayList<ClassContentAdapter.ClassContentAdapterValues> mClassContentList;
    private ArrayList<StudentAdapter.StudentAdapterValues> mStudentList;

    private StudentAdapter mStudentAdapter;
    private ClassContentAdapter mclassContentAdapter;

    private OnDeleteListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassFragment newInstance(int param2, int param3) {
        ClassFragment fragment = new ClassFragment();
        Bundle args = new Bundle();
        
        args.putInt(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3,param3);
        fragment.setArguments(args);
        return fragment;
    }
//--------------------------------------------------------------------------------------------------
    public ClassFragment() {
        // Required empty public constructor
    }
    //--------------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            mTimestamp=savedInstanceState.getLong("timestamp");
            position=savedInstanceState.getInt("position");
            mID=savedInstanceState.getInt("id");
            mClassContentList = savedInstanceState.getParcelableArrayList("ClassContentList");
            mStudentList = savedInstanceState.getParcelableArrayList("StudentList");
            mclassContentAdapter=new ClassContentAdapter(getActivity(),mClassContentList);
            mStudentAdapter=new StudentAdapter(getActivity(),mStudentList);

        }
        else if (getArguments() != null) {
            
            position = getArguments().getInt(ARG_PARAM2);
            mID=getArguments().getInt(ARG_PARAM3);
        }
        setHasOptionsMenu(true);
        getData(savedInstanceState);
    }
    //--------------------------------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class,menu);
    }
    //--------------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.UpdateClass:
                Fragment updateFrag=UpdateClassFragment.newInstance(mID,mTitle,mDays,mLocation,mHour,mEndTime,mLevel,mInfo);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.FragmentContainer, updateFrag, UpdateClassFragment.TAG);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.DeleteClass:
                showAlertDialog();
                break;
            case R.id.AddContent:
                Fragment newClassContentFragment = NewClassContentFragment.newInstance(mTimestamp, mStudentList);
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.FragmentContainer, newClassContentFragment, NewClassContentFragment.TAG);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.AddStudent:
                Fragment newStudentFragment=NewStudentFragment.newInstance(mTimestamp);
                transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.FragmentContainer,newStudentFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.NotifyAllStudents:
                String emails="";
                for(int i=0;i<mStudentList.size();i++)
                    emails+=mStudentList.get(i).email+";";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",emails,null));
                startActivity(emailIntent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }
    //--------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =inflater.inflate(R.layout.fragment_class, container, false);

        addStudents();
        addClassContent();
        TextView title=(TextView)rootView.findViewById(R.id.classfragment_title);
        title.setText(mTitle);
        TextView days=(TextView)rootView.findViewById(R.id.classFragment_days);
        days.setText(mDays);
        TextView endTime=(TextView)rootView.findViewById(R.id.classFragment_duration);
        endTime.setText(mEndTime);
        TextView info=(TextView)rootView.findViewById(R.id.classFragment_info);
        info.setText(mInfo);
        TextView level=(TextView)rootView.findViewById(R.id.classFragment_level);
        level.setText(mLevel);
        TextView location=(TextView)rootView.findViewById(R.id.classFragment_location);
        location.setText(mLocation);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationEscaped=mLocation.replace(" ","+");
                Uri mapUri=Uri.parse("http://maps.google.com/maps?q="+locationEscaped);
                Intent intent= new Intent(Intent.ACTION_VIEW,mapUri);
                getActivity().startActivity(intent);
            }
        });

        TextView hour=(TextView)rootView.findViewById(R.id.classFragment_hour);
        hour.setText(mHour);

        return rootView;
    }
    //--------------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDeleteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDeleteListener");
        }
    }
    //--------------------------------------------------------------------------------------------------
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
//--------------------------------------------------------------------------------------------------
    public interface OnDeleteListener {
        public void onDelete(int id);
    }

public void updateMemberVars(String title,String days,String location,String hour,String endTime,String level,String info){
    mTitle=title;
    mDays=days;
    mLocation=location;
    mHour=hour;
    mEndTime=endTime;
    mLevel=level;
    mInfo=info;
}
    //--------------------------------------------------------------------------------------------------
    public void updateStudents(StudentAdapter.StudentAdapterValues vals, int position){
        if(mStudentAdapter!=null) {
            //if position= -1 means we want to add a new student
            if (position != -1)
                mStudentAdapter.remove(position);
            if (vals != null)
                mStudentAdapter.add(vals);
            mStudentAdapter.notifyDataSetChanged();
        }
    }
    //--------------------------------------------------------------------------------------------------
    private void addStudents(){

        TwoWayView list = (TwoWayView)rootView.findViewById(R.id.studentListView);
        mStudentAdapter =new StudentAdapter(getActivity(),mStudentList);
        list.setAdapter(mStudentAdapter);

        list.setOnItemClickListener(new TwoWayView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment updateStudentFragment=UpdateStudentFragment.newInstance(mStudentList.get(position),position);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.FragmentContainer, updateStudentFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        /*
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new TwoWayView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"Item long clicked",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        */
    }
    //--------------------------------------------------------------------------------------------------
    public void newClassContent(ClassContentAdapter.ClassContentAdapterValues vals){
        if(mclassContentAdapter!=null) {
            mclassContentAdapter.add(vals);
            mclassContentAdapter.notifyDataSetChanged();
        }
    }
    //--------------------------------------------------------------------------------------------------
    public void deleteClassContent(ClassContentAdapter.ClassContentAdapterValues deletedObj){
        if(mclassContentAdapter!=null) {
            for(int i =0;i<mclassContentAdapter.getCount();i++){
                ClassContentAdapter.ClassContentAdapterValues val=(ClassContentAdapter.ClassContentAdapterValues)mclassContentAdapter.getItem(i);
                if(val.id== deletedObj.id){
                    mclassContentAdapter.remove(i);
                    mclassContentAdapter.notifyDataSetChanged();
                }
            }
        }
    }
    //--------------------------------------------------------------------------------------------------
    public void upDateClassContent(ClassContentAdapter.ClassContentAdapterValues vals){
        if(mclassContentAdapter!=null) {
            for(int i =0;i<mclassContentAdapter.getCount();i++){
                ClassContentAdapter.ClassContentAdapterValues val=(ClassContentAdapter.ClassContentAdapterValues)mclassContentAdapter.getItem(i);
                if(val.id== vals.id){
                    mclassContentAdapter.remove(i);
                    mclassContentAdapter.insert(vals, i);
                }
            }
            mclassContentAdapter.notifyDataSetChanged();
        }
    }
    //--------------------------------------------------------------------------------------------------
    private void addClassContent(){

        TwoWayView list = (TwoWayView)rootView.findViewById(R.id.classContentListView);
        mclassContentAdapter = new ClassContentAdapter(getActivity(), mClassContentList);
        list.setAdapter(mclassContentAdapter);

        list.setOnItemClickListener(new TwoWayView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Fragment frag = UpdateClassContentFragment.newInstance(mClassContentList.get(position).date,
                        mClassContentList.get(position).book,mClassContentList.get(position).pages,mClassContentList.get(position).info,
                        mClassContentList.get(position).id,mClassContentList.get(position).timestamp);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.FragmentContainer, frag, UpdateClassContentFragment.TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        /*
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new TwoWayView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"Item long clicked",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        */
    }
    //--------------------------------------------------------------------------------------------------
    private void getData(Bundle savedInstanceState){
        if(savedInstanceState!=null) {
            mDays=savedInstanceState.getString("days");
            mEndTime=savedInstanceState.getString("endTime");
            mInfo=savedInstanceState.getString("info");
            mLevel=savedInstanceState.getString("level");
            mLocation=savedInstanceState.getString("location");
            mHour=savedInstanceState.getString("hour");
            mTitle=savedInstanceState.getString("title");
            mTimestamp=savedInstanceState.getLong("timestamp");
        }
        else {
            ContentResolver resolver = getActivity().getContentResolver();
            Uri uri = DbContract.ClassEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mID)).build();
            Log.v("ClassFragment", "Uri: " + uri.toString());
            Cursor cursor = resolver.query(uri, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mDays = toDays(cursor.getString(0));
                mEndTime = cursor.getString(1);
                mInfo = cursor.getString(2);
                mLevel = cursor.getString(3);
                mLocation = cursor.getString(4);
                mHour = cursor.getString(5);
                mTitle = cursor.getString(6);
                mTimestamp=cursor.getLong(7);
                cursor.moveToNext();
            }
            //getting classContent
            mClassContentList = new ArrayList<>();
            resolver = getActivity().getContentResolver();
            uri = DbContract.ClassContentEntry.CONTENT_URI_WITH_FOREIGNKEY.buildUpon().appendPath(Long.toString(mTimestamp)).build();
            cursor = resolver.query(uri, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mClassContentList.add(new ClassContentAdapter.ClassContentAdapterValues(cursor.getInt(5), cursor.getString(1),cursor.getLong(2), cursor.getString(0), cursor.getString(3), cursor.getString(4)));
                cursor.moveToNext();
            }
            //getting Students
            mStudentList = new ArrayList<>();
            resolver=getActivity().getContentResolver();
            uri= DbContract.StudentEntry.CONTENT_URI_WITH_FOREIGNKEY.buildUpon().appendPath(Long.toString(mTimestamp)).build();
            cursor=resolver.query(uri,null,null,null,null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                mStudentList.add(new StudentAdapter.StudentAdapterValues(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getLong(4)));
                cursor.moveToNext();
            }
        }
    }
    //--------------------------------------------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("ClassContentList", mClassContentList);
        outState.putParcelableArrayList("StudentList", mStudentList);
        outState.putInt("id", mID);
        outState.putInt("position",position);
        outState.putString("days",mDays);
        outState.putString("endTime",mEndTime);
        outState.putString("info",mInfo);
        outState.putString("level",mLevel);
        outState.putString("location",mLocation);
        outState.putString("hour",mHour);
        outState.putString("title",mTitle);

    }
    //--------------------------------------------------------------------------------------------------
    public String toDays(String jsonFormattedString) {
        String humanReadableString="";
        try {
            JSONObject json = new JSONObject(jsonFormattedString);
            JSONArray jarr=json.optJSONArray("selectedDays");
            if (jarr != null) {
                for (int i=0;i<jarr.length();i++){
                    switch(jarr.getInt(i)){
                        case 0:
                            humanReadableString+=getActivity().getResources().getString(R.string.monday);
                            break;
                        case 1:
                            humanReadableString+=getActivity().getResources().getString(R.string.tuesday);
                            break;
                        case 2:
                            humanReadableString+=getActivity().getResources().getString(R.string.wednesday);
                            break;
                        case 3:
                            humanReadableString+=getActivity().getResources().getString(R.string.thursday);
                            break;
                        case 4:
                            humanReadableString+=getActivity().getResources().getString(R.string.friday);
                            break;
                        case 5:
                            humanReadableString+=getActivity().getResources().getString(R.string.saturday);
                            break;
                        case 6:
                            humanReadableString+=getActivity().getResources().getString(R.string.sunday);
                            break;
                    }
                    if(i!=jarr.length()-1)
                        humanReadableString+= ", ";
                }
            }
        }catch (JSONException e){Log.e("MainFragment","No valid JsonObject or wrong type in createAdapters() "+e);}
        return  humanReadableString;
    }
 //--------------------------------------------------------------------------------------------------
    private void showAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

        // set title
        alertDialogBuilder.setTitle(this.getActivity().getResources().getString(R.string.delete_Class));

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(this.getActivity().getResources().getString(R.string.delete_Class),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if (mListener != null) {
                            mListener.onDelete(mID);
                        }
                        FragmentManager fm=getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                    }
                })
                .setNegativeButton(this.getActivity().getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}
