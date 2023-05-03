package gaya.pe.kr.velocity.minecraft.qa.manager;

import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.velocity.database.DBConnection;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
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

        DBConnection.taskTransaction(connection -> {

            String sql = "SELECT `user_profiles`.`player_name`,\n" +
                    "    `user_profiles`.`discord_user_id`,\n" +
                    "    `user_profiles`.`UUID`\n" +
                    "FROM `pixelmon_01_answer`.`user_profiles`;\n";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() ) {

                String playerName = resultSet.getString(1);
                long discordUserId = resultSet.getLong(2);
                String uuidStr = resultSet.getString(3);

                QAUser qaUser = new QAUser(playerName, discordUserId, UUID.fromString(uuidStr));
                userHashSet.add(qaUser);

                System.out.println(qaUser.toString() + " ADD --------------");

            }

            QuestionManager questionManager = QuestionManager.getInstance();
            questionManager.init();

        });


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

    public boolean existUser(UUID uuid) {

        for (QAUser qaUser : userHashSet) {
            if (qaUser.getUuid().equals(uuid)) {
                return true;
            }
        }

        return false;

    }

    public QAUser getQAUserByUUID(UUID uuid) {

        for (QAUser qaUser : userHashSet) {
            if (qaUser.getUuid().equals(uuid)) {
                return qaUser;
            }
        }

        return null;
    }

    public HashSet<QAUser> getAllQAUsers() {
        return new HashSet<>(userHashSet);
    }

    public QAUser getUser(String playerName) {

        for (QAUser qaUser : userHashSet) {
            if ( qaUser.getGamePlayerName().equals(playerName) ) {
                return qaUser;
            }
        }

        QAUser qaUser = new QAUser(playerName);
        updateQAUser(qaUser, false);
        return qaUser;

    }

    public QAUser getUser(long discordId) {

        for (QAUser qaUser : userHashSet) {
            if ( qaUser.getDiscordPlayerUserId() == discordId ) {
                return qaUser;
            }
        }

        QAUser qaUser = new QAUser(DiscordManager.getInstance().getJda().getUserById(discordId));
        updateQAUser(qaUser, false); // 중복되면 위에서 이미 걸림

        return qaUser;
    }

    public String getFullName(QAUser qaUser) {

        DiscordManager discordManager = DiscordManager.getInstance();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(qaUser.getGamePlayerName());

        if ( qaUser.getDiscordPlayerUserId() != -1 ) {
            User user = discordManager.getJda().getUserById(qaUser.getDiscordPlayerUserId());
            if ( user != null ) {
                String fullName = discordManager.getFullName(user);
                stringBuilder.append(fullName);
            }
        }

        String result = stringBuilder.toString().trim();
        if ( result.isEmpty() ) {
            return "UNKNOWN";
        }

        return result;

    }


    public void updateQAUser(QAUser qaUser, boolean duplicatedRemove ) {

        if ( duplicatedRemove ) {
            QAUser removeTargetQAUser = null;

            long paramDiscordPlayerUserId = qaUser.getDiscordPlayerUserId();
            String qaUserGamePlayerName = qaUser.getGamePlayerName();

            // Discord ID와 Game Player Name이 기존 사용자와 중복되는 경우, 사용자를 제거
            if ( paramDiscordPlayerUserId != -1 && existUser(paramDiscordPlayerUserId)) {

                for (QAUser user : userHashSet) {
                    if ( user.getDiscordPlayerUserId() == paramDiscordPlayerUserId ) {
                        removeTargetQAUser = user;
                        break;
                    }
                }

            } else if ( existUser(qaUserGamePlayerName) ) {
                for (QAUser user : userHashSet) {
                    if (qaUserGamePlayerName.equals(user.getGamePlayerName())) {
                        removeTargetQAUser = user;
                        break;
                    }
                }
            }

            // 중복 사용자가 존재하면 HashSet에서 제거
            if (removeTargetQAUser != null) {

                boolean result = DBConnection.taskTransaction(connection -> {
                    String uuid = qaUser.getUuid().toString();
                    String sql = "DELETE FROM `pixelmon_01_answer`.`user_profiles`\n" +
                            "WHERE UUID = ?;\n";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, uuid);
                    preparedStatement.executeUpdate();

                });

                if ( result ) {
                    userHashSet.remove(removeTargetQAUser);
                    QuestionManager questionManager = QuestionManager.getInstance();
                    questionManager.getQAUserQuestions(removeTargetQAUser).clear();
                    AnswerManager answerManager = AnswerManager.getInstance();
                    answerManager.getQAUserAnswers(removeTargetQAUser).clear();
                }

            }
        }

        DBConnection.taskTransaction(connection -> {

            String sql = "INSERT INTO `pixelmon_01_answer`.`user_profiles` " +
                    "(`player_name`, `discord_user_id`, `UUID`) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "`player_name` = VALUES(`player_name`), " +
                    "`discord_user_id` = VALUES(`discord_user_id`)";

            String playerName = qaUser.getGamePlayerName();
            long discordUserId = qaUser.getDiscordPlayerUserId();
            String uuid = qaUser.getUuid().toString();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, playerName);
            preparedStatement.setLong(2, discordUserId);
            preparedStatement.setString(3, uuid);

            preparedStatement.executeUpdate();

            userHashSet.add(qaUser); // 최종 삽입

        });


    }

}
