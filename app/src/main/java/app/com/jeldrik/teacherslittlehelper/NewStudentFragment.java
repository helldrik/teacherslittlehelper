package app.com.jeldrik.teacherslittlehelper;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;

public class NewStudentFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLASS_TIMESTAMP = "classTimestamp";


    private long mClassTimestamp;
    private String name;
    private String email;
    private String phone;

    private View mRootView;
    private OnStudentAddedListener mListener;

    public static NewStudentFragment newInstance(long classTimestamp) {
        NewStudentFragment fragment = new NewStudentFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CLASS_TIMESTAMP, classTimestamp);
        fragment.setArguments(args);
        return fragment;
    }

    public NewStudentFragment() {
        // Required empty public constructor
    }
    public interface OnStudentAddedListener {
        public void onStudentAdded(StudentAdapter.StudentAdapterValues newStudent);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            mClassTimestamp=savedInstanceState.getLong("classTimestamp");
            name=savedInstanceState.getString("name");
            email=savedInstanceState.getString("email");
            phone=savedInstanceState.getString("phone");
        }
        else if (getArguments() != null) {
            mClassTimestamp = getArguments().getLong(ARG_CLASS_TIMESTAMP);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("classTimestamp",mClassTimestamp);
        outState.putString("name",name);
        outState.putString("email",email);
        outState.putString("phone",phone);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView= inflater.inflate(R.layout.fragment_new_student, container, false);


        
        Button btn=(Button)mRootView.findViewById(R.id.newStudentAddStudent);
        
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvName=(TextView)mRootView.findViewById(R.id.newStudentName);
                name=tvName.getText().toString();
                TextView tvEmail=(TextView)mRootView.findViewById(R.id.newStudentEmail);
                email=tvEmail.getText().toString();
                TextView tvPhone=(TextView)mRootView.findViewById(R.id.newStudentPhone);
                phone=tvPhone.getText().toString();

                ContentValues vals = new ContentValues(4);
                vals.put(DbContract.StudentEntry.COLUMN_STUDENT_NAME, name);
                vals.put(DbContract.StudentEntry.COLUMN_EMAIL, email);
                vals.put(DbContract.StudentEntry.COLUMN_PHONE,phone);
                vals.put(DbContract.StudentEntry.COLUMN_FOREIGN_KEY_CLASS,mClassTimestamp);
                Date date=new Date();
                long timestamp=date.getTime();
                vals.put(DbContract.StudentEntry.COLUMN_TIMESTAMP,timestamp);

                ContentResolver resolver = getActivity().getContentResolver();
                Uri returnUri = resolver.insert(DbContract.StudentEntry.CONTENT_URI, vals);
                int id=Integer.parseInt(returnUri.getLastPathSegment());
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                Toast.makeText(getActivity(),getActivity().getResources().getText(R.string.studentAddedAlert),Toast.LENGTH_LONG).show();
                mListener.onStudentAdded(new StudentAdapter.StudentAdapterValues(id,name,email,phone,timestamp));

                //Hiding the keyboard
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });
        
        return mRootView;
    }
    //--------------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStudentAddedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStudentAddedListener");
        }
    }
    public void onDetach(){
        super.onDetach();
        mListener=null;
    }

}
