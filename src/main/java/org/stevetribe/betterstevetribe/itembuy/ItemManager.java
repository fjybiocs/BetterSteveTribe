package org.stevetribe.betterstevetribe.itembuy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.stevetribe.betterstevetribe.BetterSteveTribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {
    private static Map<String, SellableItem> sellableItems = new HashMap<>();

    public static void loadItemsFromConfig() throws IOException {

        // Load and parse the configuration file
        File configFile = new File(BetterSteveTribe.getPlugin().getDataFolder(), "item-sell.yml");
        // 如果不存在则创建
        if (!configFile.exists()) {
            configFile.createNewFile();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (config.contains("items")) {
            List<Object> itemList = (List<Object>) config.getList("items");
            if (itemList != null) {
                for (Object itemObject : itemList) {
                    if (itemObject instanceof Map) {
                        Map<String, Object> itemMap = (Map<String, Object>) itemObject;
                        String name = (String) itemMap.get("name");
                        name = name.toUpperCase();

                        double price = (Double) itemMap.get("price");
                        int limit = (Integer) itemMap.get("limit");

                        SellableItem sellableItem = new SellableItem(name, price, limit);

                        sellableItems.put(sellableItem.getName(), sellableItem);
                    }
                }
            }
        }
    }

    public static Map<String, SellableItem> getSellableItems() {
        return sellableItems;
    }
}