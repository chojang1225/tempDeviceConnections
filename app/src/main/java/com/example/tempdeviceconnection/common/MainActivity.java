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

    private static final int BLUETOOTH_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Log.d("chojang", "onCreate");


//        String[] permissions = {
//                android.Manifest.permission.BLUETOOTH,
//                android.Manifest.permission.BLUETOOTH_ADMIN,
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//        };
//
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, permissions, BLUETOOTH_PERMISSION_REQUEST);
//                return;
//            }
//        }

        Intent intent = new Intent().setAction(SERVER_ACTION);
        intent.setPackage(SERVER_PACKAGE);
        boolean result = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.d("chojang", "result:   " + result);

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == BLUETOOTH_PERMISSION_REQUEST) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                    // 권한이 거부되었을 때 사용자에게 안내 메시지를 표시
//                    Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
//                    finish(); // 앱 종료 또는 다른 조치를 취할 수 있음
//                    return;
//                }
//            }
//
//            // 권한이 모두 허용되었을 때 실행해야 하는 코드
//            // 예를 들어, Nearby devices와 관련된 기능을 실행할 수 있습니다.
//        }
//    }


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


