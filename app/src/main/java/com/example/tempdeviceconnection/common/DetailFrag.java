package com.example.tempdeviceconnection.common;

//import static com.example.tempdeviceconnection.common.MainActivity.isAddNewClicked;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tempdeviceconnection.dialog.RequestPairingFromPhoneDialog;
import com.mobis.btconnectionservice.*;
import com.example.tempdeviceconnection.R;
import com.example.tempdeviceconnection.dialog.AddNewDeviceDialog;
import com.mobis.btconnectionservice.IBluetoothConnectionAppCmd;
import com.mobis.btconnectionservice.IBluetoothConnectionServiceCmd;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class DetailFrag extends PreferenceFragmentCompat {
    private static final String SETTING_EDITTEXT = "key_edit_text";

    public static final String ACTION_PAIRING_CANCEL = "android.bluetooth.device.action.PAIRING_CANCEL";
    public static final String ACTION_BOND_STATE_CHANGED = "android.bluetooth.device.action.BOND_STATE_CHANGED";

    private PairingReceiver pairingReceiver;

    private int pairedNumber;

    private IBluetoothConnectionServiceCmd connectionStatus;

    public static boolean isAddNewClicked;
    private static final String SERVER_PACKAGE = "com.mobis.btconnectionservice";
    private static final String SERVER_ACTION = "com.mobis.action.btconnectionservice";

    private boolean isBound = false;

    private static final int btn_cancel = 0;
    private static final int btn_addnew = 1;
    private static final int btn_hfp = 2;
    private static final int btn_a2dp = 3;
    private static final int btn_pp = 4;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("chojang", "onServiceConnected on fragment!  ");
            connectionStatus = IBluetoothConnectionServiceCmd.Stub.asInterface(iBinder);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("chojang", "onServiceDisconnected on fragment!  ");
            connectionStatus = null;
            isBound = false;
        }
    };



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_detail);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pairingReceiver = new PairingReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(pairingReceiver, intentFilter);

        if(!isBound) {
            Intent intent = new Intent().setAction(SERVER_ACTION);
            intent.setPackage(SERVER_PACKAGE);
            boolean result = getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            Log.d("chojang", "result:   " + result);
        }
        // No Devices Paired 화면의 2줄짜리 텍스트
        Preference preference_no_device = findPreference("show_category");

        // No Device 화면에서 Add New 버튼
        ButtonPreference buttonPreference_add_new_only = findPreference("add_new_only");

        // 기기리스트 화면에서 Add New / Delete Device(s) 버튼
        TwoButtonPreference preference_add_new_delete_devices = (TwoButtonPreference) findPreference("addnew_delete");

        // 기기리스트 화면에서 HFP / A2DP / PP 버튼
        ThreeButtonPreference preference_device_list = (ThreeButtonPreference) findPreference("device_00");

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
            for (int i = 0; i < 6; i++) {
                Preference pref = findPreference("device_0" + i);
                if (pref != null) {
                    pref.setVisible(false);
                }
            }
            preference_add_new_delete_devices.setVisible(false);
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
                try {
                    connectionStatus.setDiscoverableMode(1);
                    Log.d("chojang", "onAddNewClicked !!!");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
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

        preference_device_list.setOnClickListenerHfp(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "HFP", Toast.LENGTH_SHORT).show();
            }
        });

        preference_device_list.setOnClickListenerA2dp(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "A2DP", Toast.LENGTH_SHORT).show();
            }
        });

        preference_device_list.setOnClickListenerPp(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "PP", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBound) {
            getActivity().unbindService(serviceConnection);
        }
    }

    IBluetoothConnectionServiceCmd mCallback = new IBluetoothConnectionServiceCmd.Stub() {
        @Override
        public void setDiscoverableMode(int btn) {}

        @Override
        public void registerCallback(IBluetoothConnectionAppCmd cb) {}

        @Override
        public void unregisterCallback(IBluetoothConnectionAppCmd cb) {}

    };


    private void showAddNewDeviceDialog() {
        Intent intent = new Intent(getActivity(), AddNewDeviceDialog.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //this.startActivity(intent);
        launcher.launch(intent);
    }

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    try {
                        connectionStatus.setDiscoverableMode(0);
                        Log.d("chojang", "popup closed by cancel !!");
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                } else if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("chojang", "popup closed successfully !!");
                }
            }
    );




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

        for (int i = 0; i < 6; i++) {
            Preference pref = findPreference("device_0" + i);

            if (pref != null) {
                if (i < pairedNumber) {
                    ThreeButtonPreference preference_device_list = (ThreeButtonPreference) findPreference("device_0" + i);

                    BluetoothDevice device = (BluetoothDevice) pairedDevices.toArray()[i];
                    String currentDeviceName = device.getName();

                    preference_device_list.setTitle(Integer.toString(i+1));
                    preference_device_list.setSummary(currentDeviceName);

                    pref.setVisible(true);
                } else {
                    pref.setVisible(false);
                }
            }
        }

    }


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

//            if (ACTION_PAIRING_REQUEST.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Intent i = new Intent(getActivity(), RequestPairingFromPhoneDialog.class);
//                launcher.launch(i);
//            }


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

                if(pairedNumber < 1) {
                    preference_no_device.setVisible(true);
                    preference_add_new_delete_devices.setVisible(false);
                }

            }
        }
    };

}