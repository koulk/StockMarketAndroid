package com.example.kalhan.stockmarkettracker.Model;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Kalhan on 11/21/2017.
 */

public class StockInformation {

    public StockInformation() {
        this.stockName = null;
        this.rangeValue = null;
        this.timestamp = null;
        this.changePercentage = null;
        this.lastPrice = 0.0;
        this.volume = 0.0;
        this.openPrice = 0.0;
        this.closePrice = 0.0;
        this.change = 0.0;
    }
    public StockInformation(String stockName, String rangeValue, String timestamp, String changePercentage, double lastPrice, double volume, double openPrice, double closePrice, double change) {
        this.stockName = stockName;
        this.rangeValue = rangeValue;
        this.timestamp = timestamp;
        this.changePercentage = changePercentage;
        this.lastPrice = lastPrice;
        this.volume = volume;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.change = change;
    }
    public StockInformation(String stockName, String stockDisplayName, String rangeValue, String timestamp, String changePercentage, double lastPrice, double volume, double openPrice, double closePrice, double change) {
        this.stockName = stockName;
        this.stockDisplayName = stockDisplayName;
        this.rangeValue = rangeValue;
        this.timestamp = timestamp;
        this.changePercentage = changePercentage;
        this.lastPrice = lastPrice;
        this.volume = volume;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.change = change;
    }
    public String getStockName() {
        return stockName;
    }

    public String getRangeValue() {
        return rangeValue;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public double getVolume() {
        return volume;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getChangePercentage() {
        return changePercentage;
    }

    public String getStockDisplayName(){
        return stockDisplayName;
    }

    public double getChange() {
        return change;
    }

    public void setChangePercentage(String changePercentage) {
        this.changePercentage = changePercentage;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public void setStockDisplayName(String stockDisplayName){
        this.stockDisplayName = stockDisplayName;
    }

    public static Comparator<StockInformation> COMPARE_BY_LASTPRICE_ASCENDING = new Comparator<StockInformation>() {
        public int compare(StockInformation one, StockInformation other) {
           if (one.getLastPrice() < other.getLastPrice()){
               return -1;
           }
           else{
               return 1;
           }
        }
    };

    public static Comparator<StockInformation> COMPARE_BY_LASTPRICE_DESCENDING = new Comparator<StockInformation>() {
        public int compare(StockInformation one, StockInformation other) {
            if (one.getLastPrice() < other.getLastPrice()){
                return 1;
            }
            else{
                return -1;
            }
        }
    };

    public static Comparator<StockInformation> COMPARE_BY_STOCKNAME_ASCENDING = new Comparator<StockInformation>() {
        public int compare(StockInformation one, StockInformation other) {
           return one.getStockName().compareTo(other.getStockName());
        }
    };

    public static Comparator<StockInformation> COMPARE_BY_STOCKNAME_DESCENDING = new Comparator<StockInformation>() {
        public int compare(StockInformation one, StockInformation other) {
            return -1 * (one.getStockName().compareTo(other.getStockName()));
        }
    };


    public static Comparator<StockInformation> COMPARE_BY_CHANGE_ASCENDING = new Comparator<StockInformation>() {
        public int compare(StockInformation one, StockInformation other) {
            if (one.getChange() < other.getChange()){
                return -1;
            }
            else{
                return 1;
            }
        }
    };

    public static Comparator<StockInformation> COMPARE_BY_CHANGE_DESCENDING = new Comparator<StockInformation>() {
        public int compare(StockInformation one, StockInformation other) {
            if (one.getChange() < other.getChange()){
                return 1;
            }
            else{
                return -1;
            }
        }
    };


    private String timestamp;
    private String changePercentage;
    private String stockName;
    private String rangeValue;
    private String stockDisplayName;
    private double lastPrice;
    private double volume;
    private double openPrice;
    private double closePrice;
    private double change;
}
