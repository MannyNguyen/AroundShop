package vn.nip.around.Custom;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by HOME on 11/8/2017.
 */

public class ProductGridLayoutManager extends GridLayoutManager {
    public ProductGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ProductGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public ProductGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollVertically() {
        //return super.canScrollVertically();
        return false;
    }
}
