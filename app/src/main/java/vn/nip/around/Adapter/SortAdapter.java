package vn.nip.around.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import vn.nip.around.Bean.BeanSort;
import vn.nip.around.R;

/**
 * Created by HOME on 11/6/2017.
 */

public class SortAdapter extends ArrayAdapter<BeanSort> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (BeanSort)
    private List<BeanSort> sorts;

    int viewID;

    public SortAdapter(Context context, int viewID,
                       List<BeanSort> sorts) {
        super(context, viewID, sorts);
        this.context = context;
        this.sorts = sorts;
        this.viewID = viewID;
    }

    @Override
    public int getCount() {
        return sorts.size();
    }

    @Override
    public BeanSort getItem(int position) {
        return sorts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layout = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = layout.inflate(viewID, null);
        try {
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(BeanSort.class.getDeclaredField(context.getString(R.string.key_name)).get(sorts.get(position)) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        LayoutInflater layout = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = layout.inflate(viewID, null);
        try {
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(BeanSort.class.getDeclaredField(context.getString(R.string.key_name)).get(sorts.get(position)) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}