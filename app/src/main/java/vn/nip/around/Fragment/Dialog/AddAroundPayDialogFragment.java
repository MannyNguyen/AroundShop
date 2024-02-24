package vn.nip.around.Fragment.Dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import vn.nip.around.AppActivity;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.AroundWalletFragment;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Pickup.Confirm.ConfirmFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAroundPayDialogFragment extends DialogFragment implements View.OnClickListener {

    public String message;

    public AddAroundPayDialogFragment() {
        // Required empty public constructor
    }

    public static AddAroundPayDialogFragment newInstance(int fee) {

        Bundle args = new Bundle();
        args.putInt("fee", fee);
        AddAroundPayDialogFragment fragment = new AddAroundPayDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_aroundpay_dialog, container, false);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            Window window = getDialog().getWindow();
            if (window != null) {
                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            }
            TextView messageText = (TextView) getView().findViewById(R.id.message);
            messageText.setText(message);

            getView().findViewById(R.id.cancel).setOnClickListener(AddAroundPayDialogFragment.this);
            getView().findViewById(R.id.confirm).setOnClickListener(AddAroundPayDialogFragment.this);
            setCancelable(false);
            if (AppActivity.FLOW == AppActivity.PICKUP) {
                ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(getActivity(), ConfirmFragment.class.getName());
                fragment.hideProgress();
            } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(getActivity(), CartFragment.class.getName());
                fragment.hideProgress();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                try {
                    this.dismissAllowingStateLoss();
                    if (AppActivity.FLOW == AppActivity.PICKUP) {
                        if (CmmFunc.getFragmentByTag(getActivity(), ConfirmFragment.class.getName()) != null) {
                            getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                        } else{
                            FragmentHelper.pop(getActivity());
                        }
                    } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                        if (CmmFunc.getFragmentByTag(getActivity(), CartFragment.class.getName()) != null) {
                            getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                        } else{
                            FragmentHelper.pop(getActivity());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.confirm:
                try {
                    this.dismissAllowingStateLoss();
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
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, new AroundWalletFragment());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
