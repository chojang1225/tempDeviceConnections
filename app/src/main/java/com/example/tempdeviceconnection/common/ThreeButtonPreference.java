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



public class ThreeButtonPreference extends Preference {

    private View.OnClickListener hfp_button;
    private View.OnClickListener a2dp_button;
    private View.OnClickListener pp_button;

    public void setOnClickListenerHfp(View.OnClickListener listener) {
        this.hfp_button = listener;
    }
    public void setOnClickListenerA2dp(View.OnClickListener listener) {
        this.a2dp_button = listener;
    }
    public void setOnClickListenerPp(View.OnClickListener listener) {
        this.pp_button = listener;
    }

    public ThreeButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ThreeButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ThreeButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeButtonPreference(@NonNull Context context) {
        super(context);
    }


    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        holder.itemView.setOnClickListener(null);
        holder.findViewById(R.id.hfp_00).setOnClickListener(hfp_button);
        holder.findViewById(R.id.a2dp_00).setOnClickListener(a2dp_button);
        holder.findViewById(R.id.pp_00).setOnClickListener(pp_button);
    }


}
