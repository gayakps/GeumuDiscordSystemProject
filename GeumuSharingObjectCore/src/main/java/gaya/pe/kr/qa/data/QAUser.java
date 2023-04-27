package gaya.pe.kr.qa.data;


import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class QAUser implements Serializable {

    @Nullable String gamePlayerName;
    long discordPlayerUserId = -1;

    public QAUser(@NotNull String gamePlayerName) {
        this.gamePlayerName = gamePlayerName;
    }

    public QAUser(long discordPlayerUserId) {
        this.discordPlayerUserId = discordPlayerUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QAUser qaUser = (QAUser) o;

        if ( qaUser.getDiscordPlayerUserId() == getDiscordPlayerUserId() ) return true;

        if ( qaUser.getGamePlayerName() != null && getGamePlayerName() != null ) {
            return qaUser.getGamePlayerName().equals(getGamePlayerName());
        }

        return false;
    }

}
