package gaya.pe.kr.util.option.data.options.gui;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.anno.RequirePlaceHolder;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerAnswerListOption extends AbstractOption {

    public PlayerAnswerListOption(Map<String, Object> dataKeyValue) {
        super(dataKeyValue, OptionType.PLAYER_ANSWER_LIST_GUI);
    }


    @RequirePlaceHolder( placeholders = {"%question_contents%"} )
    public String getAnsweredQuestionName() {
        return (String) getNestedSectionKey("GUI", "player_answer_list_answered_question").get("name");
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
        return (String) getNestedSectionKey("GUI", "player_answer_list_player_answer_info").get("name");
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
        return (String) getNestedSectionKey("GUI", "player_answer_list_weekly_answer_ranking").get("name");
    }

    /**
     *     lore:
     *       - '1. %answer_top_player_weekly_1% | 답변수 %answer_top_count_weekly_1%회'
     *       - '2. %answer_top_player_weekly_2% | 답변수 %answer_top_count_weekly_2%회'
     *       - '3. %answer_top_player_weekly_3% | 답변수 %answer_top_count_weekly_3%회'
     *       - '4. %answer_top_player_weekly_4% | 답변수 %answer_top_count_weekly_4%회'
     *       - '5. %answer_top_player_weekly_5% | 답변수 %answer_top_count_weekly_5%회'
     *       - ''
     *       - '클릭 시 자세한 답변 랭킹 순위를 확인합니다.'
     * @return
     */
    @RequirePlaceHolder( placeholders = {"%answer_top_player_weekly_1%", "%answer_top_player_weekly_2%", "%answer_top_player_weekly_3%", "%answer_top_player_weekly_4%", "%answer_top_player_weekly_5%"})
    public List<String> getWeeklyAnswerRankingLore() {
        return getList("GUI.player_answer_list_weekly_answer_ranking.lore");
    }
}

