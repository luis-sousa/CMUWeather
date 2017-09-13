package pt.ipp.estgf.cmuweather;

import java.util.ArrayList;

import pt.ipp.estgf.cmuweatherlib.MyDbHelper;
import pt.ipp.estgf.cmuweatherlib.Place;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class PlaceWeatherFragment extends ListFragment {

	private ArrayList<Place> placesList = new ArrayList<Place>();
	private MyListAdapter mAdapter = null;
	private MyDbHelper dbHelper;
	private SQLiteDatabase db;
	private Context context;

	private PlaceListSelectedListener listener;

	public static interface PlaceListSelectedListener {
		public void onPlaceListSelected(Place place);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		try {
			listener = (PlaceListSelectedListener) getActivity();
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.places_fragment);

		dbHelper = new MyDbHelper(context);
		db = dbHelper.getWritableDatabase();

		Place.getAll(placesList, db);

		db.close();

		mAdapter = new MyListAdapter(getActivity(), placesList);
		setListAdapter(mAdapter);
	}

	private void setContentView(int fragmentLayout) {

	}

	public void onResume() {
		super.onResume();

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		if (listener != null) {
			listener.onPlaceListSelected(placesList.get(position));
		}

	}

}
