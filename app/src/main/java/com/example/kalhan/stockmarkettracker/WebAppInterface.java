package com.example.kalhan.stockmarkettracker;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by Kalhan on 11/19/2017.
 */

public class WebAppInterface {
    Context mContext;
    String stockSymbol, indicator, exportURL = null;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c, String symbol) {
        mContext = c;
        stockSymbol = symbol;
    }

    WebAppInterface(Context c, String symbol, String category) {
        mContext = c;
        stockSymbol = symbol;
        indicator = category;
    }

    public String getExportURL(){
        return exportURL;
    }

    @JavascriptInterface   // must be added for API 17 or higher
    public void setExportURL(String url) {
        exportURL = url;
    }

    @JavascriptInterface   // must be added for API 17 or higher
    public String getIndicatorCategory() {
        return indicator;
    }

    @JavascriptInterface   // must be added for API 17 or higher
    public void showErrorToast() {
        Toast.makeText(mContext, "Unable to fetch data", Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface   // must be added for API 17 or higher
    public String getStockName() {
        return stockSymbol;
    }
}