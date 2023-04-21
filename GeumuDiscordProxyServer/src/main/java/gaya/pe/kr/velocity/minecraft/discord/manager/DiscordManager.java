package gaya.pe.kr.velocity.minecraft.discord.manager;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
import gaya.pe.kr.velocity.minecraft.discord.exception.NonExistPlayerAuthenticationDataException;
import gaya.pe.kr.velocity.minecraft.discord.handler.InitHandler;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import lombok.Getter;
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


@Getter
public class DiscordManager {


    private static class SingleTon {
        private static final DiscordManager DISCORD_MANAGER = new DiscordManager();
    }

    public static DiscordManager getInstance() {
        return SingleTon.DISCORD_MANAGER;
    }

    HashMap<String, DiscordAuthentication> playerNameAsAuthentication = new HashMap<>(); // 디스코드 인증 대기자들
    HashMap<String, Long> playerNameAsDiscordUserId = new HashMap<>(); // 디스코드 인증 <유저명, 디스코드 ID>
    Set<String> questionAllowPlayerNameList = new HashSet<>();
    final String TOKEN = "OTg5MTk2NTE3ODAzOTYyNDI4.GmiO24.NAq6JH6S4ulMgXtjD4YAmPWwAgQiVPLt3QdSMc";

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    TextChannel authChannel;

    public void init() {

        try {
            JDA jda = JDABuilder.createDefault(TOKEN).build();
            jda.addEventListener(new InitHandler("1090859961845825566", "1094161426005884928"));
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
    public DiscordAuthentication generateDiscordAuthentication(String requestPlayerName, long discordId) {

        if (playerNameAsAuthentication.containsKey(requestPlayerName) ) {

            DiscordAuthentication discordAuthentication = playerNameAsAuthentication.get(requestPlayerName);
            if ( !discordAuthentication.isExpired() ) {
                return null;
            }

        }

        if ( questionAllowPlayerNameList.contains(requestPlayerName) ) {
            return null;
        }

        DiscordAuthentication discordAuthentication = new DiscordAuthentication(requestPlayerName, discordId,120);
        playerNameAsAuthentication.put(requestPlayerName, discordAuthentication);

        return discordAuthentication;

    }

    public void removeDiscordAuthentication(String requestPlayerName) {
        questionAllowPlayerNameList.add(requestPlayerName);
        playerNameAsAuthentication.remove(requestPlayerName);
    }

    public void addDiscordAuthenticationUser(DiscordAuthentication discordAuthentication) {

        long discordId = discordAuthentication.getDiscordId();
        String playerName = discordAuthentication.getPlayerName();
        playerNameAsDiscordUserId.put(playerName, discordId);
        //TODO DB 에 삽입하는 과정도 포함 되어야함

    }

    public boolean isAuthenticationPlayer(long discordId) {
       return playerNameAsDiscordUserId.containsValue(discordId);
    }

    public boolean isAuthenticationPlayer(String playerName) {
        return playerNameAsDiscordUserId.containsKey(playerName);
    }

    @Nullable
    public long getAuthenticationPlayerByDiscordId(String playerName) throws NonExistPlayerAuthenticationDataException {

        if (playerNameAsDiscordUserId.containsKey(playerName) ) {
            return playerNameAsDiscordUserId.get(playerName);
        }

        throw new NonExistPlayerAuthenticationDataException(String.format("[%s] 는 인증받지 않은 유저입니다", playerName));

    }

    @Nullable
    public String getAuthenticatedPlayerByDiscordId(long discordId) throws NonExistPlayerAuthenticationDataException  {

        for (Map.Entry<String, Long> stringLongEntry : playerNameAsDiscordUserId.entrySet()) {
            String playerName = stringLongEntry.getKey();
            long value = stringLongEntry.getValue();

            if ( value == discordId ) {
                return playerName;
            }
        }

        throw new NonExistPlayerAuthenticationDataException(String.format("[%d] 는 인증받지 않은 유저입니다", discordId));

    }

}
