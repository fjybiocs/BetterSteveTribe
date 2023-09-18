package org.stevetribe.betterstevetribe;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.stevetribe.betterstevetribe.itembuy.GUIManager;
import org.stevetribe.betterstevetribe.itembuy.ItemManager;
import org.stevetribe.betterstevetribe.teleport.TeleportPlayerRequest;

import java.io.IOException;
import java.sql.SQLException;

// import static org.stevetribe.betterstevetribe.itembuy.ItemManager.loadItemsFromConfig;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("st")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("§c该命令只能由玩家执行");
                return true;
            }
            Player player = (Player) sender;
            if (args.length == 1) {
                if (args[0].equals("sell")) {
                    // 判断是否能打开
                    // 条件：处于world，且x在-8000到8000，z在9000到25000
                    if (!player.getWorld().getName().equals("world") || player.getLocation().getX() < -8000 || player.getLocation().getX() > 8000 || player.getLocation().getZ() < 9000 || player.getLocation().getZ() > 25000) {
                        player.sendMessage("§c你只能在新区主世界打开市场");
                        return true;
                    }
                    try {
                        GUIManager.getInstance().openSellGUI(player);
                    } catch (SQLException e) {
                        player.sendMessage("§c数据库错误，请联系TPam~");
                        return true;
                    }
                    return true;
                }
                if (args[0].equals("reload")) {
                    if (!player.isOp()) {
                        player.sendMessage("§c你没有权限执行该命令");
                        return true;
                    }
                    /**
                    try {
                        // loadItemsFromConfig();
                        player.sendMessage("§a重载成功");
                        return true;
                    } catch (IOException e) {
                        player.sendMessage("§c配置文件读取错误，请联系TPam~");
                        return true;
                    }*/
                }
            }
            if (args.length == 2) {
                if (args[0].equals("tp")) {
                    String subcommand = args[1].toLowerCase();
                    if (subcommand.equals("accept")) {
                        // 处理 /tpa accept
                        TeleportPlayerRequest teleportPlayerRequest = TeleportPlayerRequest.targetRequests.get(player.getName());
                        if (teleportPlayerRequest != null) {
                            teleportPlayerRequest.accept();
                        } else {
                            player.sendMessage("§c你没有等待接受的传送请求，或者请求已经过期");
                        }
                        return true;
                    } else if (subcommand.equals("deny")) {
                        // 处理 /tpa deny

                        TeleportPlayerRequest teleportPlayerRequest = TeleportPlayerRequest.targetRequests.get(player.getName());
                        if (teleportPlayerRequest != null) {
                            teleportPlayerRequest.deny();
                        } else {
                            player.sendMessage("§c你没有可以拒绝的传送请求，或者请求已经过期");
                        }
                        return true;
                    } else if (subcommand.equals("cancel")) {
                        // 处理 /tpa cancel

                        TeleportPlayerRequest teleportPlayerRequest = TeleportPlayerRequest.senderRequests.get(player.getName());
                        if (teleportPlayerRequest != null) {
                            teleportPlayerRequest.cancel();
                        } else {
                            player.sendMessage("§c你没有可以取消的传送请求，或者请求已经过期");
                        }
                        return true;
                    }
                }
            }
            // 处理其他情况（未提供参数、无效参数等）
            sender.sendMessage("§c用法: /tpa <accept|deny>");
            return true;
        }
        return false;
    }
}
