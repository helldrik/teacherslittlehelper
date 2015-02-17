package app.com.jeldrik.teacherslittlehelper;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentFragment extends Fragment {

    public static StudentFragment newInstance() {
        StudentFragment fragment = new StudentFragment();
        return fragment;
    }

    public StudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_student, container, false);
        ListView studentList= (ListView)rootView.findViewById(R.id.studentListView);
        //TODO: get values from sqlite db
        ArrayList<StudentAdapter.StudentAdapterValues> vals=new ArrayList<>();
        vals.add(new StudentAdapter.StudentAdapterValues("Jeldrik","jeldriks@gmail.com","673300608"));

        StudentAdapter adapter=new StudentAdapter(getActivity(),vals);
        studentList.setAdapter(adapter);
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
                Toast.makeText(getActivity(),"Add student",Toast.LENGTH_LONG).show();
                //TODO:add student to db
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
