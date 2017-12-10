package com.example.kalhan.stockmarkettracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kalhan.stockmarkettracker.Model.StockInformation;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Kalhan on 11/18/2017.
 */

public class FavoriteListAdaptor extends ArrayAdapter<StockInformation> {
    DecimalFormat df = new DecimalFormat("#.##");


    public FavoriteListAdaptor(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public FavoriteListAdaptor(Context context, int resource, ArrayList<StockInformation> items) {
        super(context, resource, items);
    }
/*    public void updateFavoriteList(ArrayList<StockInformation> updatedFavoriteList) {
        super.clear();
        super.addAll(updatedFavoriteList);
        super.notifyDataSetChanged();
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.favorite_row, null);
        }

        StockInformation item = getItem(position);

        if (item != null) {
            TextView stockLabel = (TextView) v.findViewById(R.id.stock_name);
            TextView changeLabel = (TextView) v.findViewById(R.id.stock_change);
            TextView changePercentLabel = (TextView) v.findViewById(R.id.stock_change_percentage);
            TextView volumeLabel = (TextView) v.findViewById(R.id.stock_volume);
            TextView companyLabel = (TextView) v.findViewById(R.id.company_name);
            df.setRoundingMode(RoundingMode.CEILING);
            stockLabel.setText(item.getStockName());
            changeLabel.setText(df.format(item.getLastPrice()));
            changePercentLabel.setText(df.format(item.getChange())+ " "+item.getChangePercentage());
            companyLabel.setText(item.getStockDisplayName());

            if(item.getChange() < 0){
                changePercentLabel.setBackgroundColor(getContext().getResources().getColor(R.color.favorite_red_stock));
            }
            else
            {
                changePercentLabel.setBackgroundColor(getContext().getResources().getColor(R.color.favorite_green_stock));
            }

            volumeLabel.setText(getContext().getResources().getString(R.string.favorite_table_volume_header) + " "+ df.format(item.getVolume()));
        }

        return v;
    }

}
