package me.sensingself.sensingsugar.Activites.Fragments.TestTab;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.R;

import me.sensingself.sensingsugar.Lib.Utility;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Created by liujie on 1/24/18.
 */

public class SeedFragmentRoot extends Fragment {
    public static final String ARG_OBJECT = "object";//testing

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seed_tab_root, container, false);
        Bundle args = getArguments();
        int position = args.getInt(ARG_OBJECT);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentmanagerUtils.setFragmentManagerIdentify(fragmentManager);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.seed_root_frame, new SeedFragment());
        transaction.commit();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}