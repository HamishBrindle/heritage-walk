package heritagewalk.com.heritagewalk.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Site is a heritage site location holding data.
 *
 * Created by hamis on 2017-10-05.
 */

public class Site implements ClusterItem {

    private String name;
    private LatLng latLng;
    private String summary;
    private String description;
    private int id;
    private double X;
    private double Y;

    public LatLng getLatLng() {
        if (latLng == null)
            latLng = new LatLng(Y, X);
        return latLng;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public LatLng getPosition() {
        return getLatLng();
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return description;
    }



}
