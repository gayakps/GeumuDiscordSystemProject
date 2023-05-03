package gaya.pe.kr.plugin.qa.conversation;

import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
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

@Getter
public class AnswerConversation extends StringPrompt {

    static ConversationFactory conversationFactory = new ConversationFactory(GeumuDiscordSystem.getPlugin());

    Question question;
    Player player;

    public AnswerConversation(Question question, Player player) {
        this.question = question;
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

            NetworkManager networkManager = NetworkManager.getInstance();
            ConfigOption configOption = OptionManager.getInstance().getConfigOption();

            PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest = new PlayerTransientProceedingAnswerRequest(question.getId(), input, player);
            networkManager.sendPacket(playerTransientProceedingAnswerRequest, player1, targetPlayer -> {
                targetPlayer.sendRawMessage("전달 성공~");
                String[] soundData= configOption.getAnswerSendSuccessSound().split(":");
                player1.playSound(player1.getLocation(), Sound.valueOf(soundData[0]), Integer.parseInt(soundData[1]), Integer.parseInt(soundData[2])); // 사운드 입력

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
