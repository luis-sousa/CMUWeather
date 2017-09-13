package pt.ipp.estgf.cmuweather;

import pt.ipp.estgf.cmuweatherlib.Place;
import pt.ipp.estgf.cmuweatherlib.WeatherCondition;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceDetailsFragment extends Fragment {

	private final String KMPH = " km/h";
	private final String MILES = " miles";
	private final String PERCENTAGE = "%";
	private final String MM = "mm";
	private final String CELCIUS = " ºC";
	private final String FAHRENHEIT = " ºF";

	private TextView txtName;
	private TextView txtCurrentTemp;
	private TextView txtCondition;
	private TextView txtHumidity;
	private TextView txtPrecipitation;
	private TextView txtWindSpeed;
	private TextView txtWindDirection;
	private TextView txtObservationTime;
	private TextView txtForecast;
	private ImageView imgWeatherIcon;

	private Place myPlace = null;
	private WeatherCondition wc = null;

	private Context context;
	private SharedPreferences mSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getActivity();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		try {
			listener = (PlaceForecastListener) getActivity();
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		}
	}

	public void setCity(Place place) {
		this.myPlace = place;
	}

	public void setWeatherConditions(WeatherCondition wc) {
		this.wc = wc;
	}

	private PlaceForecastListener listener;

	public static interface PlaceForecastListener {
		public void onPlaceSelected(Place place);
	}

	public void updatePlace() {
		if (myPlace != null) {
				
			String tempType = mSettings.getString("listprefDegrees", "null");
			String windType = mSettings.getString("listprefWind", "null");

			txtName.setText(myPlace.getName());

			if (wc != null) {
				if(tempType.equals("C")){
					txtCurrentTemp.setText("" + wc.getCurrentTemperature()
							+ CELCIUS);
				}else{
					txtCurrentTemp.setText("" + ((wc.getCurrentTemperature()*1.8)+32)
							+ FAHRENHEIT);
				}
				txtCondition.setText(wc.getDescription());
				txtHumidity.setText("" + wc.getHumidity() + PERCENTAGE);
				txtPrecipitation.setText("" + wc.getPrecipitationMM()
						+ MM);
				if(windType.equals("Km")){
					txtWindSpeed.setText("" + wc.getWindSpeedKmph() + KMPH);
				}else{
					txtWindSpeed.setText("" + wc.getWindSpeedMiles() + MILES);
				}
				
				txtWindDirection.setText(wc.getWindDirection());
				int iconID = this.getResources().getIdentifier(wc.getIcon(),
						"drawable", MainActivity.PACKAGE_NAME);
				imgWeatherIcon.setImageDrawable(this.getResources()
						.getDrawable(iconID));

				
				txtObservationTime.setText(wc.getObservationTime());

			}
		}
	}

	public void onResume() {
		super.onResume();

		mSettings = PreferenceManager
				.getDefaultSharedPreferences(context);

		updatePlace();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View mContentView = inflater.inflate(R.layout.details_fragment,
				container, false);

		txtName = (TextView) mContentView.findViewById(R.id.city_name);
		txtCurrentTemp = (TextView) mContentView
				.findViewById(R.id.current_temp);
		txtCondition = (TextView) mContentView.findViewById(R.id.condition);
		imgWeatherIcon = (ImageView) mContentView
				.findViewById(R.id.weather_image);
		txtHumidity = (TextView) mContentView.findViewById(R.id.humidity);
		txtPrecipitation = (TextView) mContentView
				.findViewById(R.id.precipitation);
		txtWindSpeed = (TextView) mContentView.findViewById(R.id.wind_speed);
		txtWindDirection = (TextView) mContentView
				.findViewById(R.id.wind_direction);
		txtObservationTime = (TextView) mContentView
				.findViewById(R.id.last_update);
		
		txtForecast = (TextView) mContentView
				.findViewById(R.id.details_forecast);
		
		txtForecast.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToForecast(v);
			}
		});

		return mContentView;

	}

	public void goToForecast(View v) {

		if (listener != null) {
			listener.onPlaceSelected(myPlace);
		}

	}

}
