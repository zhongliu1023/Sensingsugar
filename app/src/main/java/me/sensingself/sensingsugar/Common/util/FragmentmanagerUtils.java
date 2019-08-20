package me.sensingself.sensingsugar.Common.util;

import android.support.v4.app.FragmentManager;

/**
 * Created by liujie on 3/1/18.
 */

public class FragmentmanagerUtils {

    public static FragmentManager fragmentmanagerHome;
    public static FragmentManager fragmentmanagerIdentify;
    public static FragmentManager fragmentmanagerSearch;
    public static FragmentManager fragmentmanagerReports;

    public static FragmentManager getFragmentManagerHome() {
        return fragmentmanagerHome;
    }
    public static void setFragmentManagerHome(FragmentManager fragmentManager) {
        fragmentmanagerHome = fragmentManager;
    }

    public static FragmentManager getFragmentManagerIdentify() {
        return fragmentmanagerIdentify;
    }
    public static void setFragmentManagerIdentify(FragmentManager fragmentManager) {
        fragmentmanagerIdentify = fragmentManager;
    }

    public static FragmentManager getFragmentManagerSearch() {
        return fragmentmanagerSearch;
    }
    public static void setFragmentManagerSearch(FragmentManager fragmentManager) {
        fragmentmanagerSearch = fragmentManager;
    }

    public static FragmentManager getFragmentManagerReports() {
        return fragmentmanagerReports;
    }
    public static void setFragmentManagerReports(FragmentManager fragmentManager) {
        fragmentmanagerReports = fragmentManager;
    }
}
