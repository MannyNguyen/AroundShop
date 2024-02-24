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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TryFoundShipperDialogFragment extends DialogFragment {

    public String message = "";
    public ICallback tryAgainCallback;
    public ICallback addLaterCallback;

    public TryFoundShipperDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_try_found_shipper_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TryFoundShipperDialogFragment.this.setCancelable(false);
        Window window = getDialog().getWindow();
        if (window != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        getView().findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TryFoundShipperDialogFragment.this.dismiss();
            }
        });

        TextView messageView = (TextView) getView().findViewById(R.id.message);
        messageView.setText(message);

        int pad = CmmFunc.convertDpToPx(getActivity(), 12);
        Button addLater = (Button) getView().findViewById(R.id.add_later);
        //addLater.setPadding(pad, pad, pad, pad);
        addLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TryFoundShipperDialogFragment.this.dismiss();
                if (addLaterCallback == null) {
                    return;
                }
                addLaterCallback.excute();
            }
        });
        Button tryAgain = (Button) getView().findViewById(R.id.try_again);
        //tryAgain.setPadding(pad, pad, pad, pad);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TryFoundShipperDialogFragment.this.dismiss();
                if (tryAgainCallback == null) {
                    return;
                }
                tryAgainCallback.excute();
            }
        });

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
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
