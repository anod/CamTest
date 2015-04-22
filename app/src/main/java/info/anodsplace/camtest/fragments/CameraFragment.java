package info.anodsplace.camtest.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.anodsplace.camtest.activites.CameraActivity;

/**
 * @author alex
 * @date 2015-03-14
 */
public class CameraFragment extends com.commonsware.cwac.camera.CameraFragment{
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        CameraActivity act = (CameraActivity) activity;
        setHost(new info.anodsplace.camtest.camera.CameraHost(getActivity(), act.getCameraListener()));
    }

}