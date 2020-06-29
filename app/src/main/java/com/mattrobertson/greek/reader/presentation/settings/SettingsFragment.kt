package com.mattrobertson.greek.reader.presentation.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mattrobertson.greek.reader.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}