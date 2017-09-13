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
import android.support.v4.app.ListFragment;

public class ForecastWeatherFragment extends ListFragment{
	
	private ArrayList<WeatherCondition> forecastList = new ArrayList<WeatherCondition>();
	private ForecastListAdapter forecastAdapter = null;
	private MyDbHelper dbHelper;
	private SQLiteDatabase db;
	private Context context;
	
	private Place targetPlace = null;
	
	public void setCity(Place place){
		this.targetPlace = place;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.forecast_fragment);

		dbHelper = new MyDbHelper(context);
		db = dbHelper.getWritableDatabase();

		WeatherCondition.getForPlace(forecastList, targetPlace.getId(),db);
		
		db.close();

		forecastAdapter = new ForecastListAdapter(getActivity(), forecastList);
		setListAdapter(forecastAdapter);
	}
	
	private void setContentView(int fragmentLayout) {

	}

	public void onResume() {
		super.onResume();
		forecastAdapter.notifyDataSetChanged();
	}

}
