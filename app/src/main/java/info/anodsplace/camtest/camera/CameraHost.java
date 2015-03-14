package info.anodsplace.camtest.camera;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;

import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author alex
 * @date 2015-03-14
 */
public class CameraHost extends SimpleCameraHost {

    public interface Listener {
        void onPreviewStart();
        void onPreviewStop();
        void onImageSave(File photo);
    }

    private File photoDirectory;
    private final Listener mListener;

    public CameraHost(Context _ctxt, Listener listener) {
        super(_ctxt);
        mListener = listener;
    }

    @Override
    public void autoFocusAvailable() {
        super.autoFocusAvailable();
        mListener.onPreviewStart();
    }

    @Override
    public void autoFocusUnavailable() {
        mListener.onPreviewStop();
    }

    @Override
    protected File getPhotoDirectory() {
        if (photoDirectory == null) {
            photoDirectory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "CameraTest");
        }
        return photoDirectory;
    }

    @Override
    protected boolean scanSavedImage() {
        return false;
    }

    @Override
    public void saveImage(PictureTransaction xact, byte[] image) {
        File photo=getPhotoPath();

        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());
            BufferedOutputStream bos=new BufferedOutputStream(fos);

            bos.write(image);
            bos.flush();
            fos.getFD().sync();
            bos.close();
        }
        catch (java.io.IOException e) {
            handleException(e);
        }

        mListener.onImageSave(photo);
    }
}
