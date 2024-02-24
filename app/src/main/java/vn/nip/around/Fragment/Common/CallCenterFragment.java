package vn.nip.around.Fragment.Common;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.SFSObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.ChatCSAdapter;
import vn.nip.around.Adapter.EmoticionAdapter;
import vn.nip.around.Bean.BeanCSChat;
import vn.nip.around.Bean.BeanEmoticion;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Custom.ILinearLayoutManager;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallCenterFragment extends BaseFragment implements View.OnClickListener {

    //region Contants
    final int CAPTURE = 1999;
    final int GALLERY = 2000;
    final int PERMISTION_EXTERNAL = 1001;
    //endregion

    //region Variables
    public RecyclerView recycler;
    public ChatCSAdapter adapter;
    public ILinearLayoutManager layoutManager;
    public int page = 1;
    public List<BeanCSChat> chats = new ArrayList<>();
    public EditText text;
    TextWatcher textWatcher;
    View send;
    public String room;

    public String fullName = StringUtils.EMPTY;
    public String avatar = StringUtils.EMPTY;
    Uri captureURI;

    public RecyclerView emoticionRecycler;
    public EmoticionAdapter emoticionAdapter;
    ImageButton emoticion;

    public RecyclerView.OnScrollListener scrollListener;
    //endregion

    //region Constructors
    public CallCenterFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Instance
    public static CallCenterFragment newInstance() {

        Bundle args = new Bundle();

        CallCenterFragment fragment = new CallCenterFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_call_center, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        emoticionRecycler = (RecyclerView) view.findViewById(R.id.recycler_emoticion);
        text = (EditText) view.findViewById(R.id.text);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            showProgress();
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.ask_now_247));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String response = APIHelper.getProfile();
                        JSONObject jsonObject = new JSONObject(response);
                        avatar = jsonObject.getString("avatar");
                        fullName = jsonObject.getString("fullname");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {

                    send = view.findViewById(R.id.send);

                    adapter = new ChatCSAdapter(CallCenterFragment.this, recycler, chats);
                    emoticionAdapter = new EmoticionAdapter(CallCenterFragment.this, emoticionRecycler);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layoutManager = new ILinearLayoutManager(getActivity());
                            //layoutManager.setReverseLayout(true);
                            layoutManager.setStackFromEnd(true);
                            recycler.setLayoutManager(layoutManager);
                            recycler.setAdapter(adapter);
                            recycler.setDrawingCacheEnabled(true);
                            recycler.setItemViewCacheSize(1000);
                            emoticionRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 8));
                            emoticionRecycler.setAdapter(emoticionAdapter);
                            emoticionRecycler.setVisibility(View.GONE);
                            send.setOnClickListener(CallCenterFragment.this);
                            view.findViewById(R.id.capture).setOnClickListener(CallCenterFragment.this);
                            view.findViewById(R.id.gallery).setOnClickListener(CallCenterFragment.this);
                            view.findViewById(R.id.call).setOnClickListener(CallCenterFragment.this);
                            emoticion = (ImageButton) view.findViewById(R.id.emotion);
                            emoticion.setOnClickListener(CallCenterFragment.this);
                            //Socket
                            SmartFoxHelper.initSmartFox();
                            textWatcher = new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    try {
                                        String value = s.toString();
                                        String last = value.substring(value.length() - 1, value.length());
                                        if (!last.equals(">")) {
                                            return;
                                        }

                                        int end = value.lastIndexOf(">");
                                        int start = value.lastIndexOf("<:");
                                        if (start > end || start == -1) {
                                            return;
                                        }
                                        String subString = value.substring(start, end + 1);
                                        SpannableString ss = new SpannableString(subString);
                                        int px = CmmFunc.convertDpToPx(getActivity(), 40);
                                        BeanEmoticion beanEmoticion = (BeanEmoticion) BeanEmoticion.getMap(getActivity()).get(subString);
                                        if (beanEmoticion != null) {
                                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), beanEmoticion.getIdResource());
                                            bitmap = CmmFunc.resizeBitmap(bitmap, px);
                                            ImageSpan span = new ImageSpan(bitmap);
                                            ss.setSpan(span, 0, subString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        }
                                        text.removeTextChangedListener(textWatcher);
                                        s.delete(start, end + 1);
                                        text.append(ss);
                                        text.addTextChangedListener(textWatcher);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            text.addTextChangedListener(textWatcher);
//                            text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                                @Override
//                                public void onFocusChange(View v, boolean hasFocus) {
//                                    if (hasFocus) {
//                                        emoticionRecycler.setVisibility(View.GONE);
//                                    }
//                                }
//                            });

                            scrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
//                                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && layoutManager.findLastCompletelyVisibleItemPosition() != (chats.size() - 1) && chats.size() > 10) {
//                                        adapter.notifyDataSetChanged();
//                                        moreHistory.setVisibility(View.VISIBLE);
//                                    } else {
//                                        if (moreHistory.getVisibility() == View.GONE) {
//                                            return;
//                                        }
//                                        moreHistory.setVisibility(View.GONE);
//                                    }
                                    try {
                                        recycler.removeOnScrollListener(scrollListener);
                                        showProgress();
                                        page++;
                                        SmartFoxHelper.getChatCSHistory(page);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            recycler.addOnScrollListener(scrollListener);
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
            case R.id.send:
                sendText();
                break;

            case R.id.emotion:
                if (emoticionRecycler.getVisibility() == View.VISIBLE) {
                    emoticionRecycler.setVisibility(View.GONE);
                    emoticion.setImageResource(R.drawable.ic_cs_emotion_unselected);
                } else {
                    emoticionRecycler.setVisibility(View.VISIBLE);
                    emoticion.setImageResource(R.drawable.ic_cs_emotion_selected);
                }
                break;
            case R.id.gallery:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        CallCenterFragment.this.startActivityForResult(intent, GALLERY);
                    }
                }).start();

                break;

            case R.id.capture:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (permisstionExternal()) {
                            openCamera();
                        }

                    }
                }).start();

                break;

//            case R.id.more_history:
//                try {
//                    moreHistory.setVisibility(View.GONE);
//                    recycler.removeOnScrollListener(scrollListener);
//                    showProgress();
//                    page++;
//                    SmartFoxHelper.getChatCSHistory(page);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;

            case R.id.call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", CmmVariable.phoneService, null));
                startActivity(intent);
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
                case CAPTURE:
                    capture();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SmartFoxHelper.getInstance().disconnect();
            }
        }).start();

    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
        if (!(fragment instanceof CallCenterFragment)) {
            return;
        }
        //getActivity().getWindow().addFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, final String permissions[], final int[] grantResults) {
        switch (requestCode) {
            case PERMISTION_EXTERNAL:
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
                            openCamera();
                        }
                    }
                }).start();

                break;
        }
    }
    //endregion Init

    //region Methods
    private void openCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Around" + System.currentTimeMillis());
                values.put(MediaStore.Images.Media.DESCRIPTION, "Around" + System.currentTimeMillis());
                captureURI = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, captureURI);
                startActivityForResult(intent, CAPTURE);
            }
        }).start();
    }

    private void capture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = null;
                    String realPath = CmmFunc.getPathFromUri(getActivity(), captureURI);
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

                        bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), captureURI);
                        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                    } else {
                        bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), captureURI);
                    }


                    bmp = CmmFunc.resizeBitmap(bmp, CmmVariable.IMAGE_CHAT_SIZE);
                    byte[] arr = CmmFunc.bitmapToByteArray(bmp);
                    BeanCSChat beanCSChat = new BeanCSChat();
                    beanCSChat.setSender_avatar(avatar);
                    beanCSChat.setSender_fullname(fullName);
                    beanCSChat.setSender_username(StringUtils.EMPTY);
                    DateTime dateTime = new DateTime();
                    beanCSChat.setTime(dateTime.toString(BeanCSChat.FORMAT));
                    if (SmartFoxHelper.getInstance().isConnected()) {
                        beanCSChat.setSender_username(SmartFoxHelper.getInstance().getMySelf().getName());
                    }
                    final SFSObject chat = new SFSObject();
                    chat.putInt("type", BeanCSChat.IMAGE);
                    chat.putByteArray("content", arr);
                    beanCSChat.setChat(chat);
                    chats.add(beanCSChat);
                    final int position = chats.size() - 1;
                    SmartFoxHelper.chatCS(chat);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                adapter.notifyItemInserted(position);
                                recycler.smoothScrollToPosition(position);
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
                    BeanCSChat beanCSChat = new BeanCSChat();
                    beanCSChat.setSender_avatar(avatar);
                    beanCSChat.setSender_fullname(fullName);
                    beanCSChat.setSender_username(StringUtils.EMPTY);
                    DateTime dateTime = new DateTime();
                    beanCSChat.setTime(dateTime.toString(BeanCSChat.FORMAT));

                    if (SmartFoxHelper.getInstance().isConnected()) {
                        beanCSChat.setSender_username(SmartFoxHelper.getInstance().getMySelf().getName());
                    }
                    final SFSObject chat = new SFSObject();
                    chat.putInt("type", BeanCSChat.IMAGE);
                    chat.putByteArray("content", arr);
                    beanCSChat.setChat(chat);

                    chats.add(beanCSChat);
                    final int position = chats.size() - 1;
                    SmartFoxHelper.chatCS(chat);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                adapter.notifyItemInserted(position);
                                recycler.smoothScrollToPosition(position);
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


    void sendText() {
        final String value = text.getText().toString();
        text.setText(StringUtils.EMPTY);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (value.equals(StringUtils.EMPTY)) {
                        return;
                    }
                    BeanCSChat beanCSChat = new BeanCSChat();
                    beanCSChat.setSender_avatar(avatar);
                    beanCSChat.setSender_fullname(fullName);
                    beanCSChat.setSender_username(StringUtils.EMPTY);
                    DateTime dateTime = new DateTime();
                    beanCSChat.setTime(dateTime.toString(BeanCSChat.FORMAT));
                    if (SmartFoxHelper.getInstance().isConnected()) {
                        beanCSChat.setSender_username(SmartFoxHelper.getInstance().getMySelf().getName() + "");
                    }
                    final SFSObject chat = new SFSObject();
                    chat.putInt("type", BeanCSChat.TEXT);
                    chat.putUtfString("content", value);
                    beanCSChat.setChat(chat);
                    chats.add(beanCSChat);

                    final int position = chats.size() - 1;
                    SmartFoxHelper.chatCS(chat);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                adapter.notifyItemInserted(position);
                                recycler.smoothScrollToPosition(position);
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


    private boolean permisstionExternal() {
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }

            //false nếu từ chối mãi mãi
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISTION_EXTERNAL);
            } else {
                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                messageDialogFragment.setMessage("Please turn on permission Storage!");
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    //endregion

}
