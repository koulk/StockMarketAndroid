package com.example.kalhan.stockmarkettracker.Model;

/**
 * Created by Kalhan on 12/10/2017.
 */

public class HistoricalStockData {
    public double getStockPrice() {
        return stockPrice;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public HistoricalStockData(double stockPrice, String timeStamp) {
        this.stockPrice = stockPrice;
        this.timeStamp = timeStamp;
    }

    private double stockPrice;
    private String timeStamp;
}
