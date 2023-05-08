package gaya.pe.kr.plugin.qa.conversation;

import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.plugin.util.UtilMethod;
import gaya.pe.kr.qa.answer.packet.client.PlayerRecentQuestionAnswerRequest;
import gaya.pe.kr.qa.answer.packet.client.PlayerTransientProceedingAnswerRequest;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@Getter
public class AnswerConversation extends StringPrompt {

    static ConversationFactory conversationFactory = new ConversationFactory(GeumuDiscordSystem.getPlugin());

    Question question;
    Player player;

    public AnswerConversation(Question question, Player player) {
        this.question = question;
        this.player = player;
    }

    public AnswerConversation(long questionId, Player player) {
        QARepository qaRepository = QAManager.getInstance().getQaRepository();

        for (Question allQuestion : qaRepository.getAllQuestions()) {
            if ( allQuestion.getId() == questionId ) {
                this.question = allQuestion;
            }
        }


        this.player = player;
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        return "채팅창에 답변을 입력해주세요";
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String input) {

        if ( conversationContext.getForWhom() instanceof Player ) {
            Player player1 = (Player) conversationContext.getForWhom();

            if ( input == null || input.length() == 0 ) {
                player1.sendRawMessage("§c답장을 입력해주세요");
                return null;
            }

            ConfigOption configOption = OptionManager.getInstance().getConfigOption();

            if ( question == null ) {
                player1.sendRawMessage(configOption.getInvalidQuestionNumber());
                return null;
            }

            NetworkManager networkManager = NetworkManager.getInstance();


            PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest = new PlayerTransientProceedingAnswerRequest(question.getId(), input, player);
            networkManager.sendPacket(playerTransientProceedingAnswerRequest, player1, targetPlayer -> {
                targetPlayer.sendRawMessage("전달 성공~");
                String[] soundData= configOption.getAnswerSendSuccessSound().split(":");
                SchedulerUtil.runLaterTask(()-> {
                    player1.playSound(player1.getLocation(), Sound.valueOf(soundData[0].toUpperCase(Locale.ROOT)), Integer.parseInt(soundData[1]), Integer.parseInt(soundData[2])); // 사운드 입력
                },1);
            });

        }

        return null;
    }

    public static void startConversation(StringPrompt stringPrompt, Player player) {
        player.closeInventory();
        Conversation conversation = conversationFactory
                .withFirstPrompt(stringPrompt)
                .withEscapeSequence("exit")
                .withTimeout(60)
                .buildConversation(player);
        conversation.begin();
    }

}
