package vn.nip.around.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import vn.nip.around.Class.PlaceAPI;
import vn.nip.around.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by viminh on 1/16/2017.
 */

public class PlacesAutoCompleteAdapter extends ArrayAdapter<JSONObject> implements Filterable {

    public ArrayList<JSONObject> resultList;

    Context mContext;
    int mResource;

    public PlaceAPI mPlaceAPI = new PlaceAPI();

    public PlacesAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mResource = resource;
    }

    @Override
    public int getCount() {
        // Last item will be the footer
        return resultList.size();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return resultList.get(position);
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
                row = layout.inflate(R.layout.row_autocomplete, null);
            }
            JSONObject result = resultList.get(position);
            if (null != result) {
                TextView description = (TextView) row.findViewById(R.id.description);
                TextView name = (TextView) row.findViewById(R.id.name);
                String address = result.getString("description");
                String n = result.getString("name");
                description.setText(address + "");
                name.setText(n + "");
            }
        } catch (Exception e) {

        }
        return row;
    }

//    @Override
//    public Filter getFilter() {
//        Filter filter = new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//
//                FilterResults filterResults = new FilterResults();
//                try {
//                    //List<String> arr = new ArrayList<>();
//                    if (constraint != null) {
//                        resultList = mPlaceAPI.autocomplete(constraint.toString());
//
////                        for (JSONObject item : resultList) {
////
////                            arr.add(item.getString("description"));
////
////                        }
//                        filterResults.values = resultList;
//                        filterResults.count = resultList.size();
//                    }
//                } catch (Exception e) {
//
//                }
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, final FilterResults results) {
//
//                if (results != null && results.count > 0) {
//                    notifyDataSetChanged();
//                } else {
//                    notifyDataSetInvalidated();
//                }
//            }
//        };
//
//        return filter;
//    }
}
