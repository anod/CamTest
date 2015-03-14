package info.anodsplace.camtest.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.anodsplace.camtest.R;
import info.anodsplace.camtest.activites.SettingsActivity;
import info.anodsplace.camtest.ftp.FtpSettings;

/**
 * @author alex
 * @date 2015-03-12
 */
public class FtpSettingsView extends LinearLayout {
    @InjectView(R.id.ftp_ip) EditText mIpText;
    @InjectView(R.id.ftp_port) EditText mPortText;
    @InjectView(R.id.ftp_uname) EditText mUnameText;
    @InjectView(R.id.ftp_pass) EditText mPassText;
    @InjectView(R.id.ftp_save) Button mSaveBtn;
    private FtpSettingsListener mListener;

    public interface FtpSettingsListener {
        public void onSaveClick(FtpSettings settings);
    }
    public FtpSettingsView(Context context) {
        this(context, null);
    }

    public FtpSettingsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FtpSettingsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.view_ftp_settings, this);

        ButterKnife.inject(this,this);

        mSaveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSaveClick(getFtpSettings());
            }
        });
    }

    public void setFtpSettings(FtpSettings settings) {
        mIpText.setText(settings.ip);
        if (settings.port == 0) {
            mPortText.setText("");
        } else {
            mPortText.setText(settings.port);
        }
        mUnameText.setText(settings.username);
        mPassText.setText(settings.password);
    }

    public FtpSettings getFtpSettings() {
        String ip = mIpText.getText().toString();
        int port = TextUtils.isEmpty(mPortText.getText().toString()) ? 0 : Integer.valueOf(mPortText.getText().toString());
        String uname = mUnameText.getText().toString();
        String pass = mPassText.getText().toString();
        return new FtpSettings(ip, port, uname, pass);
    }


    public void setListener(FtpSettingsListener listener) {
        mListener = listener;
    }
}
