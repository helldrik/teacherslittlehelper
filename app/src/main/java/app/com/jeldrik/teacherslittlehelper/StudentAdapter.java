package app.com.jeldrik.teacherslittlehelper;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;

/**
 * Created by jeldrik on 17/02/15.
 */
public class StudentAdapter extends ArrayAdapter {
    private final Context context;
    private final ArrayList<StudentAdapterValues> values;

    public StudentAdapter(Context context, ArrayList<StudentAdapterValues> values) {
        super(context,R.layout.student_item,values);
        this.values=values;
        this.context=context;
    }
/*
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
*/
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.student_item, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.studentListItem_name);
        name.setText(values.get(position).name);

        TextView email = (TextView) rowView.findViewById(R.id.studentListItem_email);
        email.setText(values.get(position).email);

        TextView phone = (TextView) rowView.findViewById(R.id.studentListItem_phone);
        phone.setText(values.get(position).phone);

        Button deleteBtn=(Button)rowView.findViewById(R.id.studentItem_deleteStudentData);
        deleteBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete Student
                ContentResolver resolver=context.getContentResolver();
                Uri uri= DbContract.StudentEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(values.get(position).id)).build();
                Log.v("StudentAdapter", "delete Button pressed");
                if(resolver.delete(uri,null,null)>0)
                    remove(position);
            }
        });
        Button updateBtn=(Button)rowView.findViewById(R.id.studentItem_changeStudentData);
        deleteBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update Student
            }
        });

        return rowView;
    }

    public void remove(int position){
        Log.v("MyAdapter", "removing " + values.get(position));
        values.remove(position);
        this.notifyDataSetChanged();
    }

    //implements Parcelable to be able to store it in Savedinstancestate bundle
    static class StudentAdapterValues implements Parcelable {
        int id;
        String name;
        String email;
        String phone;

        public StudentAdapterValues(int id,String name,String email,String phone){
            this.id=id;
            this.name=name;
            this.email=email;
            this.phone=phone;
        }

        public StudentAdapterValues(Parcel in){
            id=in.readInt();
            name=in.readString();
            email=in.readString();
            phone=in.readString();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(email);
            dest.writeString(phone);

        }
        public static final Creator CREATOR= new Creator(){

            @Override
            public StudentAdapterValues createFromParcel(Parcel source) {
                return null;
            }

            @Override
            public StudentAdapterValues[] newArray(int size) {
                return new StudentAdapterValues[size];
            }
        };
    }
}
