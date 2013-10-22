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

package it.andreascarpino.hostisdown.task;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import it.andreascarpino.hostisdown.MainActivity;
import it.andreascarpino.hostisdown.R;
import it.andreascarpino.hostisdown.db.HostsDataSource;
import it.andreascarpino.hostisdown.db.State;

import java.io.IOException;

public class PingTask extends AsyncTask<String, Void, Integer> {

    private String host;
    private View view;

    public PingTask(View view) {
        this.view = view;
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            host = params[0];
            return new ProcessBuilder()
                    .command("/system/bin/ping",
                             params[1],
                             host)
                    .start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return 1;
    }

    @Override
    protected void onPostExecute(Integer ret) {
        super.onPostExecute(ret);

        State status;
        if (ret == 0) {
            ((TextView) this.view.findViewById(R.id.downup)).setText(R.string
                    .up);
            ((TextView) this.view.findViewById(R.id.downup)).setTextColor
                    (Color.GREEN);
            status = State.Up;
        } else {
            ((TextView) this.view.findViewById(R.id.downup)).setText(R.string
                    .down);
            ((TextView) this.view.findViewById(R.id.downup)).setTextColor
                    (Color.RED);
            status = State.Down;
        }

        // stop the progress bar
        this.view.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        // display the result
        this.view.findViewById(R.id.hostStatus).setVisibility(View.VISIBLE);

        // re-enable the check button
        this.view.findViewById(R.id.checkButton).setEnabled(true);

        // save in the db
        HostsDataSource datasource = new HostsDataSource(view.getContext());
        datasource.open();
        datasource.createHost(host, System.currentTimeMillis(), status);

        // refresh the recent hosts list
        MainActivity.hosts.clear();
        MainActivity.hosts.addAll(datasource.getAllHosts());
        ((BaseAdapter) ((ListView) view.findViewById(R.id.listView))
                .getAdapter()).notifyDataSetChanged();

        datasource.close();
    }
}
