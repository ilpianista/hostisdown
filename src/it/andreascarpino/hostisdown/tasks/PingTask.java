/*
MIT license:

   Copyright (c) 2013-2014 Andrea Scarpino

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

package it.andreascarpino.hostisdown.tasks;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import it.andreascarpino.hostisdown.MainActivity;
import it.andreascarpino.hostisdown.R;
import it.andreascarpino.hostisdown.db.HostsDataSource;
import it.andreascarpino.hostisdown.db.State;

import java.io.IOException;

public class PingTask extends AsyncTask<String, Void, Integer> {

    private String host;
    private Activity activity;

    public PingTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            this.host = params[0];
            return new ProcessBuilder()
                    .command("/system/bin/ping",
                            params[1],
                            this.host)
                    .start().waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 1;
    }

    @Override
    protected void onPostExecute(Integer ret) {
        super.onPostExecute(ret);

        State status = State.Down;
        switch (ret) {
            case 0:
                ((TextView) this.activity.findViewById(R.id.downup)).setText(R.string.up);
                ((TextView) this.activity.findViewById(R.id.downup)).setTextColor(Color.GREEN);
                status = State.Up;
                break;
            case 1:
                ((TextView) this.activity.findViewById(R.id.downup)).setText(R.string.down);
                ((TextView) this.activity.findViewById(R.id.downup)).setTextColor(Color.RED);
                break;
            case 2:
                ((TextView) this.activity.findViewById(R.id.downup)).setText(R.string.unknown);
                ((TextView) this.activity.findViewById(R.id.downup)).setTextColor(Color.YELLOW);
                status = State.Unknown;
                break;
        }

        // stop the progress bar
        this.activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        // display the result
        this.activity.findViewById(R.id.hostStatus).setVisibility(View.VISIBLE);

        // clean the host text
        ((EditText) this.activity.findViewById(R.id.host)).setText("");

        // re-enable the check button
        this.activity.findViewById(R.id.checkButton).setEnabled(true);

        // save in the db
        final HostsDataSource datasource = new HostsDataSource(this.activity);
        datasource.open();
        datasource.createHost(this.host, System.currentTimeMillis(), status);
        datasource.close();

        // refresh the recent hosts list
        ((MainActivity) this.activity).updateRecentHosts();
    }
}

