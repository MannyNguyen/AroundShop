package vn.nip.around.Fragment.Shopping;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentHostCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.nip.around.Adapter.ShoppingAdapter;
import vn.nip.around.Bean.BeanShop;
import vn.nip.around.Class.CmmAnimation;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingsFragment extends BaseFragment implements View.OnClickListener {

    public ChildShoppingFragment childShoppingFragment;

    public ShoppingsFragment() {
        // Required empty public constructor
    }

    public static ShoppingsFragment newInstance() {

        Bundle args = new Bundle();

        ShoppingsFragment fragment = new ShoppingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_shoppings, container, false);
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
        if (childShoppingFragment == null) {
            return;
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                final RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
                final ShoppingAdapter adapter = new ShoppingAdapter(childShoppingFragment, recycler, childShoppingFragment.shops);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                view.findViewById(R.id.outer).setOnClickListener(ShoppingsFragment.this);
                view.findViewById(R.id.confirm).setOnClickListener(ShoppingsFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(adapter);
                    }
                });
            }
        });
        threadInit.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(FragmentHelper.getActiveFragment(getActivity()) instanceof ShoppingsFragment)) {
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.outer:
                FragmentHelper.pop(getActivity());
                break;
            case R.id.confirm:
                childShoppingFragment.refresh();
                FragmentHelper.pop(getActivity());
                break;
        }
    }
}
