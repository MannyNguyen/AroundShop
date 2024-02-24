package vn.nip.around.Helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.R;

/**
 * Created by HOME on 11/8/2017.
 */

public class FragmentHelper {
    public static void addFragment(final FragmentActivity activity, int id, Fragment newFragment) {
        try {
            String name = newFragment.getClass().getName();
            final FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(id, newFragment, name)
                    .addToBackStack(name)
                    .commitAllowingStateLoss();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addFragment(FragmentManager manager, int id, Fragment newFragment) {
        try {
            String name = newFragment.getClass().getName();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(id, newFragment, name)
                    .addToBackStack(name)
                    .commitAllowingStateLoss();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeFragment(final FragmentActivity activity, Fragment newFragment) {
        try {
            final FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.remove(newFragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addFragment(final FragmentActivity activity, int id, Fragment newFragment, boolean anim) {
        try {
            String name = newFragment.getClass().getName();
            final FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction
                    .add(id, newFragment, name)
                    .addToBackStack(name)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pop(final FragmentActivity activity) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final FragmentManager manager = activity.getSupportFragmentManager();
                        manager.popBackStack();
                        manager.executePendingTransactions();
                        Fragment f = manager.findFragmentById(R.id.home_content);
                        if (f != null) {
                            f.onResume();
                            ((BaseFragment) f).manualResume();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void replaceFragment(final FragmentActivity activity, int id, Fragment newFragment) {
        try {
            String name = newFragment.getClass().getName();
            final FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(id, newFragment, name)
                    .addToBackStack(name)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void pop(FragmentActivity activity, String tag) {
        try {
            FragmentManager manager = activity.getSupportFragmentManager();
            manager.popBackStackImmediate(tag, 0);
            Fragment f = manager.findFragmentById(R.id.home_content);
            if (f != null) {
                f.onResume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Fragment getActiveFragment(FragmentActivity activity) {
        try {
            activity.getSupportFragmentManager().executePendingTransactions();
            if (activity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                return null;
            }
            String tag = activity.getSupportFragmentManager().getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            return activity.getSupportFragmentManager().findFragmentByTag(tag);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
