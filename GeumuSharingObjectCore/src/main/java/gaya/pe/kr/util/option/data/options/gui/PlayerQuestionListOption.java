package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.anno.RequirePlaceHolder;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerQuestionListOption extends AbstractOption {

    public PlayerQuestionListOption(Map<String, Object> dataKeyValue) {
        super(dataKeyValue, OptionType.PLAYER_QUESTION_LIST_GUI);
    }

    @RequirePlaceHolder(placeholders = {"%question_content%"})
    public String getPlayerQuestionListRemainQuestionName() {
        return (String) getNestedSectionKey("GUI", "player_question_list_remain_question").get("name");
    }

    @RequirePlaceHolder(placeholders = {"%question_number%", "%question_playername%", "%question_time%"})
    public List<String> getPlayerQuestionListRemainQuestionLore() {
        return getList("GUI.player_question_list_remain_question.lore");
    }

    @RequirePlaceHolder(placeholders = {"%question_content%"})
    public String getPlayerQuestionListAnsweredQuestionName() {
        return (String) getNestedSectionKey("GUI", "player_question_list_answered_question").get("name");
    }

    @RequirePlaceHolder(placeholders = {"%answer_content%","%question_number%", "%question_playername%", "%question_time%", "%answer_time%", "%answer_playername%"})
    public List<String> getPlayerQuestionListAnsweredQuestionLore() {
        return getList("GUI.player_question_list_answered_question.lore");
    }

    public String getPlayerQuestionListDailyQuestionRankingName() {
        return (String) getNestedSectionKey("GUI", "player_question_list_daily_question_ranking").get("name");
    }

    @RequirePlaceHolder(placeholders = {
            "%question_top_player_daily_1%", "%question_top_count_daily_1%"
            ,"%question_top_player_daily_2%", "%question_top_count_daily_2%"
            ,"%question_top_player_daily_3%", "%question_top_count_daily_3%"
            ,"%question_top_player_daily_4%", "%question_top_count_daily_4%"
            ,"%question_top_player_daily_5%", "%question_top_count_daily_5%"
    })
    public List<String> getPlayerQuestionListDailyQuestionRankingLore() {
        return getList("GUI.player_question_list_daily_question_ranking.lore");
    }

    @RequirePlaceHolder(placeholders = {"%playername%"})
    public String getPlayerQuestionListPlayerQuestionInfoName() {
        return (String) getNestedSectionKey("GUI", "player_question_list_player_question_info").get("name");
    }

    @RequirePlaceHolder(placeholders = {"%question_count_yesterday%", "%question_count_daily%", "%question_count_weekly%", "%question_count_monthly%", "%question_count_total%"})
    public List<String> getPlayerQuestionListPlayerQuestionInfoLore() {
        return getList("GUI.player_question_list_player_question_info.lore");
    }
}
