//package heritagewalk.com.heritagewalk.maps;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.maps.DirectionsApi;
//import com.google.maps.DirectionsApiRequest;
//import com.google.maps.GeoApiContext;
//import com.google.maps.android.PolyUtil;
//import com.google.maps.errors.ApiException;
//import com.google.maps.model.DirectionsResult;
//import com.google.maps.model.TravelMode;
//
//import org.joda.time.DateTime;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import heritagewalk.com.heritagewalk.R;
//import heritagewalk.com.heritagewalk.maps.tasks.MockLocationProvider;
//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link SiteFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link SiteFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class SiteFragment extends Fragment implements OnMapReadyCallback,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener {
//
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
//    public GoogleMap mGoogleMap;
//    MapView mMapView;
//    View mView;
//    GoogleApiClient mGoogleApiClient;
//    LocationRequest mLocationRequest;
//    FusedLocationProviderClient mFusedLocationClient;
//    Location mLastLocation;
//    GeoApiContext mGeoApiContext;
//    LatLng startingLocation;
//    Marker mCurrLocationMarker;
//
//    OnSuccessListener<Location> mSuccessListener;
//    MockLocationProvider mMockLocationProvider;
//
//
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private LatLng siteLocation;
//    private String mParam1;
//    private String mParam2;
//
//    final LatLng centerOfNewWest = new LatLng(49.2057, -122.9110);
//    private OnFragmentInteractionListener mListener;
//
//    public SiteFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//
//     * @return A new instance of fragment SiteFragment.
//     */
//    public static SiteFragment newInstance() {
//        SiteFragment fragment = new SiteFragment();
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
//        mMockLocationProvider = new MockLocationProvider(LocationManager.NETWORK_PROVIDER, getContext());
//        mMockLocationProvider.pushLocation(centerOfNewWest.latitude, centerOfNewWest.longitude);
//
//        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        mFusedLocationClient.setMockLocation(mMockLocationProvider.getLocationAt(0));
//
//        mFusedLocationClient.setMockMode(true);
//        Log.d("onCreate", "oncreate");
//        LocationManager locMgr = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
//        LocationListener lis = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                //You will get the mock location
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//
//            }
//            //...
//        };
//
//        /*
//        * Checking permissions
//        * */
//        if (ActivityCompat.checkSelfPermission(getContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locMgr.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER, 1000, 1, lis);
//
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mView = inflater.inflate(R.layout.fragment_site, container, false);
//        return mView;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mMapView = (MapView) mView.findViewById(R.id.mapview1);
//        if (mMapView != null) {
//            mMapView.onCreate(null);
//            mMapView.onResume();
//            mMapView.getMapAsync(this);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mGoogleMap = googleMap;
//        getGeoContext();
//        DirectionsResult directions = null;
//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        siteLocation = new LatLng(SitePageActivity.latitude, SitePageActivity.longitude);
//        startingLocation = new LatLng(mMockLocationProvider.getLocationAt(0).getLatitude(), mMockLocationProvider.getLocationAt(0).getLongitude());
//
//        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//            return;
//        }
////        mGoogleMap.addMarker(new MarkerOptions().position(siteLocation)).setTitle("hayyy");
//        CameraPosition startingPositionCamera = CameraPosition.builder().target(startingLocation).zoom(16).bearing(0).tilt(45).build();
//        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(startingPositionCamera));
//
//
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(getContext(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                buildGoogleApiClient();
//                mGoogleMap.setMyLocationEnabled(true);
//            }
//        }
//        else {
//            buildGoogleApiClient();
//            mGoogleMap.setMyLocationEnabled(true);
//        }
//
//        try {
//            directions = getDirections(mGeoApiContext, startingLocation, siteLocation);
//            Log.d("aftertry", "aftertry");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        addMarkersToMap(directions, mGoogleMap);
//        addPolyline(directions, mGoogleMap);
//    }
//
//    private DirectionsResult getDirections(GeoApiContext mGeoApiContext, LatLng origin, LatLng destination)
//            throws InterruptedException, ApiException, IOException {
//        DateTime now = new DateTime();
//        String[] originString = origin.toString().split(",");
//        String[] destinationString = destination.toString().split(",");
//        float originLat = convertStringToFloat(originString[0]);
//        float originLon = convertStringToFloat(originString[1]);
//
//        float destinationLat = convertStringToFloat(destinationString[0]);
//        float destinationLon = convertStringToFloat(destinationString[1]);
//
//        Log.d("origin", origin.toString());
//        Log.d("destination", destination.toString());
//        DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
//                .mode(TravelMode.WALKING)
//                .origin("" + originLat + "," + originLon)
//                .destination("" + destinationLat + "," + destinationLon)
//                .departureTime(now).await();
//        return result;
//    }
//
//    private float convertStringToFloat (String toConvert) {
//        return Float.parseFloat(toConvert.replaceAll("[^\\d-.]", ""));
//    }
//
//    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0]
//                .startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(results
//                .routes[0].legs[0].startAddress));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation
//                .lat,results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0]
//                .startAddress).snippet(getEndLocationTitle(results)));
//    }
//
//    private String getEndLocationTitle(DirectionsResult results){
//        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable
//                + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
//    }
//
//    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
//        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline
//                .getEncodedPath());
//        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
//    }
//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }
//
//    private GeoApiContext getGeoContext() {
//        mGeoApiContext= new GeoApiContext();
//        return mGeoApiContext.setQueryRateLimit(3)
//                .setApiKey(getString(R.string.directions_api_key))
//                .setConnectTimeout(1, TimeUnit.SECONDS)
//                .setReadTimeout(1, TimeUnit.SECONDS)
//                .setWriteTimeout(1, TimeUnit.SECONDS);
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }
//
//        //Place current location marker
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//
//        //move map camera
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//
//        //stop location updates
//        if (mGoogleApiClient != null) {
//            mFusedLocationClient.removeLocationUpdates((LocationCallback) mSuccessListener);
//        }
//
//    }
//
//    @Override
//    public void onStatusChanged(String s, int i, Bundle bundle) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String s) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String s) {
//
//    }
//
//    /*
//    * Setting interval to continually update the user's position every 1000 ms
//    * */
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//        mSuccessListener = new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                // Got last known location. In some rare situations this can be null.
//                if (location != null) {
//                    // Logic to handle location object
//                }
//            }
//        };
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        if (ContextCompat.checkSelfPermission(getContext(),
//            android.Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) {
//                mFusedLocationClient.getLastLocation()
//                    .addOnSuccessListener((Activity) getContext(), mSuccessListener);
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // Permission was granted.
//                    if (ContextCompat.checkSelfPermission((Activity) getContext(),
//                            android.Manifest.permission.ACCESS_FINE_LOCATION)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }
//                        mGoogleMap.setMyLocationEnabled(true);
//                    }
//
//                } else {
//
//                    // Permission denied, Disable the functionality that depends on this permission.
//                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other permissions this app might request.
//            //You can add here other case statements according to your requirement.
//        }
//    }
//
//    public boolean checkLocationPermission(){
//        if (ContextCompat.checkSelfPermission(getContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Asking user if explanation is needed
//            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//                //Prompt the user once explanation has been shown
//                ActivityCompat.requestPermissions((Activity) getContext(),
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_LOCATION);
//
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions((Activity) getContext(),
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_LOCATION);
//            }
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
//
//
//
////
////
////    public void findNearbyPlaces(){
////        StringBuilder sbValue = new StringBuilder(sbMethod());
////        PlacesTask placesTask = new PlacesTask();
////        placesTask.execute(sbValue.toString());
////    }
//}
