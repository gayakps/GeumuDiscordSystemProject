package gaya.pe.kr.velocity.minecraft.discord.manager;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
import gaya.pe.kr.velocity.minecraft.discord.exception.NotExpiredDiscordAuthenticationException;
import gaya.pe.kr.velocity.minecraft.discord.handler.InitHandler;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

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

    HashMap<String, DiscordAuthentication> playerNameAsAuthentication = new HashMap<>(); // 디스코드 인증 대기자들
//    final String TOKEN = "OTg5MTk2NTE3ODAzOTYyNDI4.GmiO24.NAq6JH6S4ulMgXtjD4YAmPWwAgQiVPLt3QdSMc";

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    JDA jda;
    @Setter TextChannel authChannel;
    @Setter TextChannel questionChannel;
    ServerOptionManager serverOptionManager = ServerOptionManager.getInstance();
    QAUserManager qaUserManager = QAUserManager.getInstance();
    public void init() {

        try {

            ConfigOption configOption = serverOptionManager.getConfigOption();
            jda = JDABuilder.createDefault(configOption.getDiscordToken()).build();
//            jda.addEventListener(new InitHandler("1090859961845825566", "1094161426005884928"));
            jda.addEventListener(new InitHandler(configOption.getAuthenticationChannelId(), configOption.getQuestionChannelId()));
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
    public DiscordAuthentication generateDiscordAuthentication(String requestPlayerName, long discordId) throws NotExpiredDiscordAuthenticationException {

        if (playerNameAsAuthentication.containsKey(requestPlayerName) ) {
            DiscordAuthentication discordAuthentication = playerNameAsAuthentication.get(requestPlayerName);
            if ( !discordAuthentication.isExpired() ) {
                throw new NotExpiredDiscordAuthenticationException("");
            }
        }


        DiscordAuthentication discordAuthentication = new DiscordAuthentication(requestPlayerName, discordId,120);
        playerNameAsAuthentication.put(requestPlayerName, discordAuthentication);

        return discordAuthentication;

    }

    public void removeDiscordAuthentication(String requestPlayerName) {
        playerNameAsAuthentication.remove(requestPlayerName);
    }

    public void addDiscordAuthenticationUser(DiscordAuthentication discordAuthentication) {

        long discordId = discordAuthentication.getDiscordId();
        String playerName = discordAuthentication.getPlayerName();
        QAUser qaUser = QAUserManager.getInstance().getUser(playerName);
        qaUser.setDiscordPlayerUserId(discordId);
        qaUserManager.updateQAUser(qaUser);
    }


    public boolean isAuthenticationPlayer(String playerName) {
        return qaUserManager.getUser(playerName).getDiscordPlayerUserId() != -1;
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public TextChannel getAuthChannel() {
        return authChannel;
    }

    public JDA getJda() {
        return jda;
    }

    public String getFullName(User user) {
        String username = user.getName(); // 사용자 이름을 가져옵니다.
        String discriminator = user.getDiscriminator(); // 사용자 태그 (예: #1234)를 가져옵니다.
        return username + "#" + discriminator;
    }


    public Message sendMessage(String message, TextChannel textChannel) {

        MessageAction messageAction = textChannel.sendMessage(message);
        return messageAction.complete();

    }

}
