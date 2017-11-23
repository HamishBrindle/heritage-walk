package heritagewalk.com.heritagewalk.maps;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.models.Site;

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

    protected GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_page);
        Intent intent = getIntent();
        siteName = intent.getStringExtra("selectedSiteName");
        sitePosition = intent.getStringExtra("selectedSiteLatLng");
        siteSummary = intent.getStringExtra("selectedSiteSummary");
        String[] latlong = sitePosition.split(",");
        latitude = convertStringToFloat(latlong[0]);
        longitude = convertStringToFloat(latlong[1]);

        setUpViews();

        site = SiteFragment.newInstance();
//
////        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);
////
////        // Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
//
//        // TODO: Start using the Places API.
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

    public void findNearbyPlaces(View v){
        StringBuilder sbValue = new StringBuilder(sbMethod());
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(sbValue.toString());
    }

    // -- Places --
    public StringBuilder sbMethod() {

        //use your current location here
        double mLatitude = 49.212719;
        double mLongitude = -122.919485;

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + mLatitude + "," + mLongitude);
        sb.append("&radius=5000");
        sb.append("&types=" + "restaurant");
        sb.append("&sensor=true");
        sb.append("&key=" + R.string.google_maps_key);

        Log.d("Map", "api: " + sb.toString());

        return sb;
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParserTask
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while dl url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJson.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            mGoogleMap = site.mGoogleMap;

            Log.d("Map", "list size: " + list.size());
            // Clears all the existing markers;
            mGoogleMap.clear();

            for (int i = 0; i < list.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);


                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                Log.d("Map", "place: " + name);

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                markerOptions.title(name + " : " + vicinity);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                // Placing a marker on the touched position
                Marker m = mGoogleMap.addMarker(markerOptions);

            }
        }
    }

}

