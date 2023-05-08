package gaya.pe.kr.plugin.util;

import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.util.option.data.options.gui.CommonlyUsedButtonOption;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class MinecraftInventoryReactor implements Listener {

    Inventory inventory;
    Player player;

    public QARepository getQaRepository() {

        if ( qaRepository == null ) {
            qaRepository = QAManager.getInstance().getQaRepository();
        }

        return qaRepository;
    }

    protected QARepository qaRepository;

    public MinecraftInventoryReactor(Player player, QARepository qaRepository) {
        this.player = player;
        this.qaRepository = qaRepository;
    }

    protected abstract void init();



    public void start() {
        EventUtil.register(this);
        SchedulerUtil.runLaterTask(this::init,1);
    }

    @EventHandler
    public void clickInventoryEvent(InventoryClickEvent event) {

        Inventory clickedInventory = event.getClickedInventory();

        if ( clickedInventory == null ) return;

        Inventory nowInventory = event.getInventory();

        Player clickedPlayer = (Player) event.getWhoClicked();

        if ( !clickedPlayer.getUniqueId().equals(player.getUniqueId()) ) return;

        if ( !clickedInventory.equals(nowInventory) ) return;

        event.setCancelled(true);
        event.setResult(Event.Result.DENY);

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

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }


    protected void setUpDefaultPoketmonInventory(Inventory inventory) {

        CommonlyUsedButtonOption commonlyUsedButtonOption = OptionManager.getInstance().getCommonlyUsedButtonOption();

        for ( int index = 36; index < 45; index++ ) {
            inventory.setItem(index, ItemCreator.createItemStack(Material.GRAY_STAINED_GLASS_PANE, ""));
        }

        inventory.setItem(48, ItemCreator.createItemStack(Material.getMaterial(commonlyUsedButtonOption.getPreviousPageButtonType().toUpperCase(Locale.ROOT)), commonlyUsedButtonOption.getPreviousPageButtonName()));
        inventory.setItem(50, ItemCreator.createItemStack(Material.getMaterial(commonlyUsedButtonOption.getNextPageButtonType().toUpperCase(Locale.ROOT)), commonlyUsedButtonOption.getNextPageButtonName()));

            // 52 inventory
            ItemStack index52Item = ItemCreator.createItemStack(
                    Material.getMaterial(commonlyUsedButtonOption.getAnswerRewardInfoButtonType().toUpperCase(Locale.ROOT))
                    , commonlyUsedButtonOption.getAnswerRewardInfoButtonName()
                    , commonlyUsedButtonOption.getAnswerRewardInfoButtonLore()
            );
            inventory.setItem(52, index52Item);

        // 52 inventory
        ItemStack index53Item = ItemCreator.createItemStack(
                Material.getMaterial(commonlyUsedButtonOption.getQuestionHelpButtonType().toUpperCase(Locale.ROOT))
                , commonlyUsedButtonOption.getQuestionHelpButtonName()
                , commonlyUsedButtonOption.getQuestionHelpButtonLore()
        );
        inventory.setItem(53, index53Item);

    }

}
