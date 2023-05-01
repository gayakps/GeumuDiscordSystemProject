package gaya.pe.kr.plugin.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class MinecraftInventoryReactor implements Listener {

    Inventory inventory;
    Player player;

    public MinecraftInventoryReactor(Inventory inventory, Player player) {
        this.inventory = inventory;
        this.player = player;
    }

    protected abstract void init();

    public void start() {

        EventUtil.register(this);
        init();
    }

    @EventHandler
    public void clickInventoryEvent(InventoryClickEvent event) {

        Inventory clickedInventory = event.getClickedInventory();

        if ( clickedInventory == null ) return;

        Inventory nowInventory = event.getInventory();

        Player clickedPlayer = (Player) event.getWhoClicked();

        if ( !clickedPlayer.getUniqueId().equals(player.getUniqueId()) ) return;

        if ( !clickedInventory.equals(nowInventory) ) return;

        clickInventory(event);
    }

    @EventHandler
    public void closeInventoryEvent(InventoryCloseEvent event) {

        Inventory nowInventory = event.getInventory();

        if ( !nowInventory.equals(this.inventory) ) return;

        closeInventory(event);
    }

    abstract protected void clickInventory(InventoryClickEvent event);
    abstract protected void closeInventory(InventoryCloseEvent event);

    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    protected void close() {
        HandlerList.unregisterAll(this);
    }

}
