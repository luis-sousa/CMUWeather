package pt.ipp.estgf.cmuweather;

import java.util.ArrayList;

import pt.ipp.estgf.cmuweatherlib.MyDbHelper;
import pt.ipp.estgf.cmuweatherlib.Place;
import pt.ipp.estgf.cmuweatherlib.WeatherCondition;
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

public class MyListAdapter extends ArrayAdapter<Place> {

	private ArrayList<Place> placesList;
	private MyDbHelper dbHelper;
	private SQLiteDatabase db;
	private Context context;

	public MyListAdapter(Context context, ArrayList<Place> placesList) {
		super(context, R.layout.place_row, placesList);
		this.setPlacesList(placesList);
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {

			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.place_row, null);
		}

		Place targetPlace = placesList.get(position);
		SharedPreferences mSettings = PreferenceManager
				.getDefaultSharedPreferences(context);

		dbHelper = new MyDbHelper(context);
		db = dbHelper.getWritableDatabase();
		
		

		WeatherCondition wc = WeatherCondition.getCurrentForPlace(
				targetPlace.getId(), db);

		// Views
		TextView viewPlaceName = (TextView) v.findViewById(R.id.row_city_name);
		TextView viewCurrentTemp = (TextView) v.findViewById(R.id.row_current_temp);
		TextView viewCondition = (TextView) v.findViewById(R.id.row_condition);
		ImageView viewIcon = (ImageView) v.findViewById(R.id.row_weather_image);
		
		
		String tempType = mSettings.getString("listprefDegrees", "null");

		// Populate Views
		viewPlaceName.setText(targetPlace.getName());
		if(wc != null){
			if(tempType.equals("C")){
				viewCurrentTemp.setText(""+wc.getCurrentTemperature()+"ºC");
			}else{
				viewCurrentTemp.setText("" + ((wc.getCurrentTemperature()*1.8)+32)
						+ "ºF");
			}
			
			viewCondition.setText(wc.getDescription());
			int iconID = context.getResources().getIdentifier("s20x20"+wc.getIcon(), "drawable", context.getPackageName());
			viewIcon.setImageDrawable(context.getResources().getDrawable(iconID));
		}

		db.close();

		return v;
	}

	public ArrayList<Place> getPlacesList() {
		return placesList;
	}

	public void setPlacesList(ArrayList<Place> placesList) {
		this.placesList = placesList;
	}

}
