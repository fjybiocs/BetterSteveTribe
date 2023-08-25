package org.stevetribe.betterstevetribe.lift;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class PlayerJumpListener implements Listener {

    //private ConsoleCommandSender console = Bukkit.getConsoleSender();;

    @EventHandler
    public void OnPlayerJump(PlayerJumpEvent event) {
        Block block = event.getPlayer().getLocation().getBlock();

        // case:玩家在未氧化的铜块上跳跃
        if (block.getType() == Material.AIR &&
                isLiftType(block.getRelative(0, -1, 0).getType())) {
            Player player = event.getPlayer();
            for(int i = 0 ; i < 10 ; i++) {
                Location location = new Location(player.getWorld(),
                        player.getLocation().getX(),
                        player.getLocation().getY() + i,
                        player.getLocation().getZ());

                // 传送玩家
                Block targetBlock = location.getBlock();
                if (isLiftType(targetBlock.getType()) && isBlockAboveSafe(targetBlock)) {
                    Location blockLocation = targetBlock.getLocation();
                    blockLocation.setX(blockLocation.getX() + 0.5);
                    blockLocation.setY(blockLocation.getY() + 1);
                    blockLocation.setZ(blockLocation.getZ() + 0.5);
                    event.getPlayer().teleport(blockLocation);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void OnPlayerSneak(PlayerToggleSneakEvent event) {
        Block block = event.getPlayer().getLocation().getBlock();
        // case:玩家在未氧化的铜块上潜行
        if(block.getType() == Material.AIR &&
                isLiftType(block.getRelative(0, -1, 0).getType())) {
            Player player = event.getPlayer();
            for(int i = 2 ; i <= 12 ; i++) {
                Location location = new Location(player.getWorld(),
                        player.getLocation().getX(),
                        player.getLocation().getY() - i,
                        player.getLocation().getZ());

                // 传送玩家
                Block targetBlock = location.getBlock();
                if (isLiftType(targetBlock.getType()) && isBlockAboveSafe(targetBlock)) {
                    Location blockLocation = targetBlock.getLocation();
                    blockLocation.setX(blockLocation.getX() + 0.5);
                    blockLocation.setY(blockLocation.getY() + 1);
                    blockLocation.setZ(blockLocation.getZ() + 0.5);
                    event.getPlayer().teleport(blockLocation);
                    break;
                }
            }
        }
    }

    private boolean isLiftType(Material type) {
        return type == Material.COPPER_BLOCK ||
                type == Material.EXPOSED_COPPER ||
                type == Material.WEATHERED_COPPER ||
                type == Material.WAXED_COPPER_BLOCK ||
                type == Material.WAXED_EXPOSED_COPPER ||
                type == Material.WAXED_WEATHERED_COPPER;
    }

    private boolean isBlockAboveSafe(Block block) {
        Block blockAbove = block.getRelative(0, 1, 0); // 获取上方方块
        Block blockTwoAbove = block.getRelative(0, 2, 0); // 获取上方方块的上方方块
        return blockAbove.getType().isAir() && blockTwoAbove.getType().isAir();
    }
}
