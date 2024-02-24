package vn.nip.around.Fragment.Profile;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanAddress;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Custom.CustomTextView;
import vn.nip.around.Fragment.Common.*;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileAddressFragment extends BaseFragment implements View.OnClickListener {

    ProfileFragment profileFragment;
    String tab;
    View container;
    EditText address;
    RecyclerView recycler;
    List<BeanAddress> items = new ArrayList<>();
    ProfileAddressAdapter adapter;
    Handler handler;

    public ProfileAddressFragment() {
        // Required empty public constructor
    }

    public static ProfileAddressFragment newInstance(String tab) {

        Bundle args = new Bundle();
        args.putString("tab", tab);
        ProfileAddressFragment fragment = new ProfileAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile_address, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tab = getArguments().getString("tab");
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
                    container = view.findViewById(R.id.container);
                    address = (EditText) view.findViewById(R.id.address);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    adapter = new ProfileAddressAdapter(ProfileAddressFragment.this, recycler, items);
                    view.findViewById(R.id.pin).setOnClickListener(ProfileAddressFragment.this);
                    view.findViewById(R.id.remove).setOnClickListener(ProfileAddressFragment.this);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            address.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(final Editable s) {
                                    try {
                                        if (s.toString().equals(StringUtils.EMPTY)) {
                                            items.clear();
                                            adapter.notifyDataSetChanged();
                                            handler.removeCallbacksAndMessages(null);
                                            return;
                                        }
                                        handler.removeCallbacksAndMessages(null);
                                        // Now add a new one
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                items.clear();
                                                threadAPI(s.toString());
                                                handler.sendEmptyMessage(1);
                                            }
                                        }, 500);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            container.setTranslationY(AppActivity.WINDOW_HEIGHT / 2);
                            container.animate().translationY(0).setDuration(300).start();
                            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recycler.setAdapter(adapter);
                            HandlerThread handlerThread = new HandlerThread(ProfileAddressFragment.this.getClass().getName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
                            handlerThread.start();
                            handler = new Handler(handlerThread.getLooper()) {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //adapter.notifyDataSetChanged();
                                            }
                                        });

                                    }
                                }
                            };
                        }
                    });
                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }


    private void threadAPI(final String input) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json");
                    sb.append("?key=" + CmmVariable.GOOGLE_KEY);
                    sb.append("&input=" + URLEncoder.encode(input, "utf8"));
                    sb.append("&components=");
                    sb.append("country:vn");
                    sb.append("&language=vi");
                    String response = HttpHelper.get(sb.toString(), null);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray predsJsonArray = jsonObject.getJSONArray("predictions");
                    for (int i = 0; i < predsJsonArray.length(); i++) {
                        BeanAddress bean = new BeanAddress();
                        bean.setAddress(predsJsonArray.getJSONObject(i).getString("description"));
                        bean.setPlaceID(predsJsonArray.getJSONObject(i).getString("place_id"));
                        JSONObject structured_formatting = new JSONObject(predsJsonArray.getJSONObject(i).getString("structured_formatting"));
                        bean.setName(structured_formatting.getString("main_text"));
                        items.add(bean);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pin:
                FragmentHelper.replaceFragment(getActivity(), R.id.home_content, ProfilePinFragment.newInstance(tab));
                break;
            case R.id.remove:
                address.setText("");
                break;
        }
    }


    public class ProfileAddressAdapter extends RecyclerView.Adapter<ProfileAddressAdapter.AddressViewHolder> {
        View itemView;
        Fragment fragment;
        RecyclerView recycler;
        public List<BeanAddress> list;


        public ProfileAddressAdapter(Fragment fragment, RecyclerView recycler, List<BeanAddress> list) {
            this.fragment = fragment;
            this.recycler = recycler;
            this.list = list;

        }

        @Override
        public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_address, parent, false);
            return new AddressViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(AddressViewHolder holder, final int position) {
            try {
                final BeanAddress item = list.get(position);
                if (item != null) {
                    holder.address.setText(item.getAddress());
                    holder.name.setText(item.getName());

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("address", item.getAddress());
                                        jsonObject.put("place_id", item.getPlaceID());
                                        StorageHelper.set(tab.toLowerCase(), jsonObject.toString());
                                        FragmentHelper.pop(getActivity());
                                        CmmFunc.hideKeyboard(getActivity());

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();


                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class AddressViewHolder extends RecyclerView.ViewHolder {
            TextView address;
            TextView name;

            public AddressViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                address = (CustomTextView) view.findViewById(R.id.address);
            }
        }
    }
}
