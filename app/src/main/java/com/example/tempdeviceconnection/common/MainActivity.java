package com.example.tempdeviceconnection.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.example.tempdeviceconnection.R;

public class MainActivity extends AppCompatActivity implements OnListTiemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

    }


//    public static class SettingsFragment extends PreferenceFragmentCompat {
//        @Override
//        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//            setPreferencesFromResource(R.xml.preference_detail, rootKey);
//
//            EditTextPreference editTextPreference = findPreference("edit_text_preference");
//            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
//                    editor.putString("edit_text_preference", (String) newValue);
//                    editor.apply();
//                    return true;
//                }
//            });
//        }
//    }


    @Override
    public void onListItemSelected(int position) {
        DetailFrag df = (DetailFrag) getSupportFragmentManager().findFragmentById(R.id.detail);
    }


}


