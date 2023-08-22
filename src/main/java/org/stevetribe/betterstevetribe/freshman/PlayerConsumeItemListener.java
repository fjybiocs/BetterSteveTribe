package org.stevetribe.betterstevetribe.freshman;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;

import org.stevetribe.betterstevetribe.teleport.TeleportPlayerRequest;
import org.stevetribe.betterstevetribe.utils.SQLiteManager;

public class PlayerConsumeItemListener implements Listener {
    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        // 获取事件触发的玩家
        Player player = event.getPlayer();

        // 如果吃下的不是苹果
        if (event.getItem().getType() != Material.APPLE) {
            return;
        }

        LocalDateTime joinTime = SQLiteManager.getFirstJoinTime(player.getName());
        if (joinTime == null) {
            return;
        }

        // 判断加入服务器是否已经超过了一天
        if (joinTime.plusWeeks(1).isBefore(LocalDateTime.now())) {
            return;
        }

        // 判断使用次数是否超过了7次
        int consumeCount = SQLiteManager.getFreshmanTeleportUsageCount(player.getName());
        if (consumeCount >= 7) {
            return;
        }

        // 发起传送
        try {
            new TeleportPlayerRequest(player);

            player.sendMessage("§e你吃下了专属于新人的神奇的苹果！你还剩" + (7 - consumeCount) + "次传送机会" );
            player.sendMessage("§e可以在聊天框输入#玩家名字 来传送到他的身边，如#TPam");
            player.sendMessage("§c请在两分钟内完成输入~ 不输入不消耗神奇苹果哦~");

        } catch (IllegalArgumentException ignored) {
        }
    }

    // 玩家进入服务器，判断是否已经记录入服时间，如果没有，则记录
    @EventHandler
    public void onPlayerJoinListener(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        LocalDateTime joinTime = SQLiteManager.getFirstJoinTime(player.getName());
        if (joinTime == null) {
            SQLiteManager.setFirstJoinTime(player.getName(), LocalDateTime.now());
            player.sendMessage("§e欢迎来到SteveTribe！");
            player.sendMessage("§e送你7个神奇的苹果，吃下它可以传送到其他玩家身边，七天内有效哦~");
            // 给玩家7个苹果
            player.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.APPLE, 7));
        }
    }
}
