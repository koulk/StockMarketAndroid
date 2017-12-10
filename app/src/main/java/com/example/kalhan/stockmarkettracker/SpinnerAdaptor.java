package com.example.kalhan.stockmarkettracker;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kalhan.stockmarkettracker.Model.SpinnerItem;

import java.util.ArrayList;

/**
 * Created by Kalhan on 11/22/2017.
 */

public class SpinnerAdaptor extends ArrayAdapter<SpinnerItem> {
    LayoutInflater flater;

    public SpinnerAdaptor(Activity context, int textViewResourceId, ArrayList<SpinnerItem> items) {
        super(context, textViewResourceId, items);
        flater = context.getLayoutInflater();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = flater.inflate(R.layout.spinner_dropdown,parent, false);
        }
        SpinnerItem rowItem = getItem(position);
        TextView txtTitle = convertView.findViewById(R.id.sort_option);
        txtTitle.setText(rowItem.getType());
        if(!rowItem.getEnabled()){
            txtTitle.setTextColor(Color.parseColor("#808080"));
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        if (!super.getItem(position).getEnabled()){
            return false;
        }
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = flater.inflate(R.layout.spinner_dropdown,parent, false);
        }
        SpinnerItem rowItem = getItem(position);
        TextView txtTitle = convertView.findViewById(R.id.sort_option);
        txtTitle.setText(rowItem.getType());

        return convertView;
    }


}
