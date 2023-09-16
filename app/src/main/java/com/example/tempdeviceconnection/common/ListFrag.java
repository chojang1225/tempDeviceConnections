package com.example.tempdeviceconnection.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.ListFragment;

public class ListFrag extends ListFragment {
    OnListTiemSelectedListener listener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            listener = (OnListTiemSelectedListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + "must implement OnListItemSelectedListener interface");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                new String[]{"Device Connections"}));
    }

    @Override
    public void onListItemClick(ListView l, View v,int position, long id) {
        listener.onListItemSelected(position);
    }
}
