package com.example.tempdeviceconnection.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceManager;

import com.example.tempdeviceconnection.R;
import com.example.tempdeviceconnection.common.DetailFrag;

public class AddNewDeviceDialog extends AppCompatActivity {
    private final String TAG = RequestPairingFromPhoneDialog.class.getName();
    public static final String ACTION_PAIRING_CANCEL = "android.bluetooth.device.action.PAIRING_CANCEL";

    public static AppCompatActivity activity; // KIMD: This should be protected later.
    private TextView mDescription;
    private Button mCancel;
    private String mDeviceName;
    protected BluetoothAdapter mBluetoothAdapter;
    private AlwaysDiscoverable mAlwaysDiscoverable;

    private BluetoothDevice mDevice;
    private boolean mReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device_dialog);


//        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        int scanMode = BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
//        mBluetoothAdapter.startDiscovery();

        IntentFilter intentFilter = new IntentFilter(ACTION_PAIRING_CANCEL);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);

        mDevice = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (mDevice != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mDeviceName = mDevice.getName();
        } else {
            mDeviceName = "Phone Name"; // KIMD
        }


        // to obtain vehicle name
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String editTextValue = sharedPreferences.getString("key_edit_text", "");

        mDescription = (TextView) findViewById(R.id.add_new_device_text);
        mCancel = (Button) findViewById(R.id.add_new_device_cancel);

        mDescription.setText(getResources().getString(R.string.pairing) + "\n" +
                getResources().getString(R.string.vehicle_name) + editTextValue + "\n" +
                getResources().getString(R.string.add_new_request));
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        activity = AddNewDeviceDialog.this;

        mBluetoothAdapter = getSystemService(BluetoothManager.class).getAdapter();
        mAlwaysDiscoverable = new AlwaysDiscoverable(this, mBluetoothAdapter);
        mAlwaysDiscoverable.start();
        mReceiverRegistered = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlwaysDiscoverable.stop();
        activity = null;

        if (mReceiverRegistered) {
            mReceiverRegistered = false;
            unregisterReceiver(mReceiver);
        }

    }

    private void dismiss() {
        if (!isFinishing()) {
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        }
    }


    private static final class AlwaysDiscoverable extends BroadcastReceiver {

        private final Context mContext;
        private final BluetoothAdapter mAdapter;
        private final IntentFilter mIntentFilter = new IntentFilter(
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        private boolean mStarted;

        AlwaysDiscoverable(Context context, BluetoothAdapter adapter) {
            mContext = context;
            mAdapter = adapter;
        }

        void start() {
            if (mStarted) {
                return;
            }
            mContext.registerReceiver(this, mIntentFilter);
            mStarted = true;
            setDiscoverable();
        }

        void stop() {
            if (!mStarted) {
                return;
            }
            mContext.unregisterReceiver(this);
            mStarted = false;
            // KIMD: Temporarily commented out since we don't have permission right now
            // mAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE);
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            setDiscoverable();
        }

        private void setDiscoverable() {

            BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d("chojang", "no permission !!");
                return;
            }
            //mBluetoothAdapter.startDiscovery();
            BluetoothManager mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mAdapter = bluetoothManager.getAdapter();

            if (mAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                mContext.startActivity(discoverableIntent);
             }
        }

    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("chojang", "action:  " + action);
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);

                Log.d("chojang", "bondState:  " + bondState);
                if (bondState == BluetoothDevice.BOND_BONDED ||
                        bondState == BluetoothDevice.BOND_NONE) {
                    dismiss();
                }
            } else if (ACTION_PAIRING_CANCEL.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null || device == mDevice) {
                    dismiss();
                }
            }
        }
    };
}