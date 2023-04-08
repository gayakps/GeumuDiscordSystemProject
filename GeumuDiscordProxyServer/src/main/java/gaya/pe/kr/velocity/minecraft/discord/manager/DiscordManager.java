package gaya.pe.kr.velocity.minecraft.discord.manager;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
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
