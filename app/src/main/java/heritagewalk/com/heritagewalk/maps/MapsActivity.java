package heritagewalk.com.heritagewalk.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Arrays;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.models.Site;
import heritagewalk.com.heritagewalk.maps.tasks.JSONParseAsyncTask;
import heritagewalk.com.heritagewalk.maps.tasks.OnJSONParseCompleted;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnJSONParseCompleted {

    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 13.5f;
    private static final LatLng NEW_WEST = new LatLng(49.2057179, -122.910956);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 200;

    private GoogleMap mMap;
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

        // Once JSON is ready, get map ready.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Check if user has given permissions to access location
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        // If user has given permission, turn on user location
        // If NOT, try and get the permission.
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            getPermissions();
        }

        // Setup the camera and it's boundaries.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NEW_WEST, DEFAULT_ZOOM));
        mMap.setMinZoomPreference(13.0f);
        mMap.setMaxZoomPreference(18.0f);

        // We want to use clusters, so we need to set that up
        setUpClusterManager();
    }

    private void getPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO: Show an explanation to the user *asynchronously* --
                // don't block this thread waiting for the user's response!
                // After the user sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    mMap.setMyLocationEnabled(true);

                } else {
                    mMap.setMyLocationEnabled(false);
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Sets up clustering of map markers on the map using a ClusterManager.
     */
    private void setUpClusterManager() {

        mClusterManager = new ClusterManager<Site>(this, mMap);

        // We implement our own renderer so we can use custom-styled markers.
        mClusterManager.setRenderer(new SiteMarkerRenderer(this, mMap, mClusterManager));

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    /**
     * Adds markers to the map.
     */
    private void addItems() {
        if (mHeritageSites != null) {
            Log.e(TAG, "onMapReady: Adding markers to map");
            for (Site site :
                    mHeritageSites) {
                mClusterManager.addItem(site);
            }
        } else {
            Log.e(TAG, "onMapReady: Sites array is null!");
        }
    }

}
