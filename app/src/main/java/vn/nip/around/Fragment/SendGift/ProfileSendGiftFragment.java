package vn.nip.around.Fragment.SendGift;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import vn.nip.around.Bean.BeanSendGift;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Custom.CircleTransform;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSendGiftFragment extends BaseFragment implements View.OnClickListener {

    BeanSendGift param;

    ImageView avatar;
    TextView name, birthday;


    public ProfileSendGiftFragment() {
        // Required empty public constructor
    }

    public static ProfileSendGiftFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        ProfileSendGiftFragment fragment = new ProfileSendGiftFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile_send_gift, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        param = (BeanSendGift) CmmFunc.tryParseJson(getArguments().getString("data"), BeanSendGift.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.send_gift));
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    avatar = (ImageView) view.findViewById(R.id.avatar);
                    name = (TextView) view.findViewById(R.id.name);
                    birthday = (TextView) view.findViewById(R.id.birthday);
                    view.findViewById(R.id.confirm).setOnClickListener(ProfileSendGiftFragment.this);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (param != null) {
                                Picasso.with(getActivity()).load(param.getAvatar()).transform(new CircleTransform()).into(avatar);
                                name.setText(param.getFullname());
                                birthday.setText(param.getBirthday());
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
            case R.id.confirm:
                TypeSendGiftFragment.newInstance(getArguments().getString("data")).show(getActivity().getSupportFragmentManager(), TypeSendGiftFragment.class.getName());
                break;
        }
    }
}
