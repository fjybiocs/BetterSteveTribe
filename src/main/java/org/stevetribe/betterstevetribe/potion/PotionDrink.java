package org.stevetribe.betterstevetribe.potion;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.stevetribe.betterstevetribe.potion.item.Home;

public class PotionDrink implements Listener {

    @EventHandler
    public void onHomePotionDrink(PlayerItemConsumeEvent event) {
        System.out.println("eat");

        // 获取触发事件的玩家
        Player player = event.getPlayer();

        // 获取被消耗的物品
        ItemStack consumedItem = event.getItem();

        // 检查被消耗的物品类型是否为苹果
        if (consumedItem.getType() == Material.HONEY_BOTTLE) {
            // 检查NBT标签是否存在且为true
            if (Home.isHomeEssence(consumedItem)) {
                // 执行你的逻辑
                player.sendMessage("你吃了一个带有isHomeEssence标签的苹果！");
            }
        }
    }
}
