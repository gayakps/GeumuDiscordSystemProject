package gaya.pe.kr.qa.answer.data;


import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@ToString
public class Answer {

    long answerId;
    long questionId;
    String contents;
    QAUser answerPlayer;
    Date answerDate = new Date();

    @Setter boolean receivedToQuestionPlayer;
    @Setter boolean receiveReward;

    public Answer(long answerId, long questionId, String contents, QAUser answerPlayer) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.contents = contents;
        this.answerPlayer = answerPlayer;
    }

    public Answer(long answerId, long questionId, String contents, QAUser answerPlayer, Date answerDate, boolean receivedToQuestionPlayer, boolean receiveReward) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.contents = contents;
        this.answerPlayer = answerPlayer;
        this.answerDate = answerDate;
        this.receivedToQuestionPlayer = receivedToQuestionPlayer;
        this.receiveReward = receiveReward;
    }
}
