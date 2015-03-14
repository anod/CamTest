package info.anodsplace.camtest.ftp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author alex
 * @date 2015-03-12
 */
public class FtpSettingsStorage {



    public static FtpSettings load(Context context) {
        SharedPreferences prefs = getPreferences(context);

        String ip = prefs.getString("ftp_ip","");
        int port = prefs.getInt("ftp_port", 0);
        String uname = prefs.getString("ftp_uname", "");
        String pass = prefs.getString("ftp_pass","");

        return new FtpSettings(ip, port, uname, pass);
    }

    public static void save(Context context, FtpSettings settings) {
        SharedPreferences.Editor edit = getPreferences(context).edit();

        edit.putInt("ftp_port", settings.port);
        edit.putString("ftp_ip", settings.ip);
        edit.putString("ftp_uname", settings.username);
        edit.putString("ftp_pass", settings.password);

        edit.apply();

    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return context.getSharedPreferences(FtpSettingsStorage.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
