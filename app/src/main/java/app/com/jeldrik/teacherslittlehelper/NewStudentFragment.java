package app.com.jeldrik.teacherslittlehelper;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewStudentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLASS_ID = "classId";


    private int mClassId;
    private View mRootView;



    // TODO: Rename and change types and number of parameters
    public static NewStudentFragment newInstance(int classId) {
        NewStudentFragment fragment = new NewStudentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLASS_ID, classId);
        fragment.setArguments(args);
        return fragment;
    }

    public NewStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mClassId = getArguments().getInt(ARG_CLASS_ID);
        }
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
                String name=tvName.getText().toString();
                TextView tvEmail=(TextView)mRootView.findViewById(R.id.newStudentEmail);
                String email=tvEmail.getText().toString();
                TextView tvPhone=(TextView)mRootView.findViewById(R.id.newStudentPhone);
                String phone=tvPhone.getText().toString();

                ContentValues vals = new ContentValues(7);
                vals.put(DbContract.StudentEntry.COLUMN_STUDENT_NAME, name);
                vals.put(DbContract.StudentEntry.COLUMN_EMAIL, email);
                vals.put(DbContract.StudentEntry.COLUMN_PHONE,phone);
                vals.put(DbContract.StudentEntry.COLUMN_FOREIGN_KEY_CLASS,mClassId);

                ContentResolver resolver = getActivity().getContentResolver();
                Uri returnUri = resolver.insert(DbContract.StudentEntry.CONTENT_URI, vals);
                int id=Integer.parseInt(returnUri.getLastPathSegment());
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                Toast.makeText(getActivity(),getActivity().getResources().getText(R.string.studentAddedAlert),Toast.LENGTH_LONG).show();

            }
        });

        
        
        
        
        return mRootView;
    }


}
