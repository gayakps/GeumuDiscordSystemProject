package gaya.pe.kr.velocity.minecraft.discord.handler;

import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
import gaya.pe.kr.velocity.minecraft.discord.exception.NonExistPlayerException;
import gaya.pe.kr.velocity.minecraft.discord.exception.NotExpiredDiscordAuthenticationException;
import gaya.pe.kr.velocity.minecraft.discord.handler.abs.MessageChannelHandler;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class AuthenticationChannelMessageHandler extends MessageChannelHandler {


    DiscordManager discordManager;
    ServerOptionManager serverOptionManager;

    QuestionManager questionManager;
    QAUserManager qaUserManager;
    public AuthenticationChannelMessageHandler(TextChannel textChannel) {
        super(textChannel);
        qaUserManager = QAUserManager.getInstance();
        questionManager = QuestionManager.getInstance();
        discordManager = DiscordManager.getInstance();
        serverOptionManager = ServerOptionManager.getInstance();
        System.out.println("AuthenticationChannelMessageHandler created");
    }

    /**
     * 디스코드로부터 요청된 인증을 처리하는 과정임
     * @see gaya.pe.kr.velocity.minecraft.network.handler.MinecraftClientPacketHandler 에서
     * @see gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest 로 처리함
     *
     * @param event
     */
    @Override
    protected void handleEvent(MessageReceivedEvent event) {

        Message receivedMessage = event.getMessage();
        User user = receivedMessage.getAuthor();
        long userId = user.getIdLong(); // 사용자 고유 ID를 가져옵니다.

        String prefix = "!";

        String receivedMessageContent = receivedMessage.getContentRaw();
        MessageChannel messageChannel = event.getChannel();

        ConfigOption configOption = serverOptionManager.getConfigOption();

        if ( !receivedMessageContent.startsWith(prefix)) {
            discordManager.sendMessageAndRemove(messageChannel, String.format("```%s```", configOption.getDiscordAuthenticationChannelHelpMessage()), 5000, true, receivedMessage);
            return;
        }

        if ( receivedMessageContent.contains("!인증")) {

            String playerName = receivedMessageContent.replace("!인증", "").trim();

            try {

                VelocityThreadUtil.delayTask(() -> receivedMessage.delete().queue(), 3000);

                if ( discordManager.isAuthenticationPlayer(playerName) ) {
                    discordManager.sendMessageAndRemove(messageChannel, "```이미 등록되어있는 사용자명입니다```", 5000, true, receivedMessage);
                    return;
                }

                if ( qaUserManager.existUser(userId) ) {
                    QAUser qaUser = qaUserManager.getUser(userId);
                    if ( !qaUser.getGamePlayerName().contains("#") ) { // # 이 있다면 디코로만 질문한 사람임
                        discordManager.sendMessageAndRemove(messageChannel, "```이미 인증을 완료한 계정입니다```", 5000, true, receivedMessage);

                        return;
                    }
                }

                DiscordAuthentication discordAuthentication = discordManager.generateDiscordAuthentication(playerName, user);

                for (String authenticationCodeGenerationSuccess : configOption.getAuthenticationCodeGenerationSuccess()) {
                    user.openPrivateChannel().queue((PrivateChannel privateChannel) -> {
                        privateChannel.sendMessage(authenticationCodeGenerationSuccess
                                .replace("%authentication_code%", Long.toString(discordAuthentication.getCode()))
                                .replace("%authentication_code_expire_time%", discordManager.getSimpleDateFormat().format(discordAuthentication.getExpiredDate()))
                        ).queue();
                    });
                }


            } catch ( NotExpiredDiscordAuthenticationException e ) {

                user.openPrivateChannel().queue((PrivateChannel privateChannel) -> {

                    long remainCodeExpireTime = TimeUtil.getTimeDiffSec(discordManager.getDiscordAuthentication(playerName).getExpiredDate()); // 만료 시간

                    for (String s : configOption.getAuthenticationCodeGenerationFailAuthenticationCodeAlreadyGenerated()) {
                        privateChannel.sendMessage(String.format("%s", s.replace("%authentication_code_expire_time%", Long.toString(Math.abs(remainCodeExpireTime))))).queue();
                    }

                });
            } catch ( NonExistPlayerException e ) {
                // 존재하지 않는 플레이어일때
                user.openPrivateChannel().queue((PrivateChannel privateChannel) -> {
                    for (String s : configOption.getAuthenticationCodeGenerationFailNotExistMinecraftAccount()) {
                        privateChannel.sendMessage(s).queue();
                    }
                });

                for (String s : configOption.getAuthenticationCodeGenerationFailNotExistMinecraftAccount()) {
                    discordManager.sendMessageAndRemove(messageChannel, "```"+s+"```", 5000, true, receivedMessage);
                }

            }

        }
        else {
            discordManager.sendMessageAndRemove(messageChannel, String.format("```%s```", configOption.getDiscordAuthenticationChannelHelpMessage()), 5000, true, receivedMessage);
        }
    }
}
