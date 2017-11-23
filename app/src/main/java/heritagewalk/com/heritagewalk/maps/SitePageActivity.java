package heritagewalk.com.heritagewalk.maps;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.maps.tasks.MockLocationProvider;

public class SitePageActivity extends FragmentActivity implements OnMapReadyCallback{
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected String sitePosition;
    protected String siteName;
    protected String siteDescription;
    protected String siteSummary;
    final LatLng centerOfNewWest = new LatLng(49.2057, -122.9110);
    FusedLocationProviderClient mFusedLocationClient;
    MockLocationProvider mMockLocationProvider;
    LatLng startingLocation;
    GeoApiContext mGeoApiContext;


    static float latitude;
    static float longitude;

    private TextView siteTitle;

    protected GoogleMap mGoogleMap;
    protected LatLngBounds mLatLngBounds;

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
        mLatLngBounds = new LatLngBounds(new LatLng(latitude,longitude), new LatLng(latitude + 0.0001, longitude + 0.0001));

        setUpViews();
//
////        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);
////
////        // Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
//
//        // TODO: Start using the Places API.

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mMockLocationProvider = new MockLocationProvider(LocationManager.NETWORK_PROVIDER, this);
        mMockLocationProvider.pushLocation(centerOfNewWest.latitude, centerOfNewWest.longitude);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.setMockLocation(mMockLocationProvider.getLocationAt(0));

        mFusedLocationClient.setMockMode(true);
        Log.d("onCreate", "oncreate");
        LocationManager locMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener lis = new LocationListener() {
            public void onLocationChanged(Location location) {
                //You will get the mock location
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
            //...
        };

        /*
        * Checking permissions
        * */
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locMgr.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000, 1, lis);
    }

    private float convertStringToFloat (String toConvert) {
        return Float.parseFloat(toConvert.replaceAll("[^\\d-.]", ""));
    }

    private void setUpViews() {
        //Set up googlemapsfragment
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        TextView siteTitleView = findViewById(R.id.siteTitle);
        siteTitleView.setText(siteName);

        TextView siteSummView = findViewById(R.id.siteSummary);
        siteSummView.setText(siteSummary);

        TextView businessPromptView = findViewById(R.id.businessPrompt);
        String BUSINESS_PROMPT = "Come check out these businesses!";
        businessPromptView.setText(BUSINESS_PROMPT);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        DirectionsResult directions = null;
        mGoogleMap = googleMap;
        mGoogleMap.setMaxZoomPreference(15.0f);
        LatLng siteLocation = new LatLng(latitude,longitude);
        startingLocation = new LatLng(mMockLocationProvider.getLocationAt(0).getLatitude(), mMockLocationProvider.getLocationAt(0).getLongitude());
        CameraPosition startingPositionCamera = CameraPosition.builder().target(startingLocation).zoom(16).bearing(0).tilt(45).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(startingPositionCamera));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        else {
            mGoogleMap.setMyLocationEnabled(true);
        }

        try {
            directions = getDirections(mGeoApiContext, startingLocation, siteLocation);
            Log.d("aftertry", "aftertry");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        addStartEndMarkersToMap(directions, mGoogleMap);
        addPath(directions, mGoogleMap);
    }

    private void addPath(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline
                .getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private DirectionsResult getDirections(GeoApiContext mGeoApiContext, LatLng origin, LatLng destination)
            throws InterruptedException, ApiException, IOException {
        DateTime now = new DateTime();
        String[] originString = origin.toString().split(",");
        String[] destinationString = destination.toString().split(",");
        float originLat = convertStringToFloat(originString[0]);
        float originLon = convertStringToFloat(originString[1]);

        float destinationLat = convertStringToFloat(destinationString[0]);
        float destinationLon = convertStringToFloat(destinationString[1]);

        Log.d("origin", origin.toString());
        Log.d("destination", destination.toString());
        DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                .mode(TravelMode.WALKING)
                .origin("" + originLat + "," + originLon)
                .destination("" + destinationLat + "," + destinationLon)
                .departureTime(now).await();
        return result;
    }

    private GeoApiContext getGeoContext() {
        mGeoApiContext= new GeoApiContext();
        return mGeoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.directions_api_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addStartEndMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0]
                .startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(results
                .routes[0].legs[0].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation
                .lat,results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0]
                .startAddress).snippet(getEndLocationTitle(results)));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable
                + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    public void findNearbyPlaces(View v){
        StringBuilder sbValue = new StringBuilder(sbMethod());
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(sbValue.toString());
    }

    // -- Places --
    public StringBuilder sbMethod() {

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + latitude + "," + longitude);
        sb.append("&radius=1000");
        sb.append("&types=" + "restaurant");
        sb.append("&sensor=true");
        sb.append("&key=" + "AIzaSyC0jBiaFqbQytOt_KPExxKL8GRrEYiQgJY");

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

