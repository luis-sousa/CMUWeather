package pt.ipp.estgf.cmuweather;

import java.util.ArrayList;

import pt.ipp.estgf.cmuweatherlib.MyDbHelper;
import pt.ipp.estgf.cmuweatherlib.Place;
import pt.ipp.estgf.cmuweatherlib.WWOSaxHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadInfoTask extends AsyncTask<Void, Void, Void> {

	private Context context;
	private MyDbHelper dbHelper;
	private SQLiteDatabase db;


	public DownloadInfoTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		//NOTHING TO DO
	}

	@Override
	protected Void doInBackground(Void... values) {

		dbHelper = new MyDbHelper(context);
		db = dbHelper.getWritableDatabase();

		WWOSaxHandler ww = new WWOSaxHandler(db);

		ArrayList<Place> places = new ArrayList<Place>();
		Place.getAll(places, db);

		for (Place place : places) {
			ww.updateWeatherConditions(place);
		}
		
		db.close();
		
		return null;
	}

	@Override
	protected void onPostExecute(Void unused) {
		
		Intent mIntent = new Intent();
		mIntent.setAction("pt.ipp.estgf.cmuweather.WEATHER_CONDITION_UPDATED");		
		context.sendBroadcast(mIntent);	
		
	}

}
