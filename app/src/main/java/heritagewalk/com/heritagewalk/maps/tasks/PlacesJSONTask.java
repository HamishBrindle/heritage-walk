package heritagewalk.com.heritagewalk.maps.tasks;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * PlacesJSONTask pulls information about the place to a map.
 * Created by Wei on 11/22/2017.
 */

public class PlacesJSONTask {

    private static final String TAG = "PlacesJSONTask";

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
     * Parsing the PlacesJSONTask JSON object
     */
    private HashMap<String, String> getPlace(JSONObject jPlace) {

        HashMap<String, String> place = new HashMap<String, String>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String rating = "-NA-";
        String phoneNumber = "[Phone-number Unavailable]";
        String website = "-NA-";

        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
            // Extracting PlacesJSONTask name, if available
            if (!jPlace.isNull("name")) {
                placeName = jPlace.getString("name");
            }

            // Extracting PlacesJSONTask name, if available
            if (!jPlace.isNull("rating")) {
                rating = jPlace.getString("rating");
            }

            // Extracting PlacesJSONTask Vicinity, if available
            if (!jPlace.isNull("vicinity")) {
                vicinity = jPlace.getString("vicinity");
            }

            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = jPlace.getString("reference");

            place.put("place_name", placeName);
            place.put("vicinity", vicinity);
            place.put("rating", rating);

            place.put("lat", latitude);
            place.put("lng", longitude);
            place.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}