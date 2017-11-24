package heritagewalk.com.heritagewalk.maps;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import heritagewalk.com.heritagewalk.R;

public class PlacesActivity extends FragmentActivity {
    protected GeoDataClient mGeoDataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // TODO: Start using the Places API.
    }
}