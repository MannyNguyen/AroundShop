package vn.nip.around.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Common.AroundPayWebviewFragment;
import vn.nip.around.Fragment.Dialog.AroundPayDialogFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class AroundPayMethodAdapter extends RecyclerView.Adapter<AroundPayMethodAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    AroundPayDialogFragment fragment;
    private List<String> list;
    int fee;


    public AroundPayMethodAdapter(FragmentActivity activity, AroundPayDialogFragment fragment, RecyclerView recycler, int fee) {
        this.activity = activity;
        this.fragment = fragment;
        this.recycler = recycler;
        this.fee = fee;
        this.list = new ArrayList<>();
        for (int i = 0; i < CmmVariable.onepayTypes.length(); i++) {
            try {
                list.add(i, CmmVariable.onepayTypes.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_payment, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        String item = list.get(position);
        holder.name.setText(item + "");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            int itemPosition = recycler.getChildLayoutPosition(view);
            String type = list.get(itemPosition);
            fragment.dismissAllowingStateLoss();
            Fragment fragment = AroundPayWebviewFragment.newInstance(type, fee);
            FragmentHelper.addFragment(activity, R.id.home_content, fragment);

        } catch (Exception e) {

        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MenuViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
        }
    }
}