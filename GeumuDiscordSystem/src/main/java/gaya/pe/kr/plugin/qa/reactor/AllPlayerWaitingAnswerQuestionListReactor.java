package gaya.pe.kr.plugin.qa.reactor;

import gaya.pe.kr.plugin.discord.manager.BukkitDiscordManager;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.conversation.AnswerConversation;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.reactor.ranking.WeeklyAnswerRankingReactor;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.plugin.util.ItemCreator;
import gaya.pe.kr.plugin.util.MinecraftInventoryReactor;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.client.AllQAUserDataRequest;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.PlayerAnswerListOption;
import gaya.pe.kr.util.option.data.options.gui.WaitingAnswerListOption;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static gaya.pe.kr.plugin.qa.service.AnswerRankingService.countAnswersForUser;
import static gaya.pe.kr.plugin.qa.service.AnswerRankingService.getAnswerCountMap;

public class AllPlayerWaitingAnswerQuestionListReactor extends MinecraftInventoryReactor {


    List<Question> notAnsweredQuestions;
    int page = 1;
    int totalPage = 1;

    HashMap<Integer, Question> hashMap = new LinkedHashMap<>();

    QAUser requestPlayerQAUser;
    public AllPlayerWaitingAnswerQuestionListReactor(Player player, QAUser requestPlayerQAUser, QARepository qaRepository) {
        super(player, qaRepository);
        this.notAnsweredQuestions = qaRepository.getNotAnsweredQuestion();
        this.requestPlayerQAUser = requestPlayerQAUser;
    }

    @Override
    protected void init() {
        open();
    }

    public void open() {

        getPlayer().closeInventory();

        int startIndex = (page - 1) * 36;
        int lastIndex = (page * 36);

        totalPage = (notAnsweredQuestions.size() / 36) + 1;

        int size = notAnsweredQuestions.size();

        if (startIndex > lastIndex || page < 1 || size < startIndex) {
            getPlayer().sendMessage("§c접근할 수 없는 페이지 입니다");
            return;
        }

        if (notAnsweredQuestions.isEmpty()) {
            getPlayer().sendMessage("§c서버에 등록된 질문 목록이 없습니다");
            return;
        }

        OptionManager optionManager = OptionManager.getInstance();
        ConfigOption configOption = optionManager.getConfigOption();

        PlayerAnswerListOption playerAnswerListOption = optionManager.getPlayerAnswerListOption();
        WaitingAnswerListOption waitingAnswerListOption = optionManager.getWaitingAnswerListOption();

        Inventory inventory = Bukkit.createInventory(null, 54, configOption.getWaitingAnswerListTitle()
                .replace("%current_page%", Integer.toString(page))
                .replace("%total_page%", Integer.toString(totalPage))
        );

        int inventoryIndex = 0;
        for (int index = startIndex; index < lastIndex; index++) {
            if (index < size) {
                Question question = notAnsweredQuestions.get(index);

                SimpleDateFormat simpleDateFormat = TimeUtil.getSimpleDateFormat();
                ItemStack itemStack;

                List<String> lore = new ArrayList<>();
                String itemName = waitingAnswerListOption.getWaitingAnswerListRemainQuestionName().replace("%question_content%", question.getContents());

                for (String s : waitingAnswerListOption.getWaitingAnswerListRemainQuestionLore()) {
                    lore.add(s
                            .replace("%question_number%", Long.toString(question.getId()))
                            .replace("%question_playername%", BukkitDiscordManager.getInstance().getFullName(question.getQaUser()))
                            .replace("%question_time%", simpleDateFormat.format(question.getQuestionDate()))
                    );
                }

                itemStack = ItemCreator.createItemStack(Material.GREEN_WOOL, itemName, lore);

                inventory.setItem(inventoryIndex, itemStack);
                hashMap.put(inventoryIndex,question);
                inventoryIndex++;
            } else {
                break;
            }
        }

        setUpDefaultPoketmonInventory(inventory);


        PermissionLevelType permissionLevelType = PermissionLevelType.getPermissionLevelType(getPlayer());

        if ( permissionLevelType.equals(PermissionLevelType.STAFF) || permissionLevelType.equals(PermissionLevelType.ADMIN)) {

            List<String> lore = new ArrayList<>();

            //주간 답변수 랭킹

            LocalDate today = LocalDate.now();

            LocalDate weekStart = today.minusDays(6);
            Map<QAUser, Integer> answerCountMap = getAnswerCountMap(qaRepository.getAllAnswers(), weekStart, today);

            List<Map.Entry<QAUser, Integer>> sortedEntries = answerCountMap.entrySet().stream()
                    .sorted(Map.Entry.<QAUser, Integer>comparingByValue().reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            System.out.println("주간 답변수 수 랭킹:");

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

            List<Answer> answers = qaRepository.getQAUserAnswers(requestPlayerQAUser);
            long receivedRewardCount = answers.stream().filter(answer -> !answer.isReceiveReward()).count();

            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate weekStart = today.minusWeeks(1);
            LocalDate monthStart = today.minusMonths(1);

            int yesterdayQuestions = countAnswersForUser(answers, yesterday, today.minusDays(1));
            int dailyQuestions = countAnswersForUser(answers, today, today);
            int weeklyQuestions = countAnswersForUser(answers, weekStart, today);
            int monthlyQuestions = countAnswersForUser(answers, monthStart, today);
            int totalQuestions = countAnswersForUser(answers, LocalDate.MIN, LocalDate.MAX);

            System.out.println("어제 답변 수: " + yesterdayQuestions);
            System.out.println("일간 답변 수: " + dailyQuestions);
            System.out.println("주간 답변 수: " + weeklyQuestions);
            System.out.println("월간 답변 수: " + monthlyQuestions);
            System.out.println("전체 답변 질문 수: " + totalQuestions);

            for (String s : waitingAnswerListOption.getWaitingAnswerListMyAnswerInfoLore() ) {
                index49ItemLore.add(s
                        .replace("%answer_count_yesterday%", Integer.toString(yesterdayQuestions))
                        .replace("%answer_count_daily%", Integer.toString(dailyQuestions))
                        .replace("%answer_count_weekly%", Integer.toString(weeklyQuestions))
                        .replace("%answer_count_monthly%", Integer.toString(monthlyQuestions))
                        .replace("%answer_count_total%", Integer.toString(totalQuestions))
                        .replace("%reward_count%", Long.toString(receivedRewardCount))
                );
            }


            ItemStack index49Item = ItemCreator.createItemStack(Material.PAPER, waitingAnswerListOption.getWaitingAnswerListMyAnswerInfoName()
                            .replace("%playername%", getPlayer().getName())
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
                        weeklyAnswerRankingReactor.start();
                    });

                break;
            }
            default: {

                if ( hashMap.containsKey(clickedSlot) ) {
                    Question question = hashMap.get(clickedSlot);
                    AnswerConversation answerConversation = new AnswerConversation(question, getPlayer());
                    AnswerConversation.startConversation(answerConversation, getPlayer());
                }

            }
        }
    }

    @Override
    protected void closeInventory(InventoryCloseEvent event) {

    }

}
