package gaya.pe.kr.velocity.minecraft.discord.data;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.util.TimeUtil;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;

import java.util.Date;

@Getter
public class DiscordAuthentication {

    String playerName;
    long discordId;
    Date expiredDate;
    int code;

    public DiscordAuthentication(String playerName, long discordId, int expireSecond) {
        this.playerName = playerName;
        this.discordId = discordId;
        this.code = Integer.parseInt(RandomStringUtils.randomNumeric(8));
        this.expiredDate = TimeUtil.getAfterSecTime(expireSecond);
        System.out.printf("Player : [%s] | Code : [%d] | ExpiredDate : [%s]\n", playerName, code, getExpiredDate());
    }

    public boolean isExpired() {
        return TimeUtil.getTimeDiffSec(expiredDate) > 0;
    }

    public boolean isEqualCodeAndPlayerName(DiscordAuthenticationRequest discordAuthenticationRequest) {
        return discordAuthenticationRequest.getAuthenticationCode() == code
                && discordAuthenticationRequest.getPlayerName().equals(playerName);
    }

}
