package org.stevetribe.betterstevetribe;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.stevetribe.betterstevetribe.disk.DiskCheckTask;
import org.stevetribe.betterstevetribe.fly.PlayerFlyListener;
import org.stevetribe.betterstevetribe.fly.PlayerFlyTask;
import org.stevetribe.betterstevetribe.death.PlayerRespawnListener;
import org.stevetribe.betterstevetribe.freshman.PlayerConsumeItemListener;
import org.stevetribe.betterstevetribe.itembuy.InventoryClickListener;
import org.stevetribe.betterstevetribe.itembuy.ItemManager;
import org.stevetribe.betterstevetribe.lift.PlayerJumpListener;
import org.stevetribe.betterstevetribe.skill.PlayerLevelListener;
import org.stevetribe.betterstevetribe.utils.SQLiteManager;
import org.stevetribe.betterstevetribe.teleport.PlayerChatListener;
import org.stevetribe.betterstevetribe.entity.LivingEntityHandler;

import java.io.IOException;
import java.sql.SQLException;

public final class BetterSteveTribe extends JavaPlugin {

    @Override
    public void onEnable() {
        // 读取配置文件并加载物品信息
        saveDefaultConfig(); // 保证默认配置文件存在
        ItemManager.getInstance().init(this); // 初始化ItemManager

        // 初始化sqlite
        try {
            getLogger().info("Initializing SQLite...");
            SQLiteManager.initializeConnection();
            SQLiteManager.createTables();
            getLogger().info("SQLite initialized.");
        } catch (SQLException e) {
            getLogger().severe("Failed to initialize SQLite.");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
            // 中断插件启动
        }

        // 定时任务：飞行
        PlayerFlyTask flyTask = new PlayerFlyTask();
        flyTask.runTaskTimer(this, 0, 12);
        // 定时任务：低磁盘容量检查
        DiskCheckTask diskCheckTask = new DiskCheckTask();
        diskCheckTask.runTaskTimer(this, 0, 200);

        // 注册监听器
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConsumeItemListener(), this);
        pluginManager.registerEvents(new PlayerRespawnListener(), this);
        pluginManager.registerEvents(new PlayerFlyListener(), this);
        pluginManager.registerEvents(new PlayerChatListener(), this);
        pluginManager.registerEvents(new InventoryClickListener(), this);
        pluginManager.registerEvents(new PlayerJumpListener(), this);
        pluginManager.registerEvents(new LivingEntityHandler(), this);
        pluginManager.registerEvents(new PlayerLevelListener(), this);

        // 注册命令
        this.getCommand("st").setExecutor(new CommandExecutor());

        // log
        getLogger().info("BetterSteveTribe now enabled.");
    }

    // a function to get plugin
    public static BetterSteveTribe getPlugin() {
        return BetterSteveTribe.getPlugin(BetterSteveTribe.class);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("BetterSteveTribe now disabled.");
        ItemManager.getInstance().onDestory();
    }
}
