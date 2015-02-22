package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class ClassContentFragment extends Fragment {
    private static final String ARG_CLASS_ID = "classID";
    public static final String TAG="CLASSCONTENTFRAGMENT";
    private int mClassId;

    public static ClassContentFragment newInstance(int classId) {
        ClassContentFragment fragment = new ClassContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLASS_ID, classId);
        fragment.setArguments(args);
        return fragment;
    }

    public ClassContentFragment() {
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

        TwoWayView list=(TwoWayView)inflater.inflate(R.layout.fragment_class_content, container, false);
        ArrayList<ClassContentAdapter.ClassContentAdapterValues> values=new ArrayList<>();
        values.add(new ClassContentAdapter.ClassContentAdapterValues(0,"01.10.1981","Book of Eden","12-133","something special"));
        values.add(new ClassContentAdapter.ClassContentAdapterValues(0,"01.10.1981","Book of Eden","12-133","something special"));
        values.add(new ClassContentAdapter.ClassContentAdapterValues(0,"01.10.1981","Book of Eden","12-133","something special"));
        values.add(new ClassContentAdapter.ClassContentAdapterValues(0,"01.10.1981","Book of Eden","12-133","something special"));

        ClassContentAdapter adapter=new ClassContentAdapter(getActivity(),values);
        list.setAdapter(adapter);
        return list;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
