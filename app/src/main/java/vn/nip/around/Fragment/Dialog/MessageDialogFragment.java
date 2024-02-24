package vn.nip.around.Fragment.Dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageDialogFragment extends BaseDialogFragment {

    public static final String FOLLOW_ORDER_STATUS = "FOLLOW_ORDER_STATUS";
    private String message;
    private ICallback callback;
    private ICallback cancelCallback;
    private String name;
    private boolean outToHide;
    private String titleOk;

    public MessageDialogFragment() {
        // Required empty public constructor
    }

    public static MessageDialogFragment newInstance() {

        Bundle args = new Bundle();

        MessageDialogFragment fragment = new MessageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_dialog, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.setCancelable(isOutToHide());
        Window window = getDialog().getWindow();
        if (window != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        TextView messageView = (TextView) view.findViewById(R.id.message);
        messageView.setText(getMessage());

        Button ok = (Button) view.findViewById(R.id.confirm);
        if (getTitleOk()!=null && !getTitleOk().equals("")) {
            ok.setText(getTitleOk());
        }
        //addLater.setPadding(pad, pad, pad, pad);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageDialogFragment.this.dismiss();
                if (getCallback() == null) {
                    return;
                }
                getCallback().excute();
            }
        });
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ICallback getCallback() {
        return callback;
    }

    public void setCallback(ICallback callback) {
        this.callback = callback;
    }

    public static boolean isExist(String name) {
        Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
        if (fragment instanceof MessageDialogFragment) {
            if (((MessageDialogFragment) fragment).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isOutToHide() {
        return outToHide;
    }

    public void setOutToHide(boolean outToHide) {
        this.outToHide = outToHide;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getCancelCallback() != null) {
            getCancelCallback().excute();
        }
    }

    public ICallback getCancelCallback() {
        return cancelCallback;
    }

    public void setCancelCallback(ICallback cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    public String getTitleOk() {
        return titleOk;
    }

    public void setTitleOk(String titleOk) {
        this.titleOk = titleOk;
    }
}
