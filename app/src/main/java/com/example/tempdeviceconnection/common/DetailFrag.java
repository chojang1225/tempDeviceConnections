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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private int pairedNumber;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_detail);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pairingReceiver = new PairingReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(pairingReceiver, intentFilter);

        // No Devices Paired 화면의 2줄짜리 텍스트
        Preference preference_no_device = findPreference("show_category");

        // No Device 화면에서 Add New 버튼
        ButtonPreference buttonPreference_add_new_only = findPreference("add_new_only");

        // 기기리스트 화면에서 Add New + Delete Device(s) 버튼
        TwoButtonPreference preference_add_new_delete_devices = (TwoButtonPreference) findPreference("addnew_delete");

        // vehicle name 표시 및 저장
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
        pairedNumber = pairedDevices.size();
        Log.d("chojang", "size: " + pairedNumber);

        checkBondedDevice();

        if (pairedDevices != null && (pairedNumber > 0)) {
            preference_no_device.setVisible(false);
            preference_add_new_delete_devices.setVisible(true);


        } else {
            preference_no_device.setVisible(true);
            preference_add_new_delete_devices.setVisible(false);
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

        // No Device Paired 화면에서 Add New 버튼
        buttonPreference_add_new_only.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showAddNewDeviceDialog();
                return true;
            }
        });

        // 기기리스트 화면에서 Add New 버튼
        preference_add_new_delete_devices.setOnClickListenerAddNew(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewDeviceDialog();
            }
        });

        // 기기리스트 화면에서 Delete Device(s) 버튼
        preference_add_new_delete_devices.setOnClickListenerDeleteDevices(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "기기삭제 화면으로..", Toast.LENGTH_SHORT).show();
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


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.preference_device_list, container, false);
//
//        // 사용자 정의 화면에서 TextView에 값을 설정합니다.
//        TextView textView = rootView.findViewById(R.id.device_name_01);
//        String preferenceValue = "갤럭시S8"; // Preference에서 가져온 값 또는 기본값
//        textView.setText(preferenceValue);
//
//        return rootView;
//    }


    private void showAddNewDeviceDialog() {
        Intent intent = new Intent(getActivity(), AddNewDeviceDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private void checkBondedDevice() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
        pairedNumber = pairedDevices.size();

        for (int i = 0; i < 2; i++) {
            Preference pref = findPreference("device_0" + i);

            if (pref != null) {
                /////
//                TextView textView = getView().findViewById(R.id.device_name_01);
//                if(textView != null) {
//                    textView.setText("갤럭시S8");
//                }
                /////

                if (i < pairedNumber) {
                    pref.setVisible(true);
                } else {
                    pref.setVisible(false);
                }
            }
        }
    }

    ////////////////////////////////////
    private class PairingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("chojang", "action ----------> " + action);
            Preference preference_no_device = findPreference("show_category");
            TwoButtonPreference preference_add_new_delete_devices = findPreference("addnew_delete");

            if (preference_no_device == null) {
                return;
            }

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)
                    && (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR) == BluetoothDevice.BOND_BONDED)) {
                Log.d("chojang", "paired successfully!!!");

                preference_no_device.setVisible(false);
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

                checkBondedDevice();

                String deviceName = device.getName();

                if (deviceName != null) {
                    Log.d("chojang", "deviceName=" + deviceName);
                    // TODO: 기기명 얻어와서 설정하기


                }

            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)
                    && (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR) == BluetoothDevice.BOND_NONE)) {
                Log.d("chojang", "unpaired !!!");

                checkBondedDevice();

                if(pairedNumber<1) {
                    preference_no_device.setVisible(true);
                    preference_add_new_delete_devices.setVisible(false);
                }

            }
        }
    };
////////////////////////////////////

}