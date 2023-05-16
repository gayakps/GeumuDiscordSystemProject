package gaya.pe.kr.plugin.player.listener;

import gaya.pe.kr.network.packet.startDirection.client.UpdatePlayerList;
import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.answer.packet.client.AnswerModifyRequest;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.client.TargetQAUserDataRequest;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.packet.client.QuestionModifyRequest;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void syncComplete(PlayerJoinEvent event) {
        NetworkManager networkManager = NetworkManager.getInstance();
        Player player = event.getPlayer();
        networkManager.sendPacket(new UpdatePlayerList(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList())));

        ConfigOption configOption = OptionManager.getInstance().getConfigOption();

        SchedulerUtil.runLaterTask( ()-> {

            if ( !player.isOnline() ) return;

            TargetQAUserDataRequest targetQAUserDataRequest = new TargetQAUserDataRequest( new String[] {player.getName()} , player, true);
            networkManager.sendDataExpectResponse(targetQAUserDataRequest, player, QAUser[].class, (player1, qaUsers) -> {

                QARepository qaRepository = QAManager.getInstance().getQaRepository();

                QAUser qaUser = qaUsers[0];

                List<Question> questionList = qaRepository.getQAUserQuestions(qaUser); // 내가 질문했던 것 중에
                List<Answer> notReceivedAnswers = new ArrayList<>();

                for (Question question : questionList) {
                    if ( question.isAnswer() ) { // 답변이 완료되었고
                        for (Answer answer : qaRepository.getAllAnswers()) { // 모든 답변 중에
                            if ( answer.getQuestionId() == question.getId() ) { // 내 질문에 대한 답변 중
                                if ( !answer.isReceivedToQuestionPlayer() ) { // 질문자에게 답변 알람이 가지 않은것들을
                                    notReceivedAnswers.add(answer);
                                    answer.setReceivedToQuestionPlayer(true);
                                }
                            }
                        }
                    }
                }

                if ( !notReceivedAnswers.isEmpty() ) {

                    int answerReceivedTitleFadeInTime = configOption.getAnswerReceiveTitleFadeInTime();
                    int answerReceivedTitleFadeOutTime = configOption.getAnswerReceiveTitleFadeOutTime();
                    int answerReceivedTitleFadeStayTime = configOption.getAnswerReceiveTitleStayTime();

                    AnswerModifyRequest answerModifyRequest = new AnswerModifyRequest(QAModifyType.MODIFY, notReceivedAnswers.toArray(new Answer[0]));
                    networkManager.sendPacket(answerModifyRequest, player, player2 -> GeumuDiscordSystem.msg(player2, ""));

                    int count = notReceivedAnswers.size();
                    GeumuDiscordSystem.msg(player, configOption.getAnswerReceiveSuccessIfQuestionerOfflineAndJoinAfter().replace("%arrived_answer_count%", Integer.toString(count)));
                    player.sendTitle(configOption.getAnswerReceiveSuccessIfQuestionerOfflineAndJoinAfterTitle().replace("%arrived_answer_count%", Integer.toString(count)), configOption.getAnswerReceiveSuccessIfQuestionerOnlineSubtitle(), answerReceivedTitleFadeInTime, answerReceivedTitleFadeStayTime, answerReceivedTitleFadeOutTime);

                }

            });
        }, configOption.getAnswerAnnouncerDelayWhenLogIn()*20);



    }

}
