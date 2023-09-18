package org.stevetribe.betterstevetribe.disk;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.stevetribe.betterstevetribe.BetterSteveTribe;

import java.io.File;

public class DiskCheckTask extends BukkitRunnable {
    Plugin plugin = BetterSteveTribe.getPlugin();

    @Override
    public void run() {
        // 检查磁盘空间
        File serverDirectory = new File(".");
        long freeSpace = serverDirectory.getFreeSpace(); // 获取剩余磁盘空间，单位是字节

        long minFreeSpace = 7L * 1024 * 1024 * 1024; // 7GB，以字节为单位

        System.out.println("checking..." + freeSpace / ( 1024 * 1024));

        if (freeSpace < minFreeSpace) {
            plugin.getLogger().warning("Server is running low on disk space. Shutting down...");
            plugin.getServer().shutdown();
        }
    }
}
