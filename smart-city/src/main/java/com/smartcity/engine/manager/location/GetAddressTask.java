package com.smartcity.engine.manager.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import com.smartcity.engine.manager.Manager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by po on 10/14/14.
 */
public class GetAddressTask extends AsyncTask<Location, Void, String> {

    public GetAddressTask() {
        super();
    }

    @Override
    protected String doInBackground(Location... params) {
        Geocoder geocoder = new Geocoder(Manager.activity(), Locale.getDefault());
        // Get the current location from the input parameter list
        Location loc = params[0];
        // Create a list to contain the result address
        List<Address> addresses = null;
        try {
                /*
                 * Return 1 address.
                 */
            addresses = geocoder.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
        } catch (IOException e1) {
            Log.e("LocationSampleActivity", "IO Exception in getFromLocation()");
            e1.printStackTrace();
            return ("IO Exception trying to get address");
        } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments " + Double.toString(loc.getLatitude()) +
                    " , " + Double.toString(loc.getLongitude()) + " passed to address service";
            Log.e("LocationSampleActivity", errorString);
            e2.printStackTrace();
            return errorString;
        }
        // If the reverse geocode returned an address
        if (addresses != null && addresses.size() > 0) {
            // Get the first address
            Address address = addresses.get(0);

            /*String addressText = String.format(
                    "%s, %s, %s",
                    // If there's a street address, add it
                    address.getMaxAddressLineIndex() > 0 ?
                            address.getAddressLine(0) : "",
                    // Locality is usually a city
                    address.getLocality(),
                    // The country of the address
                    address.getCountryName());*/

            String addressText = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";

            // Return the text
            return addressText;
        } else {
            return "No address found";
        }
    }

    @Override
    protected void onPostExecute(String address) {
        Log.e("location test", address);

        Manager.view().setPositionTextView(address);
    }
}
