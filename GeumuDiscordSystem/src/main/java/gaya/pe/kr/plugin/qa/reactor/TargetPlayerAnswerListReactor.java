package gaya.pe.kr.plugin.qa.reactor;

import gaya.pe.kr.plugin.discord.manager.BukkitDiscordManager;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.reactor.ranking.WeeklyAnswerRankingReactor;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.plugin.util.ItemCreator;
import gaya.pe.kr.plugin.util.MinecraftInventoryReactor;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.client.AllQAUserDataRequest;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.PlayerAnswerListOption;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gaya.pe.kr.plugin.qa.service.AnswerRankingService.countAnswersForUser;
import static gaya.pe.kr.plugin.qa.service.AnswerRankingService.getAnswerCountMap;

public class TargetPlayerAnswerListReactor extends MinecraftInventoryReactor {


    List<Answer> targetPlayerAnswers = new ArrayList<>();
    QARepository qaRepository;
    QAUser targetPlayerQAUser;

    int page = 1;
    int totalPage = 1;

    public TargetPlayerAnswerListReactor(Player player,QAUser targetPlayerQAUser, QARepository qaRepository) {
        super(player);
        this.targetPlayerQAUser = targetPlayerQAUser;
        targetPlayerAnswers = qaRepository.getQAUserAnswers(targetPlayerQAUser);
    }

    @Override
    protected void init() {
        open();
    }

    public void open() {

        getPlayer().closeInventory();

        int startIndex = (page-1) * 36;
        int lastIndex = (page * 36);

        totalPage = ( targetPlayerAnswers.size() / 36 ) + 1;

        int size = targetPlayerAnswers.size();

        if ( startIndex > lastIndex || page < 1 || size < startIndex ) {
            getPlayer().sendMessage("§c접근할 수 없는 페이지 입니다");
            return;
        }

        if ( targetPlayerAnswers.isEmpty() ) {
            getPlayer().sendMessage("§c질문한 목록이 없습니다");
            return;
        }

        QAUser targetPlayerQAUser = targetPlayerAnswers.get(0).getAnswerPlayer();

        OptionManager optionManager = OptionManager.getInstance();

        ConfigOption configOption = optionManager.getConfigOption();

        PlayerAnswerListOption playerAnswerListOption = optionManager.getPlayerAnswerListOption();

        Inventory inventory = Bukkit.createInventory(null, 54, configOption.getPlayerAnswerListTitle()
                .replace("%playername%", targetPlayerQAUser.getGamePlayerName())
                .replace("%current_page%", Integer.toString(page))
                .replace("%total_page%", Integer.toString(totalPage))
        );

        int inventoryIndex = 0;
        for ( int index = startIndex; index < lastIndex; index++ ) {
            if ( index < size ) {

                Answer targetAnswer = targetPlayerAnswers.get(index);

                SimpleDateFormat simpleDateFormat = TimeUtil.getSimpleDateFormat();
                ItemStack itemStack;

                List<String> lore = new ArrayList<>();

                Question targetQuestion = null;

                for (Question question : qaRepository.getAllQuestions()) {
                    if ( targetAnswer.getQuestionId() == question.getId() ) {
                        targetQuestion = question;
                        break;
                    }
                }


                String itemName = playerAnswerListOption.getAnsweredQuestionName().replace("%question_content%", targetQuestion.getContents());

                //    @RequirePlaceHolder( placeholders =
                //    {"%answer_content%", "%question_number%", "%question_playername%", "%question_time%", "%answer_time%", "%answer_playername%"} )
                for (String s : playerAnswerListOption.getAnsweredQuestionLore()) {
                    lore.add(s
                            .replace("%answer_content%", targetAnswer.getContents())
                            .replace("%question_number%", Long.toString(targetQuestion.getId()))
                            .replace("%question_playername%", BukkitDiscordManager.getInstance().getFullName(targetQuestion.getQaUser()))
                            .replace("%question_time%", simpleDateFormat.format(targetQuestion.getQuestionDate()))
                            .replace("%answer_time%", simpleDateFormat.format(targetAnswer.getAnswerDate()))
                            .replace("%answer_playername%", BukkitDiscordManager.getInstance().getFullName(targetAnswer.getAnswerPlayer()))
                    );
                }

                itemStack = ItemCreator.createItemStack(Material.RED_WOOL,
                        itemName,
                        lore
                );


                inventory.setItem(inventoryIndex, itemStack);
                inventoryIndex++;

            } else {
                break;
            }
        }

        setUpDefaultPoketmonInventory(inventory);

        if (PermissionLevelType.getPermissionLevelType(getPlayer()).equals(PermissionLevelType.STAFF) ) {

            List<String> lore = new ArrayList<>();

            //주간 답변수 랭킹

            LocalDate today = LocalDate.now();

            LocalDate weekStart = today.minusDays(6);
            Map<QAUser, Integer> answerCountMap = getAnswerCountMap(qaRepository.getAllAnswers(), weekStart, today);

            List<Map.Entry<QAUser, Integer>> sortedEntries = answerCountMap.entrySet().stream()
                    .sorted(Map.Entry.<QAUser, Integer>comparingByValue().reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            System.out.println("일간 질문 수 랭킹:");

            for (int i = 0; i < sortedEntries.size(); i++) {
                // 1등 부터 5등까지
                Map.Entry<QAUser, Integer> entry = sortedEntries.get(i);
                //            "%question_top_player_daily_1%", "%question_top_count_daily_1%"
                //            ,"%question_top_player_daily_2%", "%question_top_count_daily_2%"
                //            ,"%question_top_player_daily_3%", "%question_top_count_daily_3%"
                //            ,"%question_top_player_daily_4%", "%question_top_count_daily_4%"
                //            ,"%question_top_player_daily_5%", "%question_top_count_daily_5%"

                String rank = Integer.toString(i+1);

                lore.add(playerAnswerListOption.getWeeklyAnswerRankingLore().get(i)
                        .replace("%answer_top_player_weekly_"+rank+"%", entry.getKey().getGamePlayerName())
                        .replace("%answer_top_count_weekly_"+rank+"%", Integer.toString(entry.getValue()))
                );


                System.out.printf("%d. %s - %d 답변\n", i + 1, entry.getKey().getGamePlayerName(), entry.getValue());
            }

            // Material pixelmon:arc_chalice
            ItemStack index45Item = ItemCreator.createItemStack(Material.GOLDEN_SWORD, playerAnswerListOption.getWeeklyAnswerRankingName(), lore);
            inventory.setItem(45, index45Item);
        }


        {
            List<String> index49ItemLore = new ArrayList<>();

            //    @RequirePlaceHolder(placeholders =
            //    {"%question_count_yesterday%", "%question_count_daily%", "%question_count_weekly%", "%question_count_monthly%", "%question_count_total%"})

            int receivedRewardCount = targetPlayerQAUser.getRewardAmount();
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate weekStart = today.minusWeeks(1);
            LocalDate monthStart = today.minusMonths(1);

            int yesterdayQuestions = countAnswersForUser(targetPlayerAnswers, yesterday, today.minusDays(1));
            int dailyQuestions = countAnswersForUser(targetPlayerAnswers, today, today);
            int weeklyQuestions =countAnswersForUser(targetPlayerAnswers, weekStart, today);
            int monthlyQuestions = countAnswersForUser(targetPlayerAnswers, monthStart, today);
            int totalQuestions =countAnswersForUser(targetPlayerAnswers, LocalDate.MIN, LocalDate.MAX);

            System.out.println("어제 답변 수: " + yesterdayQuestions);
            System.out.println("일간 답변 수: " + dailyQuestions);
            System.out.println("주간 답변 수: " + weeklyQuestions);
            System.out.println("월간 답변 수: " + monthlyQuestions);
            System.out.println("전체 답변 질문 수: " + totalQuestions);

            //    @RequirePlaceHolder( placeholders = {"%answer_count_yesterday%"
            //    , "%answer_count_daily%", "%answer_count_weekly%", "%answer_count_monthly%", "%answer_count_total%", "%reward_count%"})
            for (String s : playerAnswerListOption.getPlayerAnswerInfoLore()) {
                index49ItemLore.add( s
                        .replace("%answer_count_yesterday%", Integer.toString(yesterdayQuestions))
                        .replace("%answer_count_daily%", Integer.toString(dailyQuestions))
                        .replace("%answer_count_weekly%", Integer.toString(weeklyQuestions))
                        .replace("%answer_count_monthly%", Integer.toString(monthlyQuestions))
                        .replace("%answer_count_total%", Integer.toString(totalQuestions))
                        .replace("%reward_count%", Integer.toString(receivedRewardCount))
                );
            }


            ItemStack index49Item = ItemCreator.createItemStack(Material.PAPER, playerAnswerListOption.getPlayerAnswerInfoName()
                            .replace("%playername%", targetPlayerQAUser.getGamePlayerName())
                    , index49ItemLore);

            inventory.setItem(49, index49Item);
        }



        setInventory(inventory);
        getPlayer().openInventory(inventory);

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
            case 45: {
                getPlayer().closeInventory();
                NetworkManager.getInstance().sendDataExpectResponse(new AllQAUserDataRequest(getPlayer()), getPlayer(), QAUser[].class, (player, qaUsers) -> {
                    WeeklyAnswerRankingReactor weeklyAnswerRankingReactor = new WeeklyAnswerRankingReactor(getPlayer(), Arrays.asList(qaUsers), qaRepository);
                    weeklyAnswerRankingReactor.open();
                });
                break;
            }
        }

    }

    @Override
    protected void closeInventory(InventoryCloseEvent event) {

    }
}
