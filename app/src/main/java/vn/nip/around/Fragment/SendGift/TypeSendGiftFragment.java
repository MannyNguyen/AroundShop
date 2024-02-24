package vn.nip.around.Fragment.SendGift;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.nip.around.Bean.BeanSendGift;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Dialog.BaseDialogFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Fragment.Gift.GiftingHomeFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TypeSendGiftFragment extends BaseDialogFragment implements View.OnClickListener {


    public TypeSendGiftFragment() {
        // Required empty public constructor
    }

    public static TypeSendGiftFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        TypeSendGiftFragment fragment = new TypeSendGiftFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_type_send_gift, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            getView().findViewById(R.id.around).setOnClickListener(TypeSendGiftFragment.this);
            getView().findViewById(R.id.around_box).setOnClickListener(TypeSendGiftFragment.this);
            getView().findViewById(R.id.voucher).setOnClickListener(TypeSendGiftFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        dismissAllowingStateLoss();
        switch (v.getId()) {
            case R.id.around:
                try {
                    BeanSendGift param = (BeanSendGift) CmmFunc.tryParseJson(getArguments().getString("data"), BeanSendGift.class);
                    CartFragment.RECEIVER = param;
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, GiftingHomeFragment.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.around_box:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, SendGiftAroundPayFragment.newInstance(getArguments().getString("data")));
                break;

            case R.id.voucher:
                MessageDialogFragment messageDialog = MessageDialogFragment.newInstance();
                messageDialog.setMessage(getString(R.string.comming_soon));
                messageDialog.show(getActivity().getSupportFragmentManager(), messageDialog.getClass().getName());
                break;
        }
    }
}
