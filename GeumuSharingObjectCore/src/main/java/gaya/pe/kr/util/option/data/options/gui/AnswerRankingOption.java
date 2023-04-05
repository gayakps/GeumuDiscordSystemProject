package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AnswerRankingOption extends AbstractOption {
    public AnswerRankingOption(HashMap<String, Object> dataKeyValue, OptionType optionType) {
        super(dataKeyValue, optionType);
    }

    public String dailyAnswerRanking() {
       return (String) this.getDataKeyValue().getOrDefault("GUI.daily_answer_ranking", "NONE");
    }

    public String weeklyAnswerRanking() {
        return (String) this.getDataKeyValue().getOrDefault("GUI.weekly_answer_ranking", "NONE");
    }

    public String monthlyAnswerRanking() {
        return (String) this.getDataKeyValue().getOrDefault("GUI.monthly_answer_ranking", "NONE");
    }

    public String totalAnswerRanking() {
        return (String) this.getDataKeyValue().getOrDefault("GUI.total_answer_ranking", "NONE");
    }

    public String answerRankingInfoName() {
        return (String) this.getDataKeyValue().getOrDefault("GUI.answer_ranking_info.name", "NONE");
    }

    public List<String> answerRankingInfoLore() {
        return new ArrayList<>();
    }

}
