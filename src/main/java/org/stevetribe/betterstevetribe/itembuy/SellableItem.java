package org.stevetribe.betterstevetribe.itembuy;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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
