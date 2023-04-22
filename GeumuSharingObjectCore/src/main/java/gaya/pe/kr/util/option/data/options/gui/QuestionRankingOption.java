package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;

public class QuestionRankingOption extends AbstractOption {

    public QuestionRankingOption(HashMap<String, Object> dataKeyValue, OptionType optionType) {
        super(dataKeyValue, optionType);
    }

    public String getDailyQuestionRanking() {
        return (String) getNestedSectionKey("GUI", "daily_question_ranking", "name").get("name");
    }

    public String getWeeklyQuestionRanking() {
        return (String) getNestedSectionKey("GUI", "weekly_question_ranking", "name").get("name");
    }

    public String getMonthlyQuestionRanking() {
        return (String) getNestedSectionKey("GUI", "monthly_question_ranking", "name").get("name");
    }

    public String getTotalQuestionRanking() {
        return (String) getNestedSectionKey("GUI", "total_question_ranking", "name").get("name");
    }

    public String getQuestionRankingInfoName() {
        return (String) getNestedSectionKey("GUI", "question_ranking_info", "name").get("name");
    }

    public List<String> getQuestionRankingInfoLore() {
        return getList("GUI.question_ranking_info.lore");
    }
}
