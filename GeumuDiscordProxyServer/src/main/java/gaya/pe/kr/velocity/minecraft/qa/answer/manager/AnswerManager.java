package gaya.pe.kr.velocity.minecraft.qa.answer.manager;

import gaya.pe.kr.velocity.minecraft.qa.question.data.Question;

public class AnswerManager {


    private static class SingleTon {
        private static final AnswerManager ANSWER_MANAGER = new AnswerManager();
    }

    public static AnswerManager getInstance() {
        return SingleTon.ANSWER_MANAGER;
    }



    public void init() {

    }


    public void answer(Question question) {



    }




}
