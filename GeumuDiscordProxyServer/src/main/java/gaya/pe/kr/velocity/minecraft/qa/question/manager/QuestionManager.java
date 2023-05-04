package gaya.pe.kr.velocity.minecraft.qa.question.manager;


import gaya.pe.kr.network.packet.startDirection.server.non_response.BroadCastClickableMessage;
import gaya.pe.kr.network.packet.startDirection.server.non_response.BroadCastMessage;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QARequestResult;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.server.BukkitAnswerModify;
import gaya.pe.kr.qa.packet.server.BukkitQuestionModify;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.exception.NonExistQuestionException;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.AnswerPatternOptions;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.PatternMatcher;
import gaya.pe.kr.velocity.database.DBConnection;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.network.manager.NetworkManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class QuestionManager {

    private static class SingleTon {
        private static final QuestionManager QUESTION_MANAGER = new QuestionManager();
    }

    public static QuestionManager getInstance() {
        return SingleTon.QUESTION_MANAGER;
    }

    HashMap<Long, Question> questIdByQuestHashMap = new HashMap<>();
    HashMap<QAUser, List<Question>> qaUserHasQuestion = new HashMap<>();
    ServerOptionManager serverOptionManager;
    QAUserManager qaUserManager;


    public void init() {

        serverOptionManager = ServerOptionManager.getInstance();
        qaUserManager = QAUserManager.getInstance();

        DBConnection.taskTransaction(connection -> {
            QAUserManager qaUserManager = QAUserManager.getInstance();

            String sql = "SELECT `questions`.`id`,\n" +
                    "    `questions`.`qauser_uuid`,\n" +
                    "    `questions`.`contents`,\n" +
                    "    `questions`.`question_date`,\n" +
                    "    `questions`.`discord_message_number`,\n" +
                    "    `questions`.`answer`\n" +
                    "FROM `pixelmon_01_answer`.`questions`;\n";


            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() ) {

                long questionId = resultSet.getLong(1);
                String questionUserUUID = resultSet.getString(2);
                String contents = resultSet.getString(3);
                Date questionDate = resultSet.getTimestamp(4);
                long discordMessageNumber = resultSet.getLong(5);
                boolean answer = resultSet.getBoolean(6);

                UUID uuid = UUID.fromString(questionUserUUID);
                if ( qaUserManager.existUser(uuid) ) {
                    QAUser qaUser = qaUserManager.getQAUserByUUID(uuid);
                    Question question = new Question(questionId, qaUser, contents, questionDate, discordMessageNumber, answer);
                    questIdByQuestHashMap.put(questionId, question);
                    System.out.println(question.toString() + " ADD ----------------");
                }

            }

            for (QAUser qaUser : qaUserManager.getAllQAUsers()) {
                List<Question> questions = getQAUserQuestions(qaUser);

                for (Question value : questIdByQuestHashMap.values()) {
                    if ( value.getQaUser().equals(qaUser) ) {
                        questions.add(value);
                        System.out.printf("%s : %d ADD\n%n", qaUser.getGamePlayerName(), value.getId());
                    }
                }

            }

            AnswerManager answerManager = AnswerManager.getInstance();
            answerManager.init();

        });

        System.out.println("QuestionManager init");
    }

    public List<Question> getAllQuestions() {
        return new ArrayList<>(questIdByQuestHashMap.values());
    }

    public boolean existQuest(long questId) {
        return questIdByQuestHashMap.containsKey(questId);
    }

    public Question getQuestionByQuestId(long questId) {
        return questIdByQuestHashMap.get(questId);
    }


    public Question removeQuestionByQuestId(long questId) {
        if (questIdByQuestHashMap.containsKey(questId) ) {
            Question question = questIdByQuestHashMap.get(questId);
            NetworkManager.getInstance().sendPacketAllChannel(new BukkitQuestionModify(QAModifyType.REMOVE, new Question[]{question}));
            questIdByQuestHashMap.remove(questId);
            return question;
        }
        return null;
    }

    public Question addQuestion(Question question) {
        broadCastQuestion(question, null);
        return question;
    }

    public boolean existQuestionByDiscordMessageId(Long messageId) {

        for (Map.Entry<Long, Question> integerQuestionEntry : questIdByQuestHashMap.entrySet()) {
            Question question = integerQuestionEntry.getValue();

            if ( messageId == question.getDiscordMessageId() ) {
                return true;
            }

        }
        return false;
    }
    
    public Question getQuestionByDiscordMessageId(long messageId) throws NonExistQuestionException {
        for (Map.Entry<Long, Question> integerQuestionEntry : questIdByQuestHashMap.entrySet()) {
            Question question = integerQuestionEntry.getValue();
            if ( messageId == question.getDiscordMessageId() ) {
                return question;
            }
        }
        throw new NonExistQuestionException(String.format("[%d] Discord Message Id 의 질문은 존재하지 않습니다", messageId));
    }

    /**
     *
     */
    @Nullable
    public Question getTargetQAUserRecentQuestion(QAUser qaUser) {

        int targetTime = serverOptionManager.getConfigOption().getRecentQuestionAnswerTime();
        for (Question qaUserQuestion : getQAUserQuestions(qaUser)) {
            long diffSec = TimeUtil.getTimeDiffSec(qaUserQuestion.getQuestionDate());
            if ( diffSec < targetTime ) {
                //만일 Delay 시간이 덜 지났으면?
                return qaUserQuestion;
            }
        }
        return null;
    }

    public int getQuestionableAmount(QAUser user) {

        int result = 0;

        for (Question qaUserQuestion : getQAUserQuestions(user)) {
            if ( !qaUserQuestion.isAnswer() ) {
                result++;
            }
        }

        return result;

    }

    /**
     *
     * @param playerTransientProceedingQuestionRequest
     * @return Discord 혹은 In-Game 에서도 사용할 수 있도록 설정
     */
    public QARequestResult processQuestion(PlayerTransientProceedingQuestionRequest playerTransientProceedingQuestionRequest) {

        PlayerTransientProceedingQuestionRequest.RequestType requestType = playerTransientProceedingQuestionRequest.getRequestType();

        QARequestResult qaRequestResult = new QARequestResult(); // 반환 결과값

        ConfigOption configOption = serverOptionManager.getConfigOption();

        int minLength = configOption.getQuestionMinLength();
        int maxLength = configOption.getQuestionMaxLength();

        String content = playerTransientProceedingQuestionRequest.getContent();

        int contentSize = content.length();

        if ( contentSize > maxLength ) {
            // 너무 길어
            qaRequestResult.setMessage(configOption.getQuestionFailQuestionTooLong().replace("%question_max_length%", Integer.toString(maxLength))); // 길이 제한
            return qaRequestResult;
        }

        if ( contentSize < minLength ) {
            // 너무 짧아
            qaRequestResult.setMessage(configOption.getQuestionFailQuestionTooShort().replace("%question_min_length%", Integer.toString(minLength))); // 길이 제한
            return qaRequestResult;
        }

        QAUser qaUser;

        if (requestType.equals(PlayerTransientProceedingQuestionRequest.RequestType.DISCORD)) {
            //TODO Discord 일 경우 현재 플레이어 중 디스코드 & 인게임에서 질문 여부를 파악한함
            qaUser = qaUserManager.getUser(playerTransientProceedingQuestionRequest.getDiscordUserId());
        } else {
            qaUser = qaUserManager.getUser(playerTransientProceedingQuestionRequest.getPlayerName());
        }

        if ( qaUser != null ) {
            System.out.println(qaUser.toString() + " <<< 정보");
        }

        int delay = configOption.getQuestionCooldown();

        List<Question> questions = getQAUserQuestions(qaUser);

        for (Question question : questions) {

            long diffSec = TimeUtil.getTimeDiffSec(question.getQuestionDate());

            if ( diffSec < delay ) {
                //만일 Delay 시간이 덜 지났으면?
                qaRequestResult.setMessage(configOption.getQuestionFailQuestionCooldown()
                        .replace("%question_cooldown%", Integer.toString(delay))
                        .replace("%question_remain_cooldown%", Integer.toString(delay-(int)diffSec))
                );
                return qaRequestResult;
            }

        }

        // 1차 진행을 했기 때문에 Filtering 을 진행

        AnswerPatternOptions answerPatternOptions = serverOptionManager.getAnswerPatternOptions();

        for (PatternMatcher patternMatcher : answerPatternOptions.getPatternMatcherList()) {
            if ( patternMatcher.isMatch(content) ) {
                String answer = patternMatcher.getMessage();
                qaRequestResult.setMessage(configOption.getAnswerSendSuccessIfQuestionerOnlineBroadcast()
                        .replace("%playername%", configOption.getAnswerPlayerNamePlaceholderAutoAnswer())
                        .replace("%answer%", answer)
                );
                return qaRequestResult;
            }
        }

        qaRequestResult.setType(QARequestResult.Type.SUCCESS);

        if ( qaRequestResult.getType().equals(QARequestResult.Type.SUCCESS) ) {
            long lastQuestionNumber = getQuestionNumber();
            Question question = new Question(lastQuestionNumber, content, qaUser );
            broadCastQuestion(question, qaRequestResult);
            System.out.println(question.getQaUser().toString() + " <<< Questioner ");
        }

        return qaRequestResult;

    }

    private void broadCastQuestion(Question question, @Nullable QARequestResult qaRequestResult) {

        if ( qaRequestResult == null ) {
            qaRequestResult = new QARequestResult();
        }

        DiscordManager discordManager = DiscordManager.getInstance();
        Message message = discordManager.sendMessage( "```"+getQuestionFormat(question)+"```" , discordManager.getQuestionChannel() ); // 디스코드 전체 전송

        QAUser qaUser = question.getQaUser();

        long messageId = message.getIdLong();
        question.setDiscordMessageId(messageId); // 전체적으로 질문 전송

        boolean databaseResult = DBConnection.taskTransaction( connection -> {

            String sql = "INSERT INTO `pixelmon_01_answer`.`questions`" +
                    "(`id`, `qauser_uuid`, `contents`, `question_date`, `discord_message_number`, `answer`)" +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setLong(1, question.getId());
            preparedStatement.setString(2, question.getQaUser().getUuid().toString());
            preparedStatement.setString(3, question.getContents());
            preparedStatement.setTimestamp(4, new Timestamp(question.getQuestionDate().getTime()));
            preparedStatement.setLong(5, question.getDiscordMessageId());
            preparedStatement.setBoolean(6, question.isAnswer());

            preparedStatement.executeUpdate();

        });

        if ( databaseResult ) {
            qaRequestResult.setType(QARequestResult.Type.SUCCESS);
            qaRequestResult.clearMessages(); // 전체 메세지를 사용하기 떄문에 개인적인 메세지는 보낼 필요가 없음
            ConfigOption configOption = ServerOptionManager.getInstance().getConfigOption();
            //    @RequirePlaceHolder(placeholders = {"%playername%", "%question_content%"})
            BroadCastClickableMessage broadCastMessage = new BroadCastClickableMessage(configOption.getQuestionSuccessBroadcast()
                    .replace("%playername%", qaUserManager.getFullName(qaUser))
                    .replace("%question_content%", question.getContents())
                    , "클릭 시 답변을 진행합니다"
                    , "/답변 conversation "+question.getId()
            );

            NetworkManager.getInstance().sendPacketAllChannel(broadCastMessage); // 전체 서버로 메세지 전송

            questIdByQuestHashMap.put(question.getId(), question);
            NetworkManager.getInstance().sendPacketAllChannel(new BukkitQuestionModify(QAModifyType.ADD, new Question[]{question}));
            getQAUserQuestions(qaUser).add(question); // 데이터 삽입 최종적인 작업 끝.

        } else {
            // 문제 발생했음을 알림
            qaRequestResult.setMessage("데이터 베이스에 질문 넣을때 문제 발생함");
        }

    }

    public void modifyQuestionData(Question question, QAModifyType qaModifyType) {

        switch ( qaModifyType ) {
            case ADD:{
                addQuestion(question);
                break;
            }
            case REMOVE: {

                DBConnection.taskTransaction(connection -> {

                    String sql = "DELETE FROM `pixelmon_01_answer`.`questions`\n" +
                            "WHERE id = ?;\n";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setLong(1, question.getId());
                    preparedStatement.executeUpdate();
                    removeQuestionByQuestId(question.getId());

                });

                break;
            }
            case MODIFY: {

                DBConnection.taskTransaction(connection -> {

                    String sql = "INSERT INTO `pixelmon_01_answer`.`questions` " +
                            "(`id`, `qauser_uuid`, `contents`, `question_date`, `discord_message_number`, `answer`) " +
                            "VALUES (?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "`qauser_uuid` = ?, " +
                            "`contents` = ?, " +
                            "`question_date` = ?, " +
                            "`discord_message_number` = ?, " +
                            "`answer` = ?;";

                    PreparedStatement preparedStatement = connection.prepareStatement(sql);


                    long questionId = question.getId();
                    String uuid = question.getQaUser().getUuid().toString();
                    String contents = question.getContents();
                    Timestamp timestamp = new Timestamp(question.getQuestionDate().getTime());
                    long discordMessage = question.getDiscordMessageId();
                    boolean answer = question.isAnswer();

                        preparedStatement.setLong(1, questionId);
                    preparedStatement.setString(2, uuid);
                    preparedStatement.setString(3, contents);
                    preparedStatement.setTimestamp(4, timestamp);
                    preparedStatement.setLong(5, discordMessage);
                    preparedStatement.setBoolean(6, answer);

                    preparedStatement.setString(7, uuid);
                    preparedStatement.setString(8, contents);
                    preparedStatement.setTimestamp(9,timestamp);
                    preparedStatement.setLong(10, discordMessage);
                    preparedStatement.setBoolean(11, answer);

                    preparedStatement.executeUpdate();

                    updateQuestionData(question);

                });

            }
        }

    }

    public void updateQuestionData(Question question) {

        NetworkManager.getInstance().sendPacketAllChannel(new BukkitQuestionModify(QAModifyType.ADD, new Question[]{question}));
        questIdByQuestHashMap.put(question.getId(), question);

    }

    public String getQuestionFormat(Question question) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(qaUserManager.getFullName(question.getQaUser()));
        stringBuilder.append(String.format(" ( 질문번호 | %d )\n", question.getId()));
        stringBuilder.append(String.format("Q: %s", question.getContents()));

        return stringBuilder.toString().trim();

    }

    /**
     * @return DB에 내장되어있는 최종 질문 번호
     */
    public int getQuestionNumber() {

        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) + 1 AS next_id FROM questions");
            int count = 0;
            if (resultSet.next()) {
                count = resultSet.getInt("next_id");
                System.out.println("전체 데이터 개수: " + count);
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

    public List<Question> getQAUserQuestions(QAUser qaUser) {

        if ( qaUserHasQuestion.containsKey(qaUser) ) {
            return qaUserHasQuestion.get(qaUser);
        }

        List<Question> questions = new ArrayList<>();
        qaUserHasQuestion.put(qaUser, questions);

        return questions;
    }

    public String getQuestPrefix() {
        return serverOptionManager.getConfigOption().getDiscordQuestionPrefix();
    }

    public String getQuestPrefixHelpMessage() {
        return serverOptionManager.getConfigOption().getDiscordQuestionChannelPrefixHelpMessage();
    }

}
