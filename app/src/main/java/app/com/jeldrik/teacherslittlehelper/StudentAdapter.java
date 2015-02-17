package app.com.jeldrik.teacherslittlehelper;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.student_item, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.studentListItem_name);
        name.setText(values.get(position).name);

        TextView email = (TextView) rowView.findViewById(R.id.studentListItem_email);
        email.setText(values.get(position).email);

        TextView phone = (TextView) rowView.findViewById(R.id.studentListItem_phone);
        phone.setText(values.get(position).phone);

        return rowView;
    }

    public void remove(int position){
        Log.v("MyAdapter", "removing " + values.get(position));
        values.remove(position);
        this.notifyDataSetChanged();
    }

    //implements Parcelable to be able to store it in Savedinstancestate bundle
    static class StudentAdapterValues implements Parcelable {
        String name;
        String email;
        String phone;

        public StudentAdapterValues(String name,String email,String phone){
            this.name=name;
            this.email=email;
            this.phone=phone;
        }

        public StudentAdapterValues(Parcel in){
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
