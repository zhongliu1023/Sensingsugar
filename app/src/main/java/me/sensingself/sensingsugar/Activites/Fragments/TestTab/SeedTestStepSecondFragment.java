package me.sensingself.sensingsugar.Activites.Fragments.TestTab;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liujie on 1/30/18.
 */

public class SeedTestStepSecondFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TextView tsLabel, tsNote, tsTime, tsMinue;
    private ImageView tsImage, tsClock;
    private boolean isStarted = false;
    Timer timer = new Timer();
    int timeTotal = 61;
    int ringCount = 0;
    MediaPlayer mediaPlayer = new MediaPlayer();
    boolean isBuzzing = false;
    int tapCounts = 0;
    static SeedTestStepsFragment seedTestStepsFragment;
    public static SeedTestStepSecondFragment newInstance(String param1, String param2, SeedTestStepsFragment parentFragment) {
        seedTestStepsFragment = parentFragment;
        SeedTestStepSecondFragment fragment = new SeedTestStepSecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seed_tab_ts_second, container, false);
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);
        return rootView;
    }
    private void initController(View rootView){
        tsLabel = (TextView) rootView.findViewById(R.id.tsLabelTxtView);
        tsNote = (TextView) rootView.findViewById(R.id.tsNoteTxtView);
        tsTime = (TextView)rootView.findViewById(R.id.timeTxtView);
        tsMinue = (TextView)rootView.findViewById(R.id.minutesTxtView);
        tsImage = (ImageView)rootView.findViewById(R.id.tsImageView);
        tsClock = (ImageView)rootView.findViewById(R.id.clockImageView);
        tsImage.setImageResource(R.drawable.test_second);
        tsClock.setImageResource(R.drawable.tap_to_start);
    }

    private void initFontAndText(View rootView){
        tsLabel.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        tsNote.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        tsTime.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        tsMinue.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
    }

    private void setListener(final View rootView){
        tsClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStarted){
                    isStarted = true;
                    tsClock.setImageResource(R.drawable.alarm_will_buzz);
                    timer = new Timer();
                    timer.schedule(new SeedTestStepSecondFragment.timeCountTask(), 0,1000);
                }
            }
        });
        tsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapCounts ++;
                if (tapCounts == 4){
                    timer.cancel();
                    timer.purge();
                    if (isBuzzing){
                        mediaPlayer.stop();
                    }
                    requestNextSteps();
                }
            }
        });
    }
    private void requestNextSteps(){
        seedTestStepsFragment.onSelectPager(2);
    }
    private void ringTesting(){
        if (isBuzzing)return;
        isBuzzing = true;
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.signal);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
    class timeCountTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeTotal --;
                    if (timeTotal < 0){
                        ringCount ++;
                        if (ringCount == 4){
                            timer.cancel();
                            timer.purge();
                            mediaPlayer.stop();
                            requestNextSteps();
                        }
                        tsClock.setImageResource(R.drawable.alarm_is_buzzing);
                        ringTesting();
                    }else{
                        int minutes = timeTotal / 60;
                        int seconds     = timeTotal % 60;
                        if (seconds < 10){
                            tsTime.setText(minutes + ":0" + seconds);
                        }else{
                            tsTime.setText(minutes + ":" + seconds);
                        }
                    }
                }
            });
        }
    };
}