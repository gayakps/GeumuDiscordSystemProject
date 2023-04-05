package gaya.pe.kr.plugin.thread.abstaract;

import gaya.pe.kr.plugin.util.EventUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class MinecraftInventoryListener implements Listener {

    Player player;
    Inventory inventory;

    protected void init() {
        EventUtil.register(this);
    }

    protected void close() {
        HandlerList.unregisterAll(this);
    }

    abstract protected void initInventory();

    public MinecraftInventoryListener(Player player) {
        this.player = player;
    }

    /**
     * @param event
     * Inventory close 가 되면 해당 Listener 를 Interrupt 해줘야한다
     */
    @EventHandler
    public void closeInventory(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();
        if ( player.equals(getPlayer()) ) {
            if ( closedInventory.equals(getInventory()) ) {
                close();
            }
        }
    }

    @EventHandler
    abstract public void clickInventory(InventoryClickEvent event);

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        if ( inventory != null ) {
            this.inventory = inventory;
            player.openInventory(inventory);
            init();
        }
    }

    protected boolean isCurrentPlayerInventory(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if ( clickedInventory == null ) return false;
        if ( player.equals(getPlayer()) ) {
            if ( clickedInventory.equals(getInventory()) ) {
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }

}
