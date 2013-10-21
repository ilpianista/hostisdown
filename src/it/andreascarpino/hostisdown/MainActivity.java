/*
  MIT license:

        Copyright (c) 2013 Andrea Scarpino

        Permission is hereby granted, free of charge, to any person obtaining
        a copy of this software and associated documentation files (the
        "Software"), to deal in the Software without restriction, including
        without limitation the rights to use, copy, modify, merge, publish,
        distribute, sublicense, and/or sell copies of the Software, and to
        permit persons to whom the Software is furnished to do so, subject to
        the following conditions:

        The above copyright notice and this permission notice shall be
        included in all copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
        EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
        MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
        NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
        LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
        WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

        String host = ((EditText) findViewById(R.id.host)).getText().toString();
        String params = "-c 1 -q";

        // ping the host
        new PingTask(view.getRootView()).execute(params, host);
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
