package info.anodsplace.camtest.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import info.anodsplace.camtest.ftp.FtpSettingsStorage;
import info.anodsplace.camtest.gcm.GcmRegistration;

/**
 * @author alex
 * @date 2015-03-08
 */
public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);


        GcmRegistration reg = new GcmRegistration(this, null);

        boolean ftpInitilized = !TextUtils.isEmpty(FtpSettingsStorage.load(this).ip);

        if (reg.getRegistrationId() == null || !ftpInitilized) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else {
            startActivity(new Intent(this, CameraActivity.class));
        }

        finish();
    }
}
