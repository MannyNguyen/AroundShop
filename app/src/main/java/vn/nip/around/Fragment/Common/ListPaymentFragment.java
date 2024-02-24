package vn.nip.around.Fragment.Common;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.nip.around.Adapter.PaymentMethodAdapter;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListPaymentFragment extends BaseFragment {

    public RecyclerView recyclerView;

    public String paymentType;

    //region Contructors
    public ListPaymentFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_list_payment, container, false);
        }
        return view;
    }

    public static ListPaymentFragment newInstance(String paymentType) {

        Bundle args = new Bundle();

        ListPaymentFragment fragment = new ListPaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isLoaded == false) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_payment);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            PaymentMethodAdapter adapter = new PaymentMethodAdapter(getActivity(), ListPaymentFragment.this, recyclerView);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    public void reload() {
        try {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_payment);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            PaymentMethodAdapter adapter = new PaymentMethodAdapter(getActivity(), ListPaymentFragment.this, recyclerView);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Actions

    //endregion


}
