package vn.nip.around.Fragment.Pickup;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Dialog.BaseDialogFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoCODFragment extends BaseDialogFragment implements View.OnClickListener {
    final int PICK_CONTACT = 1999;
    final int PERMISSION_READ_CONTACT = 1001;
    MapCODFragment mapCODFragment;
    BeanPoint beanPoint;
    EditText itemName, itemCost, name, phone, note;
    TextView errorMessage;
    TextView positionText, address;
    int position;
    View view;

    boolean isLoad;

    public InfoCODFragment() {
        // Required empty public constructor
    }

    public static InfoCODFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        InfoCODFragment fragment = new InfoCODFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_cod, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        position = getArguments().getInt("position");
        if (position == 0) {
            view.findViewById(R.id.popup_cod_first).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.popup_cod_last).setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        if (isLoad) {
            return;
        }
        mapCODFragment = (MapCODFragment) CmmFunc.getFragmentByTag(getActivity(), MapCODFragment.class.getName());
        beanPoint = mapCODFragment.points.get(getArguments().getInt("position"));

        if (position == 0) {
            name = (EditText) view.findViewById(R.id.name_first);
            phone = (EditText) view.findViewById(R.id.phone_first);
            note = (EditText) view.findViewById(R.id.note_first);

        } else {
            view.findViewById(R.id.header).setVisibility(View.VISIBLE);
            positionText = (TextView) view.findViewById(R.id.position);
            address = (TextView) view.findViewById(R.id.address);
            itemName = (EditText) view.findViewById(R.id.item_name);
            itemCost = (EditText) view.findViewById(R.id.item_cost);
            name = (EditText) view.findViewById(R.id.name);
            phone = (EditText) view.findViewById(R.id.phone);
            note = (EditText) view.findViewById(R.id.note);
            errorMessage = (TextView) view.findViewById(R.id.error_message);
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


            itemName.setText(beanPoint.getItem_name());
            if (beanPoint.getItem_cost() == 0) {
                itemCost.setText("");
            } else {
                itemCost.setText(beanPoint.getItem_cost() + "");
            }
            positionText.setText((position + 1) + "");
            address.setText(mapCODFragment.points.get(position).getAddress() + "");

            name.setText(beanPoint.getRecipent_name());
            phone.setText(beanPoint.getPhone());
            note.setText(beanPoint.getNote());

        }
        if (position == 0) {
            final Thread threadGetProfile = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String response = APIHelper.getProfile();
                        final JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        if (code == 1) {
                            //beanPoint.setRecipent_name(jsonObject.getString("fullname"));
                            //beanPoint.setPhone(jsonObject.getString("phone"));
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        name.setText(jsonObject.getString("fullname"));
                                        phone.setText(jsonObject.getString("phone"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (beanPoint.getRecipent_name().equals(StringUtils.EMPTY)) {
                threadGetProfile.start();
            } else {
                name.setText(beanPoint.getRecipent_name());
                phone.setText(beanPoint.getPhone());
                note.setText(beanPoint.getNote());
            }
        }
        view.findViewById(R.id.confirm).setOnClickListener(InfoCODFragment.this);
        view.findViewById(R.id.close).setOnClickListener(InfoCODFragment.this);
        view.findViewById(R.id.contact).setOnClickListener(InfoCODFragment.this);
        view.findViewById(R.id.contact_first).setOnClickListener(InfoCODFragment.this);
        isLoad = true;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, final Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ContentResolver contentResolver = getContext().getContentResolver();
                                Cursor cursor = contentResolver.query(data.getData(), null, null, null, null);
                                if (cursor.getCount() > 0) {
                                    while (cursor.moveToNext()) {
                                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                                        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                            Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                                            while (cursorInfo.moveToNext()) {
                                                String txtPhone = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                                if (txtPhone != null) {
                                                    txtPhone = txtPhone.replaceAll("\\D", "");
                                                    txtPhone = txtPhone.replaceAll("&", "");
                                                    txtPhone.replace("|", "");
                                                    txtPhone = txtPhone.replace("|", "");
                                                }
                                                final String txtName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) + "";
                                                final String finalTxtPhone = txtPhone;
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        name.setText(txtName);
                                                        phone.setText(finalTxtPhone);
                                                    }
                                                });
                                                return;
                                            }

                                            cursorInfo.close();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                if (position > 0) {
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
                        if (cost > CmmVariable.maxCodFee || cost < CmmVariable.minCodeFee) {
                            itemCost.setCompoundDrawables(null, null, icError, null);
                            itemCost.requestFocus();
                            errorMessage.setText(getString(R.string.max_cod_fee, CmmFunc.formatMoney(CmmVariable.minCodeFee, false), CmmFunc.formatMoney(CmmVariable.maxCodFee, false)));
                            errorMessage.setVisibility(View.VISIBLE);
                            return;
                        }
                        beanPoint.setItem_cost(cost);
                    } else {
                        itemCost.setCompoundDrawables(null, null, icError, null);
                        itemCost.requestFocus();
                        errorMessage.setText(getString(R.string.max_cod_fee, CmmFunc.formatMoney(CmmVariable.minCodeFee, false), CmmFunc.formatMoney(CmmVariable.maxCodFee, false)));
                        errorMessage.setVisibility(View.VISIBLE);
                        return;
                    }

                    beanPoint.setPickup_type(BeanPoint.COD);
                    beanPoint.setItem_name(itemNameText);
                    beanPoint.setRecipent_name(name.getText().toString().trim());
                    beanPoint.setPhone(phone.getText().toString().trim());
                    beanPoint.setNote(note.getText().toString().trim());
                    mapCODFragment.points.set(getArguments().getInt("position"), beanPoint);
                    dismissAllowingStateLoss();
                    mapCODFragment.adapter.notifyItemChanged(getArguments().getInt("position"));
                } else {
                    Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                    icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));

                    String nameText = name.getText().toString().trim();
                    if (nameText.equals(StringUtils.EMPTY)) {
                        name.setCompoundDrawables(null, null, icError, null);
                        name.requestFocus();
                        return;
                    }

                    String phoneText = phone.getText().toString().trim();

                    if (phoneText.equals(StringUtils.EMPTY)) {
                        phone.setCompoundDrawables(null, null, icError, null);
                        phone.requestFocus();
                        return;
                    }
                    if (!phoneText.equals(StringUtils.EMPTY) && !android.util.Patterns.PHONE.matcher(phoneText).matches()) {
                        phone.setCompoundDrawables(null, null, icError, null);
                        phone.requestFocus();
                        return;
                    }

                    beanPoint.setRecipent_name(nameText);
                    beanPoint.setPhone(phone.getText().toString().trim());
                    beanPoint.setNote(note.getText().toString().trim());

                    dismissAllowingStateLoss();

                    beanPoint.setPickup_type(BeanPoint.DROP);
                    mapCODFragment.points.set(position, beanPoint);
                    mapCODFragment.adapter.notifyItemChanged(position);
                }
                break;
            case R.id.close:
                dismissAllowingStateLoss();
                break;
            case R.id.contact:
            case R.id.contact_first:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (permissionContact()) {
                            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            startActivityForResult(intent, PICK_CONTACT);
                        }
                    }
                }).start();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, final String permissions[], final int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_CONTACT:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isPerpermissionForAllGranted = true;
                        for (int i = 0; i < permissions.length; i++) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                isPerpermissionForAllGranted = false;
                            }
                        }
                        if (isPerpermissionForAllGranted) {
                            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            startActivityForResult(intent, PICK_CONTACT);
                        }
                    }
                }).start();

                break;
        }
    }

    private boolean permissionContact() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACT);
        } else {
            MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
            messageDialogFragment.setMessage("Please turn on permission Read Contact!");
            messageDialogFragment.setCallback(new ICallback() {
                @Override
                public void excute() {
                    try {
                        //Open the specific App Info page:
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        startActivity(intent);

                    } catch (Exception e) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(intent);

                    }
                }
            });
            messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
        }
        return false;
    }
}
