package info.anodsplace.camtest.activites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.anodsplace.camtest.ftp.FtpConnection;
import info.anodsplace.camtest.ftp.FtpSettings;
import info.anodsplace.camtest.ftp.FtpSettingsStorage;
import info.anodsplace.camtest.gcm.GcmRegistration;
import info.anodsplace.camtest.R;
import info.anodsplace.camtest.views.FtpSettingsView;


public class SettingsActivity extends Activity implements GcmRegistration.Listener, FtpSettingsView.FtpSettingsListener {
    @InjectView(R.id.regId)
    TextView mRegIdView;
    @InjectView(R.id.shareRegId)
    ImageButton mShareRegIdBtn;
    @InjectView(R.id.ftpSettings)
    FtpSettingsView mFtpSettingsView;


    private static final String TAG = "Activity";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;
    private GcmRegistration mGCM;
    private Context mContext;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
        mContext = this;
        ButterKnife.inject(this);
        if (checkPlayServices()) {
            mGCM = new GcmRegistration(this, this);
            mGCM.register();
        }

        mFtpSettingsView.setFtpSettings(FtpSettingsStorage.load(this));

        mFtpSettingsView.setListener(this);

        mShareRegIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(SettingsActivity.this);
                builder.setSubject("GCM Registration Id");
                builder.setText(mRegIdView.getText());
                builder.setType("text/plain");
                builder.startChooser();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        REQUEST_CODE_RESOLUTION).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                mIsInResolution = false;
                break;
        }
    }

    @Override
    public void onGcmRegister(String regid) {
        mRegIdView.setText(regid);
    }

    @Override
    public void onGcmRegisterError(String msg) {
        mRegIdView.setText(msg);
    }

    @Override
    public void onSaveClick(FtpSettings settings) {
        FtpSettingsStorage.save(this, settings);

        if (!TextUtils.isEmpty(settings.getHost())) {
            new FtpSettingsTask(this).execute(settings);
        }

    }

    private static class FtpSettingsTask extends AsyncTask<FtpSettings, Integer, Boolean>
    {
        private Context mContext;

        private FtpSettingsTask(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(FtpSettings... params) {
            FtpSettings settings = params[0];
            FtpConnection connection = new FtpConnection();
            connection.setSettings(settings);
            boolean res = connection.connect();
            connection.disconnect();
            return res;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Toast.makeText(mContext, (success) ? "Ftp settings are valid" : "Cannot connect to the FTP server", Toast.LENGTH_SHORT ).show();
        }
    }
}
