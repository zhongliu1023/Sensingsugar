package me.sensingself.sensingsugar.Activites.LoginRegisterProfile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import me.sensingself.sensingsugar.Activites.HomeActivity;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.R;

/**
 * Created by liujie on 1/6/18.
 */

public class Splash extends AppCompatActivity {
    private ImageView logoImageView;
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    SharedPreferencesManager sharedM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedM = SharedPreferencesManager.getInstance(Splash.this);

        logoImageView = (ImageView)findViewById(R.id.logoImageView);
        logoImageView.setImageResource(R.drawable.splash_spoon);

        sharedM.setPreferenceValueInt(SharedPreferencesKeys.SORTINGMODEREPORTS, 0);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                String session = sharedM.getPreferenceValueString(SharedPreferencesKeys.ID_TOKEN);
                String currentUserInfo = sharedM.getPreferenceValueString(SharedPreferencesKeys.CURRENTUSER);
                Intent mainIntent;
                if (!session.equals("") && !currentUserInfo.equals("")) {
                    mainIntent = new Intent(Splash.this,HomeActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else {
                    mainIntent = new Intent(Splash.this, LoginActivity.class);
                }
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        TextView labelSensing = (TextView) findViewById(R.id.sensingTxtView);
        String redString = getResources().getString(R.string.pre_app_name);
        labelSensing.setText(Html.fromHtml(redString));
    }
}