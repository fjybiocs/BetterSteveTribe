package org.stevetribe.betterstevetribe.elevator;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.event.EventHandler;

public class PlayerJumpListener {
    // 在玩家跳跃时
    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        // 如果玩家在world世界
        if (event.getPlayer().getWorld().getName().equals("world")) {

        }
    }
}
