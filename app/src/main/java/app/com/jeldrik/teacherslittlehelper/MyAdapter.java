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
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.class_item, parent, false);
        TextView title = (TextView) rowView.findViewById(R.id.classTitle);
        title.setText(values.get(position).title);

        TextView startTime = (TextView) rowView.findViewById(R.id.classTime);
        startTime.setText(values.get(position).time);

        return rowView;
    }

    public void remove(int position){
        Log.v("MyAdapter","removing "+values.get(position));
        values.remove(position);
        this.notifyDataSetChanged();
    }

    //implements Parcelable to be able to store it in Savedinstancestate bundle
    static class ClassAdapterValues implements Parcelable{
        String title;
        String time;
        int _id;

        public ClassAdapterValues(String title,String time,int _id){
            this._id=_id;
            this.time=time;
            this.title=title;
        }

        public ClassAdapterValues(Parcel in){
            title=in.readString();
            time=in.readString();
            _id=in.readInt();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(time);
            dest.writeInt(_id);

        }
        public static final Creator CREATOR= new Creator(){

            @Override
            public ClassAdapterValues createFromParcel(Parcel source) {
                return null;
            }

            @Override
            public ClassAdapterValues[] newArray(int size) {
                return new ClassAdapterValues[size];
            }
        };
    }
}
