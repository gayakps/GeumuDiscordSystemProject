package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonlyUsedButtonOption extends AbstractOption {

    public CommonlyUsedButtonOption(Map<String, Object> dataKeyValue) {
        super(dataKeyValue, OptionType.COMMONLY_USED_BUTTON_GUI);
    }

    public String getPreviousPageButtonType() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_previous_page").get("type");
    }

    public String getPreviousPageButtonName() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_previous_page").get("name");
    }

    public String getNextPageButtonType() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_next_page").get("type");
    }

    public String getNextPageButtonName() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_next_page").get("name");
    }

    public String getAnswerRewardInfoButtonType() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_answer_reward_info").get("type");
    }

    public String getAnswerRewardInfoButtonName() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_answer_reward_info").get("name");
    }

    public List<String> getAnswerRewardInfoButtonLore() {
        return getList("GUI.commonly_used_button_answer_reward_info.lore");
    }

    public String getQuestionHelpButtonType() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_question_help").get("type");
    }

    public String getQuestionHelpButtonName() {
        return (String) getNestedSectionKey("GUI", "commonly_used_button_question_help").get("name");
    }

    public List<String> getQuestionHelpButtonLore() {
        return getList("GUI.commonly_used_button_question_help.lore");
    }
}
