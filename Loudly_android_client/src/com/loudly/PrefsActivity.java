package com.loudly;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.loudly.types.Preferences;

@SuppressWarnings("deprecation")
public class PrefsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

	Button b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Loudly);
		addPreferencesFromResource(R.xml.client_preferences);
		setContentView(R.layout.pref_layout);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		displaySetPreferences();

		b = (Button) findViewById(R.id.btnSavePreferences);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void displaySetPreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		for (String key : Preferences.preferenceKeys) {
			if (prefs.contains(key)) {
				updatePreference(key);
			}
		}
	}

	private void updatePreference(String key) {
		Preference p = findPreference(key);
		if (p instanceof ListPreference) {
			ListPreference listPref = (ListPreference) p;
			p.setSummary(listPref.getEntry());
		}
		if (p instanceof EditTextPreference) {
			EditTextPreference editTextPref = (EditTextPreference) p;
			p.setSummary(editTextPref.getText().toString());
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updatePreference(key);
	}

}
