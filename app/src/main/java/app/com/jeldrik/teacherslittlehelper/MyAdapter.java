package app.com.jeldrik.teacherslittlehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by jeldrik on 29/01/15.
 */
public class MyAdapter extends ArrayAdapter {
    private final Context context;
    private final String[] values;

    public MyAdapter(Context context, String[] values) {
        super(context, R.layout.class_item, values);
        this.context = context;
        this.values = values;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.class_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.classTitle);
        textView.setText(values[position]);

        return rowView;
    }
}
