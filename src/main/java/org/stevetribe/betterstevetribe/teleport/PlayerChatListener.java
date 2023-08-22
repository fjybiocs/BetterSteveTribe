package org.stevetribe.betterstevetribe.teleport;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.stevetribe.betterstevetribe.utils.SQLiteManager;

public class PlayerChatListener implements Listener {
    // 在玩家发送信息以"#"开头时
    @EventHandler
    public void onPlayerChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        // 如果玩家发送的信息以"#"开头
        if (event.getMessage().startsWith("#")) {
            // 查看玩家是否有等待添加target的传送请求
            TeleportPlayerRequest teleportPlayerRequest = TeleportPlayerRequest.senderRequests.get(event.getPlayer().getName());
            if(teleportPlayerRequest != null) {
                // 设置target
                teleportPlayerRequest.setTarget(event.getMessage().substring(1));
                SQLiteManager.incrementFreshmanTeleportUsageCount(event.getPlayer().getName());
                event.setCancelled(true);
            }
        }
    }
}
