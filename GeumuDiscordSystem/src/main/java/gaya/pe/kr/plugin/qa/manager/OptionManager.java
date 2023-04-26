package gaya.pe.kr.plugin.qa.manager;

import gaya.pe.kr.util.option.data.options.AnswerPatternOptions;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OptionManager {

    private static class SingleTon {
        private static final OptionManager OPTION_MANAGER = new OptionManager();
    }

    public static OptionManager getInstance() {
        return SingleTon.OPTION_MANAGER;
    }

    AnswerRankingOption answerRankingOption;
    CommonlyUsedButtonOption commonlyUsedButtonOption;
    PlayerAnswerListOption playerAnswerListOption;
    PlayerQuestionListOption playerQuestionListOption;
    QuestionRankingOption questionRankingOption;
    WaitingAnswerListOption waitingAnswerListOption;

    AnswerPatternOptions answerPatternOptions;
    ConfigOption configOption;



}
