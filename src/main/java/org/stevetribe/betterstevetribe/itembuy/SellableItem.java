package org.stevetribe.betterstevetribe.itembuy;

public class SellableItem {

    private final String name;
    private final double price;
    private final int maxSellCountPerDay;

    public SellableItem(String name, double price, int maxSellCountPerDay) {
        this.name = name;
        this.price = price;
        this.maxSellCountPerDay = maxSellCountPerDay;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getMaxSellCountPerDay() {
        return maxSellCountPerDay;
    }
}
