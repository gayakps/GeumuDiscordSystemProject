package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;

public class CommonlyUsedButtonOption extends AbstractOption {

    public CommonlyUsedButtonOption(HashMap<String, Object> dataKeyValue, OptionType optionType) {
        super(dataKeyValue, optionType);
    }

    public String getPreviousPageButtonType() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_previous_page", "type").get("type");
    }

    public String getPreviousPageButtonName() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_previous_page", "name").get("name");
    }

    public String getNextPageButtonType() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_next_page", "type").get("type");
    }

    public String getNextPageButtonName() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_next_page", "name").get("name");
    }

    public String getAnswerRewardInfoButtonType() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_answer_reward_info", "type").get("type");
    }

    public String getAnswerRewardInfoButtonName() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_answer_reward_info", "name").get("name");
    }

    public List<String> getAnswerRewardInfoButtonLore() {
        return getList("GUI.commonly_used_button_answer_reward_info.lore");
    }

    public String getQuestionHelpButtonType() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_question_help", "type").get("type");
    }

    public String getQuestionHelpButtonName() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_question_help", "name").get("name");
    }

    public List<String> getQuestionHelpButtonLore() {
        return getList("GUI.commonly_used_button_question_help.lore");
    }
}
