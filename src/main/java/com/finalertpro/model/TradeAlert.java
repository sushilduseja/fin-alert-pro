package com.finalertpro.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TradeAlert {
    private String id;
    private String symbol;
    private AlertType type;
    private String message;
    private double currentPrice;
    private double previousPrice;
    private long volume;
    private String currency;
    private String market;
    private String sector;
    private String recommendedAction;
    private int urgency;
    private String timestamp;

    public TradeAlert(String symbol, AlertType type, String message, double currentPrice, 
                      double previousPrice, long volume, String currency, String market, 
                      String sector, String recommendedAction, int urgency) {
        this.id = java.util.UUID.randomUUID().toString().substring(0, 8);
        this.symbol = symbol;
        this.type = type;
        this.message = message;
        this.currentPrice = currentPrice;
        this.previousPrice = previousPrice;
        this.volume = volume;
        this.currency = currency;
        this.market = market;
        this.sector = sector;
        this.recommendedAction = recommendedAction;
        this.urgency = urgency;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public AlertType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    public long getVolume() {
        return volume;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMarket() {
        return market;
    }

    public String getSector() {
        return sector;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public int getUrgency() {
        return urgency;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
