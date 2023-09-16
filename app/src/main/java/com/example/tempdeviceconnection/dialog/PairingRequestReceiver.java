package com.example.tempdeviceconnection.dialog;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class PairingRequestReceiver extends BroadcastReceiver {
    private final String TAG = PairingRequestReceiver.class.getName();
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
        Log.e(TAG, "[KIMD] onReceive: " + action);
        if (!action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
            return;
        }
        mPairingProcessHandler.sendMessage(mPairingProcessHandler.obtainMessage(
                MSG_PROCESS_PAIRING_REQUEST, intent));


    }

    private Intent getPairingDialogIntent(Context context, Intent intent, Class<?> cls) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT,
                BluetoothDevice.ERROR);
        Intent pairingIntent = new Intent();
        pairingIntent.setClass(context, cls);
        pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, type);
        if (type == BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION) {
            int pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
                    BluetoothDevice.ERROR);
            pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY, pairingKey);
        }
        pairingIntent.setAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        pairingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return pairingIntent;
    }

    private boolean shouldAceeptPairingImmediately() {
        // TODO: Check current condition of IVI
        return true;
    }

    private boolean shouldRejectPairingImmediately() {
        // TODO: Check current condition of IVI
        return false;
    }

    private boolean shouldWaitForDialogToShow() {
        // TODO: Check current condition of IVI
        return false;
    }

    private final int MSG_PROCESS_PAIRING_REQUEST = 1001;
    private final int MSG_REQUEST_PAIRING_FROM_PHONE_DIALOG = 1002;
    private final int MSG_DEVICE_AUTHENTICATION_DIALOG = 1003;
    private final int MSG_IMMEDIATE_PAIRING_ACCEPTANCE = 1004;
    private final int MSG_IMMEDIATE_PAIRING_REJECTION = 1005;

    private Handler mPairingProcessHandler = new Handler(Looper.getMainLooper()) {
        Intent pairingIntent;
        BluetoothDevice pairingDevice;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROCESS_PAIRING_REQUEST:
                    if (shouldAceeptPairingImmediately()) {
                        mPairingProcessHandler.sendMessage(mPairingProcessHandler.obtainMessage(
                                MSG_IMMEDIATE_PAIRING_ACCEPTANCE, msg.obj));
                    } else if (shouldRejectPairingImmediately()) {
                        mPairingProcessHandler.sendMessage(mPairingProcessHandler.obtainMessage(
                                MSG_IMMEDIATE_PAIRING_REJECTION, msg.obj));
                    }
                    // KIMD: Temporarily check dialog status, will check Discoverable status later.
                    else if (AddNewDeviceDialog.activity != null) {
                        mPairingProcessHandler.sendMessage(mPairingProcessHandler.obtainMessage(
                                MSG_DEVICE_AUTHENTICATION_DIALOG, msg.obj));
                    } else {
                        mPairingProcessHandler.sendMessage(mPairingProcessHandler.obtainMessage(
                                MSG_REQUEST_PAIRING_FROM_PHONE_DIALOG, msg.obj));
                    }
                    break;
                case MSG_REQUEST_PAIRING_FROM_PHONE_DIALOG:
                    removeMessages(MSG_REQUEST_PAIRING_FROM_PHONE_DIALOG);
                    if (shouldWaitForDialogToShow()) {
                        mPairingProcessHandler.sendMessageDelayed(mPairingProcessHandler
                                .obtainMessage(msg.what, msg.obj), 1000);
                    } else {
                        pairingIntent = getPairingDialogIntent(mContext, (Intent) msg.obj,
                                RequestPairingFromPhoneDialog.class);
                        mContext.startActivity(pairingIntent);
                    }
                    break;
                case MSG_DEVICE_AUTHENTICATION_DIALOG:
                    removeMessages(MSG_DEVICE_AUTHENTICATION_DIALOG);
                    pairingIntent = getPairingDialogIntent(mContext, (Intent) msg.obj,
                            DeviceAuthenticationDialog.class);
                    mContext.startActivity(pairingIntent);
                    break;
                case MSG_IMMEDIATE_PAIRING_ACCEPTANCE:
                    removeMessages(MSG_IMMEDIATE_PAIRING_ACCEPTANCE);
                    pairingIntent = (Intent) msg.obj;
                    pairingDevice = pairingIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // KIMD: TODO - Need BLUETOOTH_PRIVILEGED permission
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    pairingDevice.setPairingConfirmation(true);
                    // https://developer.android.com/reference/android/Manifest.permission#BLUETOOTH_PRIVILEGED
                    break;
                case MSG_IMMEDIATE_PAIRING_REJECTION:
                    removeMessages(MSG_IMMEDIATE_PAIRING_REJECTION);
                    pairingIntent = (Intent) msg.obj;
                    pairingDevice = pairingIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // KIMD: TODO - Need BLUETOOTH_PRIVILEGED permission
                    // pairingDevice.setPairingConfirmation(false);
                    // https://developer.android.com/reference/android/Manifest.permission#BLUETOOTH_PRIVILEGED
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}