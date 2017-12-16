package com.example.kalhan.stockmarkettracker;

import android.os.AsyncTask;
import android.util.Log;

import com.example.kalhan.stockmarkettracker.Model.StockInformation;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;

/**
 * Created by Kalhan on 11/29/2017.
 */

public class FavoriteStockRefreshTask extends AsyncTask<Void, Void, StockInformation> {
    private String serverUrl = "http://alphavantageapi-env.us-east-2.elasticbeanstalk.com/api/compactdata?stockName=";
    private WeakReference<StockSearchActivity> activityReference;
    private String stockSymbol, stockDisplayName;

    public FavoriteStockRefreshTask(StockSearchActivity context, String stockSymbol, String stockDisplayName)
    {
        activityReference = new WeakReference<>(context);
        this.stockSymbol = stockSymbol;
        this.stockDisplayName = stockDisplayName;
    }

    // This is run in a background thread
    @Override
    protected StockInformation doInBackground(Void... params) {
        Log.i("Background", stockSymbol);
        StockInformation stockInfo = null;
        HttpURLConnection conn = null;
        double open =0,high=0,volume=0,change=0,lastPrice=0.00,low=0;
        String timestamp=null, changePercentage=null;
        InputStream input = null;
        try{
            URL url = new URL (serverUrl+stockSymbol);
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
            JSONObject jsonResponse = new JSONObject (builder.toString());
            jsonResponse =  jsonResponse.getJSONObject("prices");
            Log.i("stockData", builder.toString());
            Iterator keys = jsonResponse.keys();
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            int count = 0;
            while (keys.hasNext()) {
                Object key = keys.next();
                JSONObject stock = jsonResponse.getJSONObject((String) key);
                if(count == 0){
                    open = stock.getDouble("open");
                    lastPrice = stock.getDouble("close");
                    high = stock.getDouble("high");
                    volume = stock.getDouble("volume");
                    low = stock.getDouble("low");
                    timestamp = (String) key;
                    count++;
                }
                else if(count == 1){

                    change = lastPrice-stock.getDouble("close");
                    double tempChangePercentage = (change * 100.0)/stock.getDouble("close");
                    changePercentage = "("+df.format(tempChangePercentage)+"%)";
                    change = Math.round(change*100.0)/100.0;
                    stockInfo = new StockInformation(stockSymbol, stockDisplayName, String.valueOf(high)+" - "+String.valueOf(low), timestamp, changePercentage,lastPrice,volume,open,stock.getDouble("close"),change);
                    break;
                }
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return stockInfo;
    }

    @Override
    protected void onPostExecute(StockInformation result) {
        StockSearchActivity activity = activityReference.get();
        if (activity == null) return;
        synchronized (activity.favoriteList){
            StockSearchActivity.requestPendingCount--;

            // Null check is for failed request.
            for(int itr=0; itr< activity.favoriteList.size() && result!=null; itr++){
                if(activity.favoriteList.get(itr).getStockName().equals(stockSymbol)){
                    activity.favoriteList.set(itr, result);
                    activity.sortFavoriteList(activity.sortCategoryIndex, activity.sortTypeIndex);
                    activity.favoriteListAdaptor.notifyDataSetChanged();
                    activity.updateFavorites();
                    break;
                }
            }
            // All requests fulfilled
            if(StockSearchActivity.requestPendingCount == 0){
                activity.refreshButton.clearAnimation();
                activity.refreshButton.setClickable(true);
            }
        }

    }

}