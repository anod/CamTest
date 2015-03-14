package info.anodsplace.camtest.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

import java.io.File;

/**
 * @author alex
 * @date 2015-03-13
 */
public class AutoShutter implements CameraHost.Listener {
    private static final String TAG = "AutoShutter";
    public static final int DELAY_MILLIS = 300;
    private Handler mHandler = null;
    private int mCounter = 0;
    private final Activity mActivity;
    private final Listener mListener;
    private boolean mStopped = false;
    private boolean mPreviewStarted;

    public interface Listener {
        void takePicture();
        void onImageTaken(int num, File file);
    }

    public AutoShutter(Activity activity, Listener listener) {
        mActivity = activity;
        mListener = listener;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            /* do what you need to do */
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "takePicture");
                    takePicture();
                }
            });

        }
    };

    public void start() {
        Log.e(TAG, "start");
        mStopped = false;
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(mRunnable, 100);
    }

    public void stop() {
        Log.e(TAG, "stop");
        mStopped = true;
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
    }

    private void takePicture() {
        if (mStopped) {
            return;
        }
        if (mPreviewStarted) {
            mListener.takePicture();
        } else {
            schedule();
        }
    }

    @Override
    public void onPreviewStart() {
        mPreviewStarted = true;
    }

    @Override
    public void onPreviewStop() {
        mPreviewStarted = false;
    }

    @Override
    public void onImageSave(final File file) {
        Log.e(TAG, "onImageSave");
        if (mStopped) {
            return;
        }
        mCounter++;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListener.onImageTaken(mCounter, file);
            }
        });

        schedule();
    }

    private void schedule() {
        mHandler.postDelayed(mRunnable, DELAY_MILLIS);
    }

}
