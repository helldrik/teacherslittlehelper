package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;


//---------------------------------------------------------------------------------------------
public class NewClassFragment extends Fragment {
    // TODO: Edit the layout to adjust hours picking (spinner?) and day selection (checkboxes?)
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    public static final String TAG="NEWCLASSFRAGMENT";
    private String mDay;
    //saving the selected weekdays that get chosen in DialogFragment
    private String selectedDays;

    private OnAddNewClassListener mListener;
    private Button mBtn;
    View mRootView;

    //---------------------------------------------------------------------------------------------
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     */
    public interface OnAddNewClassListener {
        public void onAddNewClass(String day,String title,String time, int id);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment NewClassFragment.
     */
//---------------------------------------------------------------------------------------------
    public static NewClassFragment newInstance(String param1) {
        NewClassFragment fragment = new NewClassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    //---------------------------------------------------------------------------------------------
    public NewClassFragment() {
        // Required empty public constructor
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDay = getArguments().getString(ARG_PARAM1);
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView =inflater.inflate(R.layout.fragment_new_class, container, false);

        Button daysBtn=(Button)mRootView.findViewById(R.id.newClassDays);
        daysBtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                showDaysDialog(0);
            }
        });

        mBtn=(Button)mRootView.findViewById(R.id.createClassBtn);
        mBtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                EditText title=(EditText)mRootView.findViewById(R.id.newClassTitle);

                EditText time=(EditText)mRootView.findViewById(R.id.newClassStartTime);
                EditText duration=(EditText)mRootView.findViewById(R.id.newClassDuration);
                EditText level=(EditText)mRootView.findViewById(R.id.newClassLevel);
                EditText location=(EditText)mRootView.findViewById(R.id.newClassLocation);
                EditText info=(EditText)mRootView.findViewById(R.id.newClassInfo);

                if(title.getText().toString().matches("")||selectedDays==null||time.getText().toString().matches("")||
                        duration.getText().toString().matches("")||location.getText().toString().matches(""))
                    Toast.makeText(getActivity(),R.string.notAllFieldsFilledOutWarning,Toast.LENGTH_LONG).show();
                else {
                    ContentValues vals = new ContentValues(7);
                    vals.put(DbContract.ClassEntry.COLUMN_TITLE, title.getText().toString());
                    vals.put(DbContract.ClassEntry.COLUMN_TIME, time.getText().toString());
                    vals.put(DbContract.ClassEntry.COLUMN_DATE, selectedDays);
                    vals.put(DbContract.ClassEntry.COLUMN_DURATION, Integer.parseInt(duration.getText().toString()));
                    vals.put(DbContract.ClassEntry.COLUMN_LOCATION, location.getText().toString());
                    vals.put(DbContract.ClassEntry.COLUMN_LEVEL, level.getText().toString());
                    vals.put(DbContract.ClassEntry.COLUMN_EXTRA_INFO, info.getText().toString());

                    ContentResolver resolver = getActivity().getContentResolver();
                    Uri returnUri = resolver.insert(DbContract.ClassEntry.CONTENT_URI, vals);
                    int id=Integer.parseInt(returnUri.getLastPathSegment());
                    Log.v("MainActivity", "The ID: "+returnUri.getLastPathSegment());


                    //Hiding the keyboard
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(title.getWindowToken(), 0);

                    mListener.onAddNewClass(mDay, title.getText().toString(),time.getText().toString(),id);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }
            }
        });
        return mRootView;

    }
    //---------------------------------------------------------------------------------------------
    //called from DialogFragment through MainActivity
    public void setSelectedWeekdays(ArrayList<String>data){
        JSONObject jDays=new JSONObject();
        JSONArray jarr=new JSONArray(data);
        try {
            jDays.put("selectedDays", jarr);
        }catch(JSONException e){
            Log.e(TAG, "Something went wrong with forming JsonObject "+e);
        }

        String arrayList = jDays.toString();

        /*
        JSONObject json = new JSONObject(stringreadfromsqlite);
        ArrayList items = json.optJSONArray("uniqueArrays");
         */

    }
    //---------------------------------------------------------------------------------------------
    private void showDaysDialog(int i) {
        DaysDialogFragment dialogFragment= DaysDialogFragment.newInstance(0);
        dialogFragment.show(getActivity().getFragmentManager(),"dialog");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAddNewClassListener) activity;
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
    //DialogFragment for selecting days
    //---------------------------------------------------------------------------------------------
    public static class DaysDialogFragment extends DialogFragment{

        public String[] mdays;
        public boolean[] mselections;

        //---------------------------------------------------------------------------------------------
        public static DaysDialogFragment newInstance(int title) {
            DaysDialogFragment frag = new DaysDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }
        //---------------------------------------------------------------------------------------------
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mdays=getActivity().getResources().getStringArray(R.array.weekDays);
            mselections=new boolean[mdays.length];
            return new AlertDialog.Builder( getActivity() )
                    .setTitle(getActivity().getResources().getString(R.string.newClassDays) )
                    .setMultiChoiceItems(mdays, mselections, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {/*if you want to react on selctions in realtime do it here */}
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<String> selectedDays= new ArrayList<String>();
                            for (int i = 0; i < mdays.length; i++) {
                                if (mselections[i])
                                    selectedDays.add(mdays[i]);
                            }
                            ((MainActivity) getActivity()).forwardDataFromDialogFragmentToFragment(NewClassFragment.TAG, selectedDays);
                        }
                    })
                    .create();
        }
    }

}
