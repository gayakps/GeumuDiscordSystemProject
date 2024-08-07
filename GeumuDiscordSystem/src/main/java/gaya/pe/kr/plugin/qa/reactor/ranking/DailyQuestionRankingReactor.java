package gaya.pe.kr.plugin.qa.reactor.ranking;

import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.qa.data.QARankingResult;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.reactor.TargetPlayerQuestionListReactor;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.qa.service.QARankingService;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.plugin.util.ItemCreator;
import gaya.pe.kr.plugin.util.MinecraftInventoryReactor;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.option.data.options.gui.AnswerRankingOption;
import gaya.pe.kr.util.option.data.options.gui.CommonlyUsedButtonOption;
import gaya.pe.kr.util.option.data.options.gui.QuestionRankingOption;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DailyQuestionRankingReactor extends MinecraftInventoryReactor {

    int page = 1;
    int totalPage = 1;

    QARepository qaRepository;
    List<QAUser> qaUsers;

    HashMap<Integer, QAUser> qaUserHashMap = new LinkedHashMap<>();

    public DailyQuestionRankingReactor(Player player, List<QAUser> qaUsers, QARepository qaRepository) {
        super(player, qaRepository);
        this.qaUsers = qaUsers;
    }


    public void open() {

        int startIndex = (page-1) * 36;
        int lastIndex = (page * 36);

        List<QARankingResult<Question>> questionQARankingResult = QARankingService.calculateRankings(qaUsers, getQaRepository().getAllQuestions());

        totalPage = ( questionQARankingResult.size() / 36 ) + 1;

        int size = questionQARankingResult.size();

        if ( startIndex > lastIndex || page < 1 || size < startIndex ) {
            GeumuDiscordSystem.msg(getPlayer(), "&f[&c&l!&f] 접근할 수 없는 페이지 입니다");
            return;
        }

        if ( questionQARankingResult.isEmpty() ) {
            GeumuDiscordSystem.msg(getPlayer(), "&f[&c&l!&f] 데이터가 존재하지 않습니다");
            return;
        }

        OptionManager optionManager = OptionManager.getInstance();
        QuestionRankingOption questionRankingOption = optionManager.getQuestionRankingOption();

        Inventory inventory = Bukkit.createInventory(null, 54, String.format("질문 랭킹 - %d / %d", page, totalPage));

        int inventoryIndex = 9;
        for ( int index = startIndex; index < lastIndex; index++ ) {
            if ( index < size ) {

                QARankingResult<Question> qaRankingResult = questionQARankingResult.get(index);

                QAUser qaUser = qaRankingResult.getQaUser();
                List<String> lore = new ArrayList<>();


                for (String s : questionRankingOption.getQuestionRankingInfoLore()) {
                    lore.add(
                            s
                                    .replace("%question_count_yesterday%", Integer.toString(qaRankingResult.getYesterdayCount()))
                                    .replace("%question_count_daily%", Integer.toString(qaRankingResult.getDailyCount()))
                                    .replace("%question_count_weekly%", Integer.toString(qaRankingResult.getWeeklyCount()))
                                    .replace("%question_count_monthly%", Integer.toString(qaRankingResult.getMonthlyCount()))
                                    .replace("%question_count_total%", Integer.toString(qaRankingResult.getTotalCount()))
                    );
                }

                ItemStack head = ItemCreator.getPlayerHead(
                        qaRankingResult.getQaUser().getGamePlayerName()
                        , questionRankingOption.getQuestionRankingInfoName().replace("%playername%", qaUser.getGamePlayerName())
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

        inventory.setItem(1, ItemCreator.createItemStack(Material.RED_STAINED_GLASS, questionRankingOption.getDailyQuestionRanking()));
        inventory.setItem(3, ItemCreator.createItemStack(Material.BLUE_STAINED_GLASS, questionRankingOption.getWeeklyQuestionRanking()));
        inventory.setItem(5, ItemCreator.createItemStack(Material.GREEN_STAINED_GLASS, questionRankingOption.getMonthlyQuestionRanking()));
        inventory.setItem(7, ItemCreator.createItemStack(Material.YELLOW_STAINED_GLASS, questionRankingOption.getTotalQuestionRanking()));

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
                        TargetPlayerQuestionListReactor targetPlayerQuestionListReactor = new TargetPlayerQuestionListReactor(getPlayer(), qaUser, getQaRepository());
                        targetPlayerQuestionListReactor.start();
                    }, 1);


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
