package com.example.tempdeviceconnection.common;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class BondStateReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
        Log.d("chojang", "action of BR:  " + action);

        if (!action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            return;
        }

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);

        switch (bondState) {
            case BluetoothDevice.BOND_BONDED:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d("chojang", "권한 없음!!!");
                    return;
                }
                    Log.d("chojang", "페어링 완료: " + device.getName());
                    break;
                case BluetoothDevice.BOND_BONDING:
                    Log.d("chojang", "페어링 중: " + device.getName());

                    break;
                case BluetoothDevice.BOND_NONE:
                    Log.d("chojang", "페어링 해제: " + device.getName());

                    break;
            }
        }
}