package gaya.pe.kr.qa.data;

import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.question.data.Question;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Queue;

/**
 *
 */

@Getter
public class QA {

    HashMap<Question, Answer> questionAnswerHashMap;
    public QA(HashMap<Question, Answer> questionAnswerHashMap) {
        this.questionAnswerHashMap = questionAnswerHashMap;
    }

}
