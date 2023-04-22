package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.anno.RequirePlaceHolder;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;

public class PlayerAnswerListOption extends AbstractOption {

    public PlayerAnswerListOption(HashMap<String, Object> dataKeyValue, OptionType optionType) {
        super(dataKeyValue, optionType);
    }


    @RequirePlaceHolder( placeholders = {"%question_contents%"} )
    public String getAnsweredQuestionName() {
        return (String) getNestedSectionKey("GUI", "player_answer_list_answered_question", "name").get("name");
    }

    /**
     *       - ''
     *       - 'A: %answer_content%'
     *       - ''
     *       - '질문 번호: #%question_number%'
     *       - '질문자: %question_playername%'
     *       - '질문한 시간: %question_time%'
     *       - '답변한 시간: %answer_time%'
     *       - ''
     *       - '답변자: %answer_playername%'
     */
    @RequirePlaceHolder( placeholders = {"%answer_content%", "%question_number%", "%question_playername%", "%question_time%", "%answer_time%", "%answer_playername%"} )
    public List<String> getAnsweredQuestionLore() {
        return getList("GUI.player_answer_list_answered_question.lore");
    }


    @RequirePlaceHolder( placeholders = {"%playername%"})
    public String getPlayerAnswerInfoName() {
        return (String) getNestedSectionKey("GUI", "player_answer_list_player_answer_info", "name").get("name");
    }

    /**
     *     lore:
     *       - '어제 답변수: %answer_count_yesterday%'
     *       - '일간 답변수: %answer_count_daily%'
     *       - '주간 답변수: %answer_count_weekly%'
     *       - '월간 답변수: %answer_count_monthly%'
     *       - '전체 기간 답변수: %answer_count_total%'
     *       - ''
     *       - '정산 받을 답변 보상 개수: %reward_count%개'
     */

    @RequirePlaceHolder( placeholders = {"%answer_count_yesterday%", "%answer_count_daily%", "%answer_count_weekly%", "%answer_count_monthly%", "%answer_count_total%", "%reward_count%"})
    public List<String> getPlayerAnswerInfoLore() {
        return getList("GUI.player_answer_list_player_answer_info.lore");
    }

    public String getWeeklyAnswerRankingName() {
        return (String) getNestedSectionKey("GUI", "player_answer_list_weekly_answer_ranking", "name").get("name");
    }

    public List<String> getWeeklyAnswerRankingLore() {
        return getList("GUI.player_answer_list_weekly_answer_ranking.lore");
    }
}

