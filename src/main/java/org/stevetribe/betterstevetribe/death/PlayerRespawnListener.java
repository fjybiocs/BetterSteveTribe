package org.stevetribe.betterstevetribe.death;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.stevetribe.betterstevetribe.BetterSteveTribe;

public class PlayerRespawnListener implements Listener {
    // when a player reborn
    @EventHandler
    public void onPlayerRespawnListener(PlayerRespawnEvent event) {
        double maxHealth = event.getPlayer().getMaxHealth();
        if (maxHealth > 6) {
            event.getPlayer().setMaxHealth(maxHealth - 2);
        }

        (new BukkitRunnable() {
            public void run() {
                event.getPlayer().setFoodLevel(6);
                event.getPlayer().setExp(0);
                event.getPlayer().setLevel(0);
            }
        }).runTaskLaterAsynchronously(BetterSteveTribe.getPlugin(), 5L);
    }

    // when a player wakeup because of a new day come
    @EventHandler
    public void onPlayerBedLeaveListener(PlayerBedLeaveEvent event) {
        long time = event.getPlayer().getWorld().getTime();
        // 如果时间为0，就是天亮起床
        if (time <= 500) {
            // 最大血量+2
            double maxHealth = event.getPlayer().getMaxHealth();
            if(maxHealth < 20) {
                maxHealth += 2;
                event.getPlayer().setMaxHealth(maxHealth);
            }
            event.getPlayer().setHealth(maxHealth);
            // 添加5颗Absorption Heart
            event.getPlayer().setAbsorptionAmount(10);
        }
    }
}
