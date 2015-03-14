package info.anodsplace.camtest.activites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.anodsplace.camtest.R;
import info.anodsplace.camtest.ftp.FtpConnection;
import info.anodsplace.camtest.ftp.FtpDelivery;
import info.anodsplace.camtest.ftp.FtpError;
import info.anodsplace.camtest.ftp.FtpQueue;
import info.anodsplace.camtest.ftp.FtpRequest;
import info.anodsplace.camtest.ftp.FtpSettings;
import info.anodsplace.camtest.ftp.FtpSettingsStorage;

/**
 * @author alex
 * @date 2015-03-07
 */
public class CameraActivity extends Activity implements FtpDelivery {
    private static final String TAG = "CameraActivity";

    @InjectView(R.id.btn_settings)
    ImageButton mBtnSettings;
    @InjectView(R.id.txt_taken)
    TextView mPicCounterView;
    @InjectView(R.id.txt_uploaded)
    TextView mUploadedCounterView;

    private FtpQueue mFtpQueue;
    private int mUploaded;
    private FtpConnection mFtpConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ButterKnife.inject(this);

        Log.e(TAG,"onCreate");

        mPicCounterView.setText("0");
        mUploadedCounterView.setText(mUploaded+"");
        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(CameraActivity.this, SettingsActivity.class));
            }
        });

        mFtpConnection = new FtpConnection();
        mFtpQueue = new FtpQueue(mFtpConnection, 1, this);
    }

    @Override
    protected void onResume() {
        mFtpConnection.setSettings(FtpSettingsStorage.load(this));
        mFtpQueue.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFtpQueue.cancelAll();
        mFtpQueue.disconnect();
        mFtpQueue.stop();
    }

    public void addToFtpQueue(File file) {
        FtpRequest req = new FtpRequest(file.getAbsolutePath(), file.getName());
        Log.i(TAG, "Add to FTP queue: "+req.toString());
        mFtpQueue.add(req);
    }

    public void setImageCounter(int num) {
        mPicCounterView.setText(num+"");
    }


    @Override
    public void postDelivered(FtpRequest request) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUploaded++;
                mUploadedCounterView.setText(mUploaded+"");
            }
        });
    }

    @Override
    public void postError(FtpRequest request, FtpError error) {
        Log.e(TAG, error.getMessage(), error);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, "Cannot upload to FTP server", Toast.LENGTH_LONG).show();
            }
        });
        //if (!success) {
        //    mContext.startActivity(new Intent(mContext, SettingsActivity.class));
        //}
    }

}
