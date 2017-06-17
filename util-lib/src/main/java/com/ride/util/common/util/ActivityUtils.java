package com.ride.util.common.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author Stormouble
 * @since 2017/6/18.
 */

public class ActivityUtils {

    private ActivityUtils() {
    }

    public static void addFragmentToActivity(FragmentManager fragmentManager,
                                             Fragment fragment, int frameId) {
        if (fragmentManager == null || fragment == null) {
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment, fragment.getClass().getName());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    public static void startAnotherFragment(FragmentManager fragmentManager,
                                            Fragment fromFragment,
                                            Fragment toFragment, int frameId) {
        if (fragmentManager == null || fromFragment == null || toFragment == null) {
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(frameId, toFragment, toFragment.getClass().getName());
        transaction.hide(fromFragment);
        transaction.addToBackStack(toFragment.getClass().getName());
        transaction.commit();
    }

    public static void handleMultiFragmentBack(FragmentManager fragmentManager,
                                               Activity activity) {
        if (fragmentManager == null || activity == null) {
            return;
        }

        int count = fragmentManager.getBackStackEntryCount();
        if (count > 1) {
            fragmentManager.popBackStack();
        } else {
            activity.finish();
        }
    }

    public static void shwoPopueWindow() {

    }


}
