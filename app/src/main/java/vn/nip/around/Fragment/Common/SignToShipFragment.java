package vn.nip.around.Fragment.Common;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Helper.StringHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignToShipFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    String avatar = "";
    //endregion

    public SignToShipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sign_to_ship, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            View confirm = view.findViewById(R.id.confirm);
            confirm.setOnClickListener(SignToShipFragment.this);
            ImageButton pickAvatar = (ImageButton) view.findViewById(R.id.pick_avatar);
            pickAvatar.setOnClickListener(SignToShipFragment.this);
            isLoaded = true;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                try {

                    EditText fullName = (EditText) view.findViewById(R.id.fullname);
                    EditText phoneNumber = (EditText) view.findViewById(R.id.phone_number);
                    EditText idNumber = (EditText) view.findViewById(R.id.id_number);
                    EditText address = (EditText) view.findViewById(R.id.address);

                    if (!checkValidate(fullName) || !checkValidate(phoneNumber) || !checkValidate(idNumber) || !checkValidate(address)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("fullname", fullName.getText().toString() + "");
                    jsonObject.put("phone_number", phoneNumber.getText().toString() + "");
                    jsonObject.put("id_number", idNumber.getText().toString() + "");
                    jsonObject.put("address", address.getText().toString() + "");
                    jsonObject.put("avatar", avatar);
                    new ActionConfirm().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject);
                } catch (Exception e) {

                }
                break;
            case R.id.pick_avatar:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1999);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1999 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                bmp = CmmFunc.resizeBitmap(bmp, 256);
                ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
                avatarView.setImageBitmap(bmp);
                byte[] arr = CmmFunc.bitmapToByteArray(bmp);
                String encoded = Base64.encodeToString(arr, Base64.DEFAULT);
                avatar = encoded;
            } catch (Exception e) {

            }
        }
    }

    private boolean checkValidate(EditText editText) {
        try {
            if (editText.getText().toString().trim().equals("")) {
                editText.requestFocus();
                editText.setError(getString(R.string.required));
                return false;
            }
        } catch (Exception e) {

        }
        return true;
    }

    //region Actions
    class ActionConfirm extends ActionAsync {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                JSONObject jsonObject = (JSONObject) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("fullname", jsonObject.getString("fullname")));
                params.add(new AbstractMap.SimpleEntry("id_no", jsonObject.getString("id_number")));
                params.add(new AbstractMap.SimpleEntry("address", jsonObject.getString("address")));
                params.add(new AbstractMap.SimpleEntry("phone_no", jsonObject.getString("phone_number")));
                params.add(new AbstractMap.SimpleEntry("avatar", jsonObject.getString("avatar")));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/signup_to_ship", params, true);
                jsonObject = new JSONObject(response);
                return jsonObject;


            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionConfirm", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    CustomDialog.showMessage(getActivity(), "SUCCESS", "Register successfully", new ICallback() {
                        @Override
                        public void excute() {

                        }
                    });

                }
            } catch (Exception e) {

            }
        }
    }
    //endregion
}
