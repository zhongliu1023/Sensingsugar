package me.sensingself.sensingsugar.Activites.Fragments.SearchTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.R;

/**
 * Created by liujie on 1/24/18.
 */

public class SearchFragmentRoot extends Fragment {
    public static final String ARG_OBJECT = "object";//testing
    public static final String ARG_USER_RESULT = "arg_user_result";

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_tab_root, container, false);
        Bundle args = getArguments();
        int position = args.getInt(ARG_OBJECT);
        boolean isUserResult = args.getBoolean(ARG_USER_RESULT);

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentmanagerUtils.setFragmentManagerSearch(fragmentManager);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (isUserResult){
            transaction.replace(R.id.search_root_frame, SearchResultsFragment.newInstance("", ""));
        }else{
            transaction.replace(R.id.search_root_frame, new SearchFragment());
        }

        transaction.commit();

        return rootView;
    }
}