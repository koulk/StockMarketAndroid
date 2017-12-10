package com.example.kalhan.stockmarkettracker;

public class StockSuggestions {
    public String getStockName() {
        return stockName;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public String getCompanyName() {
        return companyName;
    }

    public StockSuggestions(String stockName, String stockExchange, String companyName) {
        this.stockName = stockName;
        this.stockExchange = stockExchange;
        this.companyName = companyName;
    }

    private String stockName;
    private String stockExchange;
    private String companyName;
}
