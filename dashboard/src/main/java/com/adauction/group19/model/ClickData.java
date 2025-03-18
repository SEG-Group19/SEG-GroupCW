package com.adauction.group19.model;

import java.time.LocalDateTime;

public class ClickData {
    private LocalDateTime date;
    private String id;
    private double clickCost;

    public ClickData(LocalDateTime date, String id, double clickCost) {
        this.date = date;
        this.id = id;
        this.clickCost = clickCost;
    }

    // Getters
    public LocalDateTime getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public double getClickCost() {
        return clickCost;
    }

    // Setters
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClickCost(double clickCost) {
        this.clickCost = clickCost;
    }
} 