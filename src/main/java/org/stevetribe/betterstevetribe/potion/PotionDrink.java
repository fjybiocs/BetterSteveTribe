package org.stevetribe.betterstevetribe.potion;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.stevetribe.betterstevetribe.BetterSteveTribe;
import org.stevetribe.betterstevetribe.potion.item.Home;

import java.util.Objects;

public class PotionDrink implements Listener {

    @EventHandler
    public void onHomePotionDrink(PlayerItemConsumeEvent event) {
        // 获取触发事件的玩家
        Player player = event.getPlayer();

        // 获取被消耗的物品
        ItemStack consumedItem = event.getItem();

        // 检查被消耗的物品类型是否为苹果
        if (consumedItem.getType() == Material.HONEY_BOTTLE) {
            // 检查NBT标签是否存在且为true
            if (Home.isHomeEssence(consumedItem)) {
                Location location = player.getBedSpawnLocation();
                if (location == null) {
                    location = Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation();
                    player.sendMessage("§e还在流浪...");
                } else {
                    player.sendMessage("§e回家啦~");
                }
                player.teleport(location);
                teleportEffect(player.getLocation());
                // 播放传送声音
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            }
        }
    }

    // 播放传送粒子效果和声音
    private void teleportEffect(Location location) {
        new BukkitRunnable() {
            double phi = 0;

            @Override
            public void run() {
                phi += Math.PI / 10;
                for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {
                    double x = 0.4 * (1 - Math.sin(phi)) * Math.cos(theta);
                    double y = 0.5 * phi;
                    double z = 0.4 * (1 - Math.sin(phi)) * Math.sin(theta);

                    location.add(x, y, z);
                    location.getWorld().spawnParticle(Particle.PORTAL, location, 1);
                    location.subtract(x, y, z);
                }

                if (phi > Math.PI) {
                    this.cancel();
                }
            }
        }.runTaskTimer(BetterSteveTribe.getPlugin(), 0, 1);
    }
}
