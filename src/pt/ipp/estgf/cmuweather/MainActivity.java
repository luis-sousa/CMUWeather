package pt.ipp.estgf.cmuweather;

import pt.ipp.estgf.cmuweather.PlaceDetailsFragment.PlaceForecastListener;
import pt.ipp.estgf.cmuweather.PlaceWeatherFragment.PlaceListSelectedListener;
import pt.ipp.estgf.cmuweather.WeatherMapFragment.PlaceMapSelectedListener;
import pt.ipp.estgf.cmuweatherlib.MyDbHelper;
import pt.ipp.estgf.cmuweatherlib.Place;
import pt.ipp.estgf.cmuweatherlib.WeatherCondition;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements
		PlaceListSelectedListener, PlaceForecastListener,
		PlaceMapSelectedListener{

	private MyDbHelper dbHelper;
	private SQLiteDatabase db;
	private ProgressDialog progressD;
	private SharedPreferences mSettings;
	public DownloadWeatherConditionsReceiver receiver;

	public static String PACKAGE_NAME;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places_fragment);

		if (findViewById(R.id.places) != null) {
			if (savedInstanceState != null)
				return;

			PlaceWeatherFragment cityListFragment = new PlaceWeatherFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.places, cityListFragment).commit();
		}

		PACKAGE_NAME = getApplicationContext().getPackageName();
		receiver = new DownloadWeatherConditionsReceiver();
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);

	}
	
	

	protected void replaceWithDetail(Place place) {

		PlaceDetailsFragment detailFragment = (PlaceDetailsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_fragment);

		dbHelper = new MyDbHelper(this);
		db = dbHelper.getWritableDatabase();

		WeatherCondition wc = WeatherCondition.getCurrentForPlace(
				place.getId(), db);

		db.close();

		if (detailFragment != null) {
			detailFragment.setCity(place);
			detailFragment.setWeatherConditions(wc);
			detailFragment.updatePlace();
		} else {
			detailFragment = new PlaceDetailsFragment();
			detailFragment.setCity(place);
			detailFragment.setWeatherConditions(wc);

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.places, detailFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	protected void replaceWithMap() {

		WeatherMapFragment mapFragment = (WeatherMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_fragment);
		if (mapFragment != null) {

		} else {
			mapFragment = new WeatherMapFragment();

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.places, mapFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	protected void replaceWithForecast(Place place) {
		ForecastWeatherFragment forecastFragment = (ForecastWeatherFragment) getSupportFragmentManager()
				.findFragmentById(R.id.forecast_fragment);

		if (forecastFragment != null) {
			forecastFragment.setCity(place);
		} else {

			forecastFragment = new ForecastWeatherFragment();
			forecastFragment.setCity(place);

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.places, forecastFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.update_weather_info:
			startService();
			break;
		case R.id.map_view:
			replaceWithMap();
			break;
		case R.id.preferences:
			mSettings.registerOnSharedPreferenceChangeListener(prefListener);
			startActivity(new Intent(getApplicationContext(),
					PreferencesActivity.class));
		default:
			break;
		}

		return true;

	}

	@Override
	public void onResume() {
		super.onResume();

		IntentFilter mIF = new IntentFilter();
		mIF.addAction("pt.ipp.estgf.cmuweather.WEATHER_CONDITION_UPDATED");

		registerReceiver(receiver, mIF);

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	public void onPlaceListSelected(Place place) {
		replaceWithDetail(place);

	}

	@Override
	public void onPlaceSelected(Place place) {
		replaceWithForecast(place);
	}

	private void startService() {

		startService(new Intent(this, DownloadWeatherInfoService.class));

		progressD = new ProgressDialog(this);
		progressD.setTitle("Updating Weather Conditions");
		progressD.show();

	}

	public class DownloadWeatherConditionsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (progressD.isShowing()) {
				progressD.dismiss();
				updateWidget();
			}

		}

	}

	@Override
	public void onPlaceMapSelected(Place place) {
		replaceWithDetail(place);

	}


	SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key) {
			Log.w("WIDGET", "DEVIA UPDATE");
			updateWidget();
		}
	};

	private void updateWidget() {

		String prefPlace = mSettings.getString("listpref", "0");
		String tempType = mSettings.getString("listprefDegrees", "null");
		
		dbHelper = new MyDbHelper(this);
		db = dbHelper.getWritableDatabase();
		
		Place widgetPlace = Place.get(Integer.parseInt(prefPlace), db);

		WeatherCondition wc = WeatherCondition.getCurrentForPlace(
				widgetPlace.getId(), db);

		Intent mIntent = new Intent();
		mIntent.setAction("pt.ipp.estgf.cmuweather.UPDATE_WIDGET");

		mIntent.putExtra("Place", widgetPlace.getName());

		if (tempType.equals("C")) {
			mIntent.putExtra("Temperature", wc.getCurrentTemperature() + "ºC");
		} else {
			mIntent.putExtra("Temperature",
					((wc.getCurrentTemperature() * 1.8) + 32) + "ºF");
		}

		int iconID = this.getResources().getIdentifier(wc.getIcon(),
				"drawable", MainActivity.PACKAGE_NAME);
		mIntent.putExtra("Icon", iconID);

		mIntent.putExtra("Description", wc.getDescription());

		this.sendBroadcast(mIntent);
		
		db.close();
	}

}
