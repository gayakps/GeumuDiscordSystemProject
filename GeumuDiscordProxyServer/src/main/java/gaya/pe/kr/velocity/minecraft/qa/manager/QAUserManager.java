package gaya.pe.kr.velocity.minecraft.qa.manager;

import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.velocity.database.DBConnection;

import java.util.HashSet;

public class QAUserManager {

    private static class SingleTon {
        private static final QAUserManager QA_MANAGER = new QAUserManager();
    }

    public static QAUserManager getInstance() {
        return SingleTon.QA_MANAGER;
    }

    HashSet<QAUser> userHashSet = new HashSet<>();

    public void init() {
        //TODO DB 에서 QAUser 를 Table 로 부터 가져와야함
    }


    public QAUser getUser(String playerName) {

        for (QAUser qaUser : userHashSet) {
            if ( qaUser.getGamePlayerName().equals(playerName) ) {
                return qaUser;
            }
        }

        QAUser qaUser = new QAUser(playerName);
        addUser(qaUser);

        return qaUser;

    }

    public QAUser getUser(long discordId) {
        for (QAUser qaUser : userHashSet) {
            if ( qaUser.getDiscordPlayerUserId() == discordId ) {
                return qaUser;
            }
        }

        QAUser qaUser = new QAUser(discordId);
        addUser(qaUser);

        return qaUser;
    }

    public void addUser(QAUser qaUser) {

        boolean result = DBConnection.taskTransaction(connection -> {
            //TODO 데이터베이스에 QAUser 를 input 하는 과정을 추가 해야함
        });

        if ( result ) {
            userHashSet.add(qaUser);
        }


    }

}
