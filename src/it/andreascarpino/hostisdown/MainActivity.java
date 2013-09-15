package it.andreascarpino.hostisdown;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.about == item.getItemId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            try {
                builder.setMessage(
                        getString(R.string.app_name) + " " +
                        this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName +
                        "\n\n" +
                        "License: MIT\n" +
                        "Copyright: 2013\n" +
                        "Andrea Scarpino <me@andreascarpino.it>")
                        .setTitle("About");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            AlertDialog about = builder.create();
            about.show();

            return true;
        }

        return false;
    }

}
