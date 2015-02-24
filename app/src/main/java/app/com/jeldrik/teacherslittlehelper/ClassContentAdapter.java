package app.com.jeldrik.teacherslittlehelper;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jeldrik on 21/02/15.
 */
public class ClassContentAdapter extends ArrayAdapter {
    private final Context context;
    private final ArrayList<ClassContentAdapterValues> values;

    public ClassContentAdapter(Context context,ArrayList<ClassContentAdapterValues> values) {
        super(context,R.layout.class_content_item, values);
        this.values=values;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.class_content_item, parent, false);
        TextView date=(TextView)rowView.findViewById(R.id.classContentItemDate);
        date.setText(values.get(position).date);
        TextView book=(TextView)rowView.findViewById(R.id.classContentItemBook);
        book.setText(values.get(position).book);
        TextView pages=(TextView)rowView.findViewById(R.id.classContentItemPages);
        pages.setText(values.get(position).pages);
        TextView info=(TextView)rowView.findViewById(R.id.classContentItemInfo);
        info.setText(values.get(position).info);

        if (info.getLineCount()>6)
            info.setMaxLines(6);

        return rowView;
    }

    public void remove(int position){
        Log.v("MyAdapter", "removing " + values.get(position));
        values.remove(position);
        this.notifyDataSetChanged();
    }

    //implements Parcelable to be able to store it in Savedinstancestate bundle
    static class ClassContentAdapterValues implements Parcelable {
        int id;
        String book;
        String pages;
        String info;
        String date;

        public ClassContentAdapterValues(int id,String date,String book,String pages,String info){
            this.id=id;
            this.date=date;
            this.book=book;
            this.pages=pages;
            this.info=info;
        }

        public ClassContentAdapterValues(Parcel in){
            id=in.readInt();
            date=in.readString();
            book=in.readString();
            pages=in.readString();
            info=in.readString();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(date);
            dest.writeString(book);
            dest.writeString(pages);
            dest.writeString(info);

        }
        public static final Creator CREATOR= new Creator(){

            @Override
            public ClassContentAdapterValues createFromParcel(Parcel source) {
                return null;
            }

            @Override
            public ClassContentAdapterValues[] newArray(int size) {
                return new ClassContentAdapterValues[size];
            }
        };
    }
}
