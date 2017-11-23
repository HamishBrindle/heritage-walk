package heritagewalk.com.heritagewalk.maps;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;

import heritagewalk.com.heritagewalk.R;

public class SitePageActivity extends FragmentActivity implements SiteFragment.OnFragmentInteractionListener {
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected String sitePosition;
    protected String siteName;
    protected String siteDescription;
    protected String siteSummary;

    protected SiteFragment site;
    static float latitude;
    static float longitude;

    private TextView siteTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setActionBar(toolbar);
        }

        Intent intent = getIntent();
        siteName = intent.getStringExtra("selectedSiteName");
        sitePosition = intent.getStringExtra("selectedSiteLatLng");
        siteSummary = intent.getStringExtra("selectedSiteSummary");
        String[] latlong = sitePosition.split(",");
        latitude = convertStringToFloat(latlong[0]);
        longitude = convertStringToFloat(latlong[1]);


        setUpViews();

        SiteFragment site = SiteFragment.newInstance();

//        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
//
//        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // TODO: Start using the Places API.
    }


    private float convertStringToFloat (String toConvert) {
        return Float.parseFloat(toConvert.replaceAll("[^\\d-.]", ""));
    }

    private void setUpViews() {
        TextView siteTitleView = findViewById(R.id.siteTitle);
        siteTitleView.setText(siteName);

        TextView siteSummView = findViewById(R.id.siteSummary);
        siteSummView.setText(siteSummary);

        TextView businessPromptView = findViewById(R.id.businessPrompt);
        String BUSINESS_PROMPT = "Come check out these businesses!";
        businessPromptView.setText(BUSINESS_PROMPT);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

