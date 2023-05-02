package gaya.pe.kr.plugin.qa.reactor;

import gaya.pe.kr.plugin.util.MinecraftInventoryReactor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class DailyQuestionRankingReactor extends MinecraftInventoryReactor {

    public DailyQuestionRankingReactor(Player player) {
        super(player);
    }

    @Override
    protected void init() {

        Inventory inventory = null;
        setInventory(inventory);

    }

    @Override
    protected void clickInventory(InventoryClickEvent event) {

    }

    @Override
    protected void closeInventory(InventoryCloseEvent event) {

    }
}
