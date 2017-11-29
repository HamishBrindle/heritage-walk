package heritagewalk.com.heritagewalk.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.GeoApiContext;
import com.google.maps.DirectionsApi;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.main.BaseActivity;
import heritagewalk.com.heritagewalk.maps.tasks.MockLocationProvider;
import heritagewalk.com.heritagewalk.maps.tasks.PlacesJSONTask;
import heritagewalk.com.heritagewalk.models.Place;
import heritagewalk.com.heritagewalk.utility.DeviceUtility;
import heritagewalk.com.heritagewalk.utility.HorizontalCardViewAdapter;

public class SitePageActivity extends BaseActivity
        implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener, LocationSource.OnLocationChangedListener{

    public SitePageActivity(){}

    private static final String TAG = "SitePageActivity";
    private String siteName;
    private String siteSummary;
    private final LatLng centerOfNewWest = new LatLng(49.2057, -122.9110);
    private FusedLocationProviderClient mFusedLocationClient;
    private MockLocationProvider mMockLocationProvider;
    private GeoApiContext mGeoApiContext;
    private float mStartingBearing;

    private CardView mCardView;

    static float latitude;
    static float longitude;

    protected GoogleMap mGoogleMap;
    protected LatLngBounds mLatLngBounds;
    private boolean mFollowUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        siteName = intent.getStringExtra("selectedSiteName");
        String sitePosition = intent.getStringExtra("selectedSiteLatLng");
        siteSummary = intent.getStringExtra("selectedSiteSummary");
        String[] latlong = sitePosition.split(",");
        latitude = convertStringToFloat(latlong[0]);
        longitude = convertStringToFloat(latlong[1]);
        mLatLngBounds = new LatLngBounds(new LatLng(latitude, longitude), new LatLng(latitude + 0.0001, longitude + 0.0001));
        mCardView = findViewById(R.id.card_view);


        setUpViews();

        // Don't bother me with this Mock location crap if I'm a real device.
        if (DeviceUtility.isEmulator()) {

            /*
            * Checking if phone has Mock Location enabled.
            */
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return;
            }

            /*
            * Getting Mock location provider to emulate the location of the device to the center of
            * New Westminster.
            */
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mMockLocationProvider = new MockLocationProvider(LocationManager.NETWORK_PROVIDER, this);
            mMockLocationProvider.pushLocation(centerOfNewWest.latitude, centerOfNewWest.longitude);

            // Setting Mock location
            mFusedLocationClient.setMockLocation(mMockLocationProvider.getLocationAt(0));
            mFusedLocationClient.setMockMode(true);
        }

        LocationManager locMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        BlueDotWalker blueDot = new BlueDotWalker();

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
        locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, blueDot);
    }

    /*
    * Helper function for converting latlng.toString to a float value.
    * */
    private float convertStringToFloat(String toConvert) {
        return Float.parseFloat(toConvert.replaceAll("[^\\d-.]", ""));
    }

    /*
    * Setting up the view below the mapview.
    * */
    private void setUpViews() {
        //Set up googlemapsfragment
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFrag.getMapAsync(this);

        TextView siteTitleView = findViewById(R.id.siteTitle);
        siteTitleView.setText(siteName);
        siteTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Moves camera to target site's location
                moveToMarker(new LatLng(latitude, longitude), mGoogleMap);
            }
        });

        TextView siteSummView = findViewById(R.id.siteSummary);
        siteSummView.setText(siteSummary);

    }

    /*
    * Called when the map is ready.
    * */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        DirectionsResult directions = null;
        mGoogleMap = googleMap;

//        mCardView.setOnClickListener(new CardView.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                moveToMarker(new LatLng(latitude, longitude), mGoogleMap);
//            }
//        });

        //Heritage Site location
        LatLng siteLocation = new LatLng(latitude, longitude);
        LocationManager locMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LatLng startingLocation;
        if (DeviceUtility.isEmulator()) {
            //getting users starting position.  Currently it is using the mock location
            startingLocation = new LatLng(mMockLocationProvider.getLocationAt(0).getLatitude(),
                    mMockLocationProvider.getLocationAt(0).getLongitude());
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            LatLng latLng = new LatLng(
                    locMgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLatitude(),
                    locMgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLongitude()
            );
            startingLocation = latLng;
        }
        float startingBearing = getBearing(startingLocation, siteLocation);

        //Checking users permissions.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.setPadding(0,200,0,0);
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mGoogleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.map_style));
            }
        } else {
            mGoogleMap.setMyLocationEnabled(true);
        }
        try {
            //Getting direction from the starting location to the site location
            directions = getDirections(mGeoApiContext, startingLocation, siteLocation);
            Log.d("aftertry", "aftertry");
        } catch (InterruptedException | ApiException | IOException e) {
            e.printStackTrace();
        }

        //The addPath function sets the starting bearing and adds the path for the user
        addEndMarkerToMap(directions, mGoogleMap);
        addPath(directions, mGoogleMap);

        //Setting the camera position to user's current positiion, and tilting camera by 45 degrees
        CameraPosition startingPositionCamera = CameraPosition.builder().
                target(startingLocation).zoom(17).bearing(mStartingBearing).tilt(45).build();
        //Updating camera to the starting position
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(startingPositionCamera));

        findNearbyPlaces();
    }



    private float getBearing(LatLng startingLocation, LatLng siteLocation) {

        double bearing = 0.0;
        double startX = startingLocation.longitude;
        double startY = startingLocation.latitude;

        double endX = siteLocation.longitude;
        double endY = siteLocation.latitude;

        double xDistance = startX - endX;
        double yDistance = startY - endY;

        double absXDist = Math.abs(xDistance);
        double absYDist = Math.abs(yDistance);

        if (startX < endX && startY < endY) {
            bearing = Math.toDegrees(Math.atan(absXDist / absYDist));
        } else if (startX < endX && startY > endY) {
            bearing = Math.toDegrees(Math.atan(absYDist / absXDist));
            bearing += 90;
        } else if (startX > endX && startY > endY) {
            bearing = Math.toDegrees(Math.atan(absXDist / absYDist));
            bearing += 180;
        } else if (startX > endX && startY < endY) {
            bearing = Math.toDegrees(Math.atan(absYDist / absXDist));
            bearing += 270;
        }
        bearing -= 10;
        return (float) bearing;
    }

    /*
    * Adding path along map for user to follow to the heritage site.
    * This function also sets the starting bearing for the camera
    * */
    private void addPath(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline
                .getEncodedPath());
        mStartingBearing = getBearing(decodedPath.get(0), decodedPath.get(1));
        PolylineOptions path = new PolylineOptions().addAll(decodedPath).width(20).
                color(this.getResources().getColor(R.color.light_blue));
        mMap.addPolyline(path);
    }

    /*
    * Getting Directions for the user
    * */
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

    /*
    * Getting the context for the particular Places API.  Also setting a query limit and timeouts
    * for requests.
    * */
    private GeoApiContext getGeoContext() {
        mGeoApiContext = new GeoApiContext();
        return mGeoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.directions_api_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    Marker targetLocation;

    private void addEndMarkerToMap(DirectionsResult results, GoogleMap mMap) {

        targetLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation
                .lat, results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0]
                .startAddress).snippet(getEndLocationTitle(results)));

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
//            @Override
//            public boolean onMarkerClick(Marker marker){
//                Log.d("onMarkerClick", "before equal reached");
//                if (marker.equals(targetLocation)){
//                    findNearbyPlaces();
//                }
//                Log.d("onMarkerClick", "after equal reached");
//                return true;
//            }
//        });
    }

//    private String getEndLocationTitle(DirectionsResult results) {
//        return "Time :" + results.routes[0].legs[0].duration.humanReadable;
//    }
    private String getEndLocationTitle(DirectionsResult results) {
        return "Time: " + results.routes[0].legs[0].duration.humanReadable
                + " | Distance: " + results.routes[0].legs[0].distance.humanReadable;
    }

    public void findNearbyPlaces() {
        StringBuilder sbValue = new StringBuilder(sbMethod());
        PlacesTask placesTask = new PlacesTask(getApplicationContext());
        placesTask.execute(sbValue.toString());
    }


    /**
     * Builds string to properly call Google Places Webservices with.
     * @return String sb;
     */
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        mFollowUser = true;
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mFollowUser) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13.0f));
        }
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;
        Context mContext;

        public PlacesTask(Context context) {
            mContext = context;
        }

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
            //ParserTask parserTask = new ParserTask(mContext);
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
            Place placeJson = new Place();
            placeJson.setCurrentLatLng(new LatLng(latitude, longitude));
            //PlacesJSONTask placesJSONTaskJson = new PlacesJSONTask();

            try {
                jObject = new JSONObject(jsonData[0]);

                //places = placesJSONTaskJson.parse(jObject);
                places = placeJson.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            ArrayList<Place> businesses = new ArrayList<>();
            Log.d("Map", "list size: " + list.size());
            // Clears all the existing markers;
            // mGoogleMap.clear();

            for (int i = 0; i<list.size(); i++){
                Log.d("unsorted", ""+list.get(i).get("distance") + " " + list.get(i).get("place_name"));
                // If place doesn't have the exact number of variables we need, remove it.
                if (list.get(i).size() != 9){
                    list.remove(i);
                }
            }

            //Sort List by Distance
            Collections.sort(list, new markerSortByDistance());

            for (int i = 0; i<list.size(); i++){
                Log.d("sorted", ""+list.get(i).get("distance") + " " + list.get(i).get("place_name"));
            }

            int listSize = 5;
            if (list.size() < listSize){
                listSize = list.size();
            }

            for (int i = 0; i < listSize; i++) {

                Place place = new Place();

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                place.setName(hmPlace.get("place_name"));

                // Format newline at comma separator
                String address = hmPlace.get("vicinity");
                int commaIndex = address.indexOf(',');
                if (commaIndex > 0) {
                    address = address.substring(0, commaIndex + 1) + "\n" + (address.substring(commaIndex + 2, address.length()));
                }
                place.setVicinity(address);

                place.setRating(hmPlace.get("rating"));
                place.setLatLng(new LatLng(Double.parseDouble(hmPlace.get("lat")), Double.parseDouble(hmPlace.get("lng"))));
                place.setPlaceId(hmPlace.get("place_id"));


                LatLng latLng = new LatLng(lat, lng);
                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting title
                markerOptions.title(hmPlace.get("place_name"));
                markerOptions.snippet(hmPlace.get("vicinity"));

                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_71l));

                // Placing a marker on the touched position
                Marker m = mGoogleMap.addMarker(markerOptions);

                businesses.add(place);

            }

            //HorizontalCardViewAdapter cardViewAdapter = new HorizontalCardViewAdapter(businesses, mGoogleMap);
            HorizontalCardViewAdapter cardViewAdapter = new HorizontalCardViewAdapter(businesses, Places.getGeoDataClient(SitePageActivity.this, null), mGoogleMap);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(SitePageActivity.this, LinearLayoutManager.HORIZONTAL, false);
            RecyclerView mHorizontalLayoutView = (RecyclerView) findViewById(R.id.recycler_view);
            // mHorizontalLayoutView.setHasFixedSize(true); TODO: Better performance
            mHorizontalLayoutView.setLayoutManager(horizontalLayoutManagaer);
            mHorizontalLayoutView.setAdapter(cardViewAdapter);

        }
    }

    /**
     * Moves camera to desired lat long position.
     * @param newPos Target LatLng
     * @param gMap GoogleMap
     */
    static public void moveToMarker(LatLng newPos, GoogleMap gMap){

        CameraPosition startingPositionCamera = CameraPosition.builder().
                target(newPos).zoom(16).tilt(45).build();

        //Updating camera to the starting position
        //gMap.moveCamera(CameraUpdateFactory.newCameraPosition(startingPositionCamera));
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(startingPositionCamera));
    }

    /**
     * Getter for SitePageActivity GoogleMap
     * @return mGoogleMap GoogleMap
     */
    public GoogleMap getGMap(){
        return mGoogleMap;
    }

    //Custom Collections Comparator Class to sort Place list by distance to current position
    private class markerSortByDistance implements Comparator<HashMap<String, String>>{
        public int compare(HashMap<String, String> a, HashMap<String, String> b){
            if(Double.parseDouble(a.get("distance")) >= Double.parseDouble(b.get("distance"))) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /*
    * Setting interval to continually update the user's position every 1000 ms
    * */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        OnSuccessListener<Location> mSuccessListener = new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                }
            }
        };
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Activity) this, mSuccessListener);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_site_page;
    }

    /*
    * Location listener.  Represented by the blue dot on the map.
    * */
    public class BlueDotWalker implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}

