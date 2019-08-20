package me.sensingself.sensingsugar.Activites;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import me.sensingself.sensingsugar.Activites.Fragments.HomeTab.HomeFragment;
import me.sensingself.sensingsugar.Activites.Fragments.HomeTab.HomeFragmentRoot;
import me.sensingself.sensingsugar.Activites.Fragments.HomeTab.HomeProfileViewFragment;
import me.sensingself.sensingsugar.Activites.Fragments.ReportsTab.ReportsFragment;
import me.sensingself.sensingsugar.Activites.Fragments.ReportsTab.ReportsFragmentRoot;
import me.sensingself.sensingsugar.Activites.Fragments.SearchTab.PatientProfileViewFragment;
import me.sensingself.sensingsugar.Activites.Fragments.SearchTab.SearchFragment;
import me.sensingself.sensingsugar.Activites.Fragments.SearchTab.SearchFragmentRoot;
import me.sensingself.sensingsugar.Activites.Fragments.TestTab.SeedBeginTestFragment;
import me.sensingself.sensingsugar.Activites.Fragments.TestTab.SeedFragment;
import me.sensingself.sensingsugar.Activites.Fragments.TestTab.SeedFragmentRoot;
import me.sensingself.sensingsugar.Activites.Fragments.TestTab.SeedTestReportFragment;
import me.sensingself.sensingsugar.Common.ActivityResults.ActivityResultBus;
import me.sensingself.sensingsugar.Common.ActivityResults.ActivityResultEvent;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.ActivityCodes;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Lib.NonSwipeableViewPager;
import me.sensingself.sensingsugar.R;

/**
 * Created by liujie on 1/12/18.
 */

public class HomeActivity extends FragmentActivity {
    private ImageView imgHomeTab, imgSeedTab, imgSearchTab, imgNoteTab;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private NonSwipeableViewPager mViewPager;

    private LinearLayout statusBarLayout;

    private SharedPreferencesManager sharedPreferencesManager;

    boolean isSearchedUsers = false;

    int selectedTabIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

        init();
        initFontAndText();
        setListener();
    }
    private void init(){
        imgHomeTab = (ImageView) findViewById(R.id.img_home_tab);
        imgSeedTab = (ImageView) findViewById(R.id.img_seed_tab);
        imgSearchTab = (ImageView) findViewById(R.id.img_search_tab);
        imgNoteTab = (ImageView) findViewById(R.id.img_note_tab);

        statusBarLayout = (LinearLayout) findViewById(R.id.statusBarLayout);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.frame_content);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);

//        int typeToMove = sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER);
//        switch (typeToMove){
//            case SharedPreferencesKeys.FROM_REGISTER:
//                mViewPager.setCurrentItem(1);
//                changedTabIcons(1);
//                break;
//            default:
                mViewPager.setCurrentItem(0);
                changedTabIcons(0);
//                break;
//        }
    }
    private void initFontAndText(){

    }

    private void setListener() {

        //Set su kien click
        imgHomeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTabIndex != 0)
                {
                    List fragmentList = FragmentmanagerUtils.getFragmentManagerHome().getFragments();
                    for(Object f : fragmentList) {
                        if(f instanceof HomeFragment) {
                            ((HomeFragment)f).onReloadInfomation();
                            break;
                        }
                    }
                }
                mViewPager.setCurrentItem(0);
                changedTabIcons(0);
            }
        });
        imgSeedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTabIndex == 1)
                {
                    List fragmentList = FragmentmanagerUtils.getFragmentManagerIdentify().getFragments();
                    for(Object f : fragmentList) {
                        if(f instanceof SeedTestReportFragment) {
                            ((SeedTestReportFragment)f).onFirstFragmentBac();
                            break;
                        }
                    }
                }else{
                    mViewPager.setCurrentItem(1);
                    changedTabIcons(1);
                }
            }
        });
        imgSearchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(2);
                changedTabIcons(2);
            }
        });
        imgNoteTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTabIndex != 3)
                {
                    List fragmentList = FragmentmanagerUtils.getFragmentManagerReports().getFragments();
                    for(Object f : fragmentList) {
                        if(f instanceof ReportsFragment) {
                            ((ReportsFragment)f).onReloadReports();
                            break;
                        }
                    }
                }
                mViewPager.setCurrentItem(3);
                changedTabIcons(3);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                changedTabIcons(position);
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ActivityCodes.CROP_IMAGE:
                case ActivityCodes.CROP_GALLERY:
                case ActivityCodes.REQUEST_IMAGE_CAPTURE:
                case ActivityCodes.REQUEST_IMAGE_GALLERY:
                case ActivityCodes.REQUEST_IMAGE_CROP:
                    ActivityResultBus.getInstance().postQueue(
                            new ActivityResultEvent(requestCode, resultCode, data));
                    break;
            }
        }
    }
    private boolean isFirstPage(List fragmentList){
        for(Object f : fragmentList) {
            switch (selectedTabIndex){
                case 0:
                    if(f instanceof HomeFragment) return true;
                    break;
                case 1:
                    if(f instanceof SeedFragment) return true;
                    break;
                case 2:
                    if(f instanceof SearchFragment) return true;
                    break;
                case 3:
                    if(f instanceof ReportsFragment) return true;
                    break;
            }
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        switch (selectedTabIndex){
            case 0:
                if (isFirstPage(FragmentmanagerUtils.getFragmentManagerHome().getFragments())){
                    super.onBackPressed();
                    break;
                }
            case 1:
                if (isFirstPage(FragmentmanagerUtils.getFragmentManagerIdentify().getFragments())){
                    mViewPager.setCurrentItem(0);
                    break;
                }
            case 2:
                if (isFirstPage(FragmentmanagerUtils.getFragmentManagerSearch().getFragments())){
                    mViewPager.setCurrentItem(0);
                    break;
                }
            case 3:
                if (isFirstPage(FragmentmanagerUtils.getFragmentManagerReports().getFragments())){
                    mViewPager.setCurrentItem(0);
                    break;
                }
            default:
                FragmentsBus.getInstance().postQueue(
                        new FragmentsEvents(FragmentsEventsKeys.BACKPRESSED));
                break;
        }
    }
    private Drawable changeImageColor(int color, Drawable mDrawable){
        Drawable changedDrawable = mDrawable;
        changedDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return changedDrawable;
    }
    private boolean isHomePage(){
        if (FragmentmanagerUtils.getFragmentManagerHome() != null){
            List fragmentList = FragmentmanagerUtils.getFragmentManagerHome().getFragments();
            for(Object f : fragmentList) {
                if(f instanceof HomeFragment) {
                    return true;
                }
            }
        }
        return false;
    }
    public void changeDarkTheme(){
        statusBarLayout.setBackgroundColor(getResources().getColor(R.color.homeGradientDark));
    }
    public void changeLightTheme(){
        statusBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }
    private void changedTabIcons(int position){
        if (position == 0 && isHomePage()) {
            changeDarkTheme();
        } else {
            changeLightTheme();
        }
        selectedTabIndex = position;
        switch (position) {
            case 0:
//                showFragment(new HomeFragmentRoot());
                imgHomeTab.setImageDrawable(changeImageColor(Color.WHITE, getResources().getDrawable(R.drawable.home_tab)));
                imgSeedTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.seed_tab)));
                imgSearchTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.search_tab)));
                imgNoteTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.note_tab)));
                break;
            case 1:
                sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER, SharedPreferencesKeys.FROM_LOGIN);
//                showFragment(new SeedFragmentRoot());
                imgHomeTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.home_tab)));
                imgSeedTab.setImageDrawable(changeImageColor(Color.WHITE, getResources().getDrawable(R.drawable.seed_tab)));
                imgSearchTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.search_tab)));
                imgNoteTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.note_tab)));
                break;
            case 2:
                sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER, SharedPreferencesKeys.FROM_LOGIN);
//                showFragment(new SearchFragmentRoot());
                imgHomeTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.home_tab)));
                imgSeedTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.seed_tab)));
                imgSearchTab.setImageDrawable(changeImageColor(Color.WHITE, getResources().getDrawable(R.drawable.search_tab)));
                imgNoteTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.note_tab)));
                break;
            case 3:
                sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER, SharedPreferencesKeys.FROM_LOGIN);
//                showFragment(new SearchFragmentRoot());
                imgHomeTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.home_tab)));
                imgSeedTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.seed_tab)));
                imgSearchTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.search_tab)));
                imgNoteTab.setImageDrawable(changeImageColor(Color.WHITE, getResources().getDrawable(R.drawable.note_tab)));
                break;

            default:
                sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER, SharedPreferencesKeys.FROM_LOGIN);
//                showFragment(new ReportsFragmentRoot());
                imgHomeTab.setImageDrawable(changeImageColor(Color.WHITE, getResources().getDrawable(R.drawable.home_tab)));
                imgSeedTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.seed_tab)));
                imgSearchTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.search_tab)));
                imgNoteTab.setImageDrawable(changeImageColor(getResources().getColor(R.color.colorBlueGradient), getResources().getDrawable(R.drawable.note_tab)));
                break;
        }
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment fragment;
            Bundle args = new Bundle();
            switch (position) {
                case 0:
                    fragment = new HomeFragmentRoot();
                    args.putInt(HomeFragmentRoot.ARG_OBJECT, position + 1);
                    break;
                case 1:
                    fragment = new SeedFragmentRoot();
                    args.putInt(SeedFragmentRoot.ARG_OBJECT, position + 1);
                    break;
                case 2:
                    fragment = new SearchFragmentRoot();
                    args.putInt(SearchFragmentRoot.ARG_OBJECT, position + 1);
                    args.putBoolean(SearchFragmentRoot.ARG_USER_RESULT, isSearchedUsers);
                    isSearchedUsers = false;
                    break;
                case 3:
                    fragment = new ReportsFragmentRoot();
                    args.putInt(ReportsFragmentRoot.ARG_OBJECT, position + 1);
                    break;

                default:
                    fragment = new HomeFragment();
                    break;
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    public void gotoTestBySelectedPatientView(){
        mViewPager.setCurrentItem(1);
        changedTabIcons(1);

        FragmentmanagerUtils.getFragmentManagerIdentify().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.seed_root_frame, SeedBeginTestFragment.newInstance(FragmentPopKeys.SEED, "", true), "Post")
                .addToBackStack(null)
                .commit();
    }
    public void gotoSeedView(){
        mViewPager.setCurrentItem(1);
        changedTabIcons(1);
    }
    public void gotoSearchTab(){
        mViewPager.setCurrentItem(2);
        changedTabIcons(2);
    }
}
