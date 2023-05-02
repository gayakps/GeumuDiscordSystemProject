package gaya.pe.kr.qa.data;

import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.question.data.Question;
import lombok.Getter;

import java.io.Serializable;

/**
 *
 */

@Getter
public class QuestionAndAnswerMatch implements Serializable {

    Question question;
    Answer answer;

    public QuestionAndAnswerMatch(Question question, Answer answer) {
        this.question = question;
        this.answer = answer;
    }
}
