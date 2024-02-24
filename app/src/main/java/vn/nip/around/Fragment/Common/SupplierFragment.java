package vn.nip.around.Fragment.Common;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.ToSupplierAdapter;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupplierFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    Thread threadInit;
    RecyclerView recycler;
    List<File> list;
    //endregion

    //region Constructor
    public SupplierFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_supplier, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    final TextView title = (TextView) view.findViewById(R.id.title);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    list = new ArrayList<>();
                    list.add(null);
                    errorListener((EditText) view.findViewById(R.id.full_name));
                    errorListener((EditText) view.findViewById(R.id.store_name));
                    errorListener((EditText) view.findViewById(R.id.store_address));
                    errorListener((EditText) view.findViewById(R.id.phone_number));
                    errorListener((EditText) view.findViewById(R.id.email));
                    errorListener((EditText) view.findViewById(R.id.website));
                    errorListener((EditText) view.findViewById(R.id.facebook));
                    errorListener((EditText) view.findViewById(R.id.product_cooperation));
                    view.findViewById(R.id.confirm).setOnClickListener(SupplierFragment.this);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            title.setText(getString(R.string.to_become_supplier));
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false) {
                                @Override
                                public boolean canScrollHorizontally() {
                                    return false;
                                }
                            };
                            recycler.setLayoutManager(layoutManager);
                            ToSupplierAdapter adapter = new ToSupplierAdapter(getActivity(), SupplierFragment.this, recycler, list);
                            recycler.setAdapter(adapter);
                        }
                    });
                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (threadInit != null) {
                threadInit.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Events
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.confirm:
                confirm();
                break;
        }
    }
    //endregion

    //region OnResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1999 && resultCode == Activity.RESULT_OK) {
                String fullPath;
                Uri uri = Uri.parse(data.getDataString());
                fullPath = CmmFunc.getPathFromUri(getActivity(), uri);
                File file = new File(fullPath);
                if (ToSupplierAdapter.currentPosition == list.size() - 1) {
                    list.add(ToSupplierAdapter.currentPosition, file);
                } else {
                    list.set(ToSupplierAdapter.currentPosition, file);

                }
                recycler.getAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Methods
    private void errorListener(final EditText editText) {
        editText.removeTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setCompoundDrawables(null, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setCompoundDrawables(null, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void confirm() {
        try {
            //showProgress();
            ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
            scrollView.clearFocus();
            Drawable icError = getResources().getDrawable(R.drawable.ic_error);
            icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
            final EditText fullName = (EditText) view.findViewById(R.id.full_name);
            String fullNameVal = fullName.getText().toString().trim();
            if (fullNameVal.equals("")) {
                fullName.setCompoundDrawables(null, null, icError, null);
                scrollView.smoothScrollTo(0, 0);
                fullName.requestFocus();
                return;
            }

            EditText storeName = (EditText) view.findViewById(R.id.store_name);
            String storeNameVal = storeName.getText().toString().trim();
            if (storeNameVal.equals("")) {
                storeName.setCompoundDrawables(null, null, icError, null);
                scrollView.smoothScrollTo(0, 0);
                storeName.requestFocus();
                return;
            }
            EditText phoneNumber = (EditText) view.findViewById(R.id.phone_number);
            String phoneNumberVal = phoneNumber.getText().toString().trim();
            if (phoneNumberVal.length() < 10) {
                phoneNumber.setCompoundDrawables(null, null, icError, null);
                scrollView.smoothScrollTo(0, 0);
                phoneNumber.requestFocus();
                return;
            }

            EditText storeAddress = (EditText) view.findViewById(R.id.store_address);
            String storeAddressVal = storeAddress.getText().toString().trim();
            if (storeAddressVal.equals("")) {
                storeAddress.setCompoundDrawables(null, null, icError, null);
                scrollView.smoothScrollTo(0, 0);
                storeAddress.requestFocus();
                return;
            }


            EditText email = (EditText) view.findViewById(R.id.email);
            String emailVal = email.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
                email.setCompoundDrawables(null, null, icError, null);
                scrollView.smoothScrollTo(0, email.getTop() - 80);
                email.requestFocus();
                return;
            }

            EditText productCoop = (EditText) view.findViewById(R.id.product_cooperation);
            String productCoopVal = productCoop.getText().toString().trim();
            if (productCoopVal.equals("")) {
                productCoop.setCompoundDrawables(null, null, icError, null);
                scrollView.smoothScrollTo(0, email.getTop() - 80);
                productCoop.requestFocus();
                return;
            }
            EditText website = (EditText) view.findViewById(R.id.website);
            EditText facebook = (EditText) view.findViewById(R.id.facebook);

            int type = 1;
            RadioButton productDistribution = (RadioButton) view.findViewById(R.id.product_distributtion);
            if (productDistribution.isChecked()) {
                type = 2;
            }

            if (list.size() < 2) {
                CustomDialog.showMessage(getActivity(), "", getString(R.string.no_product_supplier));
                return;
            }

            new ActionRegister().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fullNameVal, storeNameVal, storeAddressVal, phoneNumberVal, emailVal, productCoopVal,
                    website.getText().toString().trim(), facebook.getText().toString().trim(), type);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //hideProgress();
        }

    }
    //endregion

    //region Actions
    class ActionRegister extends ActionAsync {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String fullName = (String) params[0];
                String storeName = (String) params[1];
                String address = (String) params[2];
                String phoneNumber = (String) params[3];
                String email = (String) params[4];
                String product = (String) params[5];
                String website = (String) params[6];
                String facebook = (String) params[7];
                int type = (int) params[8];

                String value = APIHelper.registerSupplier(fullName, storeName, address, phoneNumber, email, product, website, facebook, type, list);
                jsonObject = new JSONObject(value);
            } catch (JSONException e) {
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
                    CustomDialog.showMessage(getActivity(), "", getString(R.string.done_supplier), new ICallback() {
                        @Override
                        public void excute() {
                            FragmentHelper.pop(getActivity());
                        }
                    });

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
