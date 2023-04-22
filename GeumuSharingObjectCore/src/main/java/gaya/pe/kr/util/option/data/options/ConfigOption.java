package gaya.pe.kr.util.option.data.options;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.anno.RequirePlaceHolder;
import gaya.pe.kr.util.option.type.OptionType;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ConfigOption extends AbstractOption {
    public ConfigOption(Map<String, Object> dataKeyValue) {
        super(dataKeyValue, OptionType.CONFIG);
    }

    // Database related methods
    public String getDbHost() {
        return (String) getNestedSectionKey("DataBase").get("DB_HOST");
    }

    public int getDbPort() {
        return (int) getNestedSectionKey("DataBase").get("DB_PORT");
    }

    public String getDbDatabase() {
        return (String) getNestedSectionKey("DataBase").get("DB_DATABASE");
    }

    public String getDbUsername() {
        return (String) getNestedSectionKey("DataBase").get("DB_USERNAME");
    }

    public String getDbPassword() {
        return (String) getNestedSectionKey("DataBase").get("DB_PASSWORD");
    }

    // Discord related methods
    public String getDiscordToken() {
        return (String) getNestedSectionKey("Discord").get("token");
    }

    public String getQuestionChannelId() {
        return (String) getNestedSectionKey("Discord").get("question_channel_id");
    }

    public String getAuthenticationChannelId() {
        return (String) getNestedSectionKey("Discord").get("authentication_channel_id");
    }

    public String getDiscordQuestionPrefix() {
        return (String) getNestedSectionKey("Discord").get("discord_question_prefix");
    }

    // Config related methods
    public int getQuestionMinLength() {
        return (int) getNestedSectionKey("config").get("question_min_length");
    }

    public int getQuestionMaxLength() {
        return (int) getNestedSectionKey("config").get("question_max_length");
    }

    public int getAnswerAnnouncerDelayWhenLogIn() {
        return (int) getNestedSectionKey("config").get("answer_announcer_delay_when_log_in");
    }

    public int getQuestionCooldown() {
        return (int) getNestedSectionKey("config").get("question_cooldown");
    }

    public int getAnswerCooldown() {
        return (int) getNestedSectionKey("config").get("answer_cooldown");
    }

    public int getRecentQuestionAnswerTime() {
        return (int) getNestedSectionKey("config").get("recent_question_answer_time");
    }

    public int getAuthenticationCodeExpireTime() {
        return (int) getNestedSectionKey("config").get("authentication_code_expire_time");
    }

    public int getAnswerReceiveTitleFadeInTime() {
        return (int) getNestedSectionKey("config").get("answer_receive_title_fade_in_time");
    }

    public int getAnswerReceiveTitleStayTime() {
        return (int) getNestedSectionKey("config").get("answer_receive_title_stay_time");
    }

    public int getAnswerReceiveTitleFadeOutTime() {
        return (int) getNestedSectionKey("config").get("answer_receive_title_fade_out_time");
    }

    public String getRewardGracePeriodDay() {
        return (String) getNestedSectionKey("config").get("reward_grace_period_day");
    }

    public String getRewardGracePeriodTime() {
        return (String) getNestedSectionKey("config").get("reward_grace_period_time");
    }

    @RequirePlaceHolder(placeholders = {"%playername%"})
    public List<String> getRewardCommand() {
        return getList("config.reward_command");
    }

    // Sound related methods
    public String getAnswerReceiveSuccessSound() {
        return (String) getNestedSectionKey("sound").get("answer_receive_success");
    }

    public String getAnswerSendSuccessSound() {
        return (String) getNestedSectionKey("sound").get("answer_send_success");
    }

    // Title related methods
    @RequirePlaceHolder(placeholders = {"%playername%", "%current_page%", "%total_page%"})
    public String getPlayerQuestionListTitle() {
        return (String) getNestedSectionKey("title").get("player_question_list");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%current_page%", "%total_page%"})
    public String getPlayerAnswerListTitle() {
        return (String) getNestedSectionKey("title").get("player_answer_list");
    }

    @RequirePlaceHolder(placeholders = {"%current_page%", "%total_page%"})
    public String getWaitingAnswerListTitle() {
        return (String) getNestedSectionKey("title").get("waiting_answer_list");
    }

    public String getInvalidPlayerName() {
        return (String) getNestedSectionKey("message").get("invalid_playername");
    }

    public String getInvalidAnswerNumber() {
        return (String) getNestedSectionKey("message").get("invalid_answer_number");
    }

    public String getInvalidAuthenticationCode() {
        return (String) getNestedSectionKey("message").get("invalid_authentication_code");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%question_content%"})
    public String getQuestionSuccessBroadcast() {
        return (String) getNestedSectionKey("message").get("question_success_broadcast");
    }

    // Help messages for different user levels
    public List<String> getQuestionHelpSuccessUser() {
        return getList("message.question_help_success.user");
    }

    public List<String> getQuestionHelpSuccessStaff() {
        return getList("message.question_help_success.staff");
    }

    public List<String> getQuestionHelpSuccessAdmin() {
        return getList("message.question_help_success.admin");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%question_content%"})
    public String getQuestSuccessBroadCast() {
        return (String) getNestedSectionKey("message").get("quest_success_broadcast");
    }

    @RequirePlaceHolder(placeholders = {"%question_min_length%"})
    public String getQuestionFailQuestionTooShort() {
        return (String) getNestedSectionKey("message").get("question_fail_question_too_short");
    }

    @RequirePlaceHolder(placeholders = {"%question_max_length%"})
    public String getQuestionFailQuestionTooLong() {
        return (String) getNestedSectionKey("message").get("question_fail_question_too_long");
    }

    @RequirePlaceHolder(placeholders = {"%question_cooldown%", "%%question_remain_cooldown"})
    public String getQuestionFailQuestionCooldown() {
        return (String) getNestedSectionKey("message").get("question_fail_question_cooldown");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%answer%"})

    public String getAnswerSendSuccessIfQuestionerOnlineBroadcast() {
        return (String) getNestedSectionKey("message").get("answer_send_success_if_questioner_online_broadcast");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%answer_count_total%"})
    public String getAnswerSendSuccessIfQuestionerOnline() {
        return (String) getNestedSectionKey("message").get("answer_send_success_if_questioner_online");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%question_content%"})
    public String getAnswerSendSuccessIfQuestionerOffline() {
        return (String) getNestedSectionKey("message").get("answer_send_success_if_questioner_offline");
    }

    public String getAnswerReceiveSuccessIfQuestionerOnlineTitle() {
        return (String) getNestedSectionKey("message").get("answer_receive_success_if_questioner_online_title");
    }

    public String getAnswerReceiveSuccessIfQuestionerOnlineSubtitle() {
        return (String) getNestedSectionKey("message").get("answer_receive_success_if_questioner_online_subtitle");
    }

    @RequirePlaceHolder(placeholders = {"%%arrived_answer_count%"})
    public String getAnswerReceiveSuccessIfQuestionerOfflineAndJoinAfter() {
        return (String) getNestedSectionKey("message").get("answer_receive_success_if_questioner_offline_and_join_after");
    }

    @RequirePlaceHolder(placeholders = {"%%arrived_answer_count%"})
    public String getAnswerReceiveSuccessIfQuestionerOfflineAndJoinAfterTitle() {
        return (String) getNestedSectionKey("message").get("answer_receive_success_if_questioner_offline_and_join_after_title");
    }

    public String getAnswerReceiveSuccessIfQuestionerOfflineAndJoinAfterSubtitle() {
        return (String) getNestedSectionKey("message").get("answer_receive_success_if_questioner_offline_and_join_after_subtitle");
    }

    @RequirePlaceHolder(placeholders = {"%%answer_cooldown%", "%answer_remain_cooldown%"})
    public String getAnswerSendFailAnswerCooldown() {
        return (String) getNestedSectionKey("message").get("answer_send_fail_answer_cooldown");
    }

    public String getAnswerSendFailCannotSelfAnswer() {
        return (String) getNestedSectionKey("message").get("answer_send_fail_can_not_self_answer");
    }

    public String getAnswerSendFailAlreadyAnsweredRecentQuestionAndNoRemainOldQuestion() {
        return (String) getNestedSectionKey("message").get("answer_send_fail_already_answered_recent_question_and_no_remain_old_question");
    }

    @RequirePlaceHolder(placeholders = {"%%remain_question%"})
    public String getAnswerSendFailAlreadyAnsweredRecentQuestionAndRemainOldQuestion() {
        return (String) getNestedSectionKey("message").get("answer_send_fail_already_answered_recent_question_and_remain_old_question");
    }

    public String getAnswerSendFailAlreadyAnsweredRecentQuestionAndRemainOldQuestionHoverMessage() {
        return (String) getNestedSectionKey("message").get("answer_send_fail_already_answered_recent_question_and_remain_old_question_hover_message");
    }

    public String getAnswerSendFailNotExistRecentQuestionAndNoRemainOldQuestion() {
        return (String) getNestedSectionKey("message").get("answer_send_fail_not_exist_recent_question_and_no_remain_old_question");
    }

    public String getAnswerSendFailNotExistRecentQuestionAndRemainOldQuestion() {
        return (String) getNestedSectionKey("message").get("answer_send_fail_not_exist_recent_question_and_remain_old_question");
    }

    public String getAnswerSendFailNotExistRecentQuestionAndRemainOldQuestionHoverMessage() {
        return (String) getNestedSectionKey("message").get("answer_send_fail_not_exist_recent_question_and_remain_old_question_hover_message");
    }

    // 여기서부터

    @RequirePlaceHolder(placeholders = {"%playername%", "%question_number%", "%answer_count_total%"})
    public String getQuestionNumberAnswerSendSuccessIfQuestionerOnline() {
        return (String) getNestedSectionKey("message").get("question_number_answer_send_success_if_questioner_online");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%question_number%", "%answer_count_total%"})
    public String getQuestionNumberAnswerSendSuccessIfQuestionerOffline() {
        return (String) getNestedSectionKey("message").get("question_number_answer_send_success_if_questioner_offline");
    }

    @RequirePlaceHolder(placeholders = {"%question_number%"})
    public String getQuestionNumberAnswerReceiveSuccessIfQuestionerOnline() {
        return (String) getNestedSectionKey("message").get("question_number_answer_receive_success_if_questioner_online");
    }

    public String getQuestionNumberAnswerReceiveSuccessIfQuestionerOnlineTitle() {
        return (String) getNestedSectionKey("message").get("question_number_answer_receive_success_if_questioner_online_title");
    }

    public String getQuestionNumberAnswerReceiveSuccessIfQuestionerOnlineSubtitle() {
        return (String) getNestedSectionKey("message").get("question_number_answer_receive_success_if_questioner_online_subtitle");
    }

    @RequirePlaceHolder(placeholders = {"%arrived_answer_count%"})
    public String getQuestionNumberAnswerReceiveSuccessIfQuestionerOfflineAndJoinAfter() {
        return (String) getNestedSectionKey("message").get("question_number_answer_receive_success_if_questioner_offline_and_join_after");
    }

    @RequirePlaceHolder(placeholders = {"%arrived_answer_count%"})
    public String getQuestionNumberAnswerReceiveSuccessIfQuestionerOfflineAndJoinAfterTitle() {
        return (String) getNestedSectionKey("message").get("question_number_answer_receive_success_if_questioner_offline_and_join_after_title");
    }

    public String getQuestionNumberAnswerReceiveSuccessIfQuestionerOfflineAndJoinAfterSubtitle() {
        return (String) getNestedSectionKey("message").get("question_number_answer_receive_success_if_questioner_offline_and_join_after_subtitle");
    }

    public String getQuestionNumberAnswerSendFailCanNotSelfAnswer() {
        return (String) getNestedSectionKey("message").get("question_number_answer_send_fail_can_not_self_answer");
    }

    @RequirePlaceHolder(placeholders = {"%answer_cooldown%", "%answer_remain_cooldown%"})
    public String getQuestionNumberAnswerSendFailAnswerCooldown() {
        return (String) getNestedSectionKey("message").get("question_number_answer_send_fail_answer_cooldown");
    }

    public String getAuthenticationSuccess() {
        return (String) getNestedSectionKey("message").get("authentication_success");
    }

    public String getAuthenticationFailAuthenticationCodeDoesNotMatch() {
        return (String) getNestedSectionKey("message").get("authentication_fail_authentication_code_does_not_match");
    }

    /**
     * authentication_code_expire_time << 초 동안만 유지됩니다
     * @return
     */
    @RequirePlaceHolder( placeholders = {"%authentication_code%", "%authentication_code_expire_time%"})
    public List<String> getAuthenticationCodeGenerationSuccess() {
        return getList("message.authentication_code_generation_success");
    }

    /**
     * 코드 만료 시간임 ( 현재로부터 몇초 남았는지 )
     * @return
     */
    @RequirePlaceHolder( placeholders = { "%authentication_code_expire_time%"})
    public List<String> getAuthenticationCodeGenerationFailAuthenticationCodeAlreadyGenerated() {
        return getList("message.authentication_code_generation_fail_authentication_code_already_generated");
    }

    public List<String> getAuthenticationCodeGenerationFailNotExistMinecraftAccount() {
        return getList("message.authentication_code_generation_fail_not_exist_minecraft_account");
    }

    public List<String> getAuthenticationCodeExpired() {
        return getList("message.authentication_code_expired");
    }

    public String getDiscordAuthenticationChannelHelpMessage() {
        return (String) getNestedSectionKey("message").get("discord_authentication_channel_help_message");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%prefix%"})
    public String getDiscordQuestionChannelPrefixHelpMessage() {
        return (String) getNestedSectionKey("message").get("discord_question_channel_prefix_help_message");
    }

    @RequirePlaceHolder(placeholders = {"%question_number%"})
    public String getRemoveQSuccessRemovePerson() {
        return (String) getNestedSectionKey("message").get("removeq_success_remove_person");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%question_number%"})
    public String getRemoveQSuccessHasBeenRemovedPerson() {
        return (String) getNestedSectionKey("message").get("removeq_success_has_been_removed_person");
    }

    public String getRemoveQFailNotExist() {
        return (String) getNestedSectionKey("message").get("removeq_fail_not_exist");
    }

    @RequirePlaceHolder(placeholders = {"%question_number%"})
    public String getRemoveASuccessRemovePerson() {
        return (String) getNestedSectionKey("message").get("removea_success_remove_person");
    }

    @RequirePlaceHolder(placeholders = {"%playername%", "%question_number%"})
    public String getRemoveASuccessHasBeenRemovedPerson() {
        return (String) getNestedSectionKey("message").get("removea_success_has_been_removed_person");
    }

    public String getRemoveAFailNotExist() {
        return (String) getNestedSectionKey("message").get("removea_fail_not_exist");
    }

    @RequirePlaceHolder(placeholders = {"%playername%"})
    public String getRemoveRewardSuccess() {
        return (String) getNestedSectionKey("message").get("remove_reward_success");
    }

    public String getReloadSuccess() {
        return (String) getNestedSectionKey("message").get("reload_success");
    }

    public String getAnswerPlayerNamePlaceholderAutoAnswer() {
        return (String) getNestedSectionKey("message").get("answer_playername_placeholder_auto_answer");
    }

    public String getRewardPaymentSuccessBroadcast() {
        return (String) getNestedSectionKey("message").get("reward_payment_success_broadcast");
    }


}



