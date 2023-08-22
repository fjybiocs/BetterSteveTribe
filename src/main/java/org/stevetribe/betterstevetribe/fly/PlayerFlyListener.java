package org.stevetribe.betterstevetribe.fly;

import net.kyori.adventure.util.TriState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;


public class PlayerFlyListener implements Listener {
    @EventHandler
    public void onPlayerToggleFlightListener(PlayerToggleFlightEvent event) {
        event.getPlayer().setFlyingFallDamage(TriState.TRUE);
        PlayerFlyTask.concurrentSet.add(event.getPlayer().getName());
    }

    // 位置改变，重新判断能否飞行
    @EventHandler
    public void onPlayerMoveListener(PlayerMoveEvent event) {
        boolean canFly = canFly(event.getPlayer());
        event.getPlayer().setAllowFlight(canFly);
    }

    // 能否飞行
    public static boolean canFly(Player player) {
        // 如果玩家在创造模式或者旁观模式
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return true;
        }

        // 最基本条件：食物大于6，高度大于61，在world世界
        return player.getFoodLevel() > 6 && player.getLocation().getY() > 61 && player.getWorld().getName().equals("world");
    }

    // 当玩家退出服务器，将其从set中删除
    @EventHandler
    public void onPlayerQuitListener(PlayerQuitEvent event) {
        PlayerFlyTask.concurrentSet.remove(event.getPlayer().getName());
    }
}
