package org.stevetribe.betterstevetribe.fly;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerFlyTask extends BukkitRunnable {
    static Set<String> concurrentSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void run() {
        // 遍历set中的玩家
        for (String playerName : concurrentSet) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                // 首先判断是不是创造或者旁观模式
                if (player.getGameMode().getValue() == 1 || player.getGameMode().getValue() == 3) {
                    concurrentSet.remove(playerName);
                    continue;
                }
                // 判断玩家是否没有在飞行
                if(!player.isFlying()) {
                    concurrentSet.remove(playerName);
                    continue;
                }
                // 否则扣除2点饥饿值
                player.setFoodLevel(player.getFoodLevel() - 1);
                // 然后判断玩家是否不在world，或者高度小于61，或者饱食度小于6
                player.setAllowFlight(PlayerFlyListener.canFly(player));
            }
        }
    }
}
