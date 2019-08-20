package me.sensingself.sensingsugar.Common.FragmentsBus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by liujie on 3/5/18.
 */

public class FragmentsBus  extends Bus {

    private static FragmentsBus instance;

    public static FragmentsBus getInstance() {
        if (instance == null)
            instance = new FragmentsBus();
        return instance;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void postQueue(final Object obj) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentsBus.getInstance().post(obj);
            }
        });
    }
}
