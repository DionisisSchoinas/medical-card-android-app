package com.unipi.p17134.medicalcard;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Singletons.Language;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseClass {
    private Spinner languageSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_activity));

        // Spinner
        languageSelect = findViewById(R.id.langauge_select_spinner);
        List<Language> languages = getLanguages();
        ArrayAdapter<Language> adapter = new ArrayAdapter<Language>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSelect.setAdapter(adapter);

        int index = findIndexOfLanguage(languages, MyPrefs.LocalePrefs.getLanguage(this));
        if (index != -1)
            languageSelect.setSelection(index);

        languageSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Language language = (Language) parent.getSelectedItem();
                changeLanguage(language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Gets called when a new language is selected
    private void changeLanguage(Language language) {
        if (!language.getLanguageLocale().equals(MyPrefs.LocalePrefs.getLanguage(this))) {
            // Save language change
            MyPrefs.LocalePrefs.setLanguage(getApplicationContext(), new Language(language.getLanguageLocale()));
            // Set locale
            MyPrefs.LocalePrefs.setLocale(this, currentLocale);
        }
    }

    // Gets the languages and their locales from the xml arrays and returns them as Language objects in a List
    private List<Language> getLanguages() {
        List<Language> languageList = new ArrayList<Language>();
        String[] langs = getResources().getStringArray(R.array.language_select_options);
        String[] langsLoc = getResources().getStringArray(R.array.language_locales_select_options);
        for (int i=0; i<langs.length; i++){
            languageList.add(new Language(langs[i], langsLoc[i]));
        }
        return languageList;
    }

    // Finds the index of the given languageLocale in a list of objects of type Language
    private int findIndexOfLanguage(List<Language> languages, Language languageLocale) {
        for (int i=0; i<languages.size(); i++){
            if (languages.get(i).getLanguageLocale().toLowerCase().equals(languageLocale.getLanguageLocale().toLowerCase()))
                return i;
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.no_settings, menu);
        return true;
    }
}