package it.andreascarpino.hostisdown;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void checkHost(View view) {
        String host = ((EditText) findViewById(R.id.host)).getText().toString();

        try {
            Process process = new ProcessBuilder()
                    .command("/system/bin/ping", "-c 3", "-q", host)
                    .redirectErrorStream(true)
                    .start();

            if (process.waitFor() == 0) {
                ((TextView) findViewById(R.id.downup)).setText(R.string.upTxt);
                ((TextView) findViewById(R.id.downup)).setTextColor(Color.GREEN);
            }

            findViewById(R.id.hostStatus).setVisibility(View.VISIBLE);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
