package com.example.tempdeviceconnection.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tempdeviceconnection.R;

public class PairingConfirmationDialog extends Dialog {
    // private BluetoothDevice mDevice;
    private String mDevice;
    private int mKey;
    TextView description;
    Button pair;
    Button cancel;
    private Context mContext;

    public PairingConfirmationDialog(@NonNull Context context, String device, int key) {
        super(context);
        mContext = context;
        mDevice = device;
        mKey = key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_pairing_confirmation_popup);

        description = (TextView) findViewById(R.id.custom_pairing_confirm_text);
        pair = (Button) findViewById(R.id.custom_pair);
        cancel = (Button) findViewById(R.id.custom_cancel);
        description.setText(mContext.getText(R.string.pairing) + "\n" +
                mContext.getText(R.string.pairing_device) + " " +
                /* device.getName() */ "KIMD's iPhone" + "\n" +
                mContext.getText(R.string.pairing_key) + " " + mKey + "\n" +
                mContext.getText(R.string.pairing_request));

        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: BluetoothDevice.setPairingConfirmation(true)
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}

