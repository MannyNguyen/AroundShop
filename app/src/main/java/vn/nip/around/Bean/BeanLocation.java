package vn.nip.around.Bean;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by viminh on 10/7/2016.
 */

public class BeanLocation {
    private double latitude;
    private double longitude;
    private LatLng latLng;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng() {
        latLng = new LatLng(getLatitude(),getLongitude());
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
