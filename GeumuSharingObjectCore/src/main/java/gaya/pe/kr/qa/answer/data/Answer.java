package gaya.pe.kr.qa.answer.data;


import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;

@Getter
public class Answer {


    int answerId;
    int questionId;

    String contents;

    QAUser answerPlayer;

    boolean receivedToQuestionPlayer;


}
