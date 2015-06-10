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

import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jeldrik on 29/01/15.
 */
public class MyAdapter extends ArrayAdapter {
    private final Context context;
    private final ArrayList<ClassAdapterValues> values;

    public MyAdapter(Context context, ArrayList<ClassAdapterValues> values) {
        super(context, R.layout.class_item, values);
        this.context = context;
        this.values = values;
        //sorting the classes by time
        Collections.sort(values,new ClassAdapterValuesTimeComparator());
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.class_item, parent, false);
        TextView title = (TextView) rowView.findViewById(R.id.classTitle);
        title.setText(values.get(position).title);

        TextView startTime = (TextView) rowView.findViewById(R.id.classStartTime);
        startTime.setText(values.get(position).startTime);

        TextView endTime = (TextView) rowView.findViewById(R.id.classEndTime);
        endTime.setText(values.get(position).endTime);
        //Log.v("SFSF","THE ENDTIME: "+values.get(position).endTime);

        return rowView;
    }

    public void remove(int position){

        values.remove(position);
        this.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(values,new ClassAdapterValuesTimeComparator());
        super.notifyDataSetChanged();
    }

    //implements Parcelable to be able to store it in Savedinstancestate bundle
    static class ClassAdapterValues implements Parcelable{
        String title;
        String startTime;
        String endTime;
        int _id;

        public ClassAdapterValues(String title,String startTime,String endTime,int _id){
            this._id=_id;
            this.startTime=startTime;
            this.endTime=endTime;
            this.title=title;
        }

        private ClassAdapterValues(Parcel in){
            title=in.readString();
            startTime=in.readString();
            endTime=in.readString();
            _id=in.readInt();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(startTime);
            dest.writeString(endTime);
            dest.writeInt(_id);

        }
        public static final Parcelable.Creator<ClassAdapterValues> CREATOR
                = new Parcelable.Creator<ClassAdapterValues>() {
            public ClassAdapterValues createFromParcel(Parcel in) {
                return new ClassAdapterValues(in);
            }

            public ClassAdapterValues[] newArray(int size) {
                return new ClassAdapterValues[size];
            }
        };
    }
    //Custom ComparatorClass to sort classes by time
    class ClassAdapterValuesTimeComparator implements Comparator<ClassAdapterValues> {
        @Override
        public int compare(ClassAdapterValues lhs, ClassAdapterValues rhs) {
            int hour1=Integer.parseInt(lhs.startTime.substring(0,2));
            int hour2=Integer.parseInt(rhs.startTime.substring(0,2));
            return hour1-hour2;
        }
    }
}
