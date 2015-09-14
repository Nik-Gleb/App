package ru.touchin.myproject.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import ru.touchin.myproject.BuildConfig;
import ru.touchin.myproject.R;
import ru.touchin.myproject.utils.Utils;

public final class MainActivity extends Activity {

    /** The activity root view. */
    private View mRootView = null;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.DEBUG)
            mRootView = getWindow().getDecorView()
                    .findViewById(android.R.id.content);
        //remote change5
    }

    @Override
    protected final void onDestroy() {
        if (mRootView != null) {
            Utils.unbindReferences(mRootView);
            mRootView = null;
            System.gc();
            System.runFinalization();
            System.gc();
        }
        super.onDestroy();
    }

}
