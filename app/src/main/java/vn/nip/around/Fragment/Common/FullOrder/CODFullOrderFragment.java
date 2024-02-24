package vn.nip.around.Fragment.Common.FullOrder;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.List;

import vn.nip.around.Adapter.TabCODAdapter;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CODFullOrderFragment extends BaseFragment implements View.OnClickListener {

    TextView dropAddress, dropRecipent, dropNote;
    ImageView dropCall;
    List<BeanPoint> points;
    RecyclerView recyclerTab;
    TextView index, address, recipentName, itemName, itemCost, note;
    ImageView call;
    ImageButton previous, next;

    public CODFullOrderFragment() {
        // Required empty public constructor
    }

    public static CODFullOrderFragment newInstance(String locations) {
        Bundle args = new Bundle();
        args.putString("locations", locations);
        CODFullOrderFragment fragment = new CODFullOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_codfull_order, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                points = (List<BeanPoint>) CmmFunc.tryParseList(getArguments().getString("locations"), BeanPoint.class);
                dropAddress = (TextView) view.findViewById(R.id.drop_address);
                dropRecipent = (TextView) view.findViewById(R.id.drop_recipient);
                dropNote = (TextView) view.findViewById(R.id.drop_note);
                dropCall = (ImageView) view.findViewById(R.id.drop_call);
                previous = (ImageButton) view.findViewById(R.id.previous);
                next = (ImageButton) view.findViewById(R.id.next);

                index = (TextView) view.findViewById(R.id.index);
                address = (TextView) view.findViewById(R.id.address);
                recipentName = (TextView) view.findViewById(R.id.recipent_name);
                itemName = (TextView) view.findViewById(R.id.item_name);
                itemCost = (TextView) view.findViewById(R.id.item_cost);
                note = (TextView) view.findViewById(R.id.note);
                call = (ImageView) view.findViewById(R.id.call);
                recyclerTab = (RecyclerView) view.findViewById(R.id.recycler_tab);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final BeanPoint drop = points.get(0);
                        dropAddress.setText(drop.getAddress() + StringUtils.EMPTY);
                        dropRecipent.setText(drop.getRecipent_name() + StringUtils.EMPTY);
                        if (!drop.getNote().equals(StringUtils.EMPTY)) {
                            dropNote.setText(drop.getNote() + StringUtils.EMPTY);
                        } else {
                            view.findViewById(R.id.drop_note_area).setVisibility(View.GONE);
                        }
                        recyclerTab.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        recyclerTab.setAdapter(new TabCODAdapter(CODFullOrderFragment.this, recyclerTab, points.size()));
                        if (!drop.getPhone().equals("")) {
                            dropCall.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_now));
                            dropCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", drop.getPhone(), null));
                                    startActivity(intent);
                                }
                            });
                        } else {
                            dropCall.setImageDrawable(getResources().getDrawable(R.drawable.ic_uncall));
                        }
                        setData(0);

                        if (points.get(0).getShipper_fullname().equals(StringUtils.EMPTY)) {
                            view.findViewById(R.id.shipper_area).setVisibility(View.GONE);
                        } else {
                            view.findViewById(R.id.shipper_area).setVisibility(View.VISIBLE);
                            TextView shipperName = (TextView) view.findViewById(R.id.shipper_name);
                            shipperName.setText(points.get(0).getShipper_fullname());
                            view.findViewById(R.id.call_shipper).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", points.get(0).getShipper_phone(), null));
                                    startActivity(intent);
                                }
                            });
                        }

                        previous.setOnClickListener(CODFullOrderFragment.this);
                        next.setOnClickListener(CODFullOrderFragment.this);
                        setColorArrow(0, points.size() - 2);
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    public void setData(int position) {
        try {
            //Điểm đầu là drop
            final BeanPoint bean = points.get(position + 1);
            if (bean != null) {
                index.setText((position + 1) + StringUtils.EMPTY);
                address.setText(bean.getAddress() + StringUtils.EMPTY);
                itemName.setText(bean.getItem_name() + StringUtils.EMPTY);
                itemCost.setText(CmmFunc.formatMoney(bean.getItem_cost(), true));
                recipentName.setText(bean.getRecipent_name() + StringUtils.EMPTY);
                if (!bean.getNote().equals(StringUtils.EMPTY)) {
                    view.findViewById(R.id.note_area).setVisibility(View.VISIBLE);
                    note.setText(bean.getNote() + StringUtils.EMPTY);
                } else {
                    view.findViewById(R.id.note_area).setVisibility(View.GONE);
                }
                if (!bean.getPhone().equals("")) {
                    call.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_now));
                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", bean.getPhone(), null));
                            startActivity(intent);
                        }
                    });
                } else {
                    call.setImageDrawable(getResources().getDrawable(R.drawable.ic_uncall));
                    call.setOnClickListener(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        TabCODAdapter adapter = (TabCODAdapter) recyclerTab.getAdapter();
        int index = 0;
        switch (v.getId()) {
            case R.id.next:
                try {
                    if (adapter != null) {
                        for (int i = 0; i < adapter.list.size(); i++) {
                            if (adapter.list.get(i).isSelected()) {
                                index = i;
                                adapter.list.get(i).setSelected(false);
                                break;
                            }
                        }
                        if (index == points.size() - 2) {
                            adapter.list.get(index).setSelected(true);
                            return;
                        }
                        adapter.list.get(index + 1).setSelected(true);
                        recyclerTab.scrollToPosition(index + 1);
                        adapter.notifyDataSetChanged();
                        setData(index + 1);
                        setColorArrow(index + 1, points.size() - 2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.previous:
                try {
                    if (adapter != null) {
                        for (int i = 0; i < adapter.list.size(); i++) {
                            if (adapter.list.get(i).isSelected()) {
                                index = i;
                                adapter.list.get(i).setSelected(false);
                                break;
                            }
                        }
                        if (index == 0) {
                            adapter.list.get(index).setSelected(true);
                            return;
                        }
                        adapter.list.get(index - 1).setSelected(true);
                        recyclerTab.scrollToPosition(index - 1);
                        adapter.notifyDataSetChanged();
                        setData(index - 1);
                        setColorArrow(index - 1, points.size() - 2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setColorArrow(int position, int size) {
        if (size == 0) {
            previous.setImageResource(R.drawable.ic_arrow_previous);
            next.setImageResource(R.drawable.ic_arrow_next);
            return;
        }
        if (position != 0 && position != size) {
            previous.setImageResource(R.drawable.ic_arrow_previous_orange);
            next.setImageResource(R.drawable.ic_arrow_next_orange);
        }
        if (position == 0) {
            previous.setImageResource(R.drawable.ic_arrow_previous);
            next.setImageResource(R.drawable.ic_arrow_next_orange);
        }
        if (position == size) {
            previous.setImageResource(R.drawable.ic_arrow_previous_orange);
            next.setImageResource(R.drawable.ic_arrow_next);
        }
    }
}
