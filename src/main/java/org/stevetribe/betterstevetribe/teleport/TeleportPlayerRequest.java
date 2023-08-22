package org.stevetribe.betterstevetribe.teleport;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.stevetribe.betterstevetribe.BetterSteveTribe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportPlayerRequest {
    private final Player sender;
    private Player target = null;

    public static Map<String, TeleportPlayerRequest> senderRequests = new ConcurrentHashMap<>();
    public static Map<String, TeleportPlayerRequest> targetRequests = new ConcurrentHashMap<>();

    public TeleportPlayerRequest(Player sender) throws IllegalArgumentException {
        if(senderRequests.containsKey(sender.getName())) {
            throw new IllegalArgumentException();
        }

        this.sender = sender;
        senderRequests.put(this.sender.getName(), this);

        // 设置一个两分钟延时执行的任务
        Bukkit.getScheduler().runTaskLater(BetterSteveTribe.getPlugin(), () -> {
            senderRequests.remove(this.sender.getName());
        }, 2400L);
    }

    public void setTarget(String targetName) {
        if(this.target != null) {
            this.sender.sendMessage("§c你已经有一个传送请求正在等待接受");
            return;
        }

        // 查找target Player
        Player target = Bukkit.getPlayer(targetName);
        if(target == null) {
            this.sender.sendMessage("§c玩家" + targetName + "不存在");
            return;
        }

        // 检查目标玩家是否在线
        if(!target.isOnline()) {
            this.sender.sendMessage("§c你选择的玩家不在线");
            return;
        }

        // 存入map
        this.target = target;
        targetRequests.put(this.target.getName(), this);
        this.sender.sendMessage("§2传送请求已发送至 " + this.target.getName());

        // 为目标玩家发送提示
        TextComponent tip = new TextComponent("§e新人玩家" + this.sender.getName() + "请求传送到你的身边，");

        TextComponent accept = new TextComponent("§2[允许]  ");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/st tp accept"));

        TextComponent deny = new TextComponent("  §c[拒绝]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/st tp deny"));
        this.target.spigot().sendMessage(tip);
        this.target.spigot().sendMessage(accept, deny);

        TextComponent cancelText = new TextComponent("§e你可以在聊天框输入/st tp cancel来取消传送请求");
        TextComponent cancelButton = new TextComponent("§2也可以直接点击本条消息取消 [取消]");
        cancelButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/st tp cancel"));
        this.sender.spigot().sendMessage(cancelText);
        this.sender.spigot().sendMessage(cancelButton);

        // 设置一个两分钟延时执行的任务
        Bukkit.getScheduler().runTaskLater(BetterSteveTribe.getPlugin(), () -> {
            targetRequests.remove(this.target.getName());
        }, 2400L);
    }

    public void accept() {
        // 判断两个玩家是否都在线
        if(!this.sender.isOnline()) {
            this.target.sendMessage("§c" + sender.getName() + "已下线");
            return;
        }
        if(!this.target.isOnline()) {
            this.sender.sendMessage("§c" + target.getName() + "已下线");
            return;
        }

        this.sender.sendMessage("§2传送请求已被接受");
        this.target.sendMessage("§2你已接受传送请求");
        this.sender.teleport(this.target.getLocation());

        // 从map中移除（可能已经不存在了）
        senderRequests.remove(this.target.getName());
        targetRequests.remove(this.target.getName());
    }

    public void deny() {
        this.sender.sendMessage("§c传送请求已被拒绝");
        this.target.sendMessage("§e你已拒绝请求");

        // 从map中移除（可能已经不存在了）
        senderRequests.remove(this.target.getName());
        targetRequests.remove(this.target.getName());
    }

    public void cancel() {
        this.sender.sendMessage("§e传送请求已被取消");
        this.target.sendMessage("§e传送请求已被取消");

        // 从map中移除（可能已经不存在了）
        senderRequests.remove(this.target.getName());
        targetRequests.remove(this.target.getName());
    }
}
