package com.example.tempdeviceconnection.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.mobis.btconnectionservice.*;
import com.example.tempdeviceconnection.R;


public class MainActivity extends AppCompatActivity implements OnListTiemSelectedListener {

    private IBluetoothConnection connectionStatus;

    public static boolean isAddNewClicked;
    private static final String SERVER_PACKAGE = "com.mobis.btconnectionservice";
    private static final String SERVER_ACTION = "com.mobis.action.btconnectionservice";

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Log.d("chojang", "onCreate");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // 기기가 블루투스를 지원하지 않는 경우
            Toast.makeText(this, "이 기기는 블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!hasBluetoothPermission()) {
            requestBluetoothPermission();
        }



        Intent intent = new Intent().setAction(SERVER_ACTION);
        intent.setPackage(SERVER_PACKAGE);
        boolean result = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.d("chojang", "result:   " + result);

    }

    private boolean hasBluetoothPermission() {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableBluetooth();
            } else {
                // Do nothing
            }
        }
    }


    private void enableBluetooth() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 2);
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //stopServiceBind();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }



    @Override
    public void onListItemSelected(int position) {
        DetailFrag df = (DetailFrag) getSupportFragmentManager().findFragmentById(R.id.detail);
    }

    IBluetoothConnection mService;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            connectionStatus = IBluetoothConnection.Stub.asInterface(iBinder);

            Log.d("chojang", "onServiceConnected!  ");
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("chojang", "onServiceDisconnected!  ");
            connectionStatus = null;
        }
    };



}


