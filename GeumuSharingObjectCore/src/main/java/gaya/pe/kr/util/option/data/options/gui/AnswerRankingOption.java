package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.anno.RequirePlaceHolder;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AnswerRankingOption extends AbstractOption {
    public AnswerRankingOption(HashMap<String, Object> dataKeyValue, OptionType optionType) {
        super(dataKeyValue, optionType);
    }

    public String getDailyAnswerRanking() {
        return (String) getNestedSectionKey("GUI","daily_answer_ranking").get("name");
    }

    public String getWeeklyAnswerRanking() {
        return (String) getNestedSectionKey("GUI","weekly_answer_ranking").get("name");
    }


    public String getMonthlyAnswerRanking() {
        return (String) getNestedSectionKey("GUI","monthly_answer_ranking").get("name");
    }


    public String getTotalAnswerRanking() {
        return (String) getNestedSectionKey("GUI","total_answer_ranking").get("name");
    }

    /**
     * PlaceHolder
     * %playername%
     * %ranking%
     */
    @RequirePlaceHolder( placeholders = {"%playername%", "%ranking%"})
    public String getAnswerRankingInfoName() {
        return (String) getNestedSectionKey("GUI", "answer_ranking_info").get("name");
    }


    /**
     * PlaceHolder
     - "어제 답변수: %answer_count_yesterday%"
     - "일간 답변수: %answer_count_daily%"
     - "주간 답변수: %answer_count_weekly%"
     - "월간 답변수: %answer_count_monthly%"
     - "전체 기간 답변수: %answer_count_total%"
     */
    @RequirePlaceHolder( placeholders = { "%answer_count_yesterday%", "%answer_count_daily%", "%answer_count_weekly%", "%answer_count_monthly%", "%answer_count_total%"})
    public List<String> getAnswerRankingLore() {
        return getList("GUI.answer_ranking_info.lore");
    }

}
