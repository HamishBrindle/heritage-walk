package heritagewalk.com.heritagewalk.maps;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Arrays;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.maps.tasks.JSONParseAsyncTask;
import heritagewalk.com.heritagewalk.maps.tasks.OnJSONParseCompleted;
import heritagewalk.com.heritagewalk.models.Site;

public class MapsActivity extends FragmentActivity
        implements OnJSONParseCompleted {

    private static final String TAG = "MapsActivity";

    private ArrayList<Site> mHeritageSites;
    private ClusterManager<Site> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain data from JSON (later calls AddMarkersAsyncTask)
        // This is basically what kicks everything off.
        new JSONParseAsyncTask(this).execute();
    }

    /**
     * Executes once JSON data has been downloaded.
     * Sets up GoogleMap fragment.
     */
    @Override
    public void onJSONParseCompleted(Site[] sites) {
        mHeritageSites = new ArrayList<>(Arrays.asList(sites));
        Log.d(TAG, "onJSONParseCompleted: Downloaded sites successfully...");

        // TODO: Execute Fragment change to MapFragment
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
