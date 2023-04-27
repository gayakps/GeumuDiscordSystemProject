package gaya.pe.kr.qa.data;


import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class QAUser {

    String gamePlayerName;
    long discordPlayerUserId;

    public QAUser(String gamePlayerName) {
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
        if ( qaUser.getGamePlayerName().equals(getGamePlayerName()) ) return true;

        return false;
    }

}
