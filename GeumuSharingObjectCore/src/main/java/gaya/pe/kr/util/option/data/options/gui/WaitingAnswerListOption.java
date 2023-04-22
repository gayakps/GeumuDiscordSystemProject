package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.anno.RequirePlaceHolder;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitingAnswerListOption extends AbstractOption {

    public WaitingAnswerListOption(Map<String, Object> dataKeyValue) {
        super(dataKeyValue, OptionType.WAITING_ANSWER_LIST_GUI);
    }

    @RequirePlaceHolder(placeholders = {"%question_content%"})
    public String getWaitingAnswerListRemainQuestionName() {
        return (String) getNestedSectionKey("GUI", "waiting_answer_list_remain_question").get("name");
    }

    @RequirePlaceHolder(placeholders = {"%question_number%", "%question_playername%", "%question_time%"})
    public List<String> getWaitingAnswerListRemainQuestionLore() {
        return getList("GUI.waiting_answer_list_remain_question.lore");
    }

    public String getWaitingAnswerListMyAnswerInfoName() {
        return (String) getNestedSectionKey("GUI", "waiting_answer_list_my_answer_info").get("name");
    }

    @RequirePlaceHolder(placeholders = {"%answer_count_yesterday%", "%answer_count_daily%", "%answer_count_weekly%", "%answer_count_monthly%", "%answer_count_total%", "%reward_count%"})
    public List<String> getWaitingAnswerListMyAnswerInfoLore() {
        return getList("GUI.waiting_answer_list_my_answer_info.lore");
    }

    public String getWaitingAnswerListWeeklyAnswerRankingName() {
        return (String) getNestedSectionKey("GUI", "waiting_answer_list_weekly_answer_ranking").get("name");
    }

    @RequirePlaceHolder(placeholders = {"%answer_top_player_weekly_1%", "%answer_top_player_weekly_2%", "%answer_top_player_weekly_3%", "%answer_top_count_weekly_4%", "%answer_top_count_weekly_5%"})
    public List<String> getWaitingAnswerListWeeklyAnswerRankingLore() {
        return getList("GUI.waiting_answer_list_weekly_answer_ranking.lore");
    }
}
