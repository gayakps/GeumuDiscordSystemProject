package gaya.pe.kr.plugin.qa.reactor;

import gaya.pe.kr.plugin.util.ItemCreator;
import gaya.pe.kr.plugin.util.MinecraftInventoryReactor;
import gaya.pe.kr.qa.question.data.Question;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class TargetPlayerQuestionListReactor extends MinecraftInventoryReactor {

    List<Question> questions;
    String targetPlayerName;

    int page = 1;
    int totalPage = 1;

    public TargetPlayerQuestionListReactor(Inventory inventory, Player player, Question[] questionsArray, String targetPlayerName) {
        super(inventory, player);
        questions = Arrays.asList(questionsArray);

    }

    @Override
    protected void init() {

    }

    public void open() {

        getPlayer().closeInventory();

        int startIndex = (page-1) * 45;
        int lastIndex = (page * 45);
        int size = questions.size();

        if ( startIndex > lastIndex || page < 1 || size < startIndex ) {
            getPlayer().sendMessage("§c접근할 수 없는 페이지 입니다");
            return;
        }


        Inventory inventory = Bukkit.createInventory(null, 54, "개인 보관함");

        int inventoryIndex = 0;
        for ( int index = startIndex; index < lastIndex; index++ ) {
            if ( index < size ) {
                Question question = questions.get(index);
                inventory.setItem(inventoryIndex, questions.get(index));
                inventoryIndex++;
            } else {
                break;
            }
        }

        for ( int index = 45; index < 54; index++ ) {
            inventory.setItem(index, ItemCreator.createItemStack(Material.WHITE_STAINED_GLASS_PANE, "", null));
        }


        initInventory(inventory);

    }

    @Override
    protected void clickInventory(InventoryClickEvent event) {

    }

    @Override
    protected void closeInventory(InventoryCloseEvent event) {

    }
}
