package org.stevetribe.betterstevetribe.entity;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class LivingEntityHandler implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.IRON_GOLEM) {

            IronGolem ironGolem = (IronGolem) event.getEntity();
            // 移除掉落物品中的铁锭
            event.getDrops().removeIf(item -> item.getType() == Material.IRON_INGOT);
        }
    }
}
