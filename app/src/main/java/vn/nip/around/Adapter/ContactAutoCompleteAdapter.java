package vn.nip.around.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Bean.BeanContact;
import vn.nip.around.Class.PlaceAPI;
import vn.nip.around.R;

/**
 * Created by viminh on 1/16/2017.
 */

public class ContactAutoCompleteAdapter extends ArrayAdapter<BeanContact> implements Filterable {

    public List<BeanContact> items;
    public List<BeanContact> results = new ArrayList<>();

    Context mContext;
    int mResource;

    public ContactAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);
        items = BeanContact.get(context);
        mContext = context;
        mResource = resource;

    }

    @Override
    public int getCount() {
        // Last item will be the footer
        return results.size();
    }

    @Override
    public BeanContact getItem(int position) {
        try {
            return results.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        try {
            if (null == row) {
                LayoutInflater layout = (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                );
                row = layout.inflate(R.layout.row_contact, null);
            }
            BeanContact bean = results.get(position);
            if (null != bean) {
                TextView phone = (TextView) row.findViewById(R.id.phone);
                TextView name = (TextView) row.findViewById(R.id.name);

                name.setText(bean.getName() + "");
                phone.setText(bean.getPhone1() + "");
            }
        } catch (Exception e) {

        }
        return row;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                try {
                    //List<String> arr = new ArrayList<>();
                    if (constraint != null) {
                       results = new ArrayList<>();
                        for (BeanContact bean : items) {
                            if (bean.getPhone1().contains(constraint)) {
                                results.add(bean);
                            }
                        }
                        filterResults.values = results;
                        filterResults.count = results.size();
                    }
                } catch (Exception e) {

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, final FilterResults results) {

                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}
