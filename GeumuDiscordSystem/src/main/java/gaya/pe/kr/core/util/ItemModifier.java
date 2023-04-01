package gaya.pe.kr.core.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemModifier {


    public static void removeTargetLore(ItemStack itemStack, String targetLoreContain) {

        String targetLore = "";
        List<String> lore = getLore(itemStack);

        for (String s : lore) {
            if ( s.contains(targetLoreContain) ) {
                targetLore = s;
            }
        }

        if ( !targetLore.equals("") ) {
            lore.remove(targetLore);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

    }

    public static void addLore(ItemStack itemStack, List<String> loreStr) {
        if ( !Filter.isNullOrAirItem(itemStack) ) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = (itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>());
            for (String s : loreStr) {
                lore.add(s.replace("&", "§"));
            }
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
    }

    public static ItemStack createItemStack(Material material, int durability, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, 1, (short) durability);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName.replace("&","§"));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if ( lore != null ) {
            itemMeta.setLore(lore);
        }
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void replaceTargetLore(ItemStack itemStack, String targetLoreContain, String replaceLore) {

    }

    public static void addLore(ItemStack itemStack, String loreStr) {
        List<String> lore = getLore(itemStack);

        lore.add(loreStr.replace("&", "§"));

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public static List<String> getLore(ItemStack itemStack) {
        List<String> lore = new ArrayList<>();
        if ( itemStack.hasItemMeta() ) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        }
        return lore;
    }


}
