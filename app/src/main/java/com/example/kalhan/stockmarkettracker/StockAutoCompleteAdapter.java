package com.example.kalhan.stockmarkettracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kalhan on 11/17/2017.
 */

class StockAutoCompleteAdapter extends ArrayAdapter<StockSuggestions> implements Filterable {
    private ArrayList<StockSuggestions> mData;
    private String serverUrl = "http://alphavantageapi-env.us-east-2.elasticbeanstalk.com/api/stockautocomplete?stockName=";

    public StockAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mData = new ArrayList<StockSuggestions>();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public StockSuggestions getItem(int index) {
        return mData.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if(constraint != null && constraint.toString().length()>0) {
                    Log.i("input", constraint.toString());
                    // Request a string response from the provided URL.
                    HttpURLConnection conn = null;
                    InputStream input = null;
                    try{
                        URL url = new URL (serverUrl+constraint.toString());
                        conn = (HttpURLConnection) url.openConnection();
                        input = conn.getInputStream();
                        InputStreamReader reader = new InputStreamReader (input, "UTF-8");
                        BufferedReader buffer = new BufferedReader (reader, 8192);
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = buffer.readLine()) != null)
                        {
                            builder.append (line);
                        }
                        JSONArray responseArr = new JSONArray (builder.toString());
                        mData = new ArrayList<StockSuggestions>();
                        for(int itr=0; itr<responseArr.length() && itr<5; itr++){
                            JSONObject stockInfo = (JSONObject)responseArr.get(itr);
                            StockSuggestions stockSuggest = new StockSuggestions(stockInfo.getString("Symbol"),stockInfo.getString("Exchange"),stockInfo.getString("Name"));
                            mData.add(stockSuggest);
                        }

                        // Now assign the values and count to the FilterResults object
                        filterResults.values = mData;
                        filterResults.count = mData.size();
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }

            }
        };
        return myFilter;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dropdown_list, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.stock_symbol)).setText(getItem(position).getStockName());
        ((TextView) convertView.findViewById(R.id.company_name)).setText(getItem(position).getCompanyName() + " ( "+getItem(position).getStockExchange()+" )");
        return convertView;
    }
}
