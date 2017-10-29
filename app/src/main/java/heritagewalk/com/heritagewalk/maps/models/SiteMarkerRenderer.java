package heritagewalk.com.heritagewalk.maps.models;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * SiteMarkerRenderer defines the rendering of Site markers (icon, color, etc.).
 *
 * Created by hamis on 2017-10-28.
 */

public class SiteMarkerRenderer extends DefaultClusterRenderer<Site> {

    public SiteMarkerRenderer(Context context, GoogleMap map, ClusterManager<Site> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Site item, MarkerOptions markerOptions) {
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        markerOptions.icon(markerDescriptor);
    }

    @Override
    protected void onClusterItemRendered(Site clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }
}
