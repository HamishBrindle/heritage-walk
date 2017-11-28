package heritagewalk.com.heritagewalk.models;


import com.google.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Place.java is a JSON parser class designed to break down data from
 * Google Place Webservice into a format which can easily be placed
 * as markers on a google map.
 *
 * Current implementation finds the nearest restaurants to target
 * location and displays them with custom icons.
 */
public class Place {

    private String name;
    private String rating;
    private String address;
    private String website;
    private String phoneNumber;
    private String myVacinity;
    private String targetLat = "";
    private String targetLng = "";

    /**
     * Empty constructor.
     */
    public Place() {
    }

    public String getWebsite() {
        return website;
    }

    /**
     * Set latlng of target location
     * @param param LatLng of target location
     */
    public void setCurrentLatLng(LatLng param){
        targetLat = Double.toString(param.lat);
        targetLng = Double.toString(param.lng);
    }

    /**
     * Receives a JSONObject and returns a list
     */
    public List<HashMap<String, String>> parse(JSONObject jObject) {

        JSONArray jPlaces = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            jPlaces = jObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaces(jPlaces);
    }

    /**
     * List of google places objects as a hashmap
     * @param jPlaces Google Places JSON object array
     * @return List of Hashmaps
     */
    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
        int placesCount = jPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> place = null;

        /** Taking each place, parses and adds to list object */
        for (int i = 0; i < placesCount; i++) {
            try {
                /** Call getPlace with place JSON object to parse the place */
                place = getPlace((JSONObject) jPlaces.get(i));
                placesList.add(place);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    /**
     * Parsing the Place JSON object
     */
    private HashMap<String, String> getPlace(JSONObject jPlace) {

        HashMap<String, String> place = new HashMap<String, String>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String iconRef = "";

        try {
            // Extracting Place name, if available
            if (!jPlace.isNull("name")) {
                placeName = jPlace.getString("name");
            }

            // Extracting Place Vicinity, if available
            if (!jPlace.isNull("vicinity")) {
                vicinity = jPlace.getString("vicinity");
            }

            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = jPlace.getString("reference");
            //iconRef = jPlace.getString("icon"); Use for dynamic icons
            iconRef = "https://maps.gstatic.com/mapfiles/place_api/icons/shopping-71.png";

            place.put("place_name", placeName);
            place.put("vicinity", vicinity);
            place.put("lat", latitude);
            place.put("lng", longitude);
            place.put("reference", reference);
            place.put("icon", iconRef);

            double distance = haversine(Double.parseDouble(targetLat), Double.parseDouble(targetLng), Double.parseDouble(latitude), Double.parseDouble(longitude));
            place.put("distance", Double.toString(distance));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return myVacinity;
    }

    public void setVicinity(String vicinity) {
        myVacinity = vicinity;
    }

    public String getRating() {
        return rating;
    }


    /**
     * Haversine function to determine distance between two
     * geographical latlng coordinates.
     * @param lat1 Target location lat
     * @param lng1 Target location long
     * @param lat2 Current location lat
     * @param lng2 Current location long
     * @return
     */
    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
