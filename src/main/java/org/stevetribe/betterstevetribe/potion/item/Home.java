package org.stevetribe.betterstevetribe.potion.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.stevetribe.betterstevetribe.BetterSteveTribe;

public class Home {
    public static ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(BetterSteveTribe.getPlugin(), "homeEssence"), getEssenceItem());

    static public void addHomeEssenceRecipe() {
        recipe = recipe.addIngredient(1, Material.APPLE).
                addIngredient(1, Material.GOLD_INGOT).
                addIngredient(1, Material.WARPED_FUNGUS).
                addIngredient(1, Material.GLOW_BERRIES).
                addIngredient(1, Material.ENDER_EYE).
                addIngredient(1, Material.CHORUS_FRUIT);

        Bukkit.addRecipe(recipe);
    }

    static public void deleteHomeEssenceRecipe() {
        Bukkit.resetRecipes();
    }

    // 创建带NBT标签的精华物品
    static public ItemStack getEssenceItem() {
        ItemStack homeEssence = new ItemStack(Material.HONEY_BOTTLE);

        homeEssence.setDisplayName("§b回家精华");
        // 创建一个NBT标签，并设置其值为true
        homeEssence.getPersistentDataContainer().set(
            // 使用自定义的NamespacedKey，确保唯一性
            new NamespacedKey(BetterSteveTribe.getPlugin(), "isHomeEssence"),
            // 设置NBT标签的数据类型为Boolean
            PersistentDataType.BOOLEAN,
            true
        );

        return homeEssence;
    }

    // 检查NBT标签是否存在且为true
    public static boolean isHomeEssence(ItemStack item) {
        PersistentDataContainer container = item.getPersistentDataContainer();

        System.out.println(container);

        // 使用NamespacedKey获取NBT标签的值
        Boolean isHomeEssence = container.get(
            new NamespacedKey(BetterSteveTribe.getPlugin(), "isHomeEssence"),
            PersistentDataType.BOOLEAN
        );

        System.out.println(isHomeEssence);

        // 如果isHomeEssence标签存在且为true，则返回true
        return isHomeEssence != null && isHomeEssence;

    }
}
