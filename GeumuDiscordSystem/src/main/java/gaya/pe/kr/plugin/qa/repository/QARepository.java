package gaya.pe.kr.plugin.qa.repository;

import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QA;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QARepository {

    HashMap<Long, Question> questionIdByQuestId = new HashMap<>();
    HashMap<Long, Answer> answerIdByAnswerId = new HashMap<>();

    public List<Question> getQAUserQuestions(QAUser qaUser) {
        return getQAUserItems(qaUser, questionIdByQuestId);
    }

    public List<Answer> getQAUserAnswers(QAUser qaUser) {
        return getQAUserItems(qaUser, answerIdByAnswerId);
    }

    public List<Question> getAllQuestions() {
        return new ArrayList<>(questionIdByQuestId.values());
    }

    public List<Answer> getAllAnswers() {
        return new ArrayList<>(answerIdByAnswerId.values());
    }

    public List<Question> getAnsweredQuestion() {
        return questionIdByQuestId.values().stream().filter(Question::isAnswer).collect(Collectors.toList());
    }

    public List<Question> getNotAnsweredQuestion() {
        return questionIdByQuestId.values().stream().filter(question -> !question.isAnswer()).collect(Collectors.toList());
    }


    public List<QA> getQAs() {

        List<QA> result = new ArrayList<>();

        for (Answer answer : answerIdByAnswerId.values()) {

            long questionId = answer.getQuestionId();

            if ( questionIdByQuestId.containsKey(questionId) ) {
                result.add(new QA(questionIdByQuestId.get(questionId), answer));
            }

        }

        return result;

    }

    public void addAnswer(Answer answer) {
        this.answerIdByAnswerId.put(answer.getAnswerId(), answer);

        for (Question value : questionIdByQuestId.values()) {
            if ( value.getId() == answer.getQuestionId() ) {
                value.setAnswer(true);
                return;
            }
        }

    }

    public void addQuestion(Question question) {
        this.questionIdByQuestId.put(question.getId(), question);
    }

    public void removeAnswer(Answer answer) {

        this.answerIdByAnswerId.remove(answer.getAnswerId());

        Question removeTargetQuestion = null;
        for (Question value : questionIdByQuestId.values()) {
            if ( value.getId() == answer.getQuestionId() ) {
                removeTargetQuestion = value;
            }
        }

        if ( removeTargetQuestion != null ) {
            questionIdByQuestId.remove(removeTargetQuestion.getId());
        }

    }


    public void removeQuestion(Question question) {

        this.questionIdByQuestId.remove(question.getId());

        Answer removeTargetAnswer = null;
        for (Answer value : answerIdByAnswerId.values()) {
            if ( value.getQuestionId() == question.getId() ) {
                removeTargetAnswer = value;
            }
        }

        if ( removeTargetAnswer != null ) {
            answerIdByAnswerId.remove(removeTargetAnswer.getQuestionId());
        }

    }

    private  <T> List<T> getQAUserItems(QAUser qaUser, Map<Long, T> itemsById) {
        List<T> result = new ArrayList<>();

        for (T value : itemsById.values()) {
            QAUser itemUser = null;
            if (value instanceof Question) {
                itemUser = ((Question) value).getQaUser();
            } else if (value instanceof Answer) {
                itemUser = ((Answer) value).getAnswerPlayer();
            }

            if ( itemUser == null ) break;

            boolean add = false;

            if (qaUser.getGamePlayerName() != null) {
                if (itemUser.getGamePlayerName() != null) {
                    if (itemUser.getGamePlayerName().equals(qaUser.getGamePlayerName())) {
                        result.add(value);
                        add = true;
                    }
                }
            }

            if (!add) {
                if (qaUser.getDiscordPlayerUserId() != -1) {
                    if (qaUser.getDiscordPlayerUserId() == itemUser.getDiscordPlayerUserId()) {
                        result.add(value);
                    }
                }
            }
        }

        return result;
    }

}
