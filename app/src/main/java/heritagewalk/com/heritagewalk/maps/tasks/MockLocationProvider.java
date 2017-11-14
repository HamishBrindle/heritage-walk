package heritagewalk.com.heritagewalk.maps.tasks;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for pushing mock GPD data to the phone/emulator during testing.
 *
 * this sets location for GPS location provider only. If we're using a network provider then it will not work.
 * DDMS can set location for an emulator device only.
 *
 * You can not do testing using a real device.
 *
 * Created by mini_regan on 2017-11-13.
 */

public class MockLocationProvider {
    String providerName;
    Context ctx;
    ArrayList<Location> mockLocations;

    public MockLocationProvider(String name, Context ctx) {
        this.providerName = name;
        this.ctx = ctx;
        mockLocations = new ArrayList<Location>();
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        lm.addTestProvider(providerName, false, false, false, false, false,
                true, true, 0, 5);
        lm.setTestProviderEnabled(providerName, true);
    }

    public void pushLocation(double lat, double lon) {
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);

        Location mockLocation = new Location(providerName);
        mockLocation.setLatitude(lat);
        mockLocation.setLongitude(lon);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(2);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        mockLocations.add(mockLocation);
        lm.setTestProviderLocation(providerName, mockLocation);
    }

    public Location getLocationAt(int index)
    {
        return mockLocations.get(index);
    }

    public void shutdown() {
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        lm.removeTestProvider(providerName);
    }

}
