package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;

public class PlayerQuestionListOption extends AbstractOption {

    public PlayerQuestionListOption(HashMap<String, Object> dataKeyValue, OptionType optionType) {
        super(dataKeyValue, optionType);
    }

    public String getRemainQuestionName() {
        return (String) getNestedSectionKey("GUI", "player_question_list_remain_question", "name").get("name");
    }

    public List<String> getRemainQuestionLore() {
        return getList("GUI.player_question_list_remain_question.lore");
    }

    public String getAnsweredQuestionName() {
        return (String) getNestedSectionKey("GUI", "player_question_list_answered_question", "name").get("name");
    }

    public List<String> getAnsweredQuestionLore() {
        return getList("GUI.player_question_list_answered_question.lore");
    }

    public String getDailyQuestionRankingName() {
        return (String) getNestedSectionKey("GUI", "player_question_list_daily_question_ranking", "name").get("name");
    }

    public List<String> getDailyQuestionRankingLore() {
        return getList("GUI.player_question_list_daily_question_ranking.lore");
    }

    public String getPlayerQuestionInfoName() {
        return (String) getNestedSectionKey("GUI", "player_question_list_player_question_info", "name").get("name");
    }

    public List<String> getPlayerQuestionInfoLore() {
        return getList("GUI.player_question_list_player_question_info.lore");
    }
}
