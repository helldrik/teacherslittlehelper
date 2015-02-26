package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.impl.cookie.DateParseException;

import java.util.Calendar;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;


public class NewClassContentFragment extends Fragment {

    //TODO:Making a Settings item to allow changing between different date formats
    public static String DATE_FORMAT = "EUR";
    public static final String TAG="AddClassContentFragment";
    private static final String ARG_CLASS_ID = "classId";
    private int mClassId;
    private String mDate;
    private OnNewClassContentListener mListener;
    private View mRootView;



    public static NewClassContentFragment newInstance(int classId) {
        NewClassContentFragment fragment = new NewClassContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLASS_ID,classId);
        fragment.setArguments(args);
        return fragment;
    }

    public NewClassContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mClassId=getArguments().getInt(ARG_CLASS_ID);
        }
        Calendar c=Calendar.getInstance();
        if(DATE_FORMAT=="EUR")
            mDate=Integer.toString(c.get(Calendar.DAY_OF_MONTH))+"."+Integer.toString(c.get(Calendar.MONTH)+1)+"."+Integer.toString(c.get(Calendar.YEAR));
        else if(DATE_FORMAT=="US")
            mDate=Integer.toString(c.get(Calendar.MONTH)+1)+"/"+Integer.toString(c.get(Calendar.DAY_OF_MONTH))+"/"+Integer.toString(c.get(Calendar.YEAR));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView=inflater.inflate(R.layout.fragment_new_class_content, container, false);

        Button dateBtn=(Button)mRootView.findViewById(R.id.newClassContentFragmentnewDate);
        dateBtn.setText(mDate);
        dateBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddDateFragment();
                newFragment.show(getActivity().getFragmentManager(),"Date");
            }
        });
        Button btn=(Button)mRootView.findViewById(R.id.newClassContentFragmentNewClassContent);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView book=(TextView)mRootView.findViewById(R.id.newClassContentFragmentNewBook);
                String sBook=book.getText().toString();
                TextView pages=(TextView)mRootView.findViewById(R.id.newClassContentFragmentNewPages);
                String sPages=pages.getText().toString();
                TextView info=(TextView)mRootView.findViewById(R.id.newClassContentFragmentNewInfo);
                String sInfo=info.getText().toString();

                ContentValues vals = new ContentValues(5);
                vals.put(DbContract.ClassContentEntry.COLUMN_BOOK, sBook);
                vals.put(DbContract.ClassContentEntry.COLUMN_INFO, sInfo);
                vals.put(DbContract.ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS, mClassId);
                vals.put(DbContract.ClassContentEntry.COLUMN_PAGE, sPages);
                vals.put(DbContract.ClassContentEntry.COLUMN_DATE, mDate);

                ContentResolver resolver=getActivity().getContentResolver();
                Uri returnUri=resolver.insert(DbContract.ClassContentEntry.CONTENT_URI,vals);
                int id = Integer.parseInt(returnUri.getLastPathSegment());
                ClassContentAdapter.ClassContentAdapterValues newVals=new ClassContentAdapter.ClassContentAdapterValues(id,mDate,sBook,sPages,sInfo);
                mListener.OnNewClassContent(newVals);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                Toast.makeText(getActivity(),getActivity().getResources().getText(R.string.contentAddedAlert),Toast.LENGTH_LONG).show();

            }
        });
        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNewClassContentListener) activity;
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

    public interface OnNewClassContentListener {
        // TODO: Update argument type and name
        public void OnNewClassContent(ClassContentAdapter.ClassContentAdapterValues values);
    }

    public void setDate(String date){
        mDate=date;
        Button dateBtn=(Button)mRootView.findViewById(R.id.newClassContentFragmentnewDate);
        dateBtn.setText(mDate);
    }
    public static class AddDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int day=c.get(Calendar.DAY_OF_MONTH);
            int month=c.get(Calendar.MONTH);
            int year=c.get(Calendar.YEAR);
            return new DatePickerDialog(getActivity(),this,year,month,day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date="";
            if(DATE_FORMAT=="EUR")
                date=Integer.toString(dayOfMonth)+"."+Integer.toString(monthOfYear+1)+"."+Integer.toString(year);
            else if(DATE_FORMAT=="US")
                date=Integer.toString(monthOfYear+1)+"/"+Integer.toString(dayOfMonth)+"/"+Integer.toString(year);
            ((MainActivity)getActivity()).forwardDatetoNewClassContentFragment(date);

        }
    }

}
