package me.sensingself.sensingsugar.Activites.Fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.squareup.otto.Subscribe;
import me.sensingself.sensingsugar.Common.ActivityResults.ActivityResultBus;
import me.sensingself.sensingsugar.Common.ActivityResults.ActivityResultEvent;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;

/**
 * Created by liujie on 3/6/18.
 */

public abstract class BaseFragment extends Fragment {
    protected Object mFragmentsSubscriber = new Object() {
        @Subscribe
        public void onFragmentsReceived(FragmentsEvents event) {
            String eventName = event.getEventName();
            if (eventName.equals(FragmentsEventsKeys.BACKPRESSED)){
                onBackPressed();
            }
        }
    };

    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);
        }
    };

    abstract protected void onBackPressed();

    @Override
    public void onStart() {
        super.onStart();
        FragmentsBus.getInstance().register(mFragmentsSubscriber);
        ActivityResultBus.getInstance().register(mActivityResultSubscriber);
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentsBus.getInstance().unregister(mFragmentsSubscriber);
        ActivityResultBus.getInstance().unregister(mActivityResultSubscriber);
    }
}
