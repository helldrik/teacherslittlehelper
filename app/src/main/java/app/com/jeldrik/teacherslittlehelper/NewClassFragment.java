package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;


//---------------------------------------------------------------------------------------------
public class NewClassFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Edit the layout to adjust hours picking (spinner?) and day selection (checkboxes?)
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    public static final String TAG="NEWCLASSFRAGMENT";
    private String mDay;
    //saving the selected weekdays that get chosen in DialogFragment
    private String mSelectedDays;
    private ArrayList<Integer> mSelectedDaysAsArray;
    private String mLevel;
    private String mTime, mEndTime;

    private OnAddNewClassListener mListener;
    private Button mBtn, mTimeBtn,mEndTimeBtn;
    View mRootView;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mLevel=(String)parent.getItemAtPosition(position);
        //Toast.makeText(getActivity(),mLevel,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mLevel="-";
    }

    //---------------------------------------------------------------------------------------------
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     */
    public interface OnAddNewClassListener {
        public void onAddNewClass(ArrayList days,String title,String time, int id);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment NewClassFragment.
     */
//---------------------------------------------------------------------------------------------
    public static NewClassFragment newInstance(String param1) {
        NewClassFragment fragment = new NewClassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    //---------------------------------------------------------------------------------------------
    public NewClassFragment() {
        // Required empty public constructor
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDay = getArguments().getString(ARG_PARAM1);
            mTime=mEndTime="00:00";
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView =inflater.inflate(R.layout.fragment_new_class, container, false);

        setSpinners();
        Button daysBtn=(Button)mRootView.findViewById(R.id.newClassDays);
        daysBtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                showDaysDialog(0);
            }
        });

        mTimeBtn= (Button)mRootView.findViewById(R.id.newClassStartTime);
        mTimeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimeDialogFragment();
                newFragment.show(getActivity().getFragmentManager(),"Time");
            }
        });
        mEndTimeBtn= (Button)mRootView.findViewById(R.id.newClassEndTime);
        mEndTimeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new EndTimeDialogFragment();
                newFragment.show(getActivity().getFragmentManager(),"EndTime");
            }
        });

        mBtn=(Button)mRootView.findViewById(R.id.createClassBtn);
        mBtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                EditText title=(EditText)mRootView.findViewById(R.id.newClassTitle);
                EditText location=(EditText)mRootView.findViewById(R.id.newClassLocation);
                EditText info=(EditText)mRootView.findViewById(R.id.newClassInfo);

                if(title.getText().toString().matches("")||mSelectedDays==null||location.getText().toString().matches(""))
                    Toast.makeText(getActivity(),R.string.notAllFieldsFilledOutWarning,Toast.LENGTH_LONG).show();
                else {
                    ContentValues vals = new ContentValues(7);
                    vals.put(DbContract.ClassEntry.COLUMN_TITLE, title.getText().toString());
                    vals.put(DbContract.ClassEntry.COLUMN_TIME, mTime);
                    vals.put(DbContract.ClassEntry.COLUMN_DATE, mSelectedDays);
                    vals.put(DbContract.ClassEntry.COLUMN_DURATION, mEndTime);
                    vals.put(DbContract.ClassEntry.COLUMN_LOCATION, location.getText().toString());
                    vals.put(DbContract.ClassEntry.COLUMN_LEVEL, mLevel);
                    vals.put(DbContract.ClassEntry.COLUMN_EXTRA_INFO, info.getText().toString());

                    ContentResolver resolver = getActivity().getContentResolver();
                    Uri returnUri = resolver.insert(DbContract.ClassEntry.CONTENT_URI, vals);
                    int id=Integer.parseInt(returnUri.getLastPathSegment());
                    //Log.v("MainActivity", "The ID: "+returnUri.getLastPathSegment());


                    //Hiding the keyboard
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(title.getWindowToken(), 0);

                    mListener.onAddNewClass(mSelectedDaysAsArray, title.getText().toString(),mTime,id);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }
            }
        });
        return mRootView;

    }
    //---------------------------------------------------------------------------------------------
    //called from TimeDialogFragment through MainActivity
    public void setSelectedTime(String TAG,String time){
        if(TAG.equals(this.TAG)) {
            mTimeBtn.setText(time);
            mTime = time;
        }
        else if(TAG.equals(this.TAG+"END")){
            mEndTimeBtn.setText(time);
            mEndTime=time;
        }
    }
    //---------------------------------------------------------------------------------------------
    //called from DialogFragment through MainActivity
    public void setSelectedWeekdays(ArrayList<Integer>data){
        JSONObject jDays=new JSONObject();
        JSONArray jarr=new JSONArray(data);
        try {
            jDays.put("selectedDays", jarr);
        }catch(JSONException e){
            Log.e(TAG, "Something went wrong with forming JsonObject "+e);
        }
        mSelectedDaysAsArray=data;
        mSelectedDays = jDays.toString();

        /*
        JSONObject json = new JSONObject(stringreadfromsqlite);
        ArrayList items = json.optJSONArray("uniqueArrays");
         */

    }
    //---------------------------------------------------------------------------------------------
    private void showDaysDialog(int i) {
        DaysDialogFragment dialogFragment= DaysDialogFragment.newInstance(0);
        dialogFragment.show(getActivity().getFragmentManager(),"dialog");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAddNewClassListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    //---------------------------------------------------------------------------------------------
    public void setSpinners(){
        Spinner lvlSpinner=(Spinner)mRootView.findViewById(R.id.newClassLevels);
        ArrayAdapter<CharSequence>lvlAdapter= ArrayAdapter.createFromResource(getActivity(),R.array.levels,android.R.layout.simple_spinner_item);
        lvlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lvlSpinner.setAdapter(lvlAdapter);
        lvlSpinner.setOnItemSelectedListener(this);

    }
    //---------------------------------------------------------------------------------------------
    //DialogFragment for selecting days
    //---------------------------------------------------------------------------------------------
    public static class DaysDialogFragment extends DialogFragment{

        public String[] mdays;
        public boolean[] mselections;

        //---------------------------------------------------------------------------------------------
        public static DaysDialogFragment newInstance(int title) {
            DaysDialogFragment frag = new DaysDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }
        //---------------------------------------------------------------------------------------------
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mdays=getActivity().getResources().getStringArray(R.array.weekDays);
            mselections=new boolean[mdays.length];
            return new AlertDialog.Builder( getActivity() )
                    .setTitle(getActivity().getResources().getString(R.string.newClassDays) )
                    .setMultiChoiceItems(mdays, mselections, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {/*if you want to react on selections in realtime do it here */}
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<Integer> selectedDays= new ArrayList<Integer>();
                            for (int i = 0; i < mdays.length; i++) {
                                if (mselections[i])
                                    selectedDays.add(i);
                            }
                            ((MainActivity) getActivity()).forwardDataFromDialogFragmentToFragment(NewClassFragment.TAG, selectedDays);
                        }
                    })
                    .create();
        }
    }
    //---------------------------------------------------------------------------------------------
    //DialogFragment for selecting startTtime
    //---------------------------------------------------------------------------------------------
    public static class TimeDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),this,hour,minute,true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time=Integer.toString(hourOfDay)+":"+Integer.toString(minute);
            ((MainActivity)getActivity()).forwardTimeFromDialogFragmentToFragment(TAG,time);
        }
    }
    //---------------------------------------------------------------------------------------------
    //DialogFragment for selecting startTtime
    //---------------------------------------------------------------------------------------------
    public static class EndTimeDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),this,hour,minute,true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time=Integer.toString(hourOfDay)+":"+Integer.toString(minute);
            ((MainActivity)getActivity()).forwardTimeFromDialogFragmentToFragment(TAG+"END",time);
        }
    }

}
