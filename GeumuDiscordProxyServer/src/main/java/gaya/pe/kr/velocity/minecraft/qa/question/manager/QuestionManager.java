package gaya.pe.kr.velocity.minecraft.qa.question.manager;

import gaya.pe.kr.velocity.minecraft.qa.question.data.Question;
import gaya.pe.kr.velocity.minecraft.qa.question.data.TransientPlayerProceedingQuestion;
import gaya.pe.kr.velocity.minecraft.qa.question.exception.NonExistQuestionException;

import java.util.HashMap;
import java.util.Map;

public class QuestionManager {

    private static class SingleTon {
        private static final QuestionManager QUESTION_MANAGER = new QuestionManager();
    }

    public static QuestionManager getInstance() {
        return SingleTon.QUESTION_MANAGER;
    }

    HashMap<Integer, Question> questIdByQuestHashMap = new HashMap<>();


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

    public void broadCastQuestion(TransientPlayerProceedingQuestion playerProceedingQuestion) {

    }


}
