package gaya.pe.kr.velocity.minecraft.qa.manager;

import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.velocity.database.DBConnection;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.prefs.PreferencesFactory;

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

    public boolean existUser(String playerName) {
        for (QAUser qaUser : userHashSet) {
            if ( qaUser.getGamePlayerName().equals(playerName) ) {
                return true;
            }
        }
        return false;
    }

    public boolean existUser(long discordId) {

        for (QAUser qaUser : userHashSet) {
            if (qaUser.getDiscordPlayerUserId() == discordId) {
                return true;
            }
        }

        return false;
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

    public String getFullName(QAUser qaUser) {

        DiscordManager discordManager = DiscordManager.getInstance();
        StringBuilder stringBuilder = new StringBuilder();

        if ( qaUser.getGamePlayerName() != null ) {
            stringBuilder.append(qaUser.getGamePlayerName());
        }

        if ( qaUser.getDiscordPlayerUserId() != -1 ) {
            User user = discordManager.getJda().getUserById(qaUser.getDiscordPlayerUserId());
            if ( user != null ) {
                String fullName = discordManager.getFullName(user);
                stringBuilder.append(fullName);
            }
        }

        return stringBuilder.toString().trim();

    }

    public void addUser(QAUser qaUser) {

        boolean result = DBConnection.taskTransaction(connection -> {
            //TODO 데이터베이스에 QAUser 를 input 하는 과정을 추가 해야함
        });

        if ( result ) {
            userHashSet.add(qaUser);
        }


    }

    public void updateQAUser(QAUser qaUser) {

        boolean result = DBConnection.taskTransaction(connection -> {
            //TODO 데이터베이스에 QAUser 를 input Or Update 하는 과정을 추가 해야함
        });


    }

}
