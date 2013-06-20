package uk.ac.brighton.ci360.bigarrow;

import java.util.List;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyMapActivity extends MapActivity implements LocationListener, PlaceSearchRequester {

    MapView mapView;
    List<Overlay> mapOverlays;
    AddItemizedOverlay itemizedOverlay;
 
    GeoPoint geoPoint;
    MapController mc;
     
    double latitude;
    double longitude;
    OverlayItem overlayitem;
	private LocationManager locationManager;
	private Location myLocation;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
 
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		myLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 10000, 50, this);
 
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
 
        mapOverlays = mapView.getOverlays();
         
        // Geopoint to place on map
        geoPoint = new GeoPoint((int) (myLocation.getLatitude() * 1E6),
                (int) (myLocation.getLongitude() * 1E6));
         
        // Drawable marker icon
        Drawable drawable_user = this.getResources()
                .getDrawable(R.drawable.mark_red);
         
        itemizedOverlay = new AddItemizedOverlay(drawable_user, this);
         
        // Map overlay item
        overlayitem = new OverlayItem(geoPoint, "Your Location", "That is you!");
 
        itemizedOverlay.addOverlay(overlayitem);
         
        mapOverlays.add(itemizedOverlay);
        itemizedOverlay.populateNow();
         
        // Drawable marker icon
        Drawable drawable = this.getResources()
                .getDrawable(R.drawable.mark_blue);
         
        itemizedOverlay = new AddItemizedOverlay(drawable, this);
 
        mc = mapView.getController();
 
        PlaceSearch pSearch = new PlaceSearch(this);
        pSearch.search(myLocation, new SearchEstab[] {SearchEstab.BAR}, SearchType.MANY);
 
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

	@Override
	public void updateNearestPlace(Place place, Location location,
			float distance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateNearestPlaces(PlacesList places) {
		// These values are used to get map boundary area
        // The area where you can see all the markers on screen
        int minLat = Integer.MAX_VALUE;
        int minLong = Integer.MAX_VALUE;
        int maxLat = Integer.MIN_VALUE;
        int maxLong = Integer.MIN_VALUE;
 
        // check for null in case it is null
        if (places.results != null) {
            // loop through all the places
            for (Place place : places.results) {
                latitude = place.geometry.location.lat; // latitude
                longitude = place.geometry.location.lng; // longitude
                 
                // Geopoint to place on map
                geoPoint = new GeoPoint((int) (latitude * 1E6),
                        (int) (longitude * 1E6));
                 
                // Map overlay item
                overlayitem = new OverlayItem(geoPoint, place.name,
                        place.vicinity);
 
                itemizedOverlay.addOverlay(overlayitem);
                 
                // calculating map boundary area
                minLat  = (int) Math.min( geoPoint.getLatitudeE6(), minLat );
                minLong = (int) Math.min( geoPoint.getLongitudeE6(), minLong);
                maxLat  = (int) Math.max( geoPoint.getLatitudeE6(), maxLat );
                maxLong = (int) Math.max( geoPoint.getLongitudeE6(), maxLong );
            }
            mapOverlays.add(itemizedOverlay);
             
            // showing all overlay items
            itemizedOverlay.populateNow();
        }
         
        // Adjusting the zoom level so that you can see all the markers on map
        mapView.getController().zoomToSpan(Math.abs( minLat - maxLat ), Math.abs( minLong - maxLong ));
         
        // Showing the center of the map
        mc.animateTo(new GeoPoint((maxLat + minLat)/2, (maxLong + minLong)/2 ));
        mapView.postInvalidate();
	}

	@Override
	public void updatePlaceDetails(PlaceDetails details) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
 
}