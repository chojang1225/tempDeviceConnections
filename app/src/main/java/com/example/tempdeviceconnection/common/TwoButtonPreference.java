package com.example.tempdeviceconnection.common;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.example.tempdeviceconnection.R;



public class TwoButtonPreference extends Preference {

    private View.OnClickListener add_new;
    private View.OnClickListener delete_devices;


    public void setOnClickListenerAddNew(View.OnClickListener listener) {
        this.add_new = listener;
    }

    public void setOnClickListenerDeleteDevices(View.OnClickListener listener) {
        this.delete_devices = listener;
    }

    public TwoButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TwoButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TwoButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoButtonPreference(@NonNull Context context) {
        super(context);
    }


    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        holder.itemView.setOnClickListener(null);
        holder.findViewById(R.id.add_new_button).setOnClickListener(add_new);
        holder.findViewById(R.id.delete_devices_button).setOnClickListener(delete_devices);
    }


}
