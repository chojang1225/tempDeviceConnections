package com.example.tempdeviceconnection.common;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.example.tempdeviceconnection.R;
import com.example.tempdeviceconnection.dialog.AddNewDeviceDialog;

import org.w3c.dom.Text;

import java.util.Set;

public class DetailFrag extends PreferenceFragmentCompat {
    private static final String SETTING_EDITTEXT = "key_edit_text";

    public static final String ACTION_PAIRING_CANCEL = "android.bluetooth.device.action.PAIRING_CANCEL";
    public static final String ACTION_BOND_STATE_CHANGED = "android.bluetooth.device.action.BOND_STATE_CHANGED";

    private PairingReceiver pairingReceiver;

    static int _pairedNumber;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_detail);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pairingReceiver = new PairingReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(pairingReceiver, intentFilter);

        ButtonPreference buttonPreference = findPreference("add_new");

        Preference preference_no_device = findPreference("show_category");
        //Preference preference_device_paired = findPreference("device_paired");
        Preference preference_add_new_delete_devices = findPreference("addnew_delete");

        EditTextPreference vehicleName = getPreferenceManager().findPreference("key_edit_text");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        _pairedNumber = pairedDevices.size();
        Log.d("chojang", "size: " + _pairedNumber);

        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            preference_no_device.setVisible(false);
            //preference_device_paired.setVisible(true);
            preference_add_new_delete_devices.setVisible(true);


        } else {
            preference_no_device.setVisible(true);
            //preference_device_paired.setVisible(false);
            preference_add_new_delete_devices.setVisible(false);
        }

        PreferenceScreen preferenceScreen = this.getPreferenceScreen();
        //PreferenceCategory preferenceCategory = new PreferenceCategory(preferenceScreen.getContext());

        for(int i=0; i<1; i++) {
            Preference pref = preferenceScreen.findPreference("device_0"+i);
            if(i<_pairedNumber) {
                pref.setVisible(true);
                pref.setTitle(pairedDevices.toString());
            } else {
                pref.setVisible(false);
            }
        }

        vehicleName.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setSingleLine(true);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(26)});
                editText.setSelection(editText.length());
            }
        });

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        buttonPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d("chojang", "Add New 버튼 클릭!!");
                showAddNewDeviceDialog();
                return true;
            }
        });
    }

    /////////////////////////////////////////////
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void showAddNewDeviceDialog() {
        Intent intent = new Intent(getActivity(), AddNewDeviceDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    ////////////////////////////////////
    private class PairingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("chojang", "action ----------> " + action);
            Preference preference_no_device = findPreference("show_category");
            //Preference preference_device_paired = findPreference("device_paired");
            Preference preference_add_new_delete_devices = findPreference("addnew_delete");

            if (preference_no_device == null) {
                return;
            }

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)
                    && (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR) == BluetoothDevice.BOND_BONDED)) {
                Log.d("chojang", "paired successfully!!!");

                preference_no_device.setVisible(false);
                //preference_device_paired.setVisible(true);
                preference_add_new_delete_devices.setVisible(true);

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                String deviceName = device.getName();

                if (deviceName != null) {
                    Log.d("chojang", "deviceName=" + deviceName);

                    //PreferenceCategory preferenceCategory = findPreference("device_paired");

                    // TODO: 기기명 얻어와서 설정하기


                    }



            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)
                    && (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR) == BluetoothDevice.BOND_NONE)) {
                Log.d("chojang", "unpaired !!!");

                preference_no_device.setVisible(true);
                //preference_device_paired.setVisible(false);
                preference_add_new_delete_devices.setVisible(false);
            }
        }
    };
////////////////////////////////////


}