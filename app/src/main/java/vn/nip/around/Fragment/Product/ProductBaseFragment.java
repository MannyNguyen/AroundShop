package vn.nip.around.Fragment.Product;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.ProductAdapter;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Custom.ProductGridLayoutManager;
import vn.nip.around.Custom.ProductListLayoutManager;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.StorageHelper;

/**
 * Created by HOME on 10/26/2017.
 */

public class ProductBaseFragment extends BaseFragment {
    public RecyclerView recycler;
    public List<BeanProduct> products = new ArrayList<>();
    public ProductAdapter adapter;
    public boolean isActive;

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        updateLayout();
    }

    public void updateList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Log.d("ISACTIVE", isActive + "");
                    if (!isActive) {
                        return;
                    }
                    CmmVariable.producteds = APIHelper.getCart();
                    for (BeanProduct beanProduct : products) {
                        beanProduct.setIn_cart(0);
                        for (int i : CmmVariable.producteds) {
                            if (beanProduct.getId() == i) {
                                beanProduct.setIn_cart(1);
                            }
                        }
                    }
                    if (!isActive) {
                        return;
                    }

                    if (recycler != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isActive) {
                                    return;
                                }
                                if (recycler.getAdapter() != null) {
                                    recycler.getAdapter().notifyDataSetChanged();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void updateLayout() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    RecyclerView.LayoutManager layoutManager = recycler.getLayoutManager();
                    if (layoutManager.getClass().getName().equals(LinearLayoutManager.class.getName()) && StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.LIST)) {
                        return;
                    }
                    if (layoutManager.getClass().getName().equals(GridLayoutManager.class.getName()) && StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                        return;
                    }
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    if (!isActive) {
                        return;
                    }
                    final int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.LIST)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                                try {
                                    if (!isActive) {
                                        return;
                                    }
                                    recycler.getAdapter().notifyDataSetChanged();
                                    recycler.scrollToPosition(position);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
                                try {
                                    if (!isActive) {
                                        return;
                                    }
                                    recycler.getAdapter().notifyDataSetChanged();
                                    recycler.scrollToPosition(position);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

}
