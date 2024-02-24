package vn.nip.around.Fragment.SendGift;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.nip.around.Adapter.SendGiftAdapter;
import vn.nip.around.Bean.BeanCategory;
import vn.nip.around.Bean.BeanContact;
import vn.nip.around.Bean.BeanSendGift;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendGiftFragment extends BaseFragment {

    RecyclerView recycler;
    SendGiftAdapter adapter;
    List<BeanSendGift> items = new ArrayList<>();
    public String message;


    public SendGiftFragment() {
        // Required empty public constructor
    }

    public static SendGiftFragment newInstance() {

        Bundle args = new Bundle();

        SendGiftFragment fragment = new SendGiftFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_send_gift, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartFragment.RECEIVER = null;
                    FragmentHelper.pop(getActivity());
                }
            });
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.send_gift));
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    adapter = new SendGiftAdapter(SendGiftFragment.this, recycler, items);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recycler.setAdapter(adapter);
                            new ActionGet().execute();
                        }
                    });
                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }

    class ActionGet extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {

                List<BeanContact> contacts = BeanContact.get(getContext());
                if (contacts != null) {
                    String response = APIHelper.getSendGift();
                    jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    Map map = new HashMap<>();
                    if (code == 1) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray arr = data.getJSONArray("contacts");
                        message = data.getString("sms_message");
                        for (int i = 0; i < arr.length(); i++) {
                            BeanSendGift bean = (BeanSendGift) CmmFunc.tryParseJson(arr.getString(i), BeanSendGift.class);
                            bean.setInstall(true);
                            map.put(bean.getPhone(), bean);
                        }
                    }
                    for (BeanContact contact : contacts) {
                        BeanSendGift beanSendGift = new BeanSendGift();
                        if (!map.containsKey(contact.getPhone1())) {
                            beanSendGift.setFullname(contact.getName());
                            beanSendGift.setPhone(contact.getPhone1());
                        } else {
                            beanSendGift = (BeanSendGift) map.get(contact.getPhone1());
                            beanSendGift.setFullname(contact.getName());
                        }
                        items.add(beanSendGift);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                recycler.getAdapter().notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

}
