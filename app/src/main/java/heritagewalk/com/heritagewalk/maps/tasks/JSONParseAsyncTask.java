package heritagewalk.com.heritagewalk.maps.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import heritagewalk.com.heritagewalk.models.Site;

/**
 * A simple JSON parsing class.
 * <p>
 * Created by hamis on 2017-10-05.
 */

public class JSONParseAsyncTask extends AsyncTask<String, String, Site[]> {

    private static final String TAG = "JSONParseAsyncTask";
    private final String jsonURL = "http://opendata.newwestcity.ca/downloads/heritage-interest/HERITAGE_INTEREST.json";
    private ProgressDialog progressDialog;
    private OnJSONParseCompleted mListener;

    public JSONParseAsyncTask(Activity activity) {
        progressDialog = new ProgressDialog(activity);
        mListener = (OnJSONParseCompleted) activity;
    }

    protected void onPreExecute() {
        progressDialog.setMessage("Downloading Sites...");
        progressDialog.show();
    }

    @Override
    protected Site[] doInBackground(String... params) {

        URL url = null;

        try {
            // Definition of the URL with the JSON-Strings
            url = new URL(jsonURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        InputStreamReader reader = null;

        try {
            // InputStreamReader is responsible to open and consume the
            // URL
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Gson().fromJson(reader, Site[].class);
    }

    @Override
    protected void onPostExecute(Site[] sites) {
        super.onPostExecute(sites);
        this.progressDialog.dismiss();
        Log.d(TAG, "onPostExecute: Finished parsing JSON...");
        mListener.onJSONParseCompleted(sites);
    }
}
