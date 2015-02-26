package app.com.jeldrik.teacherslittlehelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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



    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.student_item, parent, false);

/*
        rowView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                mParent.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
*/
        TextView name = (TextView) rowView.findViewById(R.id.studentListItem_name);
        name.setText(values.get(position).name);

        TextView email = (TextView) rowView.findViewById(R.id.studentListItem_email);
        email.setText(values.get(position).email);
        email.setLongClickable(true);
        email.setOnLongClickListener(new TextView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",values.get(position).email,null));
                context.startActivity(emailIntent);
                return true;
            }
        });

        TextView phone = (TextView) rowView.findViewById(R.id.studentListItem_phone);
        phone.setText(values.get(position).phone);
        phone.setLongClickable(true);
        phone.setOnLongClickListener(new TextView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+values.get(position).phone));
                context.startActivity(callIntent);
                return true;
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

        private StudentAdapterValues(Parcel in){
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
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeString(email);
            dest.writeString(phone);

        }
        public static final Parcelable.Creator<StudentAdapterValues> CREATOR
                = new Parcelable.Creator<StudentAdapterValues>() {
            public StudentAdapterValues createFromParcel(Parcel in) {
                return new StudentAdapterValues(in);
            }

            public StudentAdapterValues[] newArray(int size) {
                return new StudentAdapterValues[size];
            }
        };
    }
}
