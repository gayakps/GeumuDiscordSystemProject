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
import org.sqlite.core.DB;
import org.w3c.dom.Text;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
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

        DBConnection.taskTransaction(connection -> {

            QAUserManager qaUserManager = QAUserManager.getInstance();


            String sql = "SELECT `answers`.`id`,\n" +
                    "    `answers`.`question_id`,\n" +
                    "    `answers`.`contents`,\n" +
                    "    `answers`.`answer_qauser_uuid`,\n" +
                    "    `answers`.`answer_date`,\n" +
                    "    `answers`.`receive_to_question_player`\n" +
                    "    `answers`.`received_reward`\n" +
                    "FROM `pixelmon_01_answer`.`answers`;\n";


            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() ) {

                long answerId = resultSet.getLong(1);
                long questionId = resultSet.getLong(2);
                String contents = resultSet.getString(3);
                String answerQAUserUUIDStr = resultSet.getString(4);
                Date answerDate = resultSet.getDate(5);
                boolean receivedToQuestionPlayer = resultSet.getBoolean(6);
                boolean receivedReward = resultSet.getBoolean(7);

                UUID uuid = UUID.fromString(answerQAUserUUIDStr);
                if ( qaUserManager.existUser(uuid) ) {

                    QAUser answerUser = qaUserManager.getQAUserByUUID(uuid);

                    if ( questionManager.existQuest(questionId) ) {
                        Answer answer = new Answer(answerId, questionId, contents, answerUser, answerDate, receivedToQuestionPlayer, receivedReward);
                        answerIdByAnswerHashMap.put(answerId, answer);
                        System.out.println(answer.toString() + " ADDD -----------");
                    }

                }

            }


            for (QAUser qaUser : qaUserManager.getAllQAUsers()) {
                List<Answer> answers = getQAUserAnswers(qaUser);

                for (Answer value : answerIdByAnswerHashMap.values()) {
                    if ( value.getAnswerPlayer().equals(qaUser) ) {
                        answers.add(value);
                        System.out.printf("%s : %d ADD\n%n", qaUser.getGamePlayerName(), value.getAnswerId());
                    }
                }

            }


        });


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
                    questionerPlayer = allPlayer;
                    break;
                }

            }
        }

        Player finalQuestionerPlayer = questionerPlayer;
        boolean databaseResult = DBConnection.taskTransaction(connection -> {

            String sql = "INSERT INTO `pixelmon_01_answer`.`answers` " +
                    "(`id`, `question_id`, `contents`, `answer_qauser_uuid`, `answer_date`, `receive_to_question_player`, `receive_reward`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "`question_id` = ?, " +
                    "`contents` = ?, " +
                    "`answer_qauser_uuid` = ?, " +
                    "`answer_date` = ?, " +
                    "`receive_to_question_player` = ?" +
                    "`receive_reward` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            long answerId = answer.getAnswerId();
            long questionId = question.getId();
            String answerContents = answer.getContents();
            String answerQAUserUUIDStr = answer.getAnswerPlayer().getUuid().toString();
            Timestamp timestamp = new Timestamp(answer.getAnswerDate().getTime());
            boolean receivedToQuestionPlayer = finalQuestionerPlayer != null;


            preparedStatement.setLong(1, answerId);
            preparedStatement.setLong(2, questionId);
            preparedStatement.setString(3, answerContents);
            preparedStatement.setString(4, answerQAUserUUIDStr);
            preparedStatement.setTimestamp(5, timestamp);
            preparedStatement.setBoolean(6, receivedToQuestionPlayer);
            preparedStatement.setBoolean(7, false);

            preparedStatement.setLong(8, questionId);
            preparedStatement.setString(9, answerContents);
            preparedStatement.setString(10, answerQAUserUUIDStr);
            preparedStatement.setTimestamp(11, timestamp);
            preparedStatement.setBoolean(12, receivedToQuestionPlayer);
            preparedStatement.setBoolean(13, false);

            preparedStatement.executeUpdate();

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

            this.answerIdByAnswerHashMap.put(answer.getAnswerId(), answer);
            getQAUserAnswers(answerUser).add(answer);
            NetworkManager.getInstance().sendPacketAllChannel(new BukkitAnswerModify(QAModifyType.ADD, new Answer[]{answer}));
            NetworkManager.getInstance().sendPacketAllChannel(new BukkitQuestionModify(QAModifyType.ADD, new Question[]{question}));

        });

        if ( !databaseResult ) {
            message.delete().queue();
        }

    }


    public QARequestResult processAnswer(PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest) {

        QARequestResult qaRequestResult = new QARequestResult(); // 반환 결과값

        long questId = playerTransientProceedingAnswerRequest.getQuestionId();

        ConfigOption configOption = serverOptionManager.getConfigOption();

        if ( !questionManager.existQuest(questId) ) {
            // 존재하지 않는 퀘스트 ID 입니다
            qaRequestResult.setMessage(configOption.getInvalidQuestionNumber());
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

    public void modifyAnswer(Answer answer) {

        if ( existAnswer(answer.getAnswerId()) ) {
            Answer targetAnswer = getAnswerByQuestId(answer.getAnswerId());
            targetAnswer.setReceiveReward(answer.isReceiveReward());
            targetAnswer.setReceivedToQuestionPlayer(answer.isReceivedToQuestionPlayer());
            NetworkManager.getInstance().sendPacketAllChannel(new BukkitAnswerModify(QAModifyType.MODIFY, new Answer[]{answer}));
        }

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

    @Nullable
    public Answer removeByQuestId(long questId) {
        Answer removeTarget = null;
        for (Answer value : answerIdByAnswerHashMap.values()) {
            if ( questId == value.getQuestionId() ) {
                removeTarget = value;
            }
        }

        if ( removeTarget != null ) {
            answerIdByAnswerHashMap.remove(removeTarget.getAnswerId());
            NetworkManager.getInstance().sendPacketAllChannel(new BukkitAnswerModify(QAModifyType.REMOVE, new Answer[]{removeTarget}));
            return removeTarget;
        }
        return null;
    }

    public boolean existAnswerByQuestId(long questId) {
        for (Answer value : answerIdByAnswerHashMap.values()) {
            if ( questId == value.getQuestionId() ) {
                return true;
            }
        }
        return false;

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
