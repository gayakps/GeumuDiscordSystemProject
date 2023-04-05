package gaya.pe.kr.minecraft.discord.data;

import gaya.pe.kr.network.packet.bound.client.DiscordAuthenticationRequest;
import gaya.pe.kr.util.TimeUtil;
import jdk.internal.net.http.common.Log;
import lombok.Getter;
import org.apache.log4j.Logger;

import java.util.Date;



@Getter
public class DiscordAuthentication {

    private static final Logger logger = Logger.getLogger(DiscordAuthenticationRequest.class);

    String playerName;
    Date expiredDate;
    int code;

    public DiscordAuthentication(String playerName, int code, int expireSecond) {
        this.playerName = playerName;
        this.code = code;
        this.expiredDate = TimeUtil.getAfterSecTime(expireSecond);
        logger.info(String.format("Player : [%s] | Code : [%d] | ExpiredDate : [%s]", playerName, code, getExpiredDate()));
    }

    public boolean isExpired() {

        return TimeUtil.getTimeDiffSec(expiredDate) > 0;

    }

    public boolean isEqualCodeAndPlayerName(DiscordAuthenticationRequest discordAuthenticationRequest) {
        return discordAuthenticationRequest.getAuthenticationCode() == code
                && discordAuthenticationRequest.getPlayerName().equals(playerName);
    }

}
