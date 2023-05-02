package gaya.pe.kr.qa.data;

import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.question.data.Question;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Queue;

/**
 *
 */

@Getter
public class QA implements Serializable {

    Question question;
    Answer answer;

    public QA(Question question, Answer answer) {
        this.question = question;
        this.answer = answer;
    }
}
