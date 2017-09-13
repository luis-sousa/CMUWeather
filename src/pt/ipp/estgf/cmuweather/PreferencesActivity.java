package pt.ipp.estgf.cmuweather;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences_menu);
		
		ListPreference listPref = (ListPreference) findPreference("listpref");
		
		listPref.setEntries(R.array.listOptions);
		listPref.setEntryValues(R.array.listValues);

	}

}
