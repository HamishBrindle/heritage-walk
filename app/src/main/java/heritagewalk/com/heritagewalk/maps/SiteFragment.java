package heritagewalk.com.heritagewalk.maps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import heritagewalk.com.heritagewalk.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SiteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SiteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SiteFragment extends Fragment implements OnMapReadyCallback {


    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    LatLng location;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private LatLng siteLocation;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SiteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SiteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SiteFragment newInstance(String param1, String param2) {
        Log.d("lat", "" + SitePageActivity.latitude);
        Log.d("SiteFrageNewInstance p1", param1);
        Log.d("SiteFrageNewInstance p2", param2);
        SiteFragment fragment = new SiteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("SiteFragOnCreate", "" + SitePageActivity.latitude);
        Log.d("SiteFragOnCreate", "" + SitePageActivity.longitude);

        if (getArguments() != null) {

            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static SiteFragment newInstance(float lat, float lon) {
        Log.d("SiteFrageNewInstance la", "lat");
        Log.d("SiteFrageNewInstance lo", "lon");
        SiteFragment f = new SiteFragment();
        Bundle args = new Bundle();
        args.putFloat("lat", lat);
        args.putFloat("lon", lon);
        f.setArguments(args);
        return f;
    }

    public void updateMap(float lat, float lon) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView =  inflater.inflate(R.layout.fragment_site, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.mapview);
        if(mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Log.d("onMapReady", "" + SitePageActivity.latitude);
        Log.d("onMapReady", "" + SitePageActivity.longitude);

        location = new LatLng(SitePageActivity.latitude, SitePageActivity.longitude);

        googleMap.addMarker(new MarkerOptions().position(location)).setTitle("hayyy");
        CameraPosition curSite = CameraPosition.builder().target(location).zoom(16).bearing(0).tilt(45).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(curSite));

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
}
