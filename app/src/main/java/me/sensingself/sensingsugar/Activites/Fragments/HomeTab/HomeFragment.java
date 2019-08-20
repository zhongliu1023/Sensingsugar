package me.sensingself.sensingsugar.Activites.Fragments.HomeTab;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.imagecache.CImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import me.sensingself.sensingsugar.Activites.Adapter.NewsAdapter;
import me.sensingself.sensingsugar.Activites.HomeActivity;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.util.URLConstant;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.News;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import me.sensingself.sensingsugar.webutil.AccountClient;

/**
 * Created by liujie on 1/12/18.
 */

public class HomeFragment extends Fragment {
    private TextView greetingUser;
    private CircleImageView profileImage;
    private TextView todayTxtView, totalReadingsTxtView, readingsCountTxtView,totalPatientTxtView, patentCountTxtView, diabeticTitleTxtView;
    private TextView diabeticValueTxtView, preDiabeticTitleTxtView, preDiabeticValueTxtView;

    private Button beginTestingBtn;
    private RelativeLayout noResultLayout;

    private TextView newsTxtView;
    private ListView listNews;
    ArrayList<News> feeds = new ArrayList<News>();
    NewsAdapter adapter;

    private SharedPreferencesManager sharedPreferencesManager;
    private UserVo currentUserVo = new UserVo();

    private AccountClient accountClient;

    FragmentManager fragmentManager;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_tab, container, false);

        fragmentManager = FragmentmanagerUtils.getFragmentManagerHome();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        accountClient = new AccountClient();

        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onReloadInfomation();
    }
    public void onReloadInfomation(){
        initNewUserVo();
        initUpdateValue();

        updateCurrentProfile();
    }
    private void initController(View rootView){

        greetingUser = (TextView) rootView.findViewById(R.id.greetingUser);
        profileImage = (CircleImageView) rootView.findViewById(R.id.profileImage);
        profileImage.setImageResource(R.drawable.profile_man);
        todayTxtView = (TextView) rootView.findViewById(R.id.todayTxtView);
        totalReadingsTxtView = (TextView) rootView.findViewById(R.id.totalReadingsTxtView);
        readingsCountTxtView = (TextView) rootView.findViewById(R.id.readingsCountTxtView);
        totalPatientTxtView = (TextView) rootView.findViewById(R.id.totalPatientTxtView);
        patentCountTxtView = (TextView) rootView.findViewById(R.id.patentCountTxtView);
        diabeticTitleTxtView = (TextView) rootView.findViewById(R.id.diabeticTitleTxtView);
        diabeticValueTxtView = (TextView)rootView.findViewById(R.id.diabeticValueTxtView);
        preDiabeticTitleTxtView = (TextView)rootView.findViewById(R.id.preDiabeticTitleTxtView);
        preDiabeticValueTxtView = (TextView)rootView.findViewById(R.id.preDiabeticValueTxtView);
        newsTxtView = (TextView)rootView.findViewById(R.id.newsTxtView);
        listNews = (ListView) rootView.findViewById(R.id.listNews);

        int resID = R.layout.adapter_news;
        News news = new News();
        feeds.add(news);
        feeds.add(news);
        feeds.add(news);
        adapter = new NewsAdapter(getActivity(), resID, feeds);
        listNews.setAdapter(adapter);



        beginTestingBtn = (Button)rootView.findViewById(R.id.beginTestingBtn);
        noResultLayout = (RelativeLayout)rootView.findViewById(R.id.noResultLayout);

        int typeToMove = sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER);
        if (typeToMove == SharedPreferencesKeys.FROM_REGISTER){
            noResultLayout.setVisibility(View.VISIBLE);
        }else{
            noResultLayout.setVisibility(View.INVISIBLE);
        }

    }
    private void initFontAndText(View rootView){
        greetingUser.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        todayTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        totalReadingsTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        readingsCountTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        totalPatientTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        patentCountTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        diabeticTitleTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        diabeticValueTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        preDiabeticTitleTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        preDiabeticValueTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        newsTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
    }
    private void initNewUserVo(){
        String currentUserVoStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTUSER);
        if (!currentUserVoStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<UserVo>() {
            }.getType();
            currentUserVo = gson.fromJson(currentUserVoStr, type);
            if (currentUserVo.getAvatarUrl().length() > 0){
                CImageLoader loader = new CImageLoader(getActivity(), R.drawable.profile_man);
                loader.DisplayImage(URLConstant.AVATARBASEURL + currentUserVo.getAvatarUrl(),profileImage);
            }
        }
    }
    private void setListener(final View rootView){
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((HomeActivity)getActivity()) != null) {
                    ((HomeActivity)getActivity()).changeLightTheme();
                }
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.home_root_frame, HomeProfileViewFragment.newInstance("", ""), "Post")
                        .addToBackStack(FragmentPopKeys.HOME)
                        .commit();
                FragmentmanagerUtils.setFragmentManagerHome(fragmentManager);
            }
        });
        listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(rootView.getContext(), "You tapped One news item.", Toast.LENGTH_SHORT).show();
            }
        });
        beginTestingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER, SharedPreferencesKeys.FROM_LOGIN);
                if (((HomeActivity)getActivity()) != null) {
                    ((HomeActivity)getActivity()).gotoSeedView();
                }
            }
        });
    }
    private void initUpdateValue(){
        //init
        if (currentUserVo != null){
            greetingUser.setText("Hello " + currentUserVo.getFirstName() + "!");
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        String currentDate = df.format(c.getTime());
        String dateArray[] = currentDate.split(" ");
        todayTxtView.setText(DateUtils.getDayOfMonthSuffix(Integer.parseInt(dateArray[0])) + " " + dateArray[1] + " " + dateArray[2]);
        readingsCountTxtView.setText(String.valueOf(currentUserVo.getTotalReading()));
        patentCountTxtView.setText(String.valueOf(currentUserVo.getTotalPatients()));
    }

    public void updateCurrentProfile() {
        accountClient.fetchProfile(getActivity(), sharedPreferencesManager, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                currentUserVo.setUserId(((UserVo) responseObject).getUserId());
                currentUserVo.setFirstName(((UserVo) responseObject).getFirstName());
                currentUserVo.setLastName(((UserVo) responseObject).getLastName());
                currentUserVo.setAadhaar(((UserVo) responseObject).getAadhaar());
                currentUserVo.setInstitutatinType(((UserVo) responseObject).getInstitutionType());
                currentUserVo.setInstitutationName(((UserVo) responseObject).getInstitutationName());
                currentUserVo.setState(((UserVo) responseObject).getState());
                currentUserVo.setCity(((UserVo) responseObject).getCity());
                currentUserVo.setTotalReading(((UserVo) responseObject).getTotalReading());
                currentUserVo.setTotalPatients(((UserVo) responseObject).getTotalPatients());
                currentUserVo.setTestingPeriod(((UserVo) responseObject).getTestingPeriod());
                currentUserVo.setAvatarUrl(((UserVo) responseObject).getAvatarUrl());

                UsersManagement.saveCurrentUser(currentUserVo, sharedPreferencesManager);

                onFetchUserCompleted();
            }


            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }
            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onFetchUserFailed(errorCode, errorMessage);
            }
        });
    }
    public void onFetchUserCompleted() {
        initUpdateValue();
    }

    public void onFetchUserFailed(int errorCode, String errorMessage) {
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(getActivity(), errorMessage);
                break;
            case 404:
                PrintUtil.showToast(getActivity(), errorMessage);
                break;
            case 0:
                PrintUtil.showToast(getActivity(), getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(getActivity(), getResources().getString(R.string.internet_connection));
                break;
            default:
                PrintUtil.showToast(getActivity(), getResources().getString(R.string
                        .something_went_wrong));
        }
    }
    public void showDialogBox(String message, final EditText editText, String Title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}