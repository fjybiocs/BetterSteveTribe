package org.stevetribe.betterstevetribe.skill;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class PlayerLevelListener implements Listener {
    @EventHandler
    public void OnPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
        Player player = event.getPlayer();

        int level = Objects.requireNonNull(UserManager.getPlayer(player)).getSkillLevel(event.getSkill());
        int reward = calculateReward(level);
        ConsoleCommandSender consoleSender = getServer().getConsoleSender();
        getServer().dispatchCommand(consoleSender, "cmi money add " + player.getName() + " " + reward);
        player.sendMessage("恭喜升级，" + reward + "蕉币已到账");
    }

    private int calculateReward(int level) {
        int initialReward = 100;
        int rewardIncrement = 10; // 每升一级的金币奖励增量
        return initialReward + (level - 1) * rewardIncrement;
    }
}
