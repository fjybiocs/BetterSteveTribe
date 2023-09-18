package org.stevetribe.betterstevetribe.itembuy;

import com.sun.java.accessibility.util.GUIInitializedListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.stevetribe.betterstevetribe.utils.SQLiteManager;

import java.sql.SQLException;
import java.util.Arrays;

public class GUIManager {

    private static GUIManager instance = null;

    public String guiTitle = "蕉大市场，点击出售1组";

    public static  GUIManager getInstance() {
        if(instance == null) {
            instance = new GUIManager();
        }

        return instance;
    }

    public void openSellGUI(Player player) throws SQLException {
        Inventory inventory = Bukkit.createInventory(null, 27, guiTitle);
        int i = 0;
        for (SellableItem sellableItem : ItemManager.getInstance().getSellableItems().values()) {
            ItemStack itemStack = new ItemStack(Material.valueOf(sellableItem.getName().toUpperCase()));
            // 获取今日还可出售最大值
            int maxSellCountPerDay;
            try {
                maxSellCountPerDay = sellableItem.getMaxSellCountPerDay() - SQLiteManager.getItemSellCount(player.getName(), sellableItem.getName(), System.currentTimeMillis());
            } catch (SQLException e) {
                player.sendMessage("§c数据库错误，请联系TPam~");
                return;
            }
            // 这里可以设置物品的显示名称、lore等信息
            itemStack.setDisplayName("§e" + itemStack.getLocalizedName());
            itemStack.setLore(Arrays.asList(String.format("§e价格: §6%.2f\n", sellableItem.getPrice()),
                    String.format("§e今日还可出售: §6%d", maxSellCountPerDay)));
            itemStack.setAmount(64);

            inventory.addItem(itemStack);
            i++;
        }

        player.openInventory(inventory);
    }

    private GUIManager(){

    }
}
