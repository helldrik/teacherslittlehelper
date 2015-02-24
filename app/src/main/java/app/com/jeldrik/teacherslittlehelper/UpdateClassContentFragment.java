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

public class UpdateClassContentFragment extends Fragment {

    public static final String TAG="UpdateClassContentFragment";

    private static final String ARG_DATE = "date";
    private static final String ARG_PAGES = "pages";
    private static final String ARG_BOOK = "book";
    private static final String ARG_INFO = "info";
    private static final String ARG_ID = "classId";

    private String mSDate;
    private String mSBook;
    private String mSPages;
    private String mSInfo;
    private int mId;

    private View mRootView;
    private TextView mDate, mBook, mPages,mInfo;

    private OnFragmentInteractionListener mListener;

    public static UpdateClassContentFragment newInstance(String date, String book, String pages, String info, int id) {
        UpdateClassContentFragment fragment = new UpdateClassContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE,date);
        args.putString(ARG_PAGES, pages);
        args.putString(ARG_BOOK, book);
        args.putString(ARG_INFO, info);
        args.putInt(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateClassContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSDate = getArguments().getString(ARG_DATE);
            mSBook = getArguments().getString(ARG_BOOK);
            mSPages = getArguments().getString(ARG_PAGES);
            mSInfo = getArguments().getString(ARG_INFO);
            mId = getArguments().getInt(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_update_class_content, container, false);
        mDate=(TextView) mRootView.findViewById(R.id.updateClassContentFragmentnewDate);
        mDate.setText(mSDate);
        mBook=(TextView) mRootView.findViewById(R.id.updateClassContentFragmentNewBook);
        mBook.setText(mSBook);
        mPages=(TextView) mRootView.findViewById(R.id.updateClassContentFragmentNewPages);
        mPages.setText(mSPages);
        mInfo=(TextView) mRootView.findViewById(R.id.updateClassContentFragmentNewInfo);
        mInfo.setText(mSInfo);

        Button updateBtn=(Button)mRootView.findViewById(R.id.updateClassContentFragmentUpdate);
        updateBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSDate=mDate.getText().toString();
                mSBook=mBook.getText().toString();
                mSPages=mPages.getText().toString();
                mSInfo=mInfo.getText().toString();

                ContentValues vals=new ContentValues(4);
                vals.put(DbContract.ClassContentEntry.COLUMN_BOOK, mSBook);
                vals.put(DbContract.ClassContentEntry.COLUMN_INFO, mSInfo);
                vals.put(DbContract.ClassContentEntry.COLUMN_PAGE, mSPages);
                vals.put(DbContract.ClassContentEntry.COLUMN_DATE, mSDate);

                Uri uri= DbContract.ClassContentEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mId)).build();
                ContentResolver resolver=getActivity().getContentResolver();
                if(resolver.update(uri,vals, DbContract.ClassContentEntry._ID+" = ?",new String[]{Integer.toString(mId)})>0){
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.contentUpdatedAlert), Toast.LENGTH_LONG).show();
                }
            }
        });

        Button deleteBtn=(Button)mRootView.findViewById(R.id.updateClassContentFragmentDelete);
        deleteBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= DbContract.ClassContentEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mId)).build();
                ContentResolver resolver=getActivity().getContentResolver();
                if(resolver.delete(uri, DbContract.ClassContentEntry._ID+" = ?",new String[]{Integer.toString(mId)})>0){
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.contentDeletedAlert), Toast.LENGTH_LONG).show();
                }
            }
        });

        return mRootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
