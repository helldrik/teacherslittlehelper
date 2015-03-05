package app.com.jeldrik.teacherslittlehelper;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jeldrik on 04/03/15.
 */
//---------------------------------------------------------------------------------------------
//Adapter for StudentAttendance
//---------------------------------------------------------------------------------------------
public class AttendingStudentsAdapter extends ArrayAdapter {
    ArrayList<AttendingStudentsAdapterValues> mVals;
    private final Context context;

    public AttendingStudentsAdapter(Context context,ArrayList<AttendingStudentsAdapterValues> values) {
        super(context,R.layout.student_attendance_item,values);
        this.mVals=values;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.student_attendance_item, parent, false);
        TextView name=(TextView)rowView.findViewById(R.id.attendingStudentName);
        name.setText(mVals.get(position).name);

        final int listPos=position;

        Spinner spinner=(Spinner)rowView.findViewById(R.id.attendanceSpinner);

        ArrayAdapter<CharSequence>attendanceAdapter= ArrayAdapter.createFromResource(context,R.array.attendance,android.R.layout.simple_spinner_item);
        attendanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(attendanceAdapter);
        for(int i=0;i<spinner.getCount();i++) {
            String spinnerVal=(String)spinner.getItemAtPosition(i);
            if (spinnerVal.contains(mVals.get(position).status)) {
                spinner.setSelection(i);
                break;
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mVals.set(listPos,new AttendingStudentsAdapterValues(mVals.get(listPos).id,mVals.get(listPos).name,parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rowView;
    }
    //implements Parcelable to be able to store it in Savedinstancestate bundle
    static class AttendingStudentsAdapterValues implements Parcelable {
        int id;
        String name;
        String status;

        public AttendingStudentsAdapterValues(int id,String name,String status){
            this.id=id;
            this.name=name;
            this.status=status;
        }

        private AttendingStudentsAdapterValues(Parcel in){
            id=in.readInt();
            name=in.readString();
            status=in.readString();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeString(status);
        }
        public static final Parcelable.Creator<AttendingStudentsAdapterValues> CREATOR
                = new Parcelable.Creator<AttendingStudentsAdapterValues>() {
            public AttendingStudentsAdapterValues createFromParcel(Parcel in) {
                return new AttendingStudentsAdapterValues(in);
            }

            public AttendingStudentsAdapterValues[] newArray(int size) {
                return new AttendingStudentsAdapterValues[size];
            }
        };
    }
}
