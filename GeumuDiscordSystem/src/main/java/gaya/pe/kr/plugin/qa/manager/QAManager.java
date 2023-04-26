package gaya.pe.kr.plugin.qa.manager;

import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.qa.command.AnswerCommand;
import gaya.pe.kr.plugin.qa.command.AuthenticationCommand;
import gaya.pe.kr.plugin.qa.command.QuestionCommand;
import gaya.pe.kr.qa.answer.data.Answer;

import static gaya.pe.kr.plugin.GeumuDiscordSystem.registerCommand;

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

    public void init() {
        registerCommand("질문", new QuestionCommand());
        registerCommand("답변", new AnswerCommand());
        registerCommand("인증", new AuthenticationCommand());
    }



}