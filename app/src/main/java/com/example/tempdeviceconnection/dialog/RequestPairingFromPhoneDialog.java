package com.example.tempdeviceconnection.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.tempdeviceconnection.R;

public class RequestPairingFromPhoneDialog extends AppCompatActivity {
    private final String TAG = RequestPairingFromPhoneDialog.class.getName();
    public static final String ACTION_PAIRING_CANCEL = "android.bluetooth.device.action.PAIRING_CANCEL";

    private TextView mDescription;
    private Button mPair;
    private Button mCancel;

    private BluetoothDevice mDevice;
    private String mDeviceName;
    private int mKey;
    private boolean mReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_pairing_from_phone_dialog);
        mDescription = (TextView) findViewById(R.id.pairing_confirmation_text);
        mPair = (Button) findViewById(R.id.confirm_pair);
        mCancel = (Button) findViewById(R.id.confirm_cancel);
        mDevice = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (mDevice != null) {
            mDeviceName = mDevice.getName();
        } else {
            mDeviceName = "Phone Name"; // KIMD
        }
        mKey = getIntent().getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, -1);
        mDescription.setText(getResources().getString(R.string.pairing) + "\n" +
                getResources().getString(R.string.pairing_device) + " " + mDeviceName + "\n" +
                getResources().getString(R.string.pairing_key) + " " + mKey + "\n" +
                getResources().getString(R.string.pairing_request));

        Log.e("chojang", "[chojang] RequestPairingFromPhoneDialog");
        mPair.setOnClickListener(new View.OnClickListener() {
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
                mDevice.setPairingConfirmation(true);
                // https://developer.android.com/reference/android/Manifest.permission#BLUETOOTH_PRIVILEGED
                startActivity(getIntent().setClass(getBaseContext(), DeviceAuthenticationDialog.class));
                dismiss();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // KIMD: TODO - Need BLUETOOTH_PRIVILEGED permission
                // mDevice.setPairingConfirmation(false);
                // https://developer.android.com/reference/android/Manifest.permission#BLUETOOTH_PRIVILEGED
                dismiss();
            }
        });
        IntentFilter intentFilter = new IntentFilter(ACTION_PAIRING_CANCEL);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
        mReceiverRegistered = true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        Point point = new Point();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        params.gravity = Gravity.CENTER;
        params.width = (point.x / 2);
        params.height = (point.y * 37 / 100);
        getWindow().setAttributes(params);
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
}

