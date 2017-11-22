package heritagewalk.com.heritagewalk.maps;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.maps.fragments.MapsFragment;
import heritagewalk.com.heritagewalk.maps.tasks.JSONParseAsyncTask;
import heritagewalk.com.heritagewalk.maps.tasks.OnJSONParseCompleted;
import heritagewalk.com.heritagewalk.models.Site;

public class MapsActivity extends FragmentActivity
        implements OnJSONParseCompleted,
        MapsFragment.OnFragmentInteractionListener {

    private static final String TAG = "MapsActivity";

    public static ArrayList<Site> mHeritageSites;
    private BottomNavigationView mBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mBottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                switch (item.getItemId()) {

                    case R.id.action_explore:
                        Log.e("msg", "action explore clicked");
                        break;
                    case R.id.action_sites:
                        Log.e("msg", "action sites clicked");
                        setNewFragment(new SiteFragment());
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

    /**
     * Executes once JSON data has been downloaded.
     * Sets up GoogleMap fragment.
     */
    @Override
    public void onJSONParseCompleted(Site[] sites) {
        mHeritageSites = new ArrayList<>(Arrays.asList(sites));
        Log.d(TAG, "onJSONParseCompleted: Downloaded sites successfully...");

        // TODO: Execute Fragment change to MapFragment
        // Create new fragment and transaction
        Fragment newFragment = MapsFragment.newInstance(null, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_holder, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private boolean setNewFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_holder, fragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
