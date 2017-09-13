package pt.ipp.estgf.cmuweather;

import java.util.ArrayList;

import pt.ipp.estgf.cmuweather.PlaceWeatherFragment.PlaceListSelectedListener;
import pt.ipp.estgf.cmuweatherlib.MyDbHelper;
import pt.ipp.estgf.cmuweatherlib.Place;
import pt.ipp.estgf.cmuweatherlib.WeatherCondition;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class WeatherMapFragment extends SupportMapFragment implements OnInfoWindowClickListener {

	private ArrayList<Place> placesList = new ArrayList<Place>();
	
	private GoogleMap mMap;
	private LatLng coord = null;
	
	private MyDbHelper dbHelper;
	private SQLiteDatabase db;
	private WeatherCondition weather = new WeatherCondition();
	private Context context;
	
	private PlaceMapSelectedListener listener;

	public static interface PlaceMapSelectedListener {
		public void onPlaceMapSelected(Place place);
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		
		try {
			listener = (PlaceMapSelectedListener) getActivity();
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public void onStart() {

		super.onStart();
		
		
		mMap = super.getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.getUiSettings().setZoomControlsEnabled(false);

		dbHelper = new MyDbHelper(context);
		db = dbHelper.getReadableDatabase();
		
		mMap.setOnInfoWindowClickListener(this);

		placesList = Place.getAll(placesList, db);

		for (Place place : placesList) {
			
			weather = WeatherCondition.getCurrentForPlace(place.getId(), db);

			coord = new LatLng(place.getLatitude(), place.getLongitude());
			
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 6));

			MarkerOptions mOptions = new MarkerOptions();
			mOptions.position(coord);
			mOptions.title(place.getName());
			if(weather != null){
				mOptions.snippet(context.getString(R.string.current_temp) + weather.getCurrentTemperature());
				int idImage = getResources().getIdentifier(
						"s20x20" + weather.getIcon(), "drawable",
						getActivity().getPackageName());

				mOptions.icon(BitmapDescriptorFactory.fromResource(idImage));					
			}
			
			mMap.addMarker(mOptions);


		}

	}


	@Override
	public void onInfoWindowClick(Marker marker) {
		long placeID = 0;
		
		for (Place place : placesList) {
			
			if(place.getName().equalsIgnoreCase(marker.getTitle())){
				placeID = place.getId();
				break;
			}
			
		}
		
		if (listener != null) {
			listener.onPlaceMapSelected(Place.get(placeID, db));
			db.close();
		}
		
	}
}