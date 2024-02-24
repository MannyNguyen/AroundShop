package vn.nip.around.Fragment.Payment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import vn.nip.around.Adapter.BankAdapter;
import vn.nip.around.Bean.BeanBank;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Product.ProductsFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListBankFragment extends BaseFragment {

    //region Variables
    //endregion

    public ListBankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_list_bank, container, false);
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
            if (!(fragment instanceof ListBankFragment)) {
                return;
            }
            FrameLayout layoutProgress = (FrameLayout) getView().findViewById(R.id.layout_progress);
            layoutProgress.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {

            TextView title = (TextView) getView().findViewById(R.id.title);
            title.setText(getResources().getString(R.string.payment_methods));
            CmmFunc.setDelay(400, new ICallback() {
                @Override
                public void excute() {
                    try {
                        List<BeanBank> banks = (List<BeanBank>) CmmFunc.tryParseList(getArguments().getString("data"), BeanBank.class);
                        int orderID = getArguments().getInt("order_id");
                        String paymentCode = getArguments().getString("payment_code");
                        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler);
                        BankAdapter adapter = new BankAdapter(getActivity(), recyclerView, banks, orderID, paymentCode);
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        isLoaded = true;
                    } catch (Exception e) {

                    }
                }
            });
        }
    }
}
