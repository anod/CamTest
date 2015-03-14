package info.anodsplace.camtest.fragments;

import android.app.Activity;

import com.commonsware.cwac.camera.CameraHost;

import java.io.File;

import info.anodsplace.camtest.activites.CameraActivity;
import info.anodsplace.camtest.camera.AutoShutter;

/**
 * @author alex
 * @date 2015-03-14
 */
public class CameraFragment extends com.commonsware.cwac.camera.CameraFragment implements AutoShutter.Listener {
    private AutoShutter mShutter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mShutter = new AutoShutter(activity, this);
    }


    @Override
    public CameraHost getHost() {
        return new info.anodsplace.camtest.camera.CameraHost(getActivity(), mShutter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mShutter.start();
    }

    @Override
    public void onPause() {
        mShutter.stop();
        super.onPause();
    }

    @Override
    public void onImageTaken(int num, File file) {
        CameraActivity act = (CameraActivity) getActivity();
        act.setImageCounter(num);
        act.addToFtpQueue(file);
    }
}