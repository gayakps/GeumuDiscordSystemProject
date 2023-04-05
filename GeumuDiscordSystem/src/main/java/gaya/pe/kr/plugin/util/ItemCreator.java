package gaya.pe.kr.plugin.util;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Dye;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemCreator {

    public static ItemStack createDye(Material material, DyeColor dyeColor, String displayName) {
        Dye dye = new Dye();
        dye.setColor(dyeColor);
        ItemStack dyeItemStack = dye.toItemStack(1);
        dyeItemStack.setType(material);
        ItemMeta itemMeta = dyeItemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',displayName));
        dyeItemStack.setItemMeta(itemMeta);
        return dyeItemStack;
    }

    public static ItemStack createItemStack(Material material, String displayName, List<String> lore, NamespacedKey namespacedKey, String data) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        displayName = ("§f"+displayName).replace("&","§");
        itemMeta.setDisplayName(displayName);
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, data);
        if ( lore != null ) {
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public static ItemStack createItemStack(Material material, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        displayName = ("§f"+displayName).replace("&","§");
        itemMeta.setDisplayName(displayName);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if ( lore != null ) {
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createItemStack(Material material, String displayName, List<String> lore, int durability) {
        ItemStack itemStack = new ItemStack(material, 1, (short) durability);
        ItemMeta itemMeta = itemStack.getItemMeta();
        displayName = ("§f"+displayName).replace("&","§");
        itemMeta.setDisplayName(displayName);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if ( lore != null ) {
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createColorLeatherItem(Material leatherPiece, String displayName, Color color, HashMap<Enchantment, Integer> enchantmentIntegerHashMap) {
        ItemStack item = new ItemStack(leatherPiece);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        displayName = ("§f"+displayName).replace("&","§");
        meta.setDisplayName(displayName);
        meta.setColor(color);
        meta.setUnbreakable(true);
        item.addUnsafeEnchantments(enchantmentIntegerHashMap);
        item.setItemMeta(meta);
        return item;
    }


    public static ItemStack createItemIncludedEnchant(Material material, String displayName, HashMap<Enchantment, Integer> enchantmentIntegerHashMap) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        displayName = ("§f"+displayName).replace("&","§");
        itemMeta.setDisplayName(displayName);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantments(enchantmentIntegerHashMap);
        return itemStack;
    }

    public static ItemStack createItemIncludedEnchant(Material material, String displayName, HashMap<Enchantment, Integer> enchantmentIntegerHashMap, int durability) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        displayName = ("§f"+displayName).replace("&","§");
        itemStack.setDurability((short) durability);
        itemMeta.setDisplayName(displayName);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantments(enchantmentIntegerHashMap);
        return itemStack;
    }


    public static ItemStack modifyItemStack(ItemStack targetItem, String displayName, List<String> lore) {
        ItemMeta itemMeta = targetItem.getItemMeta();
        if ( displayName != null ) {
            itemMeta.setDisplayName(displayName);
        }
        if ( lore != null ) {
            itemMeta.setLore(lore);
        }
        targetItem.setItemMeta(itemMeta);
        return targetItem;
    }

    public static ItemStack getPlayerHead(OfflinePlayer targetPlayer) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        meta.setOwningPlayer(targetPlayer);
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack addLore(ItemStack targetItem, List<String> lore) {
        ItemMeta itemMeta = targetItem.getItemMeta();
        List<String> loreList = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        for (String s : lore) {
            loreList.add(s.replace("&","§"));
        }

        itemMeta.setLore(loreList);
        targetItem.setItemMeta(itemMeta);
        return targetItem;
    }


}
