package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;

public class UpdateClassContentFragment extends Fragment {

    public static final String TAG="UpdateClassContentFragment";

    private static final String ARG_DATE = "date";
    private static final String ARG_PAGES = "pages";
    private static final String ARG_BOOK = "book";
    private static final String ARG_INFO = "info";
    private static final String ARG_ID = "classId";

    private String mSDate;
    private String mSBook;
    private String mSPages;
    private String mSInfo;
    private int mId;

    private View mRootView;
    private TextView mDate, mBook, mPages,mInfo;

    private AttendingStudentsAdapter mAdapter;
    private ArrayList<AttendingStudentsAdapter.AttendingStudentsAdapterValues> mAttendingStudents;

    private OnUpdateClassContentListener mListener;

    public static UpdateClassContentFragment newInstance(String date, String book, String pages, String info, int id) {
        UpdateClassContentFragment fragment = new UpdateClassContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE,date);
        args.putString(ARG_PAGES, pages);
        args.putString(ARG_BOOK, book);
        args.putString(ARG_INFO, info);
        args.putInt(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateClassContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
       /*     mSDate = savedInstanceState.getString("date");
            mSBook = savedInstanceState.getString("book");
            mSPages = savedInstanceState.getString("pages");
            mSInfo = savedInstanceState.getString("info");
            mId = savedInstanceState.getInt("id"); */
            mAttendingStudents=savedInstanceState.getParcelableArrayList("attendingStudents");
        }
        else if (getArguments() != null) {
            mSDate = getArguments().getString(ARG_DATE);
            mSBook = getArguments().getString(ARG_BOOK);
            mSPages = getArguments().getString(ARG_PAGES);
            mSInfo = getArguments().getString(ARG_INFO);
            mId = getArguments().getInt(ARG_ID);
            getData();
        }
    }

    private void getData() {
        mAttendingStudents=new ArrayList<>();
        ContentResolver resolver=getActivity().getContentResolver();
        Uri uri= DbContract.StudentAttendanceEntry.CONTENT_URI_WITH_CLASSCONTENTKEY.buildUpon().appendPath(Integer.toString(mId)).build();
        Cursor cursor=resolver.query(uri,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int id=cursor.getInt(1);
            String status=cursor.getString(2);
            String name=cursor.getString(3);
            //Log.v(TAG,"theStatus: "+id+" "+status+" "+name);
            mAttendingStudents.add(new AttendingStudentsAdapter.AttendingStudentsAdapterValues(id,name,status));
            cursor.moveToNext();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("attendingStudents",mAdapter.mVals);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_update_class_content, container, false);
        mDate = (TextView) mRootView.findViewById(R.id.updateClassContentFragmentnewDate);
        mDate.setText(mSDate);
        mBook = (TextView) mRootView.findViewById(R.id.updateClassContentFragmentNewBook);
        mBook.setText(mSBook);
        mPages = (TextView) mRootView.findViewById(R.id.updateClassContentFragmentNewPages);
        mPages.setText(mSPages);
        mInfo = (TextView) mRootView.findViewById(R.id.updateClassContentFragmentNewInfo);
        mInfo.setText(mSInfo);

        TwoWayView studentsList = (TwoWayView) mRootView.findViewById(R.id.studentsListView);
        mAdapter = new AttendingStudentsAdapter(getActivity(), mAttendingStudents);
        studentsList.setAdapter(mAdapter);

        //UPDATING

        Button updateBtn = (Button) mRootView.findViewById(R.id.updateClassContentFragmentUpdate);
        updateBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSDate = mDate.getText().toString();
                mSBook = mBook.getText().toString();
                mSPages = mPages.getText().toString();
                mSInfo = mInfo.getText().toString();

                ContentValues vals = new ContentValues(4);
                vals.put(DbContract.ClassContentEntry.COLUMN_BOOK, mSBook);
                vals.put(DbContract.ClassContentEntry.COLUMN_INFO, mSInfo);
                vals.put(DbContract.ClassContentEntry.COLUMN_PAGE, mSPages);
                vals.put(DbContract.ClassContentEntry.COLUMN_DATE, mSDate);

                Uri uri = DbContract.ClassContentEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mId)).build();
                ContentResolver resolver = getActivity().getContentResolver();
                if (resolver.update(uri, vals, DbContract.ClassContentEntry._ID + " = ?", new String[]{Integer.toString(mId)}) > 0) {
                    mAttendingStudents = mAdapter.mVals;
                    for (int i = 0; i < mAttendingStudents.size(); i++) {
                        uri = DbContract.StudentAttendanceEntry.CONTENT_URI_WITH_STUDENTKEY.buildUpon().appendPath(Integer.toString(mId)).build();
                        vals = new ContentValues();
                        vals.put(DbContract.StudentAttendanceEntry.COLUMN_STATUS, mAttendingStudents.get(i).status);
                        resolver.update(uri, vals, DbContract.StudentAttendanceEntry.COLUMN_FOREIGN_KEY_STUDENT + " =?", new String[]{Integer.toString(mAttendingStudents.get(i).id)});
                    }
                    ClassContentAdapter.ClassContentAdapterValues updatedObj = new ClassContentAdapter.ClassContentAdapterValues(mId, mSDate, mSBook, mSPages, mSInfo);
                    mListener.OnUpdateClassContent(updatedObj);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.contentUpdatedAlert), Toast.LENGTH_LONG).show();
                }
            }
        });

        //DELETING

        Button deleteBtn = (Button) mRootView.findViewById(R.id.updateClassContentFragmentDelete);
        deleteBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = DbContract.ClassContentEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mId)).build();
                ContentResolver resolver = getActivity().getContentResolver();
                if (resolver.delete(uri, DbContract.ClassContentEntry._ID + " = ?", new String[]{Integer.toString(mId)}) > 0) {

                    //Delete all students in StudentAttendance associated with this classcontent
                    uri = DbContract.StudentAttendanceEntry.CONTENT_URI_WITH_CLASSCONTENTKEY.buildUpon().appendPath(Integer.toString(mId)).build();
                    resolver.delete(uri, null, null);
                    ClassContentAdapter.ClassContentAdapterValues deletedObj = new ClassContentAdapter.ClassContentAdapterValues(mId, mSDate, mSBook, mSPages, mSInfo);
                    mListener.onDeleteClassContent(deletedObj);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.contentDeletedAlert), Toast.LENGTH_LONG).show();
                }
            }
        });

        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnUpdateClassContentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnUpdateClassContentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnUpdateClassContentListener {
        public void OnUpdateClassContent(ClassContentAdapter.ClassContentAdapterValues updatedObj);
        public void onDeleteClassContent(ClassContentAdapter.ClassContentAdapterValues deletedObj);
    }

}
