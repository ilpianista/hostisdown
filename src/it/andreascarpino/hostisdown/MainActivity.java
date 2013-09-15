package it.andreascarpino.hostisdown;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import it.andreascarpino.hostisdown.task.PingTask;

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
        // disable the check button
        findViewById(R.id.checkButton).setEnabled(false);

        // reset the previous result
        findViewById(R.id.hostStatus).setVisibility(View.INVISIBLE);

        // start the progress bar
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        // ping the host
        String host = ((EditText) findViewById(R.id.host)).getText().toString();
        new PingTask(view.getRootView()).execute(host);
    }
}
