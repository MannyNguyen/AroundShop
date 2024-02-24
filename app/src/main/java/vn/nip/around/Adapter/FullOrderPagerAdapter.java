package vn.nip.around.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;

import vn.nip.around.R;

/**
 * Created by HOME on 6/2/2017.
 */

public class FullOrderPagerAdapter extends PagerAdapter {
    View view;

    public FullOrderPagerAdapter(View view) {
        this.view = view;
    }

    public Object instantiateItem(View collection, int position) {
        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.page_1;
                break;
            case 1:
                resId = R.id.page_2;
                break;
        }
        return view.findViewById(resId);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }
}
