package org.stevetribe.betterstevetribe.itembuy;

import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.stevetribe.betterstevetribe.utils.SQLiteManager;

import java.sql.SQLException;
import java.util.Arrays;

import static org.bukkit.Bukkit.getServer;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        if (event.getView().getTitle().equals(GUIManager.guiTitle)) {
            event.setCancelled(true); // 防止玩家移动物品

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                // 检查该物品是否存在
                SellableItem sellableItem = ItemManager.getSellableItems().get(clickedItem.getType().name());
                if (sellableItem == null) {
                    player.sendMessage("§c出错，请联系TPam");
                    return;
                }

                // 检查玩家背包中有多少个该物品
                int count = 0;
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack != null && itemStack.getType() == clickedItem.getType()) {
                        count += itemStack.getAmount();
                    }
                }

                // 如果数量小于64
                if(count < 64) {
                    player.sendMessage("§c你的背包中没有足够的" + clickedItem.getType().name() + "，无法出售");
                    return;
                }

                // 检查是否还能出售
                int maxSellCountPerDay;
                try {
                    maxSellCountPerDay = sellableItem.getMaxSellCountPerDay() - SQLiteManager.getItemSellCount(player.getName(), sellableItem.getName(), System.currentTimeMillis());
                } catch (SQLException e) {
                    player.sendMessage("§c数据库错误，请联系TPam~");
                    return;
                }

                if (maxSellCountPerDay <= 0) {
                    player.sendMessage("§c你已经出售了太多的" + clickedItem.getType().name() + "，明天再来吧~");
                    return;
                }

                //
                try {
                    SQLiteManager.setItemSellRecord(player.getName(), clickedItem.getType().name(), 1, System.currentTimeMillis());
                } catch (Exception e) {
                    player.sendMessage("§c数据库错误，请联系TPam~");
                    return;
                }

                // 扣除64个该物品
                for (int i = 0; i < 64; i++) {
                    player.getInventory().removeItem(new ItemStack(clickedItem.getType()));
                }

                // 后台执行命令，给玩家加钱
                ConsoleCommandSender consoleSender = getServer().getConsoleSender();

                // 执行后台命令
                getServer().dispatchCommand(consoleSender, "cmi money add " + player.getName() + " " + sellableItem.getPrice());

                // 玩家成功
                player.sendMessage("§a出售成功，获得" + sellableItem.getPrice() + "蕉币");

                //
                try {
                    maxSellCountPerDay = sellableItem.getMaxSellCountPerDay() - SQLiteManager.getItemSellCount(player.getName(), sellableItem.getName(), System.currentTimeMillis());
                } catch (SQLException e) {
                    player.sendMessage("§c数据库错误，请联系TPam~");
                    return;
                }

                // 更新lore
                clickedItem.setLore(Arrays.asList(String.format("§e价格: §6%.2f\n", sellableItem.getPrice()),
                        String.format("§e今日还可出售: §6%d", maxSellCountPerDay)));
            }
        }
    }
}
