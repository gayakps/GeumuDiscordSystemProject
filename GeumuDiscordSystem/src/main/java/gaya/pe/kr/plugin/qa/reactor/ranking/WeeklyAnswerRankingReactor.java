package gaya.pe.kr.plugin.qa.reactor.ranking;

import gaya.pe.kr.network.packet.startDirection.server.response.ServerOption;
import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.discord.manager.BukkitDiscordManager;
import gaya.pe.kr.plugin.qa.data.QARankingResult;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.reactor.TargetPlayerAnswerListReactor;
import gaya.pe.kr.plugin.qa.reactor.TargetPlayerQuestionListReactor;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.qa.service.AnswerRankingService;
import gaya.pe.kr.plugin.qa.service.QARankingService;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.plugin.util.ItemCreator;
import gaya.pe.kr.plugin.util.MinecraftInventoryReactor;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.AnswerRankingOption;
import gaya.pe.kr.util.option.data.options.gui.CommonlyUsedButtonOption;
import gaya.pe.kr.util.option.data.options.gui.PlayerAnswerListOption;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

public class WeeklyAnswerRankingReactor extends MinecraftInventoryReactor {

    int page = 1;
    int totalPage = 1;

    List<QAUser> qaUsers;

    HashMap<Integer, QAUser> qaUserHashMap = new LinkedHashMap<>();

    public WeeklyAnswerRankingReactor(Player player, List<QAUser> qaUsers, QARepository qaRepository) {
        super(player, qaRepository);
        this.qaUsers = qaUsers;
    }

    public void open() {

        int startIndex = (page-1) * 36;
        int lastIndex = (page * 36);

        List<QARankingResult<Answer>> answerQARankingResult = QARankingService.calculateRankings(qaUsers, getQaRepository().getAllAnswers());

        totalPage = ( answerQARankingResult.size() / 36 ) + 1;

        int size = answerQARankingResult.size();

        if ( startIndex > lastIndex || page < 1 || size < startIndex ) {
            GeumuDiscordSystem.msg(getPlayer(), "&f[&c&l!&f] 접근할 수 없는 페이지 입니다");
            return;
        }

        if ( answerQARankingResult.isEmpty() ) {
            GeumuDiscordSystem.msg(getPlayer(), "&f[&c&l!&f] 데이터가 존재하지 않습니다");
            return;
        }

        OptionManager optionManager = OptionManager.getInstance();
        AnswerRankingOption answerRankingOption = optionManager.getAnswerRankingOption();

        Inventory inventory = Bukkit.createInventory(null, 54, String.format("답변 랭킹 - %d / %d", page, totalPage));

        int inventoryIndex = 9;
        for ( int index = startIndex; index < lastIndex; index++ ) {
            if ( index < size ) {

                QARankingResult<Answer> qaRankingResult = answerQARankingResult.get(index);

                QAUser qaUser = qaRankingResult.getQaUser();
                List<String> lore = new ArrayList<>();

                /**
                 * PlaceHolder
                 - "어제 답변수: %answer_count_yesterday%"
                 - "일간 답변수: %answer_count_daily%"
                 - "주간 답변수: %answer_count_weekly%"
                 - "월간 답변수: %answer_count_monthly%"
                 - "전체 기간 답변수: %answer_count_total%"
                 */

                for (String s : answerRankingOption.getAnswerRankingLore()) {
                   lore.add(
                           s
                                   .replace("%answer_count_yesterday%", Integer.toString(qaRankingResult.getYesterdayCount()))
                                   .replace("%answer_count_daily%", Integer.toString(qaRankingResult.getDailyCount()))
                                   .replace("%answer_count_weekly%", Integer.toString(qaRankingResult.getWeeklyCount()))
                                   .replace("%answer_count_monthly%", Integer.toString(qaRankingResult.getMonthlyCount()))
                                   .replace("%answer_count_total%", Integer.toString(qaRankingResult.getTotalCount()))
                   );
                }

                ItemStack head = ItemCreator.getPlayerHead(
                        qaRankingResult.getQaUser().getGamePlayerName()
                        , answerRankingOption.getAnswerRankingInfoName().replace("%playername%", qaUser.getGamePlayerName())
                                .replace("%ranking%", Integer.toString(qaRankingResult.getRank()))
                        , lore
                );

                qaUserHashMap.put(inventoryIndex, qaUser);
                inventory.setItem(inventoryIndex, head);
                inventoryIndex++;

            } else {
                break;
            }
        }

        CommonlyUsedButtonOption commonlyUsedButtonOption = optionManager.getCommonlyUsedButtonOption();

        for ( int index = 0; index < 9; index++ ) {
            inventory.setItem(index, ItemCreator.createItemStack(Material.BLACK_STAINED_GLASS, ""));
        }

        inventory.setItem(1, ItemCreator.createItemStack(Material.RED_STAINED_GLASS_PANE, answerRankingOption.getDailyAnswerRanking()));
        inventory.setItem(3, ItemCreator.createItemStack(Material.BLUE_STAINED_GLASS, answerRankingOption.getWeeklyAnswerRanking()));
        inventory.setItem(5, ItemCreator.createItemStack(Material.GREEN_STAINED_GLASS, answerRankingOption.getMonthlyAnswerRanking()));
        inventory.setItem(7, ItemCreator.createItemStack(Material.YELLOW_STAINED_GLASS, answerRankingOption.getTotalAnswerRanking()));

        inventory.setItem(48, ItemCreator.createItemStack(Material.getMaterial(commonlyUsedButtonOption.getPreviousPageButtonType().toUpperCase(Locale.ROOT)), commonlyUsedButtonOption.getPreviousPageButtonName()));
        inventory.setItem(49, ItemCreator.createItemStack(Material.PAPER, ""));
        inventory.setItem(50, ItemCreator.createItemStack(Material.getMaterial(commonlyUsedButtonOption.getNextPageButtonType().toUpperCase(Locale.ROOT)), commonlyUsedButtonOption.getNextPageButtonName()));


        setInventory(inventory);

        SchedulerUtil.runLaterTask( ()-> {
            getPlayer().openInventory(inventory);
        }, 1);


    }

    @Override
    protected void init() {
        open();
    }

    @Override
    protected void clickInventory(InventoryClickEvent event) {

        int clickedSlot = event.getSlot();

        switch ( clickedSlot ) {
            case 48: {
                page--;
                open();
                break;
            }
            case 50: {
                page++;
                open();
                // 다음페이지
                break;
            }
            default: {

                if ( qaUserHashMap.containsKey(clickedSlot) ) {

                    QAUser qaUser = qaUserHashMap.get(clickedSlot);

                    getPlayer().closeInventory();
                    SchedulerUtil.runLaterTask( ()-> {
                        TargetPlayerAnswerListReactor targetPlayerAnswerListReactor = new TargetPlayerAnswerListReactor(getPlayer(), qaUser, getQaRepository());
                        targetPlayerAnswerListReactor.start();
                    },1);

                    close();

                }
                break;
            }
        }


    }

    @Override
    protected void closeInventory(InventoryCloseEvent event) {

    }
}
