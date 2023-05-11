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
import net.dv8tion.jda.api.entities.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.units.qual.A;
import org.sqlite.core.DB;
import org.w3c.dom.Text;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.Date;
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
    ServerOptionManager serverOptionManager;
    QuestionManager questionManager;

    int sizeTemp = -1;

    public void init() {

        serverOptionManager = ServerOptionManager.getInstance();
        questionManager = QuestionManager.getInstance();

        DBConnection.taskTransaction(connection -> {

            QAUserManager qaUserManager = QAUserManager.getInstance();


            String sql = "SELECT `answers`.`id`,\n" +
                    "    `answers`.`question_id`,\n" +
                    "    `answers`.`contents`,\n" +
                    "    `answers`.`answer_qauser_uuid`,\n" +
                    "    `answers`.`answer_date`,\n" +
                    "    `answers`.`receive_to_question_player`,\n" +
                    "    `answers`.`received_reward`\n" +
                    "FROM `pixelmon_01_answer`.`answers`;\n";


            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() ) {

                long answerId = resultSet.getLong(1);
                long questionId = resultSet.getLong(2);
                String contents = resultSet.getString(3);
                String answerQAUserUUIDStr = resultSet.getString(4);
                Date answerDate = resultSet.getTimestamp(5);
                boolean receivedToQuestionPlayer = resultSet.getBoolean(6);
                boolean receivedReward = resultSet.getBoolean(7);

                UUID uuid = UUID.fromString(answerQAUserUUIDStr);
                if ( qaUserManager.existUser(uuid) ) {

                    QAUser answerUser = qaUserManager.getQAUserByUUID(uuid);

                    if ( questionManager.existQuest(questionId) ) {
                        Answer answer = new Answer(answerId, questionId, contents, answerUser, answerDate, receivedToQuestionPlayer, receivedReward);
                        answerIdByAnswerHashMap.put(answerId, answer);
                        System.out.println(answer.toString() + " ADD -----------");
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

        System.out.println("AnswerManager init");


    }

    public List<Answer> getAllAnswers() {
        return new ArrayList<>(answerIdByAnswerHashMap.values());
    }

    private void answer(QARequestResult qaRequestResult, Question question, Answer answer) {

        DiscordManager discordManager = DiscordManager.getInstance();
        Message message = discordManager.sendMessage( String.format("```%s\n%s```",questionManager.getQuestionFormat(question), getAnswerFormat(answer)) , discordManager.getQuestionChannel() );

        question.setAnswer(true);

        if ( question.getDiscordMessageId() != -1 ) {

            TextChannel channel = discordManager.getQuestionChannel();

            try {
                Message deleteTargetMessage = channel.retrieveMessageById(question.getDiscordMessageId()).submit().get();
                if ( deleteTargetMessage != null ) {
                    deleteTargetMessage.delete().queue();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }

        }

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

            try {

                String sql = "INSERT INTO `pixelmon_01_answer`.`answers` " +
                        "(`id`, `question_id`, `contents`, `answer_qauser_uuid`, `answer_date`, `receive_to_question_player`, `received_reward`) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "`question_id` = ?, " +
                        "`contents` = ?, " +
                        "`answer_qauser_uuid` = ?, " +
                        "`answer_date` = ?, " +
                        "`receive_to_question_player` = ?," +
                        "`received_reward` = ?";

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
                preparedStatement.setBoolean(7, false );

                preparedStatement.setLong(8, questionId);
                preparedStatement.setString(9, answerContents);
                preparedStatement.setString(10, answerQAUserUUIDStr);
                preparedStatement.setTimestamp(11, timestamp);
                preparedStatement.setBoolean(12, receivedToQuestionPlayer);
                preparedStatement.setBoolean(13, false );

                preparedStatement.executeUpdate();

                ConfigOption configOption = serverOptionManager.getConfigOption();


                List<Answer> answers = getQAUserAnswers(answerUser);
                answers.add(answer);

                int answerCountTotal = answers.size();

                this.answerIdByAnswerHashMap.put(answer.getAnswerId(), answer);
                NetworkManager.getInstance().sendPacketAllChannel(new BukkitAnswerModify(QAModifyType.ADD, new Answer[]{answer}));

                if (finalQuestionerPlayer != null) {

                    qaRequestResult.setMessage(configOption.getQuestionNumberAnswerSendSuccessIfQuestionerOnline()
                            .replace("%playername%", QAUserManager.getInstance().getFullName(questionUser))
                            .replace("%question_number%", Long.toString(question.getId()))
                            .replace("%answer_count_total%", Integer.toString(answerCountTotal))
                    ); // 답변자에게 전달

                    BroadCastMessage broadCastMessage = new BroadCastMessage(
                            configOption.getAnswerSendSuccessIfQuestionerOnlineBroadcast()
                                    .replace("%answer_playername%", QAUserManager.getInstance().getFullName(answerUser))
                                    .replace("%answer%", answer.getContents())
                    );

                    NetworkManager.getInstance().sendPacketAllChannel(broadCastMessage); // 전체 서버로 메세지 전송

                    Channel channel = PlayerListHandler.getPlayerAsChannel(questionUser.getGamePlayerName());

                    if ( channel != null ) {
                        VelocityThreadUtil.asyncTask(() -> {
                            ChannelFuture channelFuture = channel.writeAndFlush(new ExpectQuestionAnswerResponse(questionUser, answerUser, question, answer));
                            try {
                                channelFuture.get();
                                answer.setReceivedToQuestionPlayer(true); // 정상 전달했을경우
                            } catch (ExecutionException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }


                } else {
                    //    @RequirePlaceHolder( placeholders = {"%playername%", "%question_number%", "%answer_count_total%"})

                    qaRequestResult.setMessage(configOption.getQuestionNumberAnswerSendSuccessIfQuestionerOffline()
                            .replace("%playername%", QAUserManager.getInstance().getFullName(questionUser))
                            .replace("%question_number%", Long.toString(question.getId()))
                            .replace("%answer_count_total%", Integer.toString(answerCountTotal))
                    ); // 답변자에게 전달
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }



        });

        if ( !databaseResult ) {
            message.delete().queue();
        } else {
            questionManager.modifyQuestionData(question, QAModifyType.MODIFY);
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

        if ( question.isAnswer() ) {
            qaRequestResult.setMessage("§f[§c!§f] 이미 답변이 된 질문 입니다");
            return qaRequestResult;
        }

        QAUser questionUser = question.getQaUser();

        QAUser answerUser;

        if ( playerTransientProceedingAnswerRequest.getRequestType().equals(PlayerTransientProceedingAnswerRequest.RequestType.DISCORD) ) {
            answerUser = QAUserManager.getInstance().getUser(playerTransientProceedingAnswerRequest.getDiscordUserId());
        } else {
            answerUser = QAUserManager.getInstance().getUser(playerTransientProceedingAnswerRequest.getPlayerName());
        }

        if ( answerUser != null ) {

            if ( answerUser.getDiscordPlayerUserId() == -1 ) {
                qaRequestResult.setMessage("§f[§c!§f] 인증을 하지 않으면 답변할 수 없습니다");
                return qaRequestResult;
            }

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

        qaRequestResult.setType(QARequestResult.Type.SUCCESS);

        if ( qaRequestResult.getType().equals(QARequestResult.Type.SUCCESS) ) {
            Answer answer = new Answer(getAnswerNumber(), questId, answerContent, answerUser);
            answer(qaRequestResult, question, answer);
        }

        return qaRequestResult;

    }

    public void modifyAnswer(Answer answer) {

        if ( existAnswer(answer.getAnswerId()) ) {
            Answer targetAnswer = getAnswerByAnswerId(answer.getAnswerId());
            targetAnswer.setReceiveReward(answer.isReceiveReward());
            targetAnswer.setReceivedToQuestionPlayer(answer.isReceivedToQuestionPlayer());

            boolean result = DBConnection.taskTransaction(connection -> {

                String sql = "update `pixelmon_01_answer`.`answers` set receive_to_question_player = ?, received_reward = ? where id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setBoolean(1, targetAnswer.isReceivedToQuestionPlayer());
                preparedStatement.setBoolean(2, targetAnswer.isReceiveReward());
                preparedStatement.setBigDecimal(3, BigDecimal.valueOf(targetAnswer.getAnswerId()));
                preparedStatement.executeUpdate();

            });

            if ( result ) {
                NetworkManager.getInstance().sendPacketAllChannel(new BukkitAnswerModify(QAModifyType.MODIFY, new Answer[]{targetAnswer}));
            }

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

            boolean result = DBConnection.taskTransaction(connection -> {

                String sql = "delete from `pixelmon_01_answer`.`questions` where id = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setBigDecimal(1, BigDecimal.valueOf(questId));

                preparedStatement.executeUpdate();

            });

            if ( result ) {
                answerIdByAnswerHashMap.remove(removeTarget.getAnswerId());
                NetworkManager.getInstance().sendPacketAllChannel(new BukkitAnswerModify(QAModifyType.REMOVE, new Answer[]{removeTarget}));
                return removeTarget;
            }
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

        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) + 1 AS next_id FROM answers");

            int count = 0;
            if (resultSet.next()) {
                count = resultSet.getInt("next_id");
            }

            resultSet.close();
            statement.close();
            connection.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;


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
