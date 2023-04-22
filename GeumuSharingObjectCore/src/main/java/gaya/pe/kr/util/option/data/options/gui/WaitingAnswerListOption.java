package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;

public class WaitingAnswerListOption extends AbstractOption {

    public WaitingAnswerListOption(HashMap<String, Object> dataKeyValue, OptionType optionType) {
        super(dataKeyValue, optionType);
    }

    public String getWaitingAnswerListRemainQuestionName() {
        return (String) getNestedSectionKey("GUI", "waiting_answer_list_remain_question", "name").get("name");
    }

    public List<String> getWaitingAnswerListRemainQuestionLore() {
        return getList("GUI.waiting_answer_list_remain_question.lore");
    }

    public String getWaitingAnswerListMyAnswerInfoName() {
        return (String) getNestedSectionKey("GUI", "waiting_answer_list_my_answer_info", "name").get("name");
    }

    public List<String> getWaitingAnswerListMyAnswerInfoLore() {
        return getList("GUI.waiting_answer_list_my_answer_info.lore");
    }

    public String getWaitingAnswerListWeeklyAnswerRankingName() {
        return (String) getNestedSectionKey("GUI", "waiting_answer_list_weekly_answer_ranking", "name").get("name");
    }

    public List<String> getWaitingAnswerListWeeklyAnswerRankingLore() {
        return getList("GUI.waiting_answer_list_weekly_answer_ranking.lore");
    }
}
