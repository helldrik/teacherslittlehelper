package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.impl.cookie.DateParseException;
import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;


public class NewClassContentFragment extends Fragment {

    //TODO:Making a Settings item to allow changing between different date formats
    public static String DATE_FORMAT = "EUR";
    public static final String TAG="AddClassContentFragment";
    private static final String ARG_CLASS_ID = "classId";
    private static final String ARG_STUDENTS = "students";

    private int mClassId;
    private String mDate;
    private String sBook;
    private String sPages;
    private String sInfo;
    private int mTimestamp;
    private ArrayList<StudentAdapter.StudentAdapterValues> mStudents;

    private AttendingStudentsAdapter mAdapter;
    private ArrayList<AttendingStudentsAdapter.AttendingStudentsAdapterValues> mAttendingStudents;

    private OnNewClassContentListener mListener;
    private View mRootView;
    private AutoCompleteTextView mBook;


    //---------------------------------------------------------------------------------------------
    public static NewClassContentFragment newInstance(int classId, ArrayList<StudentAdapter.StudentAdapterValues> students) {
        NewClassContentFragment fragment = new NewClassContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLASS_ID,classId);
        args.putParcelableArrayList(ARG_STUDENTS,students);
        fragment.setArguments(args);
        return fragment;
    }
    //---------------------------------------------------------------------------------------------
    public NewClassContentFragment() {
        // Required empty public constructor
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            mClassId=savedInstanceState.getInt("classId");
            mDate=savedInstanceState.getString("date");
            sBook=savedInstanceState.getString("book");
            sPages=savedInstanceState.getString("pages");
            sInfo=savedInstanceState.getString("info");
            mStudents=savedInstanceState.getParcelableArrayList("students");
            mAttendingStudents=savedInstanceState.getParcelableArrayList("attendingStudents");
            mTimestamp=savedInstanceState.getInt("timestamp");
        }
        else if (getArguments() != null) {
            mClassId = getArguments().getInt(ARG_CLASS_ID);
            mStudents=getArguments().getParcelableArrayList(ARG_STUDENTS);

            mAttendingStudents=new ArrayList(mStudents.size());
            for (int i=0;i<mStudents.size();i++) {

                mAttendingStudents.add(new AttendingStudentsAdapter.AttendingStudentsAdapterValues(
                        mStudents.get(i).id,
                        mStudents.get(i).name,
                        getActivity().getResources().getIntArray(R.array.attendance).toString()));
            }
            Calendar c = Calendar.getInstance();
            mTimestamp=c.get(Calendar.DAY_OF_MONTH)+c.get(Calendar.MONTH)*30+c.get(Calendar.YEAR)*365;
            if (DATE_FORMAT == "EUR")
                mDate = Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "." + Integer.toString(c.get(Calendar.MONTH) + 1) + "." + Integer.toString(c.get(Calendar.YEAR));
            else if (DATE_FORMAT == "US")
                mDate = Integer.toString(c.get(Calendar.MONTH) + 1) + "/" + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "/" + Integer.toString(c.get(Calendar.YEAR));
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("classId", mClassId);
        outState.putString("date", mDate);
        outState.putString("book", sBook);
        outState.putString("pages", sPages);
        outState.putString("info", sInfo);
        outState.putInt("timestamp",mTimestamp);
        outState.putParcelableArrayList("students",mStudents);
        outState.putParcelableArrayList("attendingStudents",mAdapter.mVals);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView=inflater.inflate(R.layout.fragment_new_class_content, container, false);
        createStudentsList();

        mBook=(AutoCompleteTextView)mRootView.findViewById(R.id.newClassContentFragmentNewBook);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, getBookselection());
        mBook.setAdapter(adapter);

        Button dateBtn=(Button)mRootView.findViewById(R.id.newClassContentFragmentnewDate);
        dateBtn.setText(mDate);
        dateBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddDateFragment();
                newFragment.show(getActivity().getFragmentManager(),"Date");
            }
        });
        Button btn=(Button)mRootView.findViewById(R.id.newClassContentFragmentNewClassContent);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {


                sBook=mBook.getText().toString();
                TextView pages=(TextView)mRootView.findViewById(R.id.newClassContentFragmentNewPages);
                sPages=pages.getText().toString();
                TextView info=(TextView)mRootView.findViewById(R.id.newClassContentFragmentNewInfo);
                sInfo=info.getText().toString();
                Log.v(TAG,"TimeStamp: "+mTimestamp);
                ContentValues vals = new ContentValues(5);
                vals.put(DbContract.ClassContentEntry.COLUMN_BOOK, sBook);
                vals.put(DbContract.ClassContentEntry.COLUMN_INFO, sInfo);
                vals.put(DbContract.ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS, mClassId);
                vals.put(DbContract.ClassContentEntry.COLUMN_PAGE, sPages);
                vals.put(DbContract.ClassContentEntry.COLUMN_DATE, mDate);
                vals.put(DbContract.ClassContentEntry.COLUMN_TIMESTAMP, mTimestamp);

                ContentResolver resolver=getActivity().getContentResolver();
                Uri returnUri=resolver.insert(DbContract.ClassContentEntry.CONTENT_URI,vals);
                int id = Integer.parseInt(returnUri.getLastPathSegment());
                //updating mAttendingStudents with any changes that have been made in the adapter
                if(mStudents.size()>0) {
                    mAttendingStudents = mAdapter.mVals;
                }
                for(int i=0;i<mAttendingStudents.size();i++){
                    vals=new ContentValues();
                    vals.put(DbContract.StudentAttendanceEntry.COLUMN_FOREIGN_KEY_CLASSCONTENT,id);
                    vals.put(DbContract.StudentAttendanceEntry.COLUMN_FOREIGN_KEY_STUDENT,mAttendingStudents.get(i).id);
                    vals.put(DbContract.StudentAttendanceEntry.COLUMN_STATUS,mAttendingStudents.get(i).status);
                    returnUri=resolver.insert(DbContract.StudentAttendanceEntry.CONTENT_URI,vals);
                    //Log.v(TAG,"AttendanceId: "+returnUri.getLastPathSegment()+" "+mAttendingStudents.get(i).status);
                }
                ClassContentAdapter.ClassContentAdapterValues newVals=new ClassContentAdapter.ClassContentAdapterValues(id,mDate,mTimestamp,sBook,sPages,sInfo);
                mListener.OnNewClassContent(newVals);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                Toast.makeText(getActivity(),getActivity().getResources().getText(R.string.contentAddedAlert),Toast.LENGTH_LONG).show();

            }
        });
        return mRootView;
    }
    //---------------------------------------------------------------------------------------------
    private void createStudentsList(){
        if(mStudents.size()>0) {
            TwoWayView studentsList = (TwoWayView) mRootView.findViewById(R.id.studentsListView);
            mAdapter = new AttendingStudentsAdapter(getActivity(), mAttendingStudents);
            studentsList.setAdapter(mAdapter);
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNewClassContentListener) activity;
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
    public interface OnNewClassContentListener {
        public void OnNewClassContent(ClassContentAdapter.ClassContentAdapterValues values);
    }
    //---------------------------------------------------------------------------------------------
    public void setDate(String date, int timestamp){
        mDate=date;
        mTimestamp=timestamp;
        Button dateBtn=(Button)mRootView.findViewById(R.id.newClassContentFragmentnewDate);
        dateBtn.setText(mDate);
    }
    //---------------------------------------------------------------------------------------------
    private String[] getBookselection(){

        ArrayList<String> books=new ArrayList<>();
        ContentResolver resolver=getActivity().getContentResolver();
        Cursor cursor=resolver.query(DbContract.ClassContentEntry.CONTENT_URI,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            if(!books.contains(cursor.getString(0)))
                books.add(cursor.getString(0));
            cursor.moveToNext();
        }
        String[] string=new String[books.size()];
        return books.toArray(string);
    }
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public static class AddDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        int day,month,year;
        int timestamp;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if(savedInstanceState!=null){
                day=savedInstanceState.getInt("day");
                month=savedInstanceState.getInt("month");
                year=savedInstanceState.getInt("year");
                timestamp=savedInstanceState.getInt("timestamp");
            }
            else {
                final Calendar c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

            }
            return new DatePickerDialog(getActivity(),this,year,month,day);
        }
        //---------------------------------------------------------------------------------------------
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date="";
            if(DATE_FORMAT=="EUR")
                date=Integer.toString(dayOfMonth)+"."+Integer.toString(monthOfYear+1)+"."+Integer.toString(year);
            else if(DATE_FORMAT=="US")
                date=Integer.toString(monthOfYear+1)+"/"+Integer.toString(dayOfMonth)+"/"+Integer.toString(year);
            timestamp=dayOfMonth+monthOfYear*30+year*365;
            ((MainActivity)getActivity()).forwardDatetoNewClassContentFragment(date,timestamp);

        }
        //---------------------------------------------------------------------------------------------
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("day",day);
            outState.putInt("month",month);
            outState.putInt("year",year);
            outState.putInt("timestamp",timestamp);
        }

    }
}
