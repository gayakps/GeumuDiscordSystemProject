package gaya.pe.kr.velocity.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class DBConnection {

    private static HikariDataSource dataSource;
    static List<String> tableCreateList = new ArrayList<>();

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void init(ConfigOption configOption) {

        initTableList();

        HikariConfig config = new HikariConfig(); // Hikari Connection Pool 을 이용하기 위해서 사용되는 Configuration 이라 보면됨, 기본 설정
//        config.setDriverClassName("com.mysql.cj.jdbc.Driver"); // 우리가 어떤 DBMS 를 사용할 것인지 => 저희는 Maria DB 이기때문에 mariadb jdbc driver 를 사용할거에요
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=UTF-8", configOption.getDbHost(), configOption.getDbPort(), configOption.getDbDatabase())); // 데이터 서버의 IP , port , DB 명
        config.setUsername(configOption.getDbUsername()); // config 에 등록된 관리자 계정명
        config.setPassword(configOption.getDbPassword()); // config 에 등록된 관리자 패스워드
//        config.addDataSourceProperty("leak-detection-threshold", "true"); // PreparedStatement Caching을 비활성화하고 있기 때문에, 이 옵션을 허용해줘야 아래의 옵션값들이 실제 DB에 영향을 줄 수 있다.
        config.addDataSourceProperty("cachePrepStmts", "true"); // PreparedStatement Caching을 비활성화하고 있기 때문에, 이 옵션을 허용해줘야 아래의 옵션값들이 실제 DB에 영향을 줄 수 있다.
        // 여기서 PreparedStatement 는 Connection 을 가져와 db에 수정 삽입 제거 등 data 관리를 하기 위한 객체라고 보면된다.
        config.addDataSourceProperty("prepStmtCacheSize", "350"); // MySQL 드라이버가 Connection마다 캐싱할 PreparedStatement의 개수를 지정하는 옵션이다. HikariCP에서는 250 ~ 500개 정도를 추천한다
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // default : 256 max : 2048 크게 중요하지 않다. 데이터 캐싱 관련이며 PreparedStatement 와 연관되어있다.

        config.setMaximumPoolSize(20);

        config.setMaxLifetime(580000);
        config.setIdleTimeout(10000);
        config.setConnectionTimeout(10000);
        config.setValidationTimeout(10000);
        config.setMinimumIdle(20);
        config.setPoolName("그무 시스템");
        config.setLeakDetectionThreshold(24000);

        dataSource = new HikariDataSource(config);

        initTableList();
        System.out.println("[ GAYA_SOFT ] DB 접속 성공");

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = null;
            for (String tableSQL : tableCreateList) {
                if (preparedStatement != null) {
                    preparedStatement.clearParameters();
                    preparedStatement.clearBatch();
                }
                preparedStatement = connection.prepareStatement(tableSQL);
                preparedStatement.executeUpdate();
            }

            DiscordManager.getInstance().init();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    private static void initTableList() {

        tableCreateList.add("CREATE TABLE IF NOT EXISTS `user_profiles` (\n" +
                "  `player_name` varchar(45) NOT NULL,\n" +
                "  `discord_user_id` int DEFAULT '-1',\n" +
                "  `reward_amount` int DEFAULT '0',\n" +
                "  `UUID` varchar(36) NOT NULL,\n" +
                "  PRIMARY KEY (`UUID`),\n" +
                "  UNIQUE KEY `discord_user_id_UNIQUE` (`discord_user_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;\n");

        tableCreateList.add("CREATE TABLE `questions` (\n" +
                "  `id` int NOT NULL,\n" +
                "  `qauser_uuid` varchar(36) NOT NULL,\n" +
                "  `contents` varchar(500) NOT NULL,\n" +
                "  `question_date` datetime NOT NULL,\n" +
                "  `discord_message_number` int NOT NULL,\n" +
                "  `answer` tinyint DEFAULT '0',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `questions_user_profiles_UUID_fk` (`qauser_uuid`),\n" +
                "  CONSTRAINT `questions_user_profiles_UUID_fk` FOREIGN KEY (`qauser_uuid`) REFERENCES `user_profiles` (`UUID`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;\n");

        tableCreateList.add("CREATE TABLE `answers` (\n" +
                "  `id` int NOT NULL,\n" +
                "  `question_id` int NOT NULL,\n" +
                "  `contents` varchar(500) NOT NULL,\n" +
                "  `answer_qauser_uuid` varchar(36) NOT NULL,\n" +
                "  `answer_date` datetime NOT NULL,\n" +
                "  `receive_to_question_player` tinyint NOT NULL DEFAULT '0',\n" +
                "  PRIMARY KEY (`id`,`question_id`),\n" +
                "  KEY `answers_questions_id_fk` (`question_id`),\n" +
                "  KEY `answers_user_profiles_UUID_fk` (`answer_qauser_uuid`),\n" +
                "  CONSTRAINT `answers_questions_id_fk` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "  CONSTRAINT `answers_user_profiles_UUID_fk` FOREIGN KEY (`answer_qauser_uuid`) REFERENCES `user_profiles` (`UUID`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;\n");

    }

    public static boolean taskTransaction(DBTransaction dbTransaction) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            dbTransaction.task(connection);
            connection.commit();
            connection.close();
            return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.close();
                }
            } catch ( SQLException es ) {
                es.printStackTrace();
            }
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public static <T> List<T> getDataFromDataBase(String sql, String targetCol, Class<?> resultType, String... args) {

        try ( Connection connection = getConnection()  ) {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            int argCount = 1;

            for (String arg : args) {
                preparedStatement.setString(argCount, arg);
                argCount++;
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            List<T> results = new ArrayList<>();

            while ( resultSet.next() ) {
                T result = (T) resultType.cast(resultSet.getObject(targetCol));
                results.add(result);
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return List.of();
    }

}
