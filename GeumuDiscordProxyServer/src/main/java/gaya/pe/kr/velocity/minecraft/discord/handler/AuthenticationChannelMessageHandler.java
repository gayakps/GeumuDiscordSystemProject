package gaya.pe.kr.velocity.minecraft.discord.handler;

import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
import gaya.pe.kr.velocity.minecraft.discord.exception.NonExistPlayerException;
import gaya.pe.kr.velocity.minecraft.discord.exception.NotExpiredDiscordAuthenticationException;
import gaya.pe.kr.velocity.minecraft.discord.handler.abs.MessageChannelHandler;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class AuthenticationChannelMessageHandler extends MessageChannelHandler {


    DiscordManager discordManager = DiscordManager.getInstance();
    ServerOptionManager serverOptionManager = ServerOptionManager.getInstance();

    QuestionManager questionManager = QuestionManager.getInstance();
    QAUserManager qaUserManager = QAUserManager.getInstance();
    public AuthenticationChannelMessageHandler(TextChannel textChannel) {
        super(textChannel);
    }

    @Override
    protected void handleEvent(MessageReceivedEvent event) {

        Message receivedMessage = event.getMessage();
        User user = receivedMessage.getAuthor();
        long userId = user.getIdLong(); // 사용자 고유 ID를 가져옵니다.

        String prefix = "!";

        String receivedMessageContent = receivedMessage.getContentRaw();
        MessageChannel messageChannel = event.getChannel();
        System.out.printf("[ID : %s] Message Type : %s 내용 : %s\n", receivedMessage.getId(), receivedMessage.getType().name(), receivedMessageContent);

        System.out.println(receivedMessageContent + " << 수신" + receivedMessageContent.startsWith(prefix));

        if ( receivedMessageContent.isEmpty() || receivedMessageContent.isBlank() ) {
            receivedMessageContent = "!인증 gaya_kps"; // TODO 제거 해야
        }

        if ( !receivedMessageContent.startsWith(prefix)) {
            discordManager.sendMessageAndRemove(messageChannel, "```해당 채팅방에서의 모든 내용은 '!' 을 붙여주세요```", 3000, true, receivedMessage);
            return;
        }

        if ( receivedMessageContent.contains("!인증")) {

            ConfigOption configOption = serverOptionManager.getConfigOption();
            String playerName = receivedMessageContent.replace("!인증", "").trim();
            ;
            try {

                VelocityThreadUtil.delayTask(() -> {
                    receivedMessage.delete().queue();
                }, 3000);

                if ( discordManager.isAuthenticationPlayer(playerName) ) {
                    user.openPrivateChannel().queue((PrivateChannel privateChannel) -> {
                        privateChannel.sendMessage("```이미 등록되어있는 사용자의 이름입니다```").queue();
                    });
                    return;
                }

                if ( qaUserManager.existUser(userId) ) {
                    user.openPrivateChannel().queue((PrivateChannel privateChannel) -> {
                        privateChannel.sendMessage("```이미 인증을 완료한 계정입니다```").queue();
                    });
                    return;
                }

                DiscordAuthentication discordAuthentication = discordManager.generateDiscordAuthentication(playerName, userId);

                for (String authenticationCodeGenerationSuccess : configOption.getAuthenticationCodeGenerationSuccess()) {
                    user.openPrivateChannel().queue((PrivateChannel privateChannel) -> {

                        privateChannel.sendMessage(authenticationCodeGenerationSuccess
                                .replace("%authentication_code%", Long.toString(discordAuthentication.getDiscordId()))
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

            }



        }
    }
}
