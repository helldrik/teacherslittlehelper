package app.com.jeldrik.teacherslittlehelper;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentFragment extends Fragment {

    public static final String ARG_CLASS_ID="classId";

    int mClassId;

    public static StudentFragment newInstance(int param_classID) {
        StudentFragment fragment = new StudentFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_CLASS_ID, param_classID);
        fragment.setArguments(args);
        return fragment;
    }

    public StudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClassId = getArguments().getInt(ARG_CLASS_ID);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_student, container, false);
        ListView studentList= (ListView)rootView.findViewById(R.id.studentListView);
        //TODO: get values from sqlite db

        ArrayList<StudentAdapter.StudentAdapterValues> vals=getData();
        if(vals==null)
            Toast.makeText(getActivity(),"No Students in this class",Toast.LENGTH_LONG).show();
        else {
            StudentAdapter adapter = new StudentAdapter(getActivity(), vals);
            studentList.setAdapter(adapter);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_students,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.addStudent:
                Fragment newStudentFragment=NewStudentFragment.newInstance(mClassId);
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.FragmentContainer,newStudentFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<StudentAdapter.StudentAdapterValues> getData(){
        ArrayList<StudentAdapter.StudentAdapterValues> vals=new ArrayList<>();
        //vals.add(new StudentAdapter.StudentAdapterValues("Jeldrik","jeldriks@gmail.com","673300608"));

        ContentResolver resolver=getActivity().getContentResolver();
        Uri uri= DbContract.StudentEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mClassId)).build();
        Log.v("ClassFragment", "Uri: " + uri.toString());
        Cursor cursor=resolver.query(uri,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            vals.add(new StudentAdapter.StudentAdapterValues(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
            cursor.moveToNext();
        }
        return vals;
    }
}
