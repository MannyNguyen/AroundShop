package vn.nip.around.Fragment.Pickup;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PickupFragment extends BaseFragment implements View.OnClickListener {

    EditText itemName;
    EditText itemCost;
    EditText recipentName;
    EditText recipentPhone;
    EditText note;
    BookFragment bookFragment;
    View areaError;
    View containerCost;
    TextView errorMessage;
    TextView codMessage;

    public PickupFragment() {
        // Required empty public constructor
    }

    public static PickupFragment newInstance( int position, boolean isDrop) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("type", 0);
        args.putBoolean("is_drop", isDrop);
        PickupFragment fragment = new PickupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pickup, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.close).setOnClickListener(PickupFragment.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int position = getArguments().getInt("position");
                getView().findViewById(R.id.content).setOnClickListener(PickupFragment.this);
                bookFragment = (BookFragment) CmmFunc.getFragmentByTag(getActivity(), BookFragment.class.getName());
                itemName = (EditText) getView().findViewById(R.id.item_name);
                itemCost = (EditText) getView().findViewById(R.id.item_cost);
                recipentName = (EditText) getView().findViewById(R.id.recipent_name);
                recipentPhone = (EditText) getView().findViewById(R.id.recipent_phone);
                note = (EditText) getView().findViewById(R.id.note);
                areaError = getView().findViewById(R.id.area_error);
                containerCost = getView().findViewById(R.id.container_cost);
                errorMessage = (TextView) getView().findViewById(R.id.error_message);
                codMessage = (TextView) getView().findViewById(R.id.cod_message);

                getView().findViewById(R.id.confirm).setOnClickListener(PickupFragment.this);
                getView().findViewById(R.id.scroll_view).setOnClickListener(PickupFragment.this);
                getView().findViewById(R.id.child_content).setOnClickListener(PickupFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemName.setText(bookFragment.points.get(position).getItem_name() + "");
                        itemCost.setText(bookFragment.points.get(position).getItem_cost() + "");
                        if (bookFragment.points.get(position).getItem_cost() == 0) {
                            itemCost.setText("");
                        }
                        recipentName.setText(bookFragment.points.get(position).getRecipent_name() + "");
                        recipentPhone.setText(bookFragment.points.get(position).getPhone() + "");
                        note.setText(bookFragment.points.get(position).getNote() + "");


                        itemName.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                itemName.setCompoundDrawables(null, null, null, null);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        itemName.removeTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                itemName.setCompoundDrawables(null, null, null, null);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        itemCost.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                areaError.setVisibility(View.GONE);
                                itemCost.setCompoundDrawables(null, null, null, null);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        itemCost.removeTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                areaError.setVisibility(View.GONE);
                                itemCost.setCompoundDrawables(null, null, null, null);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });


                        recipentPhone.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                recipentPhone.setCompoundDrawables(null, null, null, null);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        recipentPhone.removeTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                recipentPhone.setCompoundDrawables(null, null, null, null);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        recipentName.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                recipentName.setCompoundDrawables(null, null, null, null);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        recipentName.removeTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                recipentName.setCompoundDrawables(null, null, null, null);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        BeanPoint bean = bookFragment.points.get(position);
                        if (getArguments().getBoolean("is_drop")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView title = (TextView) view.findViewById(R.id.title);
                                    title.setText(getString(R.string.delivery_info_drop));
                                    TextView recipentName = (TextView) view.findViewById(R.id.recipent_name_text);
                                    recipentName.setText(getString(R.string.recipient_name));
                                    TextView recipentPhone = (TextView) view.findViewById(R.id.recipent_phone_text);
                                    recipentPhone.setText(getString(R.string.recipent_phone));
                                    TextView message = (TextView) view.findViewById(R.id.message);
                                    message.setText(getString(R.string.message_delivery_info_drop));
                                    view.findViewById(R.id.container_item_name).setVisibility(View.GONE);
                                    containerCost.setVisibility(View.GONE);
                                    view.findViewById(R.id.require_name).setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.require_phone).setVisibility(View.VISIBLE);


                                }
                            });
                        }

                    }
                });
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                getActivity().getSupportFragmentManager().popBackStackImmediate(BookFragment.class.getName(), 0);
                CmmFunc.hideKeyboard(getActivity());
                break;
            case R.id.content:
            case R.id.scroll_view:
            case R.id.child_content:
                View current = getActivity().getCurrentFocus();
                if (current != null) current.clearFocus();
                CmmFunc.hideKeyboard(getActivity());
                break;
            case R.id.confirm:
                confirm();
                break;

        }
    }

    private void confirm() {
        try {
            final int position = getArguments().getInt("position");
            Tracking.excute("C5.3Y");
            Drawable icError = getResources().getDrawable(R.drawable.ic_error);
            icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
            if (getArguments().getBoolean("is_drop")) {
                recipentName.setCompoundDrawables(null, null, null, null);
                if (recipentName.getText().toString().trim().equals("")) {
                    recipentName.setCompoundDrawables(null, null, icError, null);
                    recipentName.requestFocus();
                    return;
                }
                recipentPhone.setCompoundDrawables(null, null, null, null);
                if (recipentPhone.getText().toString().length() < 10) {
                    recipentPhone.setCompoundDrawables(null, null, icError, null);
                    recipentPhone.requestFocus();
                    return;
                }
            } else {
                itemName.setCompoundDrawables(null, null, null, null);
                if (itemName.getText().toString().trim().equals("")) {
                    itemName.setCompoundDrawables(null, null, icError, null);
                    itemName.requestFocus();
                    return;
                }

                itemCost.setCompoundDrawables(null, null, null, null);
                switch (getArguments().getInt("type")) {
                    case BeanPoint.PURCHASE:
                        if (!itemCost.getText().toString().trim().equals("")) {
                            int cost = Integer.parseInt(itemCost.getText().toString().replace(",", "").trim());
                            if (cost < CmmVariable.minPurchaseFee) {
                                itemCost.setCompoundDrawables(null, null, icError, null);
                                itemCost.requestFocus();
                                errorMessage.setText(getString(R.string.min_purchase, CmmFunc.formatMoney(CmmVariable.minPurchaseFee, false)));
                                areaError.setVisibility(View.VISIBLE);
                                return;
                            }
                            bookFragment.points.get(position).setItem_cost(cost);
                        } else {
                            itemCost.setCompoundDrawables(null, null, icError, null);
                            itemCost.requestFocus();
                            errorMessage.setText(getString(R.string.min_purchase, CmmFunc.formatMoney(CmmVariable.minPurchaseFee, false)));
                            areaError.setVisibility(View.VISIBLE);
                            return;
                        }
                        break;

                    case BeanPoint.COD:
                        if (!itemCost.getText().toString().trim().equals("")) {
                            int cost = Integer.parseInt(itemCost.getText().toString().replace(",", "").trim());
                            if (cost > CmmVariable.maxCodFee || cost < CmmVariable.minCodeFee) {
                                itemCost.setCompoundDrawables(null, null, icError, null);
                                itemCost.requestFocus();
                                errorMessage.setText(getString(R.string.max_cod_fee, CmmFunc.formatMoney(CmmVariable.minCodeFee, false), CmmFunc.formatMoney(CmmVariable.maxCodFee, false)));
                                areaError.setVisibility(View.VISIBLE);
                                return;
                            }
                            bookFragment.points.get(position).setItem_cost(cost);
                        } else {
                            itemCost.setCompoundDrawables(null, null, icError, null);
                            itemCost.requestFocus();
                            errorMessage.setText(getString(R.string.max_cod_fee, CmmFunc.formatMoney(CmmVariable.minCodeFee, false), CmmFunc.formatMoney(CmmVariable.maxCodFee, false)));
                            areaError.setVisibility(View.VISIBLE);
                            return;
                        }
                        break;
                    case BeanPoint.TRANSPORT:
                        bookFragment.points.get(position).setItem_cost(0);
                        break;
                }
            }


            bookFragment.points.get(position).setPickup_type(getArguments().getInt("type"));
            bookFragment.points.get(position).setItem_name(itemName.getText().toString());
            bookFragment.points.get(position).setRecipent_name(recipentName.getText().toString());
            bookFragment.points.get(position).setPhone(recipentPhone.getText().toString());
            bookFragment.points.get(position).setNote(note.getText().toString());
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            CmmFunc.hideKeyboard(getActivity());
            FragmentHelper.pop(getActivity());
            bookFragment.startLocationUpdates();
            //bookFragment.recycler.getAdapter().notifyDataSetChanged();
            CmmFunc.setDelay(400, new ICallback() {
                @Override
                public void excute() {
                    if (bookFragment.checkConditionNext()) {
                        bookFragment.nextShow();
                    } else {
                        bookFragment.nextHide();
                    }
                    bookFragment.barBottom.getChildAt(position).findViewById(R.id.is_full_info).setVisibility(View.GONE);
                    if (bookFragment.points.get(bookFragment.points.size() - 1).getAddress().equals("")) {
                        View v = bookFragment.barBottom.getChildAt(bookFragment.barBottom.getChildCount() - 1);
                        View address = v.findViewById(R.id.address);
                        address.requestFocus();
                        CmmFunc.showKeyboard(getActivity());
                    }
                }
            });

        } catch (Exception e) {

        }
    }
}
