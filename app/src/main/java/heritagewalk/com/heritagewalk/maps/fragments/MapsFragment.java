package heritagewalk.com.heritagewalk.maps.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.io.Serializable;
import java.util.ArrayList;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.maps.MapsActivity;
import heritagewalk.com.heritagewalk.maps.SiteMarkerRenderer;
import heritagewalk.com.heritagewalk.maps.SitePageActivity;
import heritagewalk.com.heritagewalk.models.Site;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance(String, String)} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements
        OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<Site>,
        ClusterManager.OnClusterInfoWindowClickListener<Site>,
        ClusterManager.OnClusterItemClickListener<Site>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Site>,
        Serializable {

    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 12.0f;
    private static final float MIN_ZOOM = 10.0f;
    private static final float MAX_ZOOM = 18.0f;
    private static final LatLng NEW_WEST = new LatLng(49.2057179, -122.910956);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 200;

    private GoogleMap mMap;
    private MapView mMapView;
    private ArrayList<Site> mHeritageSites = MapsActivity.mHeritageSites;
    private ClusterManager<Site> mClusterManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View mView;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_maps, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map_view);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.e(TAG, "onMapReady: " + "Inside onMapReady");
        mMap = map;

        // Do all the map setup (Camera, permissions, etc.)
        setUpMap();

        // We want to use clusters, so we need to set that up
        setUpClusterManager();
    }

    private void setUpMap() {

        // Check if user has given permissions to access location
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
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
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO: Show an explanation to the user *asynchronously* --
                // don't block this thread waiting for the user's response!
                // After the user sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        mClusterManager = new ClusterManager<Site>(getContext(), mMap);

        // We implement our own renderer so we can use custom-styled markers.
        mClusterManager.setRenderer(new SiteMarkerRenderer(getContext(), mMap, mClusterManager));


        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                MapsActivity.SITE_LATLNG = marker.getPosition();
                Toast.makeText(getContext(), "Info Window", Toast.LENGTH_SHORT).show();
            }
        });


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
    * */
    private void startNewSiteActivity(Site newSite) {
        Intent intent = new Intent(getContext(), SitePageActivity.class);
//        Intent intent = new Intent(MapsActivity.this, SiteFragment.class);
        intent.putExtra("selectedSiteName", newSite.getName());
        intent.putExtra("selectedSiteDesc", newSite.getDescription());
        intent.putExtra("selectedSiteSummary", newSite.getSummary());

        intent.putExtra("selectedSiteLatLng", newSite.getLatLng().toString());
        startActivity(intent);
    }

    @Override
    public boolean onClusterItemClick(Site site) {
        Toast.makeText(getContext(), "Cluster CLick", Toast.LENGTH_SHORT).show();
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
        MapsActivity.SITE_LATLNG = site.getLatLng();
        Toast.makeText(getContext(), "Info Window", Toast.LENGTH_SHORT).show();
    }
}
