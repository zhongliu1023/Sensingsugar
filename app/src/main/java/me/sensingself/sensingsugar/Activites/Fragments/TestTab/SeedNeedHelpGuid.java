package me.sensingself.sensingsugar.Activites.Fragments.TestTab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.R;

/**
 * Created by liujie on 1/24/18.
 */

public class SeedNeedHelpGuid extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView noteNeedHelp, guidTxt, guidTitle;
    private ImageView backGuid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seed_tab_guid, container, false);

        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }
    public static SeedNeedHelpGuid newInstance(String param1, String param2) {
        SeedNeedHelpGuid fragment = new SeedNeedHelpGuid();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private void initController(View rootView){
        guidTitle = (TextView) rootView.findViewById(R.id.guid_title_text);
        noteNeedHelp = (TextView) rootView.findViewById(R.id.noteGuidTxt);
        guidTxt = (TextView) rootView.findViewById(R.id.guidTxt);
        backGuid = (ImageView)rootView.findViewById(R.id.backGuidImageView);
        backGuid.setImageResource(R.drawable.back_img);
    }

    private void initFontAndText(View rootView){
        guidTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        noteNeedHelp.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        guidTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
    }

    private void setListener(final View rootView){
        guidTitle.setText(getResources().getString(R.string.need_help));
        backGuid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void onBackPressed(){
        FragmentmanagerUtils.getFragmentManagerIdentify().popBackStack(FragmentPopKeys.SEEDBEGINTEST, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}