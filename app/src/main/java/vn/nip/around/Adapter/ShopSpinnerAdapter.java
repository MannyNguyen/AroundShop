package vn.nip.around.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import vn.nip.around.Bean.BeanShop;
import vn.nip.around.R;

/**
 * Created by HOME on 11/6/2017.
 */

public class ShopSpinnerAdapter extends ArrayAdapter<BeanShop> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (BeanShop)
    private List<BeanShop> shops;

    int viewID;

    public ShopSpinnerAdapter(Context context, int viewID,
                              List<BeanShop> shops) {
        super(context, viewID, shops);
        this.context = context;
        this.shops = shops;
        this.viewID = viewID;
    }

    @Override
    public int getCount() {
        return shops.size();
    }

    @Override
    public BeanShop getItem(int position) {
        return shops.get(position);
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
            name.setText(BeanShop.class.getDeclaredField(context.getString(R.string.key_name)).get(shops.get(position)) + " (" + shops.get(position).getNumber() + ")");
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
            name.setText(BeanShop.class.getDeclaredField(context.getString(R.string.key_name)).get(shops.get(position)) + " (" + shops.get(position).getNumber() + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}