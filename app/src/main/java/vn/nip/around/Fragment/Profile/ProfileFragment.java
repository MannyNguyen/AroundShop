package vn.nip.around.Fragment.Profile;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.nip.around.AppActivity;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Custom.CircleTransform;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Fragment.Product.ProductsFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.MainActivity;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    final int GALLERY = 100;
    final String TEXT = "TEXT";
    final String IMAGE = "IMAGE";
    final String HOME = "HOME";
    final String WORK = "WORK";
    final String OTHER = "OTHER";
    ImageView avatar;
    TextView name, birthday, phone;
    EditText edtName, edtEmail;
    ActionAsync actionGet;
    Spinner language;

    ImageView imgHome, imgWork, imgOther;
    TextView txtHome, txtWork, txtOther;
    View home, work, other;

    TextView address;
    ImageView imgAddress;

    DateTime birthdayDateTime = new DateTime();

    Button save;
    Drawable icError;
    public String currentTab = HOME;
    //endregion

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
        if (!(fragment instanceof ProfileFragment)) {
            return;
        }
        updateTab(currentTab);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.location));
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    final TextView title = (TextView) view.findViewById(R.id.title);
                    avatar = (ImageView) view.findViewById(R.id.avatar);
                    name = (TextView) view.findViewById(R.id.name);
                    birthday = (TextView) view.findViewById(R.id.birthday);
                    edtName = (EditText) view.findViewById(R.id.edt_name);
                    edtEmail = (EditText) view.findViewById(R.id.email);
                    phone = (TextView) view.findViewById(R.id.phone);
                    language = (Spinner) view.findViewById(R.id.language);

                    imgHome = (ImageView) view.findViewById(R.id.img_home);
                    imgWork = (ImageView) view.findViewById(R.id.img_work);
                    imgOther = (ImageView) view.findViewById(R.id.img_other);

                    txtHome = (TextView) view.findViewById(R.id.txt_home);
                    txtWork = (TextView) view.findViewById(R.id.txt_work);
                    txtOther = (TextView) view.findViewById(R.id.txt_other);

                    home = view.findViewById(R.id.home);
                    work = view.findViewById(R.id.work);
                    other = view.findViewById(R.id.other);

                    address = (TextView) view.findViewById(R.id.address_profile);
                    imgAddress = (ImageView) view.findViewById(R.id.img_address);
                    save = (Button) view.findViewById(R.id.save);
                    birthday.setOnClickListener(ProfileFragment.this);
                    avatar.setOnClickListener(ProfileFragment.this);
                    save.setOnClickListener(ProfileFragment.this);
                    home.setOnClickListener(ProfileFragment.this);
                    work.setOnClickListener(ProfileFragment.this);
                    other.setOnClickListener(ProfileFragment.this);
                    address.setOnClickListener(ProfileFragment.this);

                    icError = getResources().getDrawable(R.drawable.ic_error);
                    icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                title.setText(getString(R.string.profile));
                                save.setVisibility(View.GONE);
                                actionGet = new ActionGetProfile();
                                actionGet.execute();
                                String[] arrPay = {"English", "Tiếng Việt"};
                                final Spinner language = (Spinner) view.findViewById(R.id.language);
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row_spinner_language, arrPay);
                                language.setAdapter(arrayAdapter);

                                if (StorageHelper.getLanguage().equals(StringUtils.EMPTY)) {
                                    StorageHelper.saveLanguage("vi");
                                }

                                if (StorageHelper.getLanguage().equals("vi")) {
                                    language.setSelection(1);
                                } else {
                                    language.setSelection(0);
                                }
                                TextWatcher textWatcher = new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        showSave();
                                    }
                                };

                                edtName.addTextChangedListener(textWatcher);
                                edtEmail.addTextChangedListener(textWatcher);
                                language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                                        if (position == 0 && !StorageHelper.getLanguage().equals("vi")) {
                                            return;
                                        }
                                        if (position == 1 && StorageHelper.getLanguage().equals("vi")) {
                                            return;
                                        }
                                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                                        messageDialogFragment.setOutToHide(true);
                                        messageDialogFragment.setMessage(getString(R.string.message_change_language));
                                        messageDialogFragment.setCallback(new ICallback() {
                                            @Override
                                            public void excute() {
                                                if (position == 0) {
                                                    StorageHelper.saveLanguage("en");
                                                } else {
                                                    StorageHelper.saveLanguage("vi");
                                                }

                                                Locale locale = new Locale(StorageHelper.getLanguage());
                                                Locale.setDefault(locale);
                                                Configuration config = new Configuration();
                                                config.locale = locale;
                                                getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        });
                                        messageDialogFragment.setCancelCallback(new ICallback() {
                                            @Override
                                            public void excute() {
                                                if (StorageHelper.getLanguage().equals("vi")) {
                                                    language.setSelection(1);
                                                } else {
                                                    language.setSelection(0);
                                                }
                                            }
                                        });
                                        messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                updateTab(currentTab);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    });
                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.birthday:
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        birthdayDateTime = new DateTime(year, month + 1, dayOfMonth, 0, 0);
                        birthday.setText(birthdayDateTime.toString("dd-MM-yyyy"));
                        showSave();
                    }
                }, birthdayDateTime.getYear(), birthdayDateTime.getMonthOfYear() - 1, birthdayDateTime.getDayOfMonth()).show();
                break;

            case R.id.avatar:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        ProfileFragment.this.startActivityForResult(intent, GALLERY);
                    }
                }).start();
                break;
            case R.id.save:
                try {
                    hideSave();
                    CmmFunc.hideKeyboard(getActivity());
                    edtEmail.clearFocus();
                    edtName.clearFocus();
                    String fullName = edtName.getText().toString().trim();
                    if (fullName.equals(StringUtils.EMPTY)) {
                        edtName.setCompoundDrawables(null, null, icError, null);

                        return;
                    }

                    String birth = birthdayDateTime.toString("dd-MM-yyyy");
                    String email = edtEmail.getText().toString().trim();

                    new ActionUpdateProfile().execute(TEXT, fullName, email, birth);
                } catch (Exception e) {

                }
                break;

            case R.id.home:
                updateTab(HOME);
                break;
            case R.id.work:
                updateTab(WORK);
                break;

            case R.id.other:
                updateTab(OTHER);
                break;
            case R.id.address_profile:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, ProfileAddressFragment.newInstance(currentTab));
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY:
                    gallery(data);
                    break;
            }
        }
    }

    private void showSave() {
        if (save.getVisibility() == View.VISIBLE) {
            return;
        }
        save.setVisibility(View.VISIBLE);
    }

    private void hideSave() {
        if (save.getVisibility() == View.GONE) {
            return;
        }
        save.setVisibility(View.GONE);
    }

    private void gallery(final Intent data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = null;
                    Uri uri = Uri.parse(data.getDataString());
                    if (uri != null) {
                        String realPath = CmmFunc.getPathFromUri(getActivity(), uri);
                        if (realPath != null) {
                            ExifInterface exif = new ExifInterface(realPath);
                            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            switch (rotation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotation = 90;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotation = 180;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotation = 270;
                                    break;
                                default:
                                    rotation = 0;
                                    break;
                            }
                            Matrix matrix = new Matrix();
                            if (rotation != 0f) {
                                matrix.preRotate(rotation);
                            }

                            bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                        } else {
                            if (data.getData() == null) {
                                bmp = (Bitmap) data.getExtras().get("data");
                            } else {
                                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                                bmp = BitmapFactory.decodeStream(bufferedInputStream);
                            }
                        }
                    } else {
                        if (data.getData() == null) {
                            bmp = (Bitmap) data.getExtras().get("data");
                        } else {
                            InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                            bmp = BitmapFactory.decodeStream(bufferedInputStream);
                        }
                    }

                    bmp = CmmFunc.resizeBitmap(bmp, CmmVariable.IMAGE_CHAT_SIZE);
                    byte[] arr = CmmFunc.bitmapToByteArray(bmp);
                    final String encoded = Base64.encodeToString(arr, Base64.DEFAULT);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new ActionUpdateProfile().execute(IMAGE, encoded);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void updateTab(String tab) {
        try {
            currentTab = tab;
            imgHome.setImageResource(R.drawable.ic_home_unselected);
            txtHome.setTextColor(getResources().getColor(R.color.gray_900));

            imgWork.setImageResource(R.drawable.ic_work_unselected);
            txtWork.setTextColor(getResources().getColor(R.color.gray_900));

            imgOther.setImageResource(R.drawable.ic_place_unselected);
            txtOther.setTextColor(getResources().getColor(R.color.gray_900));

            switch (tab) {
                case HOME:
                    imgHome.setImageResource(R.drawable.ic_home_selected);
                    txtHome.setTextColor(getResources().getColor(R.color.main));
                    imgAddress.setImageResource(R.drawable.ic_home_selected);
                    if (!StorageHelper.get("home").equals("")) {
                        JSONObject jsonObject = new JSONObject(StorageHelper.get("home"));
                        address.setText(jsonObject.getString("address"));
                    } else {
                        address.setText(StringUtils.EMPTY);
                    }


                    break;
                case WORK:
                    imgWork.setImageResource(R.drawable.ic_work_selected);
                    txtWork.setTextColor(getResources().getColor(R.color.main));
                    imgAddress.setImageResource(R.drawable.ic_work_selected);
                    if (!StorageHelper.get("work").equals("")) {
                        JSONObject jsonObject = new JSONObject(StorageHelper.get("work"));
                        address.setText(jsonObject.getString("address"));
                    } else {
                        address.setText(StringUtils.EMPTY);
                    }
                    break;
                case OTHER:
                    imgOther.setImageResource(R.drawable.ic_place_selected);
                    txtOther.setTextColor(getResources().getColor(R.color.main));
                    imgAddress.setImageResource(R.drawable.ic_place_selected);
                    if (!StorageHelper.get("other").equals("")) {
                        JSONObject jsonObject = new JSONObject(StorageHelper.get("other"));
                        address.setText(jsonObject.getString("address"));
                    } else {
                        address.setText(StringUtils.EMPTY);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ActionGetProfile extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String response = APIHelper.getProfile();
                jsonObject = new JSONObject(response);
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
                    String fullName = jsonObject.getString("fullname");
                    name.setText(fullName);
                    edtName.setText(fullName);
                    edtEmail.setText(jsonObject.getString("email"));
                    phone.setText(jsonObject.getString("phone"));
                    birthdayDateTime = DateTime.parse(jsonObject.getString("birthday"), DateTimeFormat.forPattern("dd-MM-yyyy"));
                    birthday.setText(birthdayDateTime.toString("dd-MM-yyyy"));
                    Picasso.with(getActivity()).load(jsonObject.getString("avatar")).resize(CmmVariable.AVATAR, CmmVariable.AVATAR).centerCrop().transform(new CircleTransform()).into(avatar);
                    hideSave();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            hideProgress();
        }
    }

    class ActionUpdateProfile extends ActionAsync {
        String keyUpdate;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            try {
                keyUpdate = (String) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                switch (keyUpdate) {
                    case TEXT:
                        String fullName = (String) objects[1];
                        String email = (String) objects[2];
                        String birth = (String) objects[3];
                        params.add(new AbstractMap.SimpleEntry("new_fullname", fullName));
                        params.add(new AbstractMap.SimpleEntry("new_email", email));
                        params.add(new AbstractMap.SimpleEntry("birthday", birth));
                        break;
                    case IMAGE:
                        String avatar = (String) objects[1];
                        params.add(new AbstractMap.SimpleEntry("new_avatar", avatar));
                        break;
                }
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/update_profile", params);
                jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {

            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    actionGet = new ActionGetProfile();
                    actionGet.execute();

                } else {
                    if (keyUpdate.equals(TEXT)) {
                        showSave();
                    }
                }


            } catch (Exception e) {

            } finally {
                hideProgress();
            }
        }
    }
}
