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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
    public static final String TAG="STUDENTFRAGMENT";
    int mClassId;
    ListView mStudentList;

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
        mStudentList= (ListView)rootView.findViewById(R.id.studentListView);

        ArrayList<StudentAdapter.StudentAdapterValues> vals=getData();
        if(vals==null)
            Toast.makeText(getActivity(),"No Students in this class",Toast.LENGTH_LONG).show();
        else {
            StudentAdapter adapter = new StudentAdapter(getActivity(), vals);
            mStudentList.setAdapter(adapter);
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

    public void updateAdapter(StudentAdapter.StudentAdapterValues vals, int position){
        StudentAdapter adapter=(StudentAdapter)mStudentList.getAdapter();
        adapter.remove(position);
        adapter.add(vals);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<StudentAdapter.StudentAdapterValues> getData(){
        ArrayList<StudentAdapter.StudentAdapterValues> vals=new ArrayList<>();
        //vals.add(new StudentAdapter.StudentAdapterValues("Jeldrik","jeldriks@gmail.com","673300608"));

        ContentResolver resolver=getActivity().getContentResolver();
        Uri uri= DbContract.StudentEntry.CONTENT_URI_WITH_FOREIGNKEY.buildUpon().appendPath(Integer.toString(mClassId)).build();
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
