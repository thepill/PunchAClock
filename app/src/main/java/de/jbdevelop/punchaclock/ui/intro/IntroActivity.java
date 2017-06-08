package de.jbdevelop.punchaclock.ui.intro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.helper.SettingsHelper;
import de.jbdevelop.punchaclock.ui.map.AddAreaActivity;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(new ExplainIntro());
//        addSlide(secondFragment);

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_first_slide_title), getString(R.string.intro_first_slide_description), R.drawable.ic_timer_white_200dp, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_location_permission_title), getString(R.string.intro_location_permission_description), R.drawable.ic_location_on_white_200dp, getResources().getColor(R.color.colorPrimary)));
        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, getSlides().size());
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_calendar_title), getString(R.string.intro_calendar_description), R.drawable.ic_event_note_white_200dp, getResources().getColor(R.color.colorPrimary)));
        askForPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, getSlides().size());
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_finish_title), getString(R.string.intro_finish_description), R.drawable.ic_check_white_200dp, getResources().getColor(R.color.colorPrimary)));

        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

//         Hide Skip/Done button.
        showSkipButton(false);
//        setProgressButtonEnabled(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.


    }

    private boolean hasCalendarPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        if (hasCalendarPermissions()) {
            SettingsHelper.enableCalendarSync(this);
        }

        Intent startMapsActivity = new Intent(this, AddAreaActivity.class);
        startActivity(startMapsActivity);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}