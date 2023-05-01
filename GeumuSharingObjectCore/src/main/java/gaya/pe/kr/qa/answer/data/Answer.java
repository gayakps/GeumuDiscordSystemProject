package gaya.pe.kr.qa.answer.data;


import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;

import java.util.Date;

@Getter
public class Answer {


    long answerId;
    long questionId;
    String contents;
    QAUser answerPlayer;

    Date answerDate = new Date();

    boolean receivedToQuestionPlayer;
    boolean receivedReward;

    public Answer(long answerId, long questionId, String contents, QAUser answerPlayer) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.contents = contents;
        this.answerPlayer = answerPlayer;
    }
}
