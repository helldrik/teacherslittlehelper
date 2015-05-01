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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;

public class UpdateClassFragment extends Fragment {

    public static final String TAG="UpdateClassFragment";
    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DAYS = "days";
    private static final String ARG_LOCATION = "location";
    private static final String ARG_HOUR = "hour";
    private static final String ARG_ENDTIME = "endtime";
    private static final String ARG_LEVEL = "level";
    private static final String ARG_INFO = "info";

    private String mSelectedDaysAsJson;


    private int mId;
    private String mTitle;
    private String mDays;
    private String mLocation;
    private String mTime;
    private String mEndTime;
    private String mLevel;
    private String mInfo;

    private View mRootView;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("id",mId);
        outState.putString("title",mTitle);
        outState.putString("days",mDays);
        outState.putString("location",mLocation);
        outState.putString("time",mTime);
        outState.putString("endTime",mEndTime);
        outState.putString("level",mLevel);
        outState.putString("info",mInfo);
        outState.putString("selectedDaysAsJson",mSelectedDaysAsJson);

        super.onSaveInstanceState(outState);
    }

    private Button mTimeBtn,mEndTimeBtn;

    private OnClassUpdatedListener mListener;

    //---------------------------------------------------------------------------------------------
    public static UpdateClassFragment newInstance(int id,String title, String days,String location, String hour, String endTime, String level, String info) {
        UpdateClassFragment fragment = new UpdateClassFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DAYS, days);
        args.putString(ARG_LOCATION, location);
        args.putString(ARG_HOUR, hour);
        args.putString(ARG_ENDTIME, endTime);
        args.putString(ARG_LEVEL, level);
        args.putString(ARG_INFO, info);
        fragment.setArguments(args);
        return fragment;
    }
    //---------------------------------------------------------------------------------------------
    public UpdateClassFragment() {
        // Required empty public constructor
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            mId=savedInstanceState.getInt("id",0);
            mTitle=savedInstanceState.getString("title","");
            mDays=savedInstanceState.getString("days","");
            mLocation=savedInstanceState.getString("location","");
            mTime=savedInstanceState.getString("time","");
            mEndTime=savedInstanceState.getString("endTime","");
            mLevel=savedInstanceState.getString("level","");
            mInfo=savedInstanceState.getString("info","");
            mSelectedDaysAsJson=savedInstanceState.getString("selectedDaysAsJson","");
        }
        else if (getArguments() != null) {
            mId = getArguments().getInt(ARG_ID);
            mTitle = getArguments().getString(ARG_TITLE);
            mDays = getArguments().getString(ARG_DAYS);
            mLocation = getArguments().getString(ARG_LOCATION);
            mTime = getArguments().getString(ARG_HOUR);
            mEndTime = getArguments().getString(ARG_ENDTIME);
            mLevel = getArguments().getString(ARG_LEVEL);
            mInfo = getArguments().getString(ARG_INFO);
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_update_class, container, false);


        EditText title=(EditText)mRootView.findViewById(R.id.newClassTitle);
        title.setText(mTitle);
        EditText location=(EditText)mRootView.findViewById(R.id.newClassLocation);
        location.setText(mLocation);
        EditText info=(EditText)mRootView.findViewById(R.id.newClassInfo);
        info.setText(mInfo);
        EditText lvl=(EditText)mRootView.findViewById(R.id.updateClassLevel);
        lvl.setText(mLevel);

        Button daysBtn=(Button)mRootView.findViewById(R.id.newClassDays);
        daysBtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                DaysDialogFragment dialogFragment= DaysDialogFragment.newInstance(mDays);
                dialogFragment.show(getActivity().getFragmentManager(),"dialog");
            }
        });

        mTimeBtn= (Button)mRootView.findViewById(R.id.newClassStartTime);
        mTimeBtn.setText(mTime);
        mTimeBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = TimeDialogFragment.newInstance(mTime);
                timeFragment.show(getActivity().getFragmentManager(), "Time");
            }
        });
        mEndTimeBtn= (Button)mRootView.findViewById(R.id.newClassEndTime);
        mEndTimeBtn.setText(mEndTime);
        mEndTimeBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = EndTimeDialogFragment.newInstance(mEndTime);
                newFragment.show(getActivity().getFragmentManager(),"EndTime");
            }
        });
        Button updateBtn=(Button)mRootView.findViewById(R.id.updateClassBtn);
        updateBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText title=(EditText)mRootView.findViewById(R.id.newClassTitle);
                mTitle=title.getText().toString();
                EditText location=(EditText)mRootView.findViewById(R.id.newClassLocation);
                mLocation=location.getText().toString();
                EditText info=(EditText)mRootView.findViewById(R.id.newClassInfo);
                mInfo=info.getText().toString();
                EditText lvl=(EditText)mRootView.findViewById(R.id.updateClassLevel);
                mLevel=lvl.getText().toString();

                ContentValues vals=new ContentValues();
                vals.put(DbContract.ClassEntry.COLUMN_TITLE, mTitle);
                vals.put(DbContract.ClassEntry.COLUMN_TIME, mTime);

                if(mSelectedDaysAsJson!=null && mSelectedDaysAsJson!="")
                    vals.put(DbContract.ClassEntry.COLUMN_DATE, mSelectedDaysAsJson);

                vals.put(DbContract.ClassEntry.COLUMN_DURATION, mEndTime);
                vals.put(DbContract.ClassEntry.COLUMN_LOCATION, mLocation);
                vals.put(DbContract.ClassEntry.COLUMN_LEVEL, mLevel);
                vals.put(DbContract.ClassEntry.COLUMN_EXTRA_INFO, mInfo);
                Uri uri= DbContract.ClassEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mId)).build();
                ContentResolver resolver=getActivity().getContentResolver();
                if(resolver.update(uri,vals,null,null)>0){
                    //Hiding the keyboard
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    mListener.OnClassUpdated(mId,mTitle,mDays,mLocation,mTime,mEndTime,mLevel,mInfo);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    Toast.makeText(getActivity(),R.string.classUpdated,Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(getActivity(),"Could not update",Toast.LENGTH_LONG).show();
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
        mSelectedDaysAsJson = jDays.toString();
        mDays=toDays(mSelectedDaysAsJson);


        /*
        JSONObject json = new JSONObject(stringreadfromsqlite);
        ArrayList items = json.optJSONArray("uniqueArrays");
         */

    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

       try {
            mListener = (OnClassUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClassUpdatedListener");
        }

    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    //---------------------------------------------------------------------------------------------
    public interface OnClassUpdatedListener {
        public void OnClassUpdated(int id,String title, String days,String location, String startTime, String endTime, String level, String info);
    }
    //---------------------------------------------------------------------------------------------
    //DialogFragment for selecting days
    //---------------------------------------------------------------------------------------------
    public static class DaysDialogFragment extends DialogFragment {

        private String mSavedDays;
        public String[] mdays;
        public boolean[] mselections;

        //---------------------------------------------------------------------------------------------
        public static DaysDialogFragment newInstance(String days) {
            DaysDialogFragment frag = new DaysDialogFragment();
            Bundle args = new Bundle();
            args.putString("days", days);
            frag.setArguments(args);
            return frag;
        }
        //---------------------------------------------------------------------------------------------
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mdays=getActivity().getResources().getStringArray(R.array.weekDays);
            mselections=new boolean[mdays.length];
            if (getArguments() != null) {
                mSavedDays=getArguments().getString("days");
                for(int i=0;i<mdays.length;i++){
                    if(mSavedDays.contains(mdays[i]))
                        mselections[i]=true;
                }
            }
        }
        //---------------------------------------------------------------------------------------------
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

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
                            ((MainActivity) getActivity()).forwardDataFromDialogFragmentToFragment(UpdateClassFragment.TAG, selectedDays);
                        }
                    })
                    .create();
        }
    }
    //---------------------------------------------------------------------------------------------
    //DialogFragment for selecting startTtime
    //---------------------------------------------------------------------------------------------
    public static class TimeDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        String mTime;
        Calendar mCalendar;

        public static TimeDialogFragment newInstance(String time){
            TimeDialogFragment fragment = new TimeDialogFragment();
            Bundle args = new Bundle();
            args.putString("TIME",time);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mTime=getArguments().getString("TIME","");
                int dividerPos=mTime.indexOf(":");
                int hour= Integer.parseInt(mTime.substring(0,dividerPos));
                int minute=Integer.parseInt(mTime.substring(dividerPos+1));
                mCalendar=Calendar.getInstance();
                mCalendar.set(Calendar.HOUR_OF_DAY,hour);
                mCalendar.set(Calendar.MINUTE,minute);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = mCalendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),this,hour,minute,true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour=Integer.toString(hourOfDay);
            if (hour.length()==1)
                hour="0"+hour;
            String min=Integer.toString(minute);
            if(min.length()==1)
                min="0"+min;
            String time=hour+":"+min;
            ((MainActivity)getActivity()).forwardTimeFromDialogFragmentToFragment(TAG,time);
        }
    }
    //---------------------------------------------------------------------------------------------
    //DialogFragment for selecting endTtime
    //---------------------------------------------------------------------------------------------
    public static class EndTimeDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
        String mTime;
        Calendar mCalendar;

        public static EndTimeDialogFragment newInstance(String time){
            EndTimeDialogFragment fragment = new EndTimeDialogFragment();
            Bundle args = new Bundle();
            args.putString("TIME",time);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mTime=getArguments().getString("TIME","");
                int dividerPos=mTime.indexOf(":");
                int hour= Integer.parseInt(mTime.substring(0,dividerPos));
                int minute=Integer.parseInt(mTime.substring(dividerPos+1));
                mCalendar=Calendar.getInstance();
                mCalendar.set(Calendar.HOUR_OF_DAY,hour);
                mCalendar.set(Calendar.MINUTE,minute);
            }
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = mCalendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),this,hour,minute,true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour=Integer.toString(hourOfDay);
            if (hour.length()==1)
                hour="0"+hour;
            String min=Integer.toString(minute);
            if(min.length()==1)
                min="0"+min;
            String time=hour+":"+min;

            ((MainActivity)getActivity()).forwardTimeFromDialogFragmentToFragment(TAG + "END", time);
        }
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

}
