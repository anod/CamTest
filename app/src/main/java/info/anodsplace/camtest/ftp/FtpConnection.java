package info.anodsplace.camtest.ftp;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author alex
 * @date 2015-03-12
 */

public class FtpConnection {
    public static final String UPLOAD_DIR = "CamTest";
    private FTPClient ftp;
    private FtpSettings mSettings;

    public FtpConnection() {
        ftp = new FTPClient();
    }

    public boolean connect() {
        try {
            doConnect(mSettings);
        } catch (IOException e) {
            if (ftp.isConnected())
            {
                try
                {
                    ftp.disconnect();
                }
                catch (IOException f)
                {
                    // do nothing
                }
            }
            Log.e("FTPQueue", "Could not connect to server.");
            return false;
        }
        return true;
    }

    public void disconnect() {
        if (ftp == null) {
            return;
        }
        try {

            ftp.noop(); // check that control connection is working OK
            ftp.logout();
        } catch (IOException e) {
            if (ftp.isConnected())
            {
                try
                {
                    ftp.disconnect();
                }
                catch (IOException f)
                {
                    // do nothing
                }
            }
            Log.e("FTPQueue", "Could not logout from server.");
        }
    }
    private void doConnect(FtpSettings settings) throws IOException {
        ftp.connect(settings.getHost(), settings.getPort());

        Log.d("FTPQueue", "Connected to " + settings.toString());

        // After connection attempt, you should check the reply code to verify
        // success.
        int reply = ftp.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply))
        {
            ftp.disconnect();
            Log.e("FTPQueue", "FTP server refused connection.");
        }

        if (!ftp.login(settings.getUsername(), settings.getPassword()))
        {
            ftp.logout();
            throw new IOException("Cannot login");
        }

        Log.d("FTPQueue", "Remote system is " + ftp.getSystemType());
        ftp.enterLocalPassiveMode();
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
    }

    public boolean upload(FtpRequest request) throws IOException {
        if (!ftp.isConnected()) {
            doConnect(mSettings);
            ftp.makeDirectory("CamTest");
            ftp.changeWorkingDirectory(UPLOAD_DIR);
        }
        InputStream input;

        input = new FileInputStream(request.getLocal());

        boolean result = ftp.storeFile(request.getRemoteName(), input);

        input.close();

        if (!result) {
            throw new IOException("Cannot upload "+request);
        }

        return result;
    }

    public void setSettings(FtpSettings settings) {
        mSettings = settings;
    }
}
