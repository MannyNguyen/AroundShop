package vn.nip.around.Fragment.Common;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import vn.nip.around.Adapter.ChatAdapter;
import vn.nip.around.Bean.BeanChat;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.io.BufferedInputStream;
import java.io.InputStream;

import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.PublicMessageRequest;

public class ChatFragment extends BaseFragment implements View.OnClickListener, TextView.OnEditorActionListener {
    ChatAdapter adapter;
    //region Private variables
    View view;
    RecyclerView recycler;
    ImageButton pickup;
    ImageButton send;
    EditText content;

    //BeanShipper shipper;
    //endregion
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat, container, false);

        }

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CmmFunc.setDelay(400, new ICallback() {
            @Override
            public void excute() {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                TextView title = (TextView) view.findViewById(R.id.title);
                title.setText(getResources().getString(R.string.chat));
                CmmVariable.chats.clear();
                FollowJourneyFragment fragment = FollowJourneyFragment.isExist();
                if (fragment != null) {
                    fragment.numberChat = 0;
                }


                recycler = (RecyclerView) view.findViewById(R.id.recycler);
                adapter = new ChatAdapter(getActivity(), recycler, CmmVariable.chats);
                send = (ImageButton) view.findViewById(R.id.send);
                pickup = (ImageButton) view.findViewById(R.id.pick_img);
                ImageButton shoot = (ImageButton) view.findViewById(R.id.shoot);
                content = (EditText) view.findViewById(R.id.content);
                content.setOnEditorActionListener(ChatFragment.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                layoutManager.setStackFromEnd(true);
                recycler.setLayoutManager(layoutManager);
                recycler.setItemViewCacheSize(5);
                recycler.setAdapter(adapter);

                send.setOnClickListener(ChatFragment.this);
                pickup.setOnClickListener(ChatFragment.this);
                shoot.setOnClickListener(ChatFragment.this);
                getChatHistory();
            }
        });
    }

    //region Events
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                sendText();
                break;
            case R.id.pick_img:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                this.startActivityForResult(intent, 1999);
                break;
            case R.id.shoot:
                Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                this.startActivityForResult(intent1, 2000);
                break;
        }
    }

    //endregion

    //region Actions
    void sendText() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    BeanChat bean = new BeanChat();
                    if (content.getText().toString().trim().length() == 0)
                        return;
                    bean.setChat_description(content.getText() + "");
                    bean.setMessage("TEXT_TYPE");
                    CmmVariable.chats.add(bean);

                    adapter.notifyItemInserted(CmmVariable.chats.size());
                    recycler.scrollToPosition(CmmVariable.chats.size() - 1);

                    SFSObject data = new SFSObject();
                    data.putUtfString("chat_description", content.getText() + "");
                    data.putInt("order_id_chat", getArguments().getInt("order_id"));
                    SmartFoxHelper.getInstance().send(new PublicMessageRequest(bean.getMessage(), data));

                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "send", e.getMessage());
                } finally {
                    content.setText("");
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1999 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            new ActionSendImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        }
        if (requestCode == 2000 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            new ActionSendImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        }
    }

    void getChatHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_CHAT_HISTORY");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "getChatHistory", e.getMessage());
                }
            }
        }).start();

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            sendText();
            return true;
        }
        return false;
    }


    class ActionSendImage extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            try {

                //Something errors upload image from emulator
                BeanChat bean = new BeanChat();
                bean.setMessage("IMAGE_TYPE");
                CmmVariable.chats.add(bean);

                Intent data = (Intent) objects[0];
                Bitmap bmp = null;

                if (data.getData() == null) {
                    bmp = (Bitmap) data.getExtras().get("data");
                } else {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    bmp = BitmapFactory.decodeStream(bufferedInputStream);
                }

                bmp = CmmFunc.resizeBitmap(bmp, CmmVariable.IMAGE_CHAT_SIZE);
                byte[] arr = CmmFunc.bitmapToByteArray(bmp);


                BeanChat beanChat = CmmVariable.chats.get(CmmVariable.chats.size() - 1);
                beanChat.setImage(arr);
                beanChat.setBitmap(bmp);
                beanChat.setChat_description("");

                ISFSObject sfsObject = new SFSObject();
                sfsObject.putByteArray("chat_description", arr);
                sfsObject.putInt("order_id_chat", getArguments().getInt("order_id"));
                SmartFoxHelper.getInstance().send(new PublicMessageRequest(bean.getMessage(), sfsObject));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemInserted(CmmVariable.chats.size());
                        recycler.scrollToPosition(CmmVariable.chats.size() - 1);
                    }
                });
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionSendImage", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    recycler.scrollToPosition(CmmVariable.chats.size() - 1);
                }
            });

        }
    }


    //endregion


}
