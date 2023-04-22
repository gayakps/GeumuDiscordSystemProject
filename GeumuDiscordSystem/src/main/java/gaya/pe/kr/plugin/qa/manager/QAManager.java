package gaya.pe.kr.plugin.qa.manager;

/**
 * 전반적인 질문 및 퀘스트 시스템을 다루는 장소,
 */
public class QAManager {

    private static class SingleTon {
        private static final QAManager QA_MANAGER = new QAManager();

    }

    public static QAManager getInstance() {

        return SingleTon.QA_MANAGER;

    }



}
