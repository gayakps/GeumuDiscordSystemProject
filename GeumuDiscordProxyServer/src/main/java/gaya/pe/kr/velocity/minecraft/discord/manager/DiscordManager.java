package gaya.pe.kr.velocity.minecraft.discord.manager;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiscordManager {


    private static class SingleTon {
        private static final DiscordManager DISCORD_MANAGER = new DiscordManager();
    }

    public static DiscordManager getInstance() {
        return SingleTon.DISCORD_MANAGER;
    }

    //989196517803962428 App ID
    //1ec2fc1a60cba9efcf51e1874e905cf9817a3df67c038b74438215fd40ba975f public key
    //MTA5MjMxNDMzOTU1OTYwODMzMA.GTyUf-.i0c5vl5ztAnw8psyVe6gao5pCSx5emzPltQ4WQ token

    HashMap<String, DiscordAuthentication> playerNameAsAuthentication = new HashMap<>();
    Set<String> questionAllowPlayerNameList = new HashSet<>();
    final String TOKEN = "OTg5MTk2NTE3ODAzOTYyNDI4.GmiO24.NAq6JH6S4ulMgXtjD4YAmPWwAgQiVPLt3QdSMc";

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    TextChannel authChannel;

    public void init() {

        try {
            JDA jda = JDABuilder.createDefault(TOKEN).build();

            // First, get the channel object for the specific channel


            jda.addEventListener(new ListenerAdapter() {
                @Override
                public void onGenericEvent(@NotNull GenericEvent event) {
                    if ( event instanceof ReadyEvent ) {
                        authChannel = jda.getTextChannelById("1090859961845825566");
                    }
                }
            });

// Add a message listener for the channel
            jda.addEventListener(new ListenerAdapter() {
                @Override
                public void onMessageReceived(MessageReceivedEvent event) {
                    MessageChannel channel = event.getChannel();
                    if (channel.equals(authChannel)) {

                        Message receivedMessage = event.getMessage();
                        User user = receivedMessage.getAuthor();

                        if ( user.isBot() ) return;

                        String receivedMessageContent = receivedMessage.getContentRaw();
                        System.out.printf("[ID : %s] Message Type : %s 내용 : %s\n", receivedMessage.getId(), receivedMessage.getType().name(), receivedMessageContent);


                        System.out.println(receivedMessageContent + " << 수신" + receivedMessageContent.startsWith("!"));

                        if ( receivedMessageContent.isEmpty() || receivedMessageContent.isBlank() ) {
                            receivedMessageContent = "!인증 gaya_kps";
                            System.out.println("변경");
                        }

                        if ( !receivedMessageContent.startsWith("!")) {

                            MessageAction errorReply = event.getChannel().sendMessage("```해당 채팅방에서의 모든 내용은 '!' 을 붙여주세요```");
                            Message errorReplyMessage = errorReply.complete();

                            VelocityThreadUtil.delayTask(() -> {
                                receivedMessage.delete().queue();
                                errorReplyMessage.delete().queue();
                            }, 3000);
                            return;
                        }

                        if ( receivedMessageContent.contains("!인증")) {

                            String playerName = receivedMessageContent.replace("!인증", "").trim();
                            DiscordAuthentication discordAuthentication = generateDiscordAuthentication(playerName);

                            if ( discordAuthentication != null ) {
                                user.openPrivateChannel().queue((PrivateChannel privateChannel) -> {
                                    privateChannel.sendMessage(String.format("```인증코드 : %d | 만료 일자 : %s```"
                                            , discordAuthentication.getCode()
                                            , simpleDateFormat.format(discordAuthentication.getExpiredDate()))
                                    ).queue();
                                });
                            } else {
                                user.openPrivateChannel().queue((PrivateChannel privateChannel) -> {
                                    privateChannel.sendMessage("```이미 만료된 코드거나, 이미 등록되어있는 사용자의 이름입니다```").queue();
                                });
                            }

                            VelocityThreadUtil.delayTask(() -> {
                                receivedMessage.delete().queue();
                            }, 3000);

                        }

                        // Check if the message is a reply
//                        if (message.getType().equals(MessageType.INLINE_REPLY)) {
//                            // Get the replied message
//                            Message repliedMessage = event.getMessage().getReferencedMessage();
//                            // Check if the replied message matches a certain condition
//                            if (repliedMessage.getContentDisplay().equals("test")) {
//                                // Do something with the reply
//                                // For example, reply to the reply message
//
//                                MessageAction messageAction = event.getChannel().sendMessage("I detected the reply!");
//
//                                Message removeTargetMessage = event.getChannel().retrieveMessageById(message.getId()).complete();
//
//                                VelocityThreadUtil.delayTask( ()-> {
//                                    removeTargetMessage.delete().queue();
//                                    System.out.println("메시지 제거");
//                                }, 5000);
//
//                                messageAction.queue();
//                            }
//                        }
                    }
                }
            });
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    @Nullable
    public DiscordAuthentication getDiscordAuthentication(DiscordAuthenticationRequest discordAuthenticationRequest) {

        for (Map.Entry<String, DiscordAuthentication> stringDiscordAuthenticationEntry : playerNameAsAuthentication.entrySet()) {
            String playerName = stringDiscordAuthenticationEntry.getKey();
            DiscordAuthentication discordAuthentication = stringDiscordAuthenticationEntry.getValue();
            if ( discordAuthenticationRequest.getPlayerName().equals(playerName) ) {
                return discordAuthentication;
            }
        }
        return null;

    }

    /**
     *
     * @param requestPlayerName
     * @return 생성에 성공 하거나, 생성에 실패 ( 이미 존재할 때 )
     */
    @Nullable
    public DiscordAuthentication generateDiscordAuthentication(String requestPlayerName) {

        if (playerNameAsAuthentication.containsKey(requestPlayerName) ) {

            DiscordAuthentication discordAuthentication = playerNameAsAuthentication.get(requestPlayerName);
            if ( !discordAuthentication.isExpired() ) {
                return null;
            }

        }

        if ( questionAllowPlayerNameList.contains(requestPlayerName) ) {
            return null;
        }

        DiscordAuthentication discordAuthentication = new DiscordAuthentication(requestPlayerName, 120);
        playerNameAsAuthentication.put(requestPlayerName, discordAuthentication);

        return discordAuthentication;

    }

    public void removeDiscordAuthentication(String requestPlayerName) {
        questionAllowPlayerNameList.add(requestPlayerName);
        playerNameAsAuthentication.remove(requestPlayerName);
    }

}
