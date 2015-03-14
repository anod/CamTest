package info.anodsplace.camtest.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.anodsplace.camtest.R;

/**
 * @author alex
 * @date 2015-03-14
 */
public class ResultActivity extends Activity {
    public static final String EXTRA_GCM_INTENT = "extra_gcm_intent";

    @InjectView(R.id.txt_result)
    TextView mResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(EXTRA_GCM_INTENT)) {
            Log.e("ResultActivity", "Missing extra data");
            finish();
            return;
        }

        ButterKnife.inject(this);


        Intent gcmIntent = intent.getParcelableExtra(EXTRA_GCM_INTENT);
        Bundle gcmExtras = gcmIntent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(gcmIntent);

        if (!gcmExtras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            switch (messageType) {
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_SEND_ERROR:
                    setResultText("Send error: " + gcmExtras.toString());
                    break;
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED:
                    setResultText("Deleted messages on server: " +
                            gcmExtras.toString());
                    // If it's a regular GCM message, do some work.
                    break;
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_MESSAGE:
                    Toast.makeText(this, gcmExtras.get("result").toString(), Toast.LENGTH_SHORT).show();
                    setResultText("Received: " + gcmExtras.get("result"));
                    Log.i("ResultActivity", "Received: " + gcmExtras.toString());
                    break;
            }
        }


    }

    private void setResultText(String s) {
        mResultText.setText(s);
    }
}
