package gaya.pe.kr.qa.answer.data;


import lombok.Getter;

@Getter
public class Answer {


    int answerId;
    int questionId;

    String contents;
    String answerPlayerName;

    boolean receivedToQuestionPlayer;


}
