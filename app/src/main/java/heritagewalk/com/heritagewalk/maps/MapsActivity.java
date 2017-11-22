package heritagewalk.com.heritagewalk.maps;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Arrays;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.maps.fragments.MapsFragment;
import heritagewalk.com.heritagewalk.maps.tasks.JSONParseAsyncTask;
import heritagewalk.com.heritagewalk.maps.tasks.OnJSONParseCompleted;
import heritagewalk.com.heritagewalk.models.Site;

public class MapsActivity extends FragmentActivity
        implements OnJSONParseCompleted, SiteFragment.OnFragmentInteractionListener {

    private static final String TAG = "MapsActivity";

    private ArrayList<Site> mHeritageSites;
    private ClusterManager<Site> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                switch (item.getItemId()) {

                    case R.id.action_explore:
                        Log.e("msg", "action explore clicked");
                        setFragment(new MapsFragment());
                        break;
                    case R.id.action_sites:
                        Log.e("msg", "action sites clicked");
                        //SiteFragment siteFrag = new SiteFragment();
                        //siteFrag.setRetainInstance(true);
                        //fm.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).show(siteFrag).commit();
                        //siteFrag.show(fm, "userfragemnt");
                        //SiteFragment siteFrag = (SiteFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                        setFragment(new SiteFragment());
                        break;

                    default:
                        break;
                }

                return true;
            }
        });

        // Obtain data from JSON (later calls AddMarkersAsyncTask)
        // This is basically what kicks everything off.
        new JSONParseAsyncTask(this).execute();
    }



    // This could be moved into an abstract BaseActivity
    // class for being re-used by several instances
    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
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
    public void onFragmentInteraction(Uri uri) {

    }
}
