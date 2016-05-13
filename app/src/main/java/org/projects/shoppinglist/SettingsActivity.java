package org.projects.shoppinglist;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        PreferenceManager prefMan = getPreferenceManager();
        prefMan.setSharedPreferencesName("settingPrefs");
        addPreferencesFromResource(R.xml.pref_general);
    }
}
