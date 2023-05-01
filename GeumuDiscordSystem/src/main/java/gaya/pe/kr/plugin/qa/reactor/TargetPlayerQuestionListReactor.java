package gaya.pe.kr.plugin.qa.reactor;

import gaya.pe.kr.plugin.util.MinecraftInventoryReactor;
import gaya.pe.kr.qa.question.data.Question;
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

        HashMap<Double, UUID> doubleUUIDHashMap = new HashMap<>();
        List<Double> doubles = new ArrayList<>();
        getPlayerMoneyHashMap().forEach((uuid, economy) -> {
            double money = economy.getMoney();
            doubleUUIDHashMap.put(money, uuid);
            doubles.add(money);
        });

        doubles.sort(Collections.reverseOrder());

        int startIndex = (page-1)*10;
        int lastIndex = startIndex+9;
        int listSize = doubles.size()-1;


        if ( startIndex > lastIndex || page < 1 || listSize < startIndex ) {
            player.sendMessage("§c해당 페이지론 접근할 수 없습니다");
            player.closeInventory();
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "돈 순위 | 페이지 : "+page);

        int rank = 1;
        for ( int index = startIndex; index <= lastIndex; index++ ) {
            if ( index <= listSize ) {
                int slot = getIndexAsRank(rank);
                double value = doubles.get(index);
                UUID uuid = doubleUUIDHashMap.get(value);
                inventory.setItem(slot, ItemBuilder.createItemStack(Material.PLAYER_HEAD, String.format("§b%d. §f<§6%s§f>", rank, UtilMethod.getPlayerName(uuid)), Collections.singletonList(String.format("§9금액 : %.1f",value)), 1));
                rank++;
            } else {
                break;
            }
        }

        inventory.setItem(45, ItemBuilder.createItemStack(Material.SPRUCE_SIGN, "§6이전 페이지", Collections.singletonList("§9클릭 시 이전 페이지로 이동합니다"), 1));
        inventory.setItem(49, ItemBuilder.createItemStack(Material.SPRUCE_SIGN, String.format("§6현재 페이지 : %d", page), null, 1));
        inventory.setItem(53, ItemBuilder.createItemStack(Material.SPRUCE_SIGN, "§6이후 페이지", Collections.singletonList("§9클릭 시 이후 페이지로 이동합니다"), 1));



        player.openInventory(inventory);

    }

    @Override
    protected void clickInventory(InventoryClickEvent event) {

    }

    @Override
    protected void closeInventory(InventoryCloseEvent event) {

    }
}
