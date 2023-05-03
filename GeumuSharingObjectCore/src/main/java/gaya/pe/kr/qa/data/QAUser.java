package gaya.pe.kr.qa.data;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
public class QAUser implements Serializable {

    String gamePlayerName;
    long discordPlayerUserId = -1;
    int rewardAmount;

    private UUID uuid;

    public QAUser(String gamePlayerName, long discordPlayerUserId, int rewardAmount, UUID uuid) {
        this.gamePlayerName = gamePlayerName;
        this.discordPlayerUserId = discordPlayerUserId;
        this.rewardAmount = rewardAmount;
        this.uuid = uuid;
    }

    public QAUser(@NotNull String gamePlayerName) {
        this.gamePlayerName = gamePlayerName;
        uuid = UUID.randomUUID();
    }

    public QAUser(@NotNull User discordUser) {
        this.discordPlayerUserId = discordUser.getIdLong();
        String username = discordUser.getName(); // 사용자 이름을 가져옵니다.
        String discriminator = discordUser.getDiscriminator(); // 사용자 태그 (예: #1234)를 가져옵니다.
        this.gamePlayerName = username + "#" + discriminator;
        uuid = UUID.randomUUID();
    }

    public void addRewardAmount() {
        rewardAmount++;
    }

    public int clearRewardAmount() {

        int temp = rewardAmount;

        rewardAmount = 0;
        return temp;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QAUser qaUser = (QAUser) o;

        if ( qaUser.getUuid().equals(getUuid()) ) return true;

        if (qaUser.getDiscordPlayerUserId() != -1 && getDiscordPlayerUserId() != -1) {
            if (qaUser.getDiscordPlayerUserId() == getDiscordPlayerUserId()) return true;
        }

        return qaUser.getGamePlayerName().equals(getGamePlayerName());
    }

}
