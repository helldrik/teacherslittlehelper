package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;
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


public class NewClassContentFragment extends Fragment {

    public static final String TAG="AddClassContentFragment";
    private static final String ARG_CLASS_ID = "classId";
    private int mClassId;
    private OnNewClassContentListener mListener;
    View mRootView;



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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView=inflater.inflate(R.layout.fragment_new_class_content, container, false);
        Button btn=(Button)mRootView.findViewById(R.id.newClassContentFragmentNewClassContent);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView date=(TextView)mRootView.findViewById(R.id.newClassContentFragmentnewDate);
                String sDate=date.getText().toString();
                TextView book=(TextView)mRootView.findViewById(R.id.newClassContentFragmentNewBook);
                String sBook=book.getText().toString();
                TextView pages=(TextView)mRootView.findViewById(R.id.newClassContentFragmentNewPages);
                String sPages=pages.getText().toString();
                TextView info=(TextView)mRootView.findViewById(R.id.newClassContentFragmentNewInfo);
                String sInfo=info.getText().toString();

                if(sDate==""){
                    Toast.makeText(getActivity(),getActivity().getResources().getText(R.string.DateNotFilledOutWarning),Toast.LENGTH_LONG).show();
                }
                else {
                    ContentValues vals = new ContentValues(5);
                    vals.put(DbContract.ClassContentEntry.COLUMN_BOOK, sBook);
                    vals.put(DbContract.ClassContentEntry.COLUMN_INFO, sInfo);
                    vals.put(DbContract.ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS, mClassId);
                    vals.put(DbContract.ClassContentEntry.COLUMN_PAGE, sPages);
                    vals.put(DbContract.ClassContentEntry.COLUMN_DATE, sDate);
                    //TODO: hook up to DB
                    ContentResolver resolver=getActivity().getContentResolver();
                    Uri returnUri=resolver.insert(DbContract.ClassContentEntry.CONTENT_URI,vals);
                    int id = Integer.parseInt(returnUri.getLastPathSegment());
                    ClassContentAdapter.ClassContentAdapterValues newVals=new ClassContentAdapter.ClassContentAdapterValues(id,sDate,sBook,sPages,sInfo);
                    mListener.OnNewClassContent(newVals);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    Toast.makeText(getActivity(),getActivity().getResources().getText(R.string.contentAddedAlert),Toast.LENGTH_LONG).show();
                }
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

}
