package org.stevetribe.betterstevetribe.potion.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.stevetribe.betterstevetribe.BetterSteveTribe;

import java.util.Arrays;

public class Home {
    public static ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(BetterSteveTribe.getPlugin(), "homeEssence"), getEssenceItem());

    static public void addHomeEssenceRecipe() {
        recipe = recipe.addIngredient(1, Material.ENDER_EYE).
                addIngredient(1, Material.WHEAT).
                addIngredient(1, Material.HONEY_BOTTLE).
                addIngredient(1, Material.CHORUS_FLOWER);

        Bukkit.addRecipe(recipe);
    }

    static public void deleteHomeEssenceRecipe() {
        Bukkit.resetRecipes();
    }

    // 创建带NBT标签的精华
    static public ItemStack getEssenceItem() {
        ItemStack homeEssence = new ItemStack(Material.HONEY_BOTTLE);

        // 获取物品的元数据（ItemMeta）
        ItemMeta meta = homeEssence.getItemMeta();

        // 设置物品的自定义名称
        meta.setDisplayName("§b回家精华");

        // 设置物品的描述（Lore）
        meta.setLore(Arrays.asList("三界精华共同作用产生的神奇之物", "如果你有床，则会直接到床边；", "如果没有床，你会回到世界出生点"));

        // 添加其他的NBT标签，这里使用一个示例键值对
        meta.getPersistentDataContainer().set(new NamespacedKey(BetterSteveTribe.getPlugin(), "isHomeEssence"), PersistentDataType.BOOLEAN, true);

        // 将修改后的元数据重新应用到物品上
        homeEssence.setItemMeta(meta);

        return homeEssence;
    }

    // 检查NBT标签是否存在且为true
    public static boolean isHomeEssence(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            // 使用NamespacedKey获取NBT标签的值
            Boolean isHomeEssence = meta.getPersistentDataContainer().get(
                    new NamespacedKey(BetterSteveTribe.getPlugin(), "isHomeEssence"),
                    PersistentDataType.BOOLEAN
            );

            // 如果isHomeEssence标签存在且为true，则返回true
            return isHomeEssence != null && isHomeEssence;
        }
        return false;
    }
}
