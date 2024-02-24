package vn.nip.around.Fragment.Cart;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import vn.nip.around.Adapter.ContactAutoCompleteAdapter;
import vn.nip.around.Adapter.PlacesAutoCompleteAdapter;
import vn.nip.around.Adapter.ProductedAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanContact;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Bean.BeanSendGift;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.ListPaymentFragment;
import vn.nip.around.Fragment.Common.MatchFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Fragment.Pickup.AddressFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.KeyboardHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Helper.StorageHelper;

import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;
import vn.nip.around.Util.NetworkUtil;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //region Variables
    public static BeanSendGift RECEIVER;
    final int PICK_CONTACT = 1999;
    final int PERMISSION_READ_CONTACT = 1001;
    public int orderID = 0;
    RecyclerView recycler;
    ProductedAdapter adapter;
    public List<BeanProduct> products = new ArrayList<>();

    TextView itemCost;
    TextView serviceFee;
    TextView shippingFee;
    TextView total, totalTop, numberItem;
    EditText fullName;
    EditText phoneNumber;
    EditText note;
    //ProgressBar progressBar_money;
    public List<JSONObject> locations = new ArrayList<>();
    public TextView address;
    String paymentType;
    boolean oldKeyboardState = false;

    public ListPaymentFragment listPaymentFragment;

    public int day, month, year, hour, minute = 0;
    //endregion

    //region Contructors
    public CartFragment() {
        Bundle args = new Bundle();
        args.putString("delivery_time", "");
        args.putBoolean("cannot_find_shipper", false);
        setArguments(args);
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_cart, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public static CartFragment newInstance() {
        //FragmentManager fragmentManager = GlobalClass.getActivity().getSupportFragmentManager();
        CartFragment fragment = new CartFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppActivity.FLOW = AppActivity.GIFTING;
        Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
        if (!(fragment instanceof CartFragment)) {
            return;
        }
        AppActivity.FLOW = AppActivity.GIFTING;

        SmartFoxHelper.getInstance().disconnect();
        try {
            orderID = 0;
            hideProgress();
            new ActionGetNumberCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            String addr = locations.get(0).getString("address");
            address.setText(addr);

            if (listPaymentFragment != null) {
                listPaymentFragment.onResume();
            }

            if (getArguments().getBoolean("cannot_find_shipper")) {
                //show pop khong tim thay shipper

            } else {
                //Qua thoi gian lam viec
                if (!getArguments().getString("delivery_time").equals("")) {
                    String time = getArguments().getString("delivery_time");
                    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(time, df);
                    JSONObject jsonObject = CmmFunc.formatDate(dateTime);
                    day = jsonObject.getInt("day");
                    month = jsonObject.getInt("month");
                    year = jsonObject.getInt("year");
                    hour = jsonObject.getInt("hour");
                    minute = jsonObject.getInt("minute");
                    TextView dropDate = (TextView) view.findViewById(R.id.drop_date);
                    dropDate.setText(jsonObject.getString("value"));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            final String name = fullName.getText().toString();
            final String phone = phoneNumber.getText().toString();
            final String n = note.getText().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    APIHelper.updateDropInfo(name, phone, n);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(400);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.findViewById(R.id.container).setVisibility(View.VISIBLE);
                        }
                    });
                    address = (TextView) view.findViewById(R.id.address1);
                    address.setOnClickListener(CartFragment.this);
                    products = new ArrayList<>();
                    listPaymentFragment = (ListPaymentFragment) getChildFragmentManager().findFragmentById(R.id.list_payment_fragment);

                    itemCost = (TextView) view.findViewById(R.id.item_cost);
                    serviceFee = (TextView) view.findViewById(R.id.service_fee);
                    shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                    total = (TextView) view.findViewById(R.id.total);
                    totalTop = (TextView) view.findViewById(R.id.total_top);
                    numberItem = (TextView) view.findViewById(R.id.number_item);

                    fullName = (EditText) view.findViewById(R.id.full_name);
                    phoneNumber = (EditText) view.findViewById(R.id.phone);
                    note = (EditText) view.findViewById(R.id.note);

                    view.findViewById(R.id.calendar_area).setOnClickListener(CartFragment.this);
                    view.findViewById(R.id.confirm).setOnClickListener(CartFragment.this);
                    view.findViewById(R.id.back_more).setOnClickListener(CartFragment.this);
                    view.findViewById(R.id.fee_shipping).setOnClickListener(CartFragment.this);
                    view.findViewById(R.id.fee_service).setOnClickListener(CartFragment.this);
                    view.findViewById(R.id.contact).setOnClickListener(CartFragment.this);
                    new ActionGetNumberCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                recycler = (RecyclerView) view.findViewById(R.id.recycler);
                                recycler.setNestedScrollingEnabled(false);
                                recycler.setItemViewCacheSize(100);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                recycler.setLayoutManager(mLayoutManager);
                                adapter = new ProductedAdapter(getActivity(), CartFragment.this, recycler, products);
                                recycler.setAdapter(adapter);

                                LocalDateTime localDateTime = new LocalDateTime(System.currentTimeMillis() + 3600000);
                                JSONObject jsonObject = CmmFunc.formatDate(localDateTime);
                                TextView dropDate = (TextView) view.findViewById(R.id.drop_date);
                                dropDate.setText(jsonObject.getString("value"));

                                //isLoaded = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }).start();
    }

    private void tut() {
        if (!StorageHelper.getTUT(StorageHelper.TUT_GIFT_ORDER)) {
            final ImageView tut = (ImageView) view.findViewById(R.id.tut);
            tut.setVisibility(View.VISIBLE);
            tut.setAlpha(0.0f);
            tut.setImageResource(getResources().getIdentifier(getString(R.string.tut_order), "drawable", getActivity().getPackageName()));
            tut.animate().alpha(1).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    tut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tut.setVisibility(View.GONE);
                            StorageHelper.setTut(StorageHelper.TUT_GIFT_ORDER, true);
                        }
                    });
                }
            }).start();
        }
    }

    //endregion

    //region Events
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar_area:
                LocalDateTime date = new LocalDateTime(System.currentTimeMillis() + 5400000);
                if (day != 0) {
                    DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");
                    date = LocalDateTime.parse(day + "-" + (month) + "-" + year + " " + hour + ":" + minute, dtf);
                }
                final LocalDateTime dateClone = date;
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, final int y, final int m, final int d) {
                                if (datePicker.isShown()) {
                                    final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(final TimePicker timePicker, int i, int i1) {
                                            try {
                                                day = d;
                                                month = m;
                                                year = y;
                                                hour = i;
                                                minute = i1;
                                                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");
                                                LocalDateTime dateTime = LocalDateTime.parse(day + "-" + (month + 1) + "-" + year + " " + hour + ":" + minute, formatter);
                                                JSONObject jsonObject = CmmFunc.formatDate(dateTime);
                                                day = jsonObject.getInt("day");
                                                month = jsonObject.getInt("month");
                                                year = jsonObject.getInt("year");
                                                hour = jsonObject.getInt("hour");
                                                minute = jsonObject.getInt("minute");
                                                TextView dropDate = (TextView) view.findViewById(R.id.drop_date);
                                                dropDate.setText(jsonObject.getString("value"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, dateClone.getHourOfDay(), dateClone.getMinuteOfHour(), false);
                                    timePickerDialog.show();
                                }
                            }

                        }, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel_schedule), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            try {
                                day = 0;
                                month = 0;
                                year = 0;
                                hour = 0;
                                minute = 0;
                                JSONObject jsonObject = CmmFunc.formatDate(new LocalDateTime(System.currentTimeMillis() + 3600000));
                                TextView dropDate = (TextView) view.findViewById(R.id.drop_date);
                                dropDate.setText(jsonObject.getString("value"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 60000);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (CmmVariable.maxDay - 1) * 24 * 60 * 60 * 1000);
                datePickerDialog.show();
                break;
//            case R.id.is_gift_parent:
//                Tracking.excute("C15.3Y");
//                ImageButton isGift = (ImageButton) view.findViewById(R.id.is_gift_parent);
//                if (Integer.parseInt(isGift.getTag() + "") == 0) {
//                    isGift.setTag(1);
//                    isGift.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_gift_selected));
//                    new ActionUpdateGiftCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1);
//                } else {
//                    isGift.setTag(0);
//                    isGift.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_gift_unselected));
//                    new ActionUpdateGiftCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
//                }
//                for (int i = 0; i < products.size(); i++) {
//                    products.get(i).setIs_gift(0);
//                    recycler.getAdapter().notifyDataSetChanged();
//                }
//                break;

            case R.id.confirm:
                new ActionCheckCart().execute();
                break;
            case R.id.back:
                FragmentHelper.pop(getActivity());
                break;
            case R.id.back_more:
                Tracking.excute("C15.6Y");
                FragmentHelper.pop(getActivity());
                break;
            case R.id.fee_shipping:
                CustomDialog.popupShipFee(getActivity());
                break;

            case R.id.fee_service:
                CustomDialog.popupServiceFee(getActivity(), 1);
                break;

            case R.id.address1:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, AddressFragment.newInstance(0, AddressFragment.CART));
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
                                                        fullName.setText(txtName);
                                                        phoneNumber.setText(finalTxtPhone);
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                APIHelper.updateDropInfo(txtName, finalTxtPhone, "");
                                                            }
                                                        }).start();
                                                        RECEIVER = null;
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cast:
                if (b) {
                    new ActionUpdatePayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "CASH");
                }
                break;
            case R.id.credit_card:
                if (b) {
                    new ActionUpdatePayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "ONLINE");
                }
                break;
        }
    }

    //endregion

    //region Methods
//    public void setValueDate(final LocalDateTime dateTime) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String[] dayNames = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
//                String[] monthNames = new String[]{"JAN", "FEB", "MAR", "APRIL", "MAY", "JUNE", "JULY", "AUG", "SEP", "OCT", "NOV", "DEC"};
//                if (StorageHelper.getLanguage().equals("vi")) {
//                    dayNames = new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"};
//                    monthNames = new String[]{"tháng 1", "tháng 2", "tháng 3", "tháng 4", "tháng 5", "tháng 6", "tháng 7", "tháng 8", "tháng 9", "tháng 10", "tháng 11", "tháng 12"};
//                }
//                String value = StringUtils.leftPad(dateTime.getHourOfDay() + "", 2, "0") + ":" + StringUtils.leftPad(dateTime.getMinuteOfHour() + "", 2, "0") + " " +
//                        dayNames[dateTime.getDayOfWeek() - 1] + ", " + dateTime.getDayOfMonth() + " " + monthNames[dateTime.getMonthOfYear() - 1] + ", " + dateTime.getYear();
//                if (StorageHelper.getLanguage().equals("vi")) {
//                    value = StringUtils.leftPad(dateTime.getHourOfDay() + "", 2, "0") + ":" + StringUtils.leftPad(dateTime.getMinuteOfHour() + "", 2, "0") + " " +
//                            dayNames[dateTime.getDayOfWeek() - 1] + ", " + dateTime.getDayOfMonth() + " " + monthNames[dateTime.getMonthOfYear() - 1] + ", " + dateTime.getYear();
//                }
//                final String finalValue = value;
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        TextView dropDate = (TextView) view.findViewById(R.id.drop_date);
//                        dropDate.setText(finalValue);
//                    }
//                });
//            }
//        }).start();
//    }

    public void confirm(boolean isCheckTime) {
        try {
            if (NetworkUtil.getConnectivityStatus(GlobalClass.getContext()) == NetworkUtil.TYPE_NOT_CONNECTED) {
                CustomDialog.showMessage(GlobalClass.getActivity(), "", GlobalClass.getActivity().getString(R.string.disconnect_match));
                return;
            }
            showProgress();
            final View confirm = view.findViewById(R.id.confirm);
            confirm.setOnClickListener(null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        confirm.setOnClickListener(CartFragment.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Tracking.excute("C15.5Y");
            if (products.size() == 0) {
                CustomDialog.showMessage(getActivity(), getString(R.string.error), getString(R.string.empty_cart));
                hideProgress();
                return;
            }

            if (locations.size() == 0) {
                address.setError(getResources().getString(R.string.required));
                address.requestFocus();
                hideProgress();
                return;
            }
            if (isCheckTime && day != 0) {
                SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date selectedDate = dtf.parse(day + "-" + (month) + "-" + year + " " + hour + ":" + minute);
                Date now = new Date();
                if (selectedDate.getTime() - now.getTime() < 5400000 - 60000) {
                    CustomDialog.showMessage(getActivity(), "", getString(R.string.time_minimum));
                    hideProgress();
                    return;
                }
            }
            Fragment fragment = MatchFragment.newInstance(orderID, false, "GIFTING", null, year, month, day, hour, minute);
            FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Actions
    class ActionGetNumberCart extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_number_product_in_cart", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetCategory", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    int numberCart = data.getInt("number");
                    TextView numCart = (TextView) view.findViewById(R.id.number_cart);
                    if (numberCart > 0) {
                        numCart.setText(numberCart + "");
                        numCart.setVisibility(View.VISIBLE);
                        new ActionGetCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        numberItem.setText("(" + numberCart + " " + getString(R.string.items) + ")");
                    } else {
                        numCart.setText("0");
                        numCart.setVisibility(View.GONE);
                    }

                    if (locations != null && locations.size() > 0) {
                        try {
                            String addr = locations.get(0).getString("address");
                            Log.d("ADDRESS_CART", addr);
                            address.setText(addr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ActionGetCart extends AsyncTask<Void, Void, JSONObject> {
        String message = getString(R.string.cannot_data);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products.clear();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonObject = null;
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_cart", params);
                jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data.isNull("product") == false) {
                        JSONArray arrProduct = data.getJSONArray("product");
                        for (int i = 0; i < arrProduct.length(); i++) {
                            BeanProduct bean = (BeanProduct) CmmFunc.tryParseJson(arrProduct.getString(i), BeanProduct.class);
                            products.add(bean);
                        }
                    }
                }

            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetCategory", e.getMessage());
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject == null) {
                    CustomDialog.showMessage(getActivity(), getString(R.string.error), message);
                    return;
                }
                if (jsonObject.isNull("data")) {
                    return;
                }
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    adapter.notifyDataSetChanged();
                    JSONObject data = jsonObject.getJSONObject("data");

                    int d = data.getInt("delivery_day");
                    int m = data.getInt("delivery_month");
                    int y = data.getInt("delivery_year");
                    if (fullName.getText().toString().equals(StringUtils.EMPTY)) {
                        fullName.setText(data.getString("recipent_name") + "");
                    }
                    if (phoneNumber.getText().toString().equals(StringUtils.EMPTY)) {
                        phoneNumber.setText(data.getString("recipent_phone") + "");
                    }
                    if (note.getText().toString().equals(StringUtils.EMPTY)) {
                        note.setText(data.getString("recipent_note") + "");
                    }
                    itemCost.setText(CmmFunc.formatMoney(data.getInt("item_cost"), true));
                    serviceFee.setText(CmmFunc.formatMoney(data.getInt("service_fee"), true));
                    shippingFee.setText(CmmFunc.formatMoney(data.getInt("shipping_fee"), true));
                    String totalMoney = CmmFunc.formatMoney(data.getInt("total"), true);
                    total.setText(totalMoney);
                    totalTop.setText(CmmFunc.formatMoney(data.getInt("item_cost"), true));

                    paymentType = data.getString("payment_type");
                    if (paymentType.equals("CASH")) {

                    } else if (paymentType.equals("ONLINE")) {
                    }
                    locations = (List<JSONObject>) CmmFunc.tryParseList(data.getString("locations"), JSONObject.class);
                    address.setText(locations.get(0).getString("address"));
                    address.setTag(locations.get(0).getString("address"));
                } else if (code != 1) {
                    new ErrorHelper().excute(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ActionUpdatePayment extends ActionAsync {
        String pt;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                pt = (String) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("payment_type", pt));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/update_payment", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionUpdatePayment", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject.getInt("code") == 1) {
                    paymentType = pt;
                    if (paymentType.equals("CASH")) {

                    } else if (paymentType.equals("ONLINE")) {

                    }
                }
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionUpdatePayment", e.getMessage());
            } finally {
                hideProgress();
            }
        }
    }

    class ActionUpdateDrop extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String name = (String) params[0];
                String phone = (String) params[1];
                String note = (String) params[2];
                String value = APIHelper.updateDropInfo(name, phone, note);
                jsonObject = new JSONObject(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    RECEIVER = null;
                    confirm(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }

    class ActionCheckCart extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {

                String value = APIHelper.checkCart();
                jsonObject = new JSONObject(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    String data = jsonObject.getString("data");
                    JSONArray names = new JSONArray(data);
                    if (names.length() == 0) {
                        String name = fullName.getText().toString();
                        String phone = phoneNumber.getText().toString();
                        String n = note.getText().toString();
                        new ActionUpdateDrop().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name, phone, n);
                    } else {
                        String strName = StringUtils.EMPTY;
                        for (int i = 0; i < names.length(); i++) {
                            strName += StringUtils.upperCase(names.getString(i)) + "\n";
                        }

                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                        messageDialogFragment.setMessage(getString(R.string.message_product_expire, strName));
                        messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }
    //endregion
}
