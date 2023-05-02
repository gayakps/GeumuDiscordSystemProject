package gaya.pe.kr.plugin.qa.reactor;

import gaya.pe.kr.plugin.discord.manager.BukkitDiscordManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.plugin.util.ItemCreator;
import gaya.pe.kr.plugin.util.MinecraftInventoryReactor;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.AllQuestionAnswers;
import gaya.pe.kr.qa.data.QA;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.CommonlyUsedButtonOption;
import gaya.pe.kr.util.option.data.options.gui.PlayerAnswerListOption;
import gaya.pe.kr.util.option.data.options.gui.PlayerQuestionListOption;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static gaya.pe.kr.plugin.qa.service.QuestionRankingService.*;

public class TargetPlayerQuestionListReactor extends MinecraftInventoryReactor {


    List<Question> targetPlayerQuestions;
    QARepository qaRepository;

    int page = 1;
    int totalPage = 1;

    public TargetPlayerQuestionListReactor(Player player, QAUser targetQAUser, QARepository qaRepository) {
        super(player);
        this.qaRepository = qaRepository;
        this.targetPlayerQuestions = qaRepository.getQAUserQuestions(targetQAUser);

    }

    @Override
    protected void init() {

    }

    public void open() {

        getPlayer().closeInventory();

        int startIndex = (page-1) * 36;
        int lastIndex = (page * 36);

        totalPage = ( targetPlayerQuestions.size() / 36 ) + 1;

        int size = targetPlayerQuestions.size();

        if ( startIndex > lastIndex || page < 1 || size < startIndex ) {
            getPlayer().sendMessage("§c접근할 수 없는 페이지 입니다");
            return;
        }

        if ( targetPlayerQuestions.isEmpty() ) {
            getPlayer().sendMessage("§c질문한 목록이 없습니다");
            return;
        }

        QAUser targetPlayerQAUser = targetPlayerQuestions.get(0).getQaUser();

        OptionManager optionManager = OptionManager.getInstance();

        ConfigOption configOption = optionManager.getConfigOption();

        //    @RequirePlaceHolder(placeholders = {"%playername%", "%current_page%", "%total_page%"})

        PlayerAnswerListOption playerAnswerListOption = optionManager.getPlayerAnswerListOption();
        PlayerQuestionListOption playerQuestionListOption = optionManager.getPlayerQuestionListOption();
        CommonlyUsedButtonOption commonlyUsedButtonOption = optionManager.getCommonlyUsedButtonOption();

        List<Answer> answerList = qaRepository.getAllAnswers();

        Comparator<Question> answerComparator = (q1, q2) -> {
            if (q1.isAnswer() == q2.isAnswer()) {
                return 0;
            }
            return q1.isAnswer() ? -1 : 1;
        };

        targetPlayerQuestions.sort(answerComparator);

        Inventory inventory = Bukkit.createInventory(null, 54, configOption.getPlayerQuestionListTitle()
                .replace("%playername%", targetPlayerQAUser.getGamePlayerName())
                .replace("%current_page%", Integer.toString(page))
                .replace("%total_page%", Integer.toString(totalPage))
        );

        int inventoryIndex = 0;
        for ( int index = startIndex; index < lastIndex; index++ ) {
            if ( index < size ) {
                Question question = targetPlayerQuestions.get(index);

                SimpleDateFormat simpleDateFormat = TimeUtil.getSimpleDateFormat();
                ItemStack itemStack;

                List<String> lore = new ArrayList<>();

                if ( question.isAnswer() ) {

                    String itemName = playerQuestionListOption.getPlayerQuestionListAnsweredQuestionName().replace("%question_content%", question.getContents());

                    Answer targetAnswer = null;

                    for (Answer answer : answerList) {
                        if ( answer.getQuestionId() == question.getId() ) {
                            targetAnswer = answer;
                            break;
                        }
                    }

                    for (String s : playerQuestionListOption.getPlayerQuestionListAnsweredQuestionLore()) {
                        lore.add(s
                                .replace("%answer_content%", targetAnswer.getContents())
                                .replace("%question_number%", Long.toString(question.getId()))
                                .replace("%question_playername%", BukkitDiscordManager.getInstance().getFullName(question.getQaUser()))
                                .replace("%question_time%", simpleDateFormat.format(question.getQuestionDate()))
                                .replace("%answer_time%", simpleDateFormat.format(targetAnswer.getAnswerDate()))
                                .replace("%answer_playername%", BukkitDiscordManager.getInstance().getFullName(targetAnswer.getAnswerPlayer()) )
                        );
                    }

                    itemStack = ItemCreator.createItemStack(Material.RED_WOOL,
                            itemName,
                            lore
                    );

                } else {

                    String itemName = playerQuestionListOption.getPlayerQuestionListRemainQuestionName().replace("%question_content%", question.getContents());

                    for (String s : playerQuestionListOption.getPlayerQuestionListRemainQuestionLore()) {
                        lore.add(s
                                .replace("%question_number%", Long.toString(question.getId()))
                                .replace("%question_playername%", BukkitDiscordManager.getInstance().getFullName(question.getQaUser()))
                                .replace("%question_time%", simpleDateFormat.format(question.getQuestionDate()))
                        );
                    }

                    itemStack = ItemCreator.createItemStack(Material.GREEN_WOOL, itemName, lore);

                }

                inventory.setItem(inventoryIndex, itemStack);
                inventoryIndex++;
            } else {
                break;
            }
        }

        setUpDefaultPoketmonInventory(inventory);

        if (PermissionLevelType.getPermissionLevelType(getPlayer()).equals(PermissionLevelType.STAFF) ) {

            List<String> lore = new ArrayList<>();

            //TODO 일간 질문수 랭킹

            LocalDate startDate = LocalDate.now();

            Map<QAUser, Integer> questionCountMap = getQuestionCountMap(qaRepository.getAllQuestions(), startDate, startDate);

            List<Map.Entry<QAUser, Integer>> sortedEntries = questionCountMap.entrySet().stream()
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

                    lore.add(playerQuestionListOption.getPlayerQuestionListDailyQuestionRankingLore().get(i)
                            .replace("%question_top_player_daily_"+rank+"%", entry.getKey().getGamePlayerName())
                            .replace("%question_top_count_daily_"+rank+"%", Integer.toString(entry.getValue()))
                    );


                System.out.printf("%d. %s - %d 질문\n", i + 1, entry.getKey().getGamePlayerName(), entry.getValue());
            }

            // Material pixelmon:arc_chalice
            ItemStack index45Item = ItemCreator.createItemStack(Material.GOLDEN_SWORD, playerQuestionListOption.getPlayerQuestionListDailyQuestionRankingName(), lore);
            inventory.setItem(45, index45Item);
        }


        {
            List<String> index49ItemLore = new ArrayList<>();

            //    @RequirePlaceHolder(placeholders =
            //    {"%question_count_yesterday%", "%question_count_daily%", "%question_count_weekly%", "%question_count_monthly%", "%question_count_total%"})

            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate weekStart = today.minusWeeks(1);
            LocalDate monthStart = today.minusMonths(1);

            int yesterdayQuestions = countQuestionsForUser(targetPlayerQuestions, yesterday, today.minusDays(1));
            int dailyQuestions = countQuestionsForUser(targetPlayerQuestions, today, today);
            int weeklyQuestions = countQuestionsForUser(targetPlayerQuestions, weekStart, today);
            int monthlyQuestions = countQuestionsForUser(targetPlayerQuestions, monthStart, today);
            int totalQuestions = countQuestionsForUser(targetPlayerQuestions, LocalDate.MIN, LocalDate.MAX);

            for (String s : playerQuestionListOption.getPlayerQuestionListPlayerQuestionInfoLore()) {
                index49ItemLore.add( s
                        .replace("%question_count_yesterday%", Integer.toString(yesterdayQuestions))
                        .replace("%question_count_daily%", Integer.toString(dailyQuestions))
                        .replace("%question_count_weekly%", Integer.toString(weeklyQuestions))
                        .replace("%question_count_monthly%", Integer.toString(monthlyQuestions))
                        .replace("%question_count_total%", Integer.toString(totalQuestions))
                );
            }


            ItemStack index49Item = ItemCreator.createItemStack(Material.PAPER, playerQuestionListOption.getPlayerQuestionListPlayerQuestionInfoName()
                            .replace("%playername%", targetPlayerQAUser.getGamePlayerName())
                    , index49ItemLore);

            inventory.setItem(49, index49Item);
        }


        setInventory(inventory);

        getPlayer().openInventory(inventory);

    }

    @Override
    protected void clickInventory(InventoryClickEvent event) {

    }

    @Override
    protected void closeInventory(InventoryCloseEvent event) {

    }
}
