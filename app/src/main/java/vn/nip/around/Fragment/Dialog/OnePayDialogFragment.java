package vn.nip.around.Fragment.Dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import vn.nip.around.Adapter.OnePayAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Pickup.Confirm.ConfirmFragment;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnePayDialogFragment extends DialogFragment implements View.OnClickListener {


    public OnePayDialogFragment() {
        // Required empty public constructor
    }

    public static OnePayDialogFragment newInstance(int orderID) {

        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        OnePayDialogFragment fragment = new OnePayDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onepay_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        getView().findViewById(R.id.cancel).setOnClickListener(OnePayDialogFragment.this);
        setCancelable(false);
        RecyclerView recycler = (RecyclerView) getView().findViewById(R.id.recycler);
        OnePayAdapter adapter = new OnePayAdapter(getActivity(), this, recycler, getArguments().getInt("order_id"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        if (AppActivity.FLOW == AppActivity.GIFTING) {
            CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(getActivity(), CartFragment.class.getName());
            if (fragment != null) {
                fragment.hideProgress();
            }
        } else if (AppActivity.FLOW == AppActivity.PICKUP) {
            ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(getActivity(), ConfirmFragment.class.getName());
            if (fragment != null) {
                fragment.hideProgress();
            }
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (AppActivity.FLOW == AppActivity.GIFTING) {
                            CartFragment cartFragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                            cartFragment.orderID = 0;
                            cartFragment.hideProgress();
                            getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                        } else if (AppActivity.FLOW == AppActivity.PICKUP) {
                            ConfirmFragment fullOrderFragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                            fullOrderFragment.orderID = 0;
                            fullOrderFragment.hideProgress();
                            getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                        }
                    }
                });
                this.dismissAllowingStateLoss();
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
