package gaya.pe.kr.qa.data;

import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.question.data.Question;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class AllQuestionAnswers implements Serializable {

    List<Question> questions;
    List<Answer> answerList;

    public AllQuestionAnswers(List<Question> questions, List<Answer> answerList) {
        this.questions = questions;
        this.answerList = answerList;
    }

}
