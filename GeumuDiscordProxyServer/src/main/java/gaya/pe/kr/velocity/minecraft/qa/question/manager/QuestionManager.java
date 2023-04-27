package gaya.pe.kr.velocity.minecraft.qa.question.manager;


import gaya.pe.kr.qa.data.QARequestResult;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.data.TransientPlayerProceedingQuestion;
import gaya.pe.kr.qa.question.exception.NonExistQuestionException;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.velocity.database.DBConnection;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionManager {

    private static class SingleTon {
        private static final QuestionManager QUESTION_MANAGER = new QuestionManager();
    }

    public static QuestionManager getInstance() {
        return SingleTon.QUESTION_MANAGER;
    }

    HashMap<Integer, Question> questIdByQuestHashMap = new HashMap<>();
    HashMap<QAUser, List<Question>> qaUserHasQuestion = new HashMap<>();
    ServerOptionManager serverOptionManager = ServerOptionManager.getInstance();
    QAUserManager qaUserManager = QAUserManager.getInstance();

    public boolean existQuest(int questId) {
        return questIdByQuestHashMap.containsKey(questId);
    }

    public Question getQuestionByQuestId(int questId) {

        return questIdByQuestHashMap.get(questId);

    }

    public boolean existQuestionByDiscordMessageId(Long messageId) {

        for (Map.Entry<Integer, Question> integerQuestionEntry : questIdByQuestHashMap.entrySet()) {
            Question question = integerQuestionEntry.getValue();

            if ( messageId == question.getDiscordMessageId() ) {
                return true;
            }

        }
        return false;
    }
    
    public Question getQuestionByDiscordMessageId(Long messageId) throws NonExistQuestionException {
        for (Map.Entry<Integer, Question> integerQuestionEntry : questIdByQuestHashMap.entrySet()) {
            Question question = integerQuestionEntry.getValue();
            if ( messageId == question.getDiscordMessageId() ) {
                return question;
            }
        }
        throw new NonExistQuestionException(String.format("[%d] Discord Message Id 의 질문은 존재하지 않습니다", messageId));
    }

    public QARequestResult canQuestion(PlayerTransientProceedingQuestionRequest playerTransientProceedingQuestionRequest) {

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
            }

        }




        return qaRequestResult;

    }

    public void broadCastQuestion(TransientPlayerProceedingQuestion playerProceedingQuestion) {

    }

    /**
     * @return DB에 내장되어있는 최종 질문 번호
     */
    public int getQuestionNumber() {
        List<Integer> result = DBConnection.getDataFromDataBase("SELECT COUNT(*) AS last_id FROM question", "last_id", Integer.class);
        return result.isEmpty() ? 1 : result.get(0);
    }

    public List<Question> getQAUserQuestions(QAUser qaUser) {

        if ( qaUserHasQuestion.containsKey(qaUser) ) {
            return qaUserHasQuestion.get(qaUser);
        }

        List<Question> questions = new ArrayList<>();
        qaUserHasQuestion.put(qaUser, questions);

        return questions;
    }

}
