package me.sensingself.sensingsugar.Activites.Fragments.TestTab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.viewpagerindicator.LoopingCirclePageIndicator;

import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.util.ActivityCodes;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.ViewPageAdapter;
import me.sensingself.sensingsugar.Constants.PermissionRequest;
import me.sensingself.sensingsugar.Engine.CameraCropLib.CameraConfig;
import me.sensingself.sensingsugar.Engine.CameraCropLib.CropActivity;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Lib.NonSwipeableViewPager;
import me.sensingself.sensingsugar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujie on 1/30/18.
 */

public class SeedTestStepsFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView tsTitle, takePhotoTxtView;
    private ImageView tsBackImageView;

    NonSwipeableViewPager mPager;
    ViewGroup mFrameLayout;
    ViewPageAdapter mAdapter;
    private View rootView;

    private static String fileName = "";

    private static View preRootView;

    private Fragment rinseFragment = SeedTestStepFirstFragment.newInstance("", "", this);
    private Fragment holdSalivaFragment = SeedTestStepSecondFragment.newInstance("", "", this);
    private Fragment beginTestFragment =  SeedTestStepThirdFragment.newInstance("", "", this);
    private Fragment processingTestFragment =  SeedTestStepFourthFragment.newInstance("", "", this);
    private List<Fragment> fragments = new ArrayList<Fragment>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_seed_tab_test_steps, container, false);

        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }
    public static SeedTestStepsFragment newInstance(String param1, String param2, View view) {
        fileName = param1;
        preRootView = view;
        SeedTestStepsFragment fragment = new SeedTestStepsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private void initController(View rootView){
        tsTitle = (TextView) rootView.findViewById(R.id.tsTitle);
        tsBackImageView = (ImageView) rootView.findViewById(R.id.tsBackImageView);
        takePhotoTxtView = (TextView)rootView.findViewById(R.id.takePhotoTxtView);
        takePhotoTxtView.setVisibility(View.GONE);
        tsBackImageView.setImageResource(R.drawable.back_img);

        mFrameLayout = (ViewGroup)rootView.findViewById(R.id.tsPagesContainer);
        mPager = (NonSwipeableViewPager)rootView.findViewById(R.id.tsViewPager);
        mPager.setOffscreenPageLimit(4);
        initViewPager();

        LoopingCirclePageIndicator circlePageIndicator = new LoopingCirclePageIndicator(getActivity());
        circlePageIndicator.setViewPager(mPager);
        circlePageIndicator.setFillColor(Color.parseColor("#fd94bb"));
        circlePageIndicator.setPageColor(Color.parseColor("#d3d3d3"));
        circlePageIndicator.setExtraSpacing(15);
        circlePageIndicator.setPadding(0, 20, 0, 10);

        mFrameLayout.addView(circlePageIndicator);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int pos = position + 1;
                tsTitle.setText("Test: Step " + pos);
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
    }
    private void initViewPager() {
        mAdapter = new ViewPageAdapter(getFragmentManager(), fragments);

        fragments.add(rinseFragment);
        fragments.add(holdSalivaFragment);
        fragments.add(beginTestFragment);
        fragments.add(processingTestFragment);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(0);
    }
    public void onSelectPager(int position){
        mPager.setCurrentItem(position);
    }
    public void gotoTakePhoto(){
        takePhotoTxtView.setVisibility(View.VISIBLE);
    }
    private void initFontAndText(View rootView){
        tsTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        tsTitle.setText(getResources().getString(R.string.begin_test));
        takePhotoTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        takePhotoTxtView.setText(getResources().getString(R.string.take_photo));
    }

    private void setListener(final View rootView){
        tsBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        takePhotoTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });
        takePhotoTxtView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                galleryIntent();
                return true;
            }
        });
    }

    protected void onBackPressed(){
        FragmentmanagerUtils.getFragmentManagerIdentify().popBackStack(FragmentPopKeys.SEEDBEGINTEST, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    private boolean checkPermissions(int requestCode) {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        String[] PERMISSIONS;
        if (requestCode == PermissionRequest.STORAGE_GALLERY) {
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        } else { // PermissionRequest.PERMISSION_ALL
            PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS.length; i++) {
            final String perm = PERMISSIONS[i];
            if (ActivityCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(perm);
            }
        }

        if (permissionsNeeded.size() == 0)
            return true;

        requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]), requestCode);
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionRequest.PERMISSION_ALL:
                if (grantResults.length > 0) {
                    // All requested permissions must be granted.
                    boolean allPermissionsGranted = true;
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (allPermissionsGranted) {
                        cameraIntent();
                        return;
                    }
                }
                Toast.makeText(getActivity(), "No Camera Permission.", Toast.LENGTH_SHORT).show();
                break;
            case PermissionRequest.STORAGE_GALLERY:
                if (grantResults.length > 0) {
                    // All requested permissions must be granted.
                    boolean allPermissionsGranted = true;
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (allPermissionsGranted) {
                        galleryIntent();
                        return;
                    }
                }
                Toast.makeText(getActivity(), "External storage permission is necessary.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void galleryIntent() {
        if (!checkPermissions(PermissionRequest.STORAGE_GALLERY))
            return;

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 19) {
            // For Android versions of KitKat or later, we use a
            // different intent to ensure
            // we can get the file path from the returned intent URI
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.setType("image/*");
        getActivity().startActivityForResult(intent, ActivityCodes.CROP_GALLERY);

        FragmentmanagerUtils.getFragmentManagerIdentify().popBackStack();
    }
    private void cameraIntent() {
        if (!checkPermissions(PermissionRequest.PERMISSION_ALL))
            return;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Intent intent = new Intent(preRootView.getContext(), CropActivity.class);
        intent.putExtra(CameraConfig.RATIO_WIDTH, 200);
        intent.putExtra(CameraConfig.RATIO_HEIGHT, height-100);
        intent.putExtra(CameraConfig.PERCENT_WIDTH, 0.2f);
        intent.putExtra(CameraConfig.MASK_COLOR, 0x2f000000);
        intent.putExtra(CameraConfig.RECT_CORNER_COLOR, 0xff00ff00);
        intent.putExtra(CameraConfig.TEXT_COLOR, 0xffffffff);
        intent.putExtra(CameraConfig.HINT_TEXT, "");
        intent.putExtra(CameraConfig.IMAGE_PATH, Environment.getExternalStorageDirectory().getAbsolutePath()+"/SensingSugar/"+fileName+".jpg");
        getActivity().startActivityForResult(intent, ActivityCodes.CROP_IMAGE);

        FragmentmanagerUtils.getFragmentManagerIdentify().popBackStack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}