package com.finalertpro.service;

import com.finalertpro.model.AlertType;
import com.finalertpro.model.AssetData;
import com.finalertpro.model.TradeAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;

@Service
public class AlertSimulatorService {

    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();
    
    // Asset data
    private final Map<String, AssetData> assetDatabase = new ConcurrentHashMap<>();
    
    // Financial instruments with realistic data
    private static final String[][] ASSETS = {
        // Symbol, Initial Price, Currency, Market, Sector
        {"JPM", "145.23", "USD", "NYSE", "Banking"},
        {"GS", "387.56", "USD", "NYSE", "Investment Banking"},
        {"MS", "93.78", "USD", "NYSE", "Financial Services"},
        {"BAC", "37.92", "USD", "NYSE", "Banking"},
        {"C", "58.43", "USD", "NYSE", "Banking"},
        {"WFC", "54.76", "USD", "NYSE", "Banking"},
        {"BCS", "8.95", "USD", "NYSE", "Banking"},
        {"CS", "7.34", "USD", "NYSE", "Investment Banking"},
        {"DB", "13.56", "USD", "NYSE", "Banking"},
        {"UBS", "24.78", "USD", "NYSE", "Wealth Management"},
        {"HSBC", "39.45", "USD", "NYSE", "Banking"},
        {"BLK", "864.23", "USD", "NYSE", "Asset Management"},
        {"AXP", "187.34", "USD", "NYSE", "Financial Services"}
    };
    
    private static final String[] ACTIONS = {
        "BUY", "SELL", "HOLD", "STRONG BUY", "STRONG SELL", "WATCH"
    };

    @Autowired
    public AlertSimulatorService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        initializeAssetData();
    }
    
    private void initializeAssetData() {
        for (String[] asset : ASSETS) {
            AssetData data = new AssetData();
            data.setSymbol(asset[0]);
            data.setCurrentPrice(Double.parseDouble(asset[1]));
            data.setPreviousPrice(data.getCurrentPrice());
            data.setCurrency(asset[2]);
            data.setMarket(asset[3]);
            data.setSector(asset[4]);
            assetDatabase.put(data.getSymbol(), data);
        }
    }

    @Scheduled(fixedRate = 2000) // Generate alerts more frequently
    public void generateTradeAlert() {
        System.out.println("Generating trade alert: " + new Date());
        
        // Select a random asset
        String[] assetKeys = assetDatabase.keySet().toArray(new String[0]);
        String symbol = assetKeys[random.nextInt(assetKeys.length)];
        AssetData asset = assetDatabase.get(symbol);

        // Simulate price movement
        simulatePriceMovement(asset);
        
        // Determine alert type based on price and volume changes
        AlertType type = determineAlertType(asset);
        
        // Generate context-appropriate message
        String message = generateMessage(asset, type);
        
        // Recommended action based on alert type and price movement
        String action = determineAction(asset, type);
        
        // Generate volume
        long volume = 10000 + random.nextInt(990000);
        
        // Urgency level from 1-5
        int urgency = 1 + random.nextInt(5);
        
        TradeAlert alert = new TradeAlert(
            asset.getSymbol(),
            type,
            message,
            asset.getCurrentPrice(),
            asset.getPreviousPrice(),
            volume,
            asset.getCurrency(),
            asset.getMarket(),
            asset.getSector(),
            action,
            urgency
        );
        
        messagingTemplate.convertAndSend("/topic/alerts", alert);
    }
    
    private void simulatePriceMovement(AssetData asset) {
        // Save previous price
        asset.setPreviousPrice(asset.getCurrentPrice());
        
        // Calculate price movement - more volatile
        double movementPercent = (random.nextDouble() * 2 - 1) * 2; // -2% to +2%
        double newPrice = asset.getCurrentPrice() * (1 + (movementPercent / 100));
        
        // Add occasional significant movements
        if (random.nextInt(10) == 0) { // 10% chance
            double spike = (random.nextDouble() * 10 - 5); // -5% to +5%
            newPrice = newPrice * (1 + (spike / 100));
        }
        
        // Round to 2 decimal places
        newPrice = Math.round(newPrice * 100) / 100.0;
        asset.setCurrentPrice(newPrice);
    }
    
    private AlertType determineAlertType(AssetData asset) {
        double changePercent = ((asset.getCurrentPrice() - asset.getPreviousPrice()) / asset.getPreviousPrice()) * 100;
        
        // Determine alert type based on price movement and randomness
        if (Math.abs(changePercent) > 1.5) {
            return AlertType.PRICE_SWING;
        } else if (random.nextInt(10) < 2) { // 20% chance
            return AlertType.BLOCK_TRADE_OPPORTUNITY;
        } else if (random.nextInt(10) < 3) { // 30% chance
            return random.nextBoolean() ? AlertType.MARKET_ORDER_EXECUTED : AlertType.LIMIT_ORDER_EXECUTED;
        } else {
            // Return a random alert type for variety
            return AlertType.values()[random.nextInt(AlertType.values().length)];
        }
    }
    
    private String generateMessage(AssetData asset, AlertType type) {
        double change = asset.getCurrentPrice() - asset.getPreviousPrice();
        double percentChange = (change / asset.getPreviousPrice()) * 100;
        String direction = change >= 0 ? "up" : "down";
        
        switch (type) {
            case MARKET_ORDER_EXECUTED:
                return "Market order executed: " + asset.getSymbol() + " at $" + asset.getCurrentPrice();
            case LIMIT_ORDER_EXECUTED:
                return "Limit order executed: " + asset.getSymbol() + " at $" + asset.getCurrentPrice();
            case PRICE_SWING:
                return "Significant movement: " + asset.getSymbol() + " " + direction + " " 
                    + String.format("%.2f", Math.abs(percentChange)) + "%";
            case VOLUME_ANOMALY:
                return "Unusual volume detected in " + asset.getSymbol() + " - potential institutional activity";
            case LIQUIDITY_WARNING:
                return "Low liquidity alert for " + asset.getSymbol() + " - spread widening";
            case VOLATILITY_ALERT:
                return "Increased volatility in " + asset.getSymbol() + " - consider hedging positions";
            case INSTITUTIONAL_MOVEMENT:
                return "Institutional accumulation detected in " + asset.getSymbol();
            case BLOCK_TRADE_OPPORTUNITY:
                return "Block trade opportunity: " + asset.getSymbol() + " at $" + asset.getCurrentPrice();
            case ARBITRAGE_OPPORTUNITY:
                return "Arbitrage opportunity identified for " + asset.getSymbol() + " across exchanges";
            case REGULATORY_ALERT:
                return "Regulatory announcement pending for " + asset.getSymbol() + " - prepare for volatility";
            default:
                return "Alert for " + asset.getSymbol() + ": currently trading at $" + asset.getCurrentPrice();
        }
    }
    
    private String determineAction(AssetData asset, AlertType type) {
        double changePercent = ((asset.getCurrentPrice() - asset.getPreviousPrice()) / asset.getPreviousPrice()) * 100;
        
        // More sophisticated action recommendation based on alert type and price movement
        if (type == AlertType.MARKET_ORDER_EXECUTED || type == AlertType.LIMIT_ORDER_EXECUTED) {
            return "MONITOR POSITION";
        } else if (type == AlertType.BLOCK_TRADE_OPPORTUNITY) {
            return changePercent > 0 ? "CONSIDER BUY" : "CONSIDER SELL";
        } else if (type == AlertType.REGULATORY_ALERT) {
            return "HOLD";
        } else if (Math.abs(changePercent) > 1.5) {
            return changePercent > 0 ? "TAKE PROFIT" : "CUT LOSSES";
        } else {
            return ACTIONS[random.nextInt(ACTIONS.length)];
        }
    }
}
