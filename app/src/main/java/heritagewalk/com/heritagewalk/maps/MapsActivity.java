package heritagewalk.com.heritagewalk.maps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.main.BaseActivity;
import heritagewalk.com.heritagewalk.maps.tasks.JSONParseAsyncTask;
import heritagewalk.com.heritagewalk.maps.tasks.OnJSONParseCompleted;
import heritagewalk.com.heritagewalk.models.Site;

public class MapsActivity extends BaseActivity
        implements OnMapReadyCallback, OnJSONParseCompleted,
        ClusterManager.OnClusterClickListener<Site>,
        ClusterManager.OnClusterInfoWindowClickListener<Site>,
        ClusterManager.OnClusterItemClickListener<Site>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Site>,
        Serializable {
    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 14.0f;
    private static final float MIN_ZOOM = 10.0f;
    private static final float MAX_ZOOM = 18.0f;
    private static final LatLng NEW_WEST = new LatLng(49.2057179, -122.910956);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 200;

    private GoogleMap mMap;
    private ArrayList<Site> mHeritageSites;
    private ClusterManager<Site> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the current icon as the Explore icon
        selectCurrentIcon(R.id.action_explore);

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

        mMap.setPadding(0,200,0,0);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));

        // Do all the map setup (Camera, permissions, etc.)
        setUpMap();

        // We want to use clusters, so we need to set that up
        setUpClusterManager();
    }

    private void setUpMap() {
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
        mMap.setMinZoomPreference(MIN_ZOOM);
        mMap.setMaxZoomPreference(MAX_ZOOM);

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
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

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
                if (!site.getDescription().isEmpty())
                    mClusterManager.addItem(site);
            }
        } else {
            Log.e(TAG, "onMapReady: Sites array is null!");
        }
    }

    /*
    * Serializing the site object is giving me issues, so I'm going to reload the site using Google places
    * in the SitePageActivity
    */
    private void startNewSiteActivity(Site newSite) {
        Intent intent = new Intent(MapsActivity.this, SitePageActivity.class);
        intent.putExtra("selectedSiteName", newSite.getName());
        intent.putExtra("selectedSiteDesc", newSite.getDescription());
        intent.putExtra("selectedSiteSummary", newSite.getSummary());

        intent.putExtra("selectedSiteLatLng", newSite.getLatLng().toString());
        startActivity(intent);
    }

    @Override
    public boolean onClusterItemClick(Site site) {
        return false;
    }

    @Override
    public boolean onClusterClick(Cluster<Site> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Site> cluster) {
    }

    @Override
    public void onClusterItemInfoWindowClick(Site site) {
        plotMarkerConfirmation(site);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_maps;
    }

    @Override
    public void selectCurrentIcon(int id) {
        // super.selectCurrentIcon(id);
    }

    /**
     * Confirm with user if they'd like to plot their location and route to selected site.
     * @param site  The user's selected site.
     */
    public void plotMarkerConfirmation(final Site site) {

        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyDialog));
        alert.setTitle("Go For A Walk?");
        alert.setMessage("Would like you like to plot a route to " + site.getName() + "?");
        alert.setIcon(R.drawable.ic_logo_crown);
        alert.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirms
                startNewSiteActivity(site);
            }
        });
        alert.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User denies
                dialog.dismiss();
            }
        });
        alert.show();
    }
}
