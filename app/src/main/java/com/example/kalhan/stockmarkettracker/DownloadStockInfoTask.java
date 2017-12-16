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
 * Created by Kalhan on 11/21/2017.
 */

public class DownloadStockInfoTask  extends AsyncTask<String, Integer, StockInformation> {
    private String serverUrl = "http://alphavantageapi-env.us-east-2.elasticbeanstalk.com/api/compactdata?stockName=";

    private WeakReference<StockDetailsActivity.StockInfoFragment> fragmentReference;

    public DownloadStockInfoTask(StockDetailsActivity.StockInfoFragment context)
    {
        fragmentReference = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        StockDetailsActivity.StockInfoFragment fragment = fragmentReference.get();
        if (fragment == null || fragment.getView()== null) return;
        fragment.preRequestExecute(fragment.getView());
    }

    // This is run in a background thread
    @Override
    protected StockInformation doInBackground(String... params) {
        // get the string from params, which is an array
        String stockSymbol = params[0];
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
                    Log.i("Volume background", String.valueOf(volume));
                    low = stock.getDouble("low");
                    timestamp = (String) key;
                    count++;
                }
                else if(count == 1){

                    change = lastPrice-stock.getDouble("close");
                    double tempChangePercentage = (change * 100.0)/stock.getDouble("close");
                    changePercentage = "("+df.format(tempChangePercentage)+"%)";
                    change = Math.round(change*100.0)/100.0;
                    stockInfo = new StockInformation(stockSymbol, String.valueOf(high)+" - "+String.valueOf(low), timestamp, changePercentage,lastPrice,volume,open,stock.getDouble("close"),change);
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
    protected void onPostExecute(StockInformation stockInfo) {
        super.onPostExecute(stockInfo);

        StockDetailsActivity.StockInfoFragment fragment = fragmentReference.get();
        if (fragment == null || fragment.getView() == null) return;
        fragment.postRequestExecute(fragment.getView(), stockInfo);
    }

}
