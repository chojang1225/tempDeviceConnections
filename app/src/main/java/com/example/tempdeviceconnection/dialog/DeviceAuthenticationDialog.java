package com.example.tempdeviceconnection.dialog;

import static com.example.tempdeviceconnection.dialog.AddNewDeviceDialog.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tempdeviceconnection.R;

public class DeviceAuthenticationDialog extends AppCompatActivity {
    private final String TAG = DeviceAuthenticationDialog.class.getName();
    public static final String ACTION_PAIRING_CANCEL = "android.bluetooth.device.action.PAIRING_CANCEL";
    private TextView mDescription;
    private Button mCancel;

    public static AppCompatActivity DeviceAuthenticationDialog_activity;
    private BluetoothDevice mDevice;
    private String mDeviceName;
    private int mKey;
    private boolean mReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_authentication_dialog);

        mDescription = (TextView) findViewById(R.id.device_authentication_text);
        mCancel = (Button) findViewById(R.id.device_authentication_cancel);
        DeviceAuthenticationDialog_activity = DeviceAuthenticationDialog.this;
        DeviceAuthenticationDialog_activity.setTitle(R.string.add_new_device);
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
        mKey = getIntent().getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, -1);
        mDescription.setText(getResources().getString(R.string.pairing) + "\n" +
                getResources().getString(R.string.pairing_device) + " " + mDeviceName + "\n" +
                getResources().getString(R.string.pairing_key) + " " + mKey + "\n" +
                getResources().getString(R.string.pairing_request));

        mDevice.setPairingConfirmation(true);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // KIMD: TODO - Need BLUETOOTH_PRIVILEGED permission
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mDevice.setPairingConfirmation(false);
                // https://developer.android.com/reference/android/Manifest.permission#BLUETOOTH_PRIVILEGED
                dismiss();
            }
        });

        IntentFilter intentFilter = new IntentFilter(ACTION_PAIRING_CANCEL);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
        mReceiverRegistered = true;

        dismissAddNewDeviceDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverRegistered) {
            mReceiverRegistered = false;
            unregisterReceiver(mReceiver);
        }
    }

    private void dismiss() {
        if (!isFinishing()) {
            finish();
        }
    }

    private void dismissAddNewDeviceDialog() {
        // Finish AddNewDeviceDialog if it exists.
        AddNewDeviceDialog dialog = (AddNewDeviceDialog) activity;
        if (dialog != null) {
            dialog.finish();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);
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

    public class BluetoothHeadsetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);

                if (state == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("chojang", "HFP connected!!!");
                } else if (state == BluetoothProfile.STATE_DISCONNECTED) {

                }
            }
        }
    };
}