package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
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

import app.com.jeldrik.teacherslittlehelper.data.DbContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UpdateStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateStudentFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_STUDENTADAPTERVAL = "param1";
    private static final String ARG_POSITION = "param2";
    int mPosition;
    View mRootView;
    TextView mName,mEmail,mPhone;


    private StudentAdapter.StudentAdapterValues studentAdapterVal;


    private OnStudentUpdatedListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment updateStudent.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateStudentFragment newInstance(StudentAdapter.StudentAdapterValues param1,int position) {
        UpdateStudentFragment fragment = new UpdateStudentFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STUDENTADAPTERVAL,param1);
        args.putInt(ARG_POSITION,position);
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            studentAdapterVal = getArguments().getParcelable(ARG_STUDENTADAPTERVAL);
            mPosition=getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView= inflater.inflate(R.layout.fragment_update_student, container, false);

        mName=(TextView)mRootView.findViewById(R.id.newStudentName);
        mName.setText(studentAdapterVal.name);
        mEmail=(TextView)mRootView.findViewById(R.id.newStudentEmail);
        mEmail.setText(studentAdapterVal.email);
        mPhone=(TextView)mRootView.findViewById(R.id.newStudentPhone);
        mPhone.setText(studentAdapterVal.phone);

        Log.v("UpdateStudentFragment","new Values: name: "+studentAdapterVal.name+" email "+studentAdapterVal.email+" phone "+studentAdapterVal.phone);

        Button btn=(Button)mRootView.findViewById(R.id.updateStudentFragmentUpdateStudent);

        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=mName.getText().toString();
                String email=mEmail.getText().toString();
                String phone=mPhone.getText().toString();

                studentAdapterVal.name=name;
                studentAdapterVal.email=email;
                studentAdapterVal.phone=phone;

                ContentValues vals = new ContentValues(3);
                vals.put(DbContract.StudentEntry.COLUMN_STUDENT_NAME, name);
                vals.put(DbContract.StudentEntry.COLUMN_EMAIL, email);
                vals.put(DbContract.StudentEntry.COLUMN_PHONE,phone);

                ContentResolver resolver=getActivity().getContentResolver();
                Uri uri= DbContract.StudentEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(studentAdapterVal.id)).build();
                if(resolver.update(uri,vals,null,null)>0) {
                    mListener.onStudentUpdated(studentAdapterVal,mPosition);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.studentUpdatedAlert), Toast.LENGTH_LONG).show();
                }
            }
        });

        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStudentUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStudentUpdatedListener {
        // TODO: Update argument type and name
        public void onStudentUpdated(StudentAdapter.StudentAdapterValues vals,int position);
    }

}