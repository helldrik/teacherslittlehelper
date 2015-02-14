package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;
import android.content.ContentResolver;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;


public class ClassFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 ="id";

    // TODO: Rename and change types of parameters
    private String day;
    private int position;
    private int mID;

    private String mTitle;
    private String mDays;
    private String mLocation;
    private String mHour;
    private String mDuration;
    private String mLevel;
    private String mInfo;

    private OnDeleteListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassFragment newInstance(String param1, int param2, int param3) {
        ClassFragment fragment = new ClassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3,param3);
        fragment.setArguments(args);
        return fragment;
    }

    public ClassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getString(ARG_PARAM1);
            position = getArguments().getInt(ARG_PARAM2);
            mID=getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_class, container, false);
        getData();
        TextView title=(TextView)rootView.findViewById(R.id.classfragment_title);
        title.setText(mTitle);
        TextView days=(TextView)rootView.findViewById(R.id.classFragment_days);
        days.setText(mDays);
        TextView duration=(TextView)rootView.findViewById(R.id.classFragment_duration);
        duration.setText(mDuration);
        TextView info=(TextView)rootView.findViewById(R.id.classFragment_info);
        info.setText(mInfo);
        TextView level=(TextView)rootView.findViewById(R.id.classFragment_level);
        level.setText(mLevel);
        TextView location=(TextView)rootView.findViewById(R.id.classFragment_location);
        location.setText(mLocation);
        TextView hour=(TextView)rootView.findViewById(R.id.classFragment_hour);
        hour.setText(mHour);

        Button deleteBtn=(Button)rootView.findViewById(R.id.deleteClassButton);
        deleteBtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDelete(mID);
                }
                FragmentManager fm=getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDeleteListener) activity;
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
    public interface OnDeleteListener {
        // TODO: Update argument type and name
        public void onDelete(int id);
    }

    private void getData(){
        ContentResolver resolver=getActivity().getContentResolver();
        Uri uri= DbContract.ClassEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mID)).build();
        Log.v("ClassFragment","Uri: "+uri.toString());
        Cursor cursor=resolver.query(uri,new String[]{DbContract.ClassEntry.COLUMN_DATE,
                                        DbContract.ClassEntry.COLUMN_DURATION,
                                        DbContract.ClassEntry.COLUMN_EXTRA_INFO,
                                        DbContract.ClassEntry.COLUMN_LEVEL,
                                        DbContract.ClassEntry.COLUMN_LOCATION,
                                        DbContract.ClassEntry.COLUMN_TIME,
                                        DbContract.ClassEntry.COLUMN_TITLE },null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            mDays=toDays(cursor.getString(0));
            mDuration=cursor.getString(1);
            mInfo=cursor.getString(2);
            mLevel=cursor.getString(3);
            mLocation=cursor.getString(4);
            mHour=cursor.getString(5);
            mTitle=cursor.getString(6);
            cursor.moveToNext();
        }
    }

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
