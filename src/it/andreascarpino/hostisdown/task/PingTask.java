package it.andreascarpino.hostisdown.task;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import it.andreascarpino.hostisdown.R;

import java.io.IOException;

public class PingTask extends AsyncTask<String, Void, Integer> {

    private View view;

    public PingTask(View view) {
        this.view = view;
    }

    @Override
    protected Integer doInBackground(String... hosts) {
        try {
            return new ProcessBuilder()
                    .command("/system/bin/ping", "-c 1", "-q", hosts[0])
                    .redirectErrorStream(true)
                    .start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return 1;
    }

    @Override
    protected void onPostExecute(Integer ret) {
        super.onPostExecute(ret);

        if (ret == 0) {
            ((TextView) this.view.findViewById(R.id.downup)).setText(R.string.upTxt);
            ((TextView) this.view.findViewById(R.id.downup)).setTextColor(Color.GREEN);
        } else {
            ((TextView) this.view.findViewById(R.id.downup)).setText(R.string.downTxt);
            ((TextView) this.view.findViewById(R.id.downup)).setTextColor(Color.RED);
        }

        // stop the progress bar
        this.view.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        // display the result
        this.view.findViewById(R.id.hostStatus).setVisibility(View.VISIBLE);

        // re-enable the check button
        this.view.findViewById(R.id.checkButton).setEnabled(true);
    }
}
