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
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang.StringUtils;

import vn.nip.around.Bean.BeanContact;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Dialog.BaseDialogFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopupTransportFragment extends BaseFragment implements View.OnClickListener {

    final int PICK_CONTACT = 1999;
    final int PERMISSION_READ_CONTACT = 1001;
    EditText itemName, name, phone, note;
    Button confirm;
    BeanPoint beanPoint;
    HomeBookFragment homeBookFragment;

    public PopupTransportFragment() {
        // Required empty public constructor
    }

    public static PopupTransportFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        PopupTransportFragment fragment = new PopupTransportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_popup_transport, container, false);
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
                name = (EditText) view.findViewById(R.id.name);
                phone = (EditText) view.findViewById(R.id.phone);
                note = (EditText) view.findViewById(R.id.note);
                confirm = (Button) view.findViewById(R.id.confirm);
                view.findViewById(R.id.close).setOnClickListener(PopupTransportFragment.this);
                view.findViewById(R.id.contact).setOnClickListener(PopupTransportFragment.this);
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

                phone.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        phone.setCompoundDrawables(null, null, null, null);
                    }
                });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        homeBookFragment = (HomeBookFragment) CmmFunc.getFragmentByTag(getActivity(), HomeBookFragment.class.getName());
                        if (homeBookFragment != null) {
                            beanPoint = homeBookFragment.points.get(getArguments().getInt("position"));
                            itemName.setText(beanPoint.getItem_name());
                            name.setText(beanPoint.getRecipent_name());
                            phone.setText(beanPoint.getPhone());
                            note.setText(beanPoint.getNote());
                            confirm.setOnClickListener(PopupTransportFragment.this);

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
            case R.id.contact:
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
            case R.id.confirm:
                Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                String itemNameText = itemName.getText().toString().trim();
                if (itemNameText.equals(StringUtils.EMPTY)) {
                    itemName.setCompoundDrawables(null, null, icError, null);
                    itemName.requestFocus();
                    return;
                }

                String nameText = name.getText().toString().trim();
//                if (nameText.equals(StringUtils.EMPTY)) {
//                    name.setCompoundDrawables(null, null, icError, null);
//                    name.requestFocus();
//                    return;
//                }

                String phoneText = phone.getText().toString().trim();
                if (!phoneText.equals(StringUtils.EMPTY) && !android.util.Patterns.PHONE.matcher(phoneText).matches()) {
                    phone.setCompoundDrawables(null, null, icError, null);
                    phone.requestFocus();
                    return;
                }
                beanPoint.setPickup_type(BeanPoint.TRANSPORT);
                beanPoint.setItem_name(itemNameText);
                beanPoint.setRecipent_name(nameText);
                beanPoint.setPhone(phone.getText().toString().trim());
                beanPoint.setNote(note.getText().toString().trim());
                homeBookFragment.points.set(getArguments().getInt("position"), beanPoint);
                ((DialogFragment) getParentFragment()).dismissAllowingStateLoss();
                homeBookFragment.adapter.notifyItemChanged(getArguments().getInt("position"));
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
}
