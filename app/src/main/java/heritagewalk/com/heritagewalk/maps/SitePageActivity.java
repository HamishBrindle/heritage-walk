package heritagewalk.com.heritagewalk.maps;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.model.LatLng;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.models.Site;

public class SitePageActivity extends FragmentActivity implements SiteFragment.OnFragmentInteractionListener {
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected String sitePosition;
    protected String siteName;
    protected SiteFragment site;
    static float latitude;
    static float longitude;
    private String[] latlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_page);
        Intent intent = getIntent();

        siteName = intent.getStringExtra("selectedSiteName");
        sitePosition = intent.getStringExtra("selectedSiteLatLng");

        latlong =  sitePosition.split(",");
        Log.d("latlong", latlong[0]);
        Log.d("latlong", latlong[1]);
        latitude = Float.parseFloat(latlong[0].replaceAll("[^\\d-.]", ""));
        longitude = Float.parseFloat(latlong[1].replaceAll("[^\\d-.]", ""));

        SiteFragment site = SiteFragment.newInstance(latlong[0].replaceAll("[^\\d.]", ""), latlong[1].replaceAll("[^\\d.]", ""));

//        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
//
//        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // TODO: Start using the Places API.
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

