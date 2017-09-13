package pt.ipp.estgf.cmuweather;

import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pt.ipp.estgf.cmuweatherlib.MyDbHelper;
import pt.ipp.estgf.cmuweatherlib.WeatherCondition;

public class ForecastListAdapter extends ArrayAdapter<WeatherCondition> {

	private ArrayList<WeatherCondition> forecastList;
	private MyDbHelper dbHelper;
	private SQLiteDatabase db;
	private Context context;

	public ForecastListAdapter(Context context,
			ArrayList<WeatherCondition> forecastList) {
		super(context, R.layout.forecast_row, forecastList);
		this.setForecastList(forecastList);
		if(!forecastList.isEmpty()){
			forecastList.remove(0);
		}
		
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {

			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.forecast_row, null);
		}

		WeatherCondition targetWeatherCondition = forecastList.get(position);
		
		SharedPreferences mSettings = PreferenceManager
				.getDefaultSharedPreferences(context);

		dbHelper = new MyDbHelper(context);
		db = dbHelper.getWritableDatabase();

		// Views
		TextView viewDate = (TextView) v.findViewById(R.id.forecast_date);
		TextView viewPrevision = (TextView) v
				.findViewById(R.id.forecast_prevision);
		TextView viewMin = (TextView) v.findViewById(R.id.forecast_min);
		TextView viewMax = (TextView) v.findViewById(R.id.forecast_max);
		ImageView viewIcon = (ImageView) v
				.findViewById(R.id.forecast_weather_image);

		
		String tempType = mSettings.getString("listprefDegrees", "null");

		// Populate Views
		
		viewDate.setText("" + targetWeatherCondition.getObservationTime());
		viewPrevision.setText(targetWeatherCondition.getDescription());
		
		if(tempType.equals("C")){
			
			viewMin.setText("" + targetWeatherCondition.getMinTemperature() + "ºC");
			viewMax.setText("" + targetWeatherCondition.getMaxTemperature() + "ºC");
	
		}else{
			
			viewMin.setText("" + ((targetWeatherCondition.getMinTemperature()*1.8)+32) + "ºF");
			viewMax.setText("" + ((targetWeatherCondition.getMaxTemperature()*1.8)+32) + "ºF");
		}
		
		
		int iconID = context.getResources().getIdentifier(
				"s20x20" + targetWeatherCondition.getIcon(), "drawable",
				context.getPackageName());
		viewIcon.setImageDrawable(context.getResources().getDrawable(iconID));

		db.close();

		return v;
	}

	public void setForecastList(ArrayList<WeatherCondition> forecastList) {
		this.forecastList = forecastList;
	}

}
