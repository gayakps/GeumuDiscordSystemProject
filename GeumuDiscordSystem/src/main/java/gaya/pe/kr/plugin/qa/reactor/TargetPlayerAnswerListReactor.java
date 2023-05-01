package gaya.pe.kr.plugin.qa.reactor;

import gaya.pe.kr.plugin.discord.manager.BukkitDiscordManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.plugin.util.ItemCreator;
import gaya.pe.kr.plugin.util.MinecraftInventoryReactor;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.AllQuestionAnswers;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.CommonlyUsedButtonOption;
import gaya.pe.kr.util.option.data.options.gui.PlayerAnswerListOption;
import gaya.pe.kr.util.option.data.options.gui.PlayerQuestionListOption;
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
import java.util.Comparator;
import java.util.List;

public class TargetPlayerAnswerListReactor extends MinecraftInventoryReactor {


    List<Answer> targetPlayerAnswers = new ArrayList<>();
    AllQuestionAnswers allQuestionAnswers;
    String targetPlayerName;

    int page = 1;
    int totalPage = 1;

    public TargetPlayerAnswerListReactor(Player player, AllQuestionAnswers allQuestionAnswers, String targetPlayerName) {
        super(player);

        for (Answer answer : allQuestionAnswers.getAnswerList()) {
            if ( answer.getAnswerPlayer().getGamePlayerName().equals(targetPlayerName) ) {
                this.targetPlayerAnswers.add(answer);
            }
        }
        this.allQuestionAnswers = allQuestionAnswers;
        this.targetPlayerName = targetPlayerName;
    }

    @Override
    protected void init() {

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

        //    @RequirePlaceHolder(placeholders = {"%playername%", "%current_page%", "%total_page%"})

        PlayerAnswerListOption playerAnswerListOption = optionManager.getPlayerAnswerListOption();
        PlayerQuestionListOption playerQuestionListOption = optionManager.getPlayerQuestionListOption();
        CommonlyUsedButtonOption commonlyUsedButtonOption = optionManager.getCommonlyUsedButtonOption();

        Inventory inventory = Bukkit.createInventory(null, 54, configOption.getPlayerAnswerListTitle()
                .replace("%playername%", targetPlayerName)
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

                for (Question question : allQuestionAnswers.getQuestions()) {
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
            ItemStack index45Item = ItemCreator.createItemStack(Material.GOLDEN_SWORD, playerQuestionListOption.getPlayerQuestionListDailyQuestionRankingName());
            //TODO DB에 접속해서 데이터를 받아와야함
            // Material pixelmon:arc_chalice
            inventory.setItem(45, index45Item);
        }


        {
            List<String> index49ItemLore = new ArrayList<>();

            //    @RequirePlaceHolder(placeholders =
            //    {"%question_count_yesterday%", "%question_count_daily%", "%question_count_weekly%", "%question_count_monthly%", "%question_count_total%"})

            int receivedRewardCount = 0;

            for (Answer answer : allQuestionAnswers.getAnswerList()) {
                if ( answer.getAnswerPlayer().getGamePlayerName().equals(getPlayer().getName()) ) {
                    if ( !answer.isReceivedReward() ) {
                        receivedRewardCount++;
                    }
                }
            }

            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate weekStart = today.minusWeeks(1);
            LocalDate monthStart = today.minusMonths(1);

            QAManager qaManager = QAManager.getInstance();

            int yesterdayQuestions = qaManager.countAnswersForUser(targetPlayerAnswers, yesterday, today.minusDays(1));
            int dailyQuestions = qaManager.countAnswersForUser(targetPlayerAnswers, today, today);
            int weeklyQuestions = qaManager.countAnswersForUser(targetPlayerAnswers, weekStart, today);
            int monthlyQuestions = qaManager.countAnswersForUser(targetPlayerAnswers, monthStart, today);
            int totalQuestions = qaManager.countAnswersForUser(targetPlayerAnswers, LocalDate.MIN, LocalDate.MAX);

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
                            .replace("%playername%", targetPlayerName)
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
