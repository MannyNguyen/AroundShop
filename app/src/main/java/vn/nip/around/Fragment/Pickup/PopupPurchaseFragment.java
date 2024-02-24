package vn.nip.around.Fragment.Pickup;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;

import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Dialog.BaseDialogFragment;
import vn.nip.around.Fragment.Dialog.TryFoundShipperDialogFragment;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopupPurchaseFragment extends BaseFragment implements View.OnClickListener {

    EditText itemName, itemCost, note;
    Button confirm;
    HomeBookFragment homeBookFragment;
    BeanPoint beanPoint;
    TextView errorMessage;

    public PopupPurchaseFragment() {
        // Required empty public constructor
    }

    public static PopupPurchaseFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        PopupPurchaseFragment fragment = new PopupPurchaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_popup_purchase, container, false);

        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                itemName = (EditText) view.findViewById(R.id.item_name);
                itemCost = (EditText) view.findViewById(R.id.item_cost);
                note = (EditText) view.findViewById(R.id.note);
                errorMessage = (TextView) view.findViewById(R.id.error_message);
                confirm = (Button) view.findViewById(R.id.confirm);

                itemName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        itemName.setCompoundDrawables(null, null, null, null);
                    }
                });

                itemCost.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        itemCost.setCompoundDrawables(null, null, null, null);
                        errorMessage.setVisibility(View.GONE);
                    }
                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        homeBookFragment = (HomeBookFragment) CmmFunc.getFragmentByTag(getActivity(), HomeBookFragment.class.getName());
                        if (homeBookFragment != null) {
                            beanPoint = homeBookFragment.points.get(getArguments().getInt("position"));
                            itemName.setText(beanPoint.getItem_name());
                            note.setText(beanPoint.getNote());
                            if (beanPoint.getItem_cost() == 0) {
                                itemCost.setText("");
                            } else {
                                itemCost.setText(beanPoint.getItem_cost() + "");
                            }

                            confirm.setOnClickListener(PopupPurchaseFragment.this);
                            view.findViewById(R.id.close).setOnClickListener(PopupPurchaseFragment.this);
                        }
                    }
                });
            }
        });
        threadInit.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                ((DialogFragment) getParentFragment()).dismissAllowingStateLoss();
                break;
            case R.id.confirm:
                Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                String itemNameText = itemName.getText().toString().trim();
                if (itemNameText.equals(StringUtils.EMPTY)) {
                    itemName.setCompoundDrawables(null, null, icError, null);
                    itemName.requestFocus();
                    return;
                }

                if (!itemCost.getText().toString().trim().equals("")) {
                    int cost = Integer.parseInt(itemCost.getText().toString().replace(",", "").trim());
                    if (cost < CmmVariable.minPurchaseFee) {
                        itemCost.setCompoundDrawables(null, null, icError, null);
                        itemCost.requestFocus();
                        errorMessage.setText(getString(R.string.min_purchase, CmmFunc.formatMoney(CmmVariable.minPurchaseFee, false)));
                        errorMessage.setVisibility(View.VISIBLE);
                        return;
                    }
                    beanPoint.setItem_cost(cost);
                } else {
                    itemCost.setCompoundDrawables(null, null, icError, null);
                    itemCost.requestFocus();
                    errorMessage.setText(getString(R.string.min_purchase, CmmFunc.formatMoney(CmmVariable.minPurchaseFee, false)));
                    errorMessage.setVisibility(View.VISIBLE);
                    return;
                }
                beanPoint.setPickup_type(BeanPoint.PURCHASE);
                beanPoint.setItem_name(itemNameText);
                beanPoint.setNote(note.getText().toString().trim());
                beanPoint.setRecipent_name(StringUtils.EMPTY);
                beanPoint.setPhone(StringUtils.EMPTY);
                homeBookFragment.points.set(getArguments().getInt("position"), beanPoint);
                ((DialogFragment) getParentFragment()).dismissAllowingStateLoss();
                homeBookFragment.adapter.notifyItemChanged(getArguments().getInt("position"));
                break;
        }
    }
}
