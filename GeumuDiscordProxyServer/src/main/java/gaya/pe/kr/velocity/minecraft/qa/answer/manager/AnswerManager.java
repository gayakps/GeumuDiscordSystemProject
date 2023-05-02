package gaya.pe.kr.velocity.minecraft.qa.answer.manager;

import com.velocitypowered.api.proxy.Player;
import gaya.pe.kr.network.packet.startDirection.server.non_response.BroadCastMessage;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.answer.packet.client.PlayerTransientProceedingAnswerRequest;
import gaya.pe.kr.qa.answer.packet.server.ExpectQuestionAnswerResponse;
import gaya.pe.kr.qa.data.QARequestResult;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.server.BukkitAnswerModify;
import gaya.pe.kr.qa.packet.server.BukkitQuestionModify;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.exception.NonExistQuestionException;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.velocity.database.DBConnection;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.network.manager.NetworkManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.player.PlayerListHandler;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.dv8tion.jda.api.entities.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AnswerManager {


    private static class SingleTon {
        private static final AnswerManager ANSWER_MANAGER = new AnswerManager();
    }

    public static AnswerManager getInstance() {
        return SingleTon.ANSWER_MANAGER;
    }


    HashMap<Long, Answer> answerIdByAnswerHashMap = new HashMap<>();
    HashMap<QAUser, List<Answer>> qaUserHasAnswer = new HashMap<>();
    ServerOptionManager serverOptionManager = ServerOptionManager.getInstance();

    QuestionManager questionManager = QuestionManager.getInstance();

    public void init() {

    }

    private void answer(QARequestResult qaRequestResult, Question question, Answer answer) {

        //TODO Question 에 대상 보상도 존재하기 떄문에
        //TODO 디스코드 및 특정 채널에 메세지를 보내줘야함.

        DiscordManager discordManager = DiscordManager.getInstance();
        Message message = discordManager.sendMessage( String.format("%s\n%s",questionManager.getQuestionFormat(question), getAnswerFormat(answer)) , discordManager.getAuthChannel() );

        question.setAnswer(true);
        question.setDiscordMessageId(message.getIdLong());

        QAUser questionUser = question.getQaUser();
        QAUser answerUser = answer.getAnswerPlayer();

        Player questionerPlayer = null;

        String questionerGamePlayerName = questionUser.getGamePlayerName();

        if ( questionerGamePlayerName != null ) {
            for (Player allPlayer : VelocityThreadUtil.getServer().getAllPlayers()) {
                String playerName = allPlayer.getGameProfile().getName();

                if ( playerName.equals(questionerGamePlayerName) ) {
                    //질문자가 온라인일경우
                    questionerPlayer =  allPlayer;
                    break;
                }

            }
        }

        Player finalQuestionerPlayer = questionerPlayer;
        boolean databaseResult = DBConnection.taskTransaction(connection -> {
            //TODO DB에 데이터 삽입

            ConfigOption configOption = serverOptionManager.getConfigOption();

            AnswerManager answerManager = AnswerManager.getInstance();

            int answerCountTotal = answerManager.getQAUserAnswers(answerUser).size();

            if (finalQuestionerPlayer != null) {

                qaRequestResult.setMessage(configOption.getQuestionNumberAnswerSendSuccessIfQuestionerOnline()
                        .replace("%playername%", QAUserManager.getInstance().getFullName(answerUser))
                        .replace("%answer_count_total%", Long.toString(question.getId()))
                        .replace("%answer_count_total%", Integer.toString(answerCountTotal))
                ); // 답변자에게 전달

                BroadCastMessage broadCastMessage = new BroadCastMessage(
                        configOption.getAnswerSendSuccessIfQuestionerOnlineBroadcast()
                                .replace("%answer_playername%", QAUserManager.getInstance().getFullName(answerUser))
                                .replace("%answer%", answer.getContents())
                );

                NetworkManager.getInstance().sendPacketAllChannel(broadCastMessage); // 전체 서버로 메세지 전송

                Channel channel = PlayerListHandler.getPlayerAsChannel(questionUser.getGamePlayerName());

                VelocityThreadUtil.asyncTask(() -> {
                    ChannelFuture channelFuture = channel.writeAndFlush(new ExpectQuestionAnswerResponse(questionUser, answerUser, question, answer));
                    try {
                        channelFuture.get();
                        answer.setReceivedToQuestionPlayer(true); // 정상 전달했을경우
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });


            } else {
                //    @RequirePlaceHolder( placeholders = {"%playername%", "%question_number%", "%answer_count_total%"})

                qaRequestResult.setMessage(configOption.getQuestionNumberAnswerSendSuccessIfQuestionerOffline()
                        .replace("%playername%", QAUserManager.getInstance().getFullName(answerUser))
                        .replace("%answer_count_total%", Long.toString(question.getId()))
                        .replace("%answer_count_total%", Integer.toString(answerCountTotal))
                ); // 답변자에게 전달
            }

            if ( answer.isReceivedToQuestionPlayer() ) {

            }

            this.answerIdByAnswerHashMap.put(answer.getAnswerId(), answer);
            getQAUserAnswers(answerUser).add(answer);
            NetworkManager.getInstance().sendPacketAllChannel(new BukkitAnswerModify(QAModifyType.ADD, new Answer[]{answer}));
            NetworkManager.getInstance().sendPacketAllChannel(new BukkitQuestionModify(QAModifyType.ADD, new Question[]{question}));

        });

    }

    public QARequestResult processAnswer(PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest) {

        QARequestResult qaRequestResult = new QARequestResult(); // 반환 결과값

        long questId = playerTransientProceedingAnswerRequest.getQuestionId();

        ConfigOption configOption = serverOptionManager.getConfigOption();

        if ( !questionManager.existQuest(questId) ) {
            // 존재하지 않는 퀘스트 ID 입니다
            qaRequestResult.setMessage(configOption.getRemoveQFailNotExist());
            return qaRequestResult;
        }

        Question question = questionManager.getQuestionByQuestId(questId);

        QAUser questionUser = question.getQaUser();

        QAUser answerUser;

        if ( playerTransientProceedingAnswerRequest.getRequestType().equals(PlayerTransientProceedingAnswerRequest.RequestType.DISCORD) ) {
            answerUser = QAUserManager.getInstance().getUser(playerTransientProceedingAnswerRequest.getDiscordUserId());
        } else {
            answerUser = QAUserManager.getInstance().getUser(playerTransientProceedingAnswerRequest.getPlayerName());
        }

        if ( questionUser.equals(answerUser) ) {
            //자신의 질문에 답장할 때
            qaRequestResult.setMessage(configOption.getQuestionNumberAnswerSendFailCanNotSelfAnswer());
            return qaRequestResult;
        }

        String answerContent = playerTransientProceedingAnswerRequest.getAnswerContent();

        int delay = configOption.getAnswerCooldown();

        List<Answer> answers = getQAUserAnswers(answerUser);

        for (Answer answer : answers) {

            long diffSec = TimeUtil.getTimeDiffSec(answer.getAnswerDate());

            if ( diffSec < delay ) {
                //만일 Delay 시간이 덜 지났으면?
                qaRequestResult.setMessage(configOption.getAnswerSendFailAnswerCooldown()
                        .replace("%answer_cooldown%", Integer.toString(delay))
                        .replace("%answer_remain_cooldown%", Integer.toString(delay-(int)diffSec))
                );
                return qaRequestResult;
            }

        }

        if ( qaRequestResult.getType().equals(QARequestResult.Type.SUCCESS) ) {
            Answer answer = new Answer(getAnswerNumber(), questId, answerContent, answerUser);
            answer(qaRequestResult, question, answer);
        }

        return qaRequestResult;

    }

    public boolean existAnswer(long answerId) {
        return answerIdByAnswerHashMap.containsKey(answerId);
    }

    public Answer getAnswerByAnswerId(long answerId) {
        return answerIdByAnswerHashMap.get(answerId);
    }

    @Nullable
    public Answer getAnswerByQuestId(long questId) {

        for (Answer value : answerIdByAnswerHashMap.values()) {
            if ( questId == value.getQuestionId() ) {
                return value;
            }
        }

        return null;

    }


    /**
     * @return DB에 내장되어있는 최종 질문 번호
     */
    public int getAnswerNumber() {
        List<Integer> result = DBConnection.getDataFromDataBase("SELECT COUNT(*) AS last_id FROM answer", "last_id", Integer.class);
        return result.isEmpty() ? 1 : result.get(0);
    }

    public List<Answer> getQAUserAnswers(QAUser qaUser) {

        if ( qaUserHasAnswer.containsKey(qaUser) ) {
            return qaUserHasAnswer.get(qaUser);
        }

        List<Answer> questions = new ArrayList<>();
        qaUserHasAnswer.put(qaUser, questions);

        return questions;
    }

    public String getAnswerFormat(Answer answer) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(QAUserManager.getInstance().getFullName(answer.getAnswerPlayer())+"\n");
        stringBuilder.append(String.format("A: %s\n", answer.getContents()));

        return stringBuilder.toString().trim();

    }




}
