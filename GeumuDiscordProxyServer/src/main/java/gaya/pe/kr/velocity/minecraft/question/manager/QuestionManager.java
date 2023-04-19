package gaya.pe.kr.velocity.minecraft.question.manager;

public class QuestionManager {

    private static class SingleTon {
       private static final QuestionManager QUESTION_MANAGER = new QuestionManager();
    }

    public static QuestionManager getInstance() {
        return SingleTon.QUESTION_MANAGER;
    }



    public void init() {

    }



}
