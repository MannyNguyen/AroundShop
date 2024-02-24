package vn.nip.around.Fragment.Pickup;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;

import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmAnimation;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Dialog.BaseDialogFragment;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoBookFragment extends BaseDialogFragment implements View.OnClickListener {

    FrameLayout content;
    Fragment child;
    int currentType = -1;
    TextView position, address;
    //Spinner type;

    public InfoBookFragment() {
        // Required empty public constructor
    }

    public static InfoBookFragment newInstance(int type, int position) {

        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("position", position);
        InfoBookFragment fragment = new InfoBookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_book, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    content = (FrameLayout) view.findViewById(R.id.child_fragment_container);
                    position = (TextView) view.findViewById(R.id.position);
                    address = (TextView) view.findViewById(R.id.address);
                    //type = (Spinner) view.findViewById(R.id.type);


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            position.setText((getArguments().getInt("position") + 1) + "");
                            HomeBookFragment homeBookFragment = (HomeBookFragment) CmmFunc.getFragmentByTag(getActivity(), HomeBookFragment.class.getName());
                            if (homeBookFragment != null) {
                                if (getArguments().getInt("position") == homeBookFragment.points.size() - 1) {
                                    updateTab(BeanPoint.DROP);
                                } else {
                                    view.findViewById(R.id.header).setVisibility(View.VISIBLE);
                                    address.setText(homeBookFragment.points.get(getArguments().getInt("position")).getAddress());
                                    //final String[] choices = {getString(R.string.transport), getString(R.string.purchase), getString(R.string.collect)};
                                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.row_spinner_pickup_type, choices);
                                    //type.setAdapter(adapter);
                                    //type.setOnItemSelectedListener(InfoBookFragment.this);
//                                switch (getArguments().getInt("type")) {
//                                    case BeanPoint.TRANSPORT:
//                                        type.setSelection(0);
//                                        break;
//                                    case BeanPoint.PURCHASE:
//                                        type.setSelection(1);
//                                        break;
//                                    case BeanPoint.COD:
//                                        type.setSelection(2);
//                                        break;
//                                }
                                    updateTab(getArguments().getInt("type"));
                                }
                            }


                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void updateTab(int id) {
        try {
            if (currentType == id) {
                return;
            }
            //currentType = id;
            if (child != null) {
                getChildFragmentManager().beginTransaction().remove(child).commit();
            }

            int position = getArguments().getInt("position");
            if (id == BeanPoint.TRANSPORT) {
                child = PopupTransportFragment.newInstance(position);
            } else if (id == BeanPoint.PURCHASE) {
                child = PopupPurchaseFragment.newInstance(position);
            } else if (id == BeanPoint.COD) {
                child = PopupCODFragment.newInstance(position);
            } else if (id == BeanPoint.DROP) {
                child = PopupDropFragment.newInstance(position);
            }
            getChildFragmentManager().beginTransaction().add(R.id.child_fragment_container, child).commit();
        } catch (Exception e) {

        } finally {

        }
    }


}
