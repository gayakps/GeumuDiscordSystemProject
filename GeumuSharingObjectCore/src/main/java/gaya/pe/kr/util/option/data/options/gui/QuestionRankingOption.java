package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.anno.RequirePlaceHolder;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionRankingOption extends AbstractOption {

    public QuestionRankingOption(Map<String, Object> dataKeyValue) {
        super(dataKeyValue, OptionType.QUESTION_RANKING_GUI);
    }

    public String getDailyQuestionRanking() {
        return (String) getNestedSectionKey("GUI", "daily_question_ranking").get("name");
    }

    public String getWeeklyQuestionRanking() {
        return (String) getNestedSectionKey("GUI", "weekly_question_ranking").get("name");
    }

    public String getMonthlyQuestionRanking() {
        return (String) getNestedSectionKey("GUI", "monthly_question_ranking").get("name");
    }

    public String getTotalQuestionRanking() {
        return (String) getNestedSectionKey("GUI", "total_question_ranking").get("name");
    }


    @RequirePlaceHolder( placeholders = {"%playername%", "%ranking%"})
    public String getQuestionRankingInfoName() {
        return (String) getNestedSectionKey("GUI", "question_ranking_info").get("name");
    }

    @RequirePlaceHolder(placeholders = {"%question_count_yesterday%", "%question_count_daily%", "%question_count_weekly%", "%question_count_monthly%", "%question_count_total%"})
    public List<String> getQuestionRankingInfoLore() {
        return getList("GUI.question_ranking_info.lore");
    }
}
