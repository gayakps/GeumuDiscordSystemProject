package gaya.pe.kr.velocity.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gaya.pe.kr.util.option.data.options.ConfigOption;
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

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    private static void initTableList() {


        tableCreateList.add("CREATE TABLE IF NOT EXISTS `back_up_data` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `player_name` varchar(80) NOT NULL,\n" +
                "  `player_uuid` char(36) DEFAULT NULL,\n" +
                "  `nbt_data` longtext DEFAULT NULL,\n" +
                "  `game_mode` varchar(20) NOT NULL,\n" +
                "  `created_time` varchar(90) NOT NULL,\n" +
                "  `effects` longtext DEFAULT NULL,\n" +
                "  `balance` double DEFAULT -1, \n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;\n");

        tableCreateList.add("CREATE TABLE IF NOT EXISTS `player_data` (\n" +
                "  `player_uuid` char(36) NOT NULL,\n" +
                "  `nbt_data` longtext NOT NULL,\n" +
                "  `game_mode` varchar(20) NOT NULL,\n" +
                "  `effects` longtext DEFAULT NULL,\n" +
                "  `balance` double DEFAULT -1, \n" +
                "  PRIMARY KEY (`player_uuid`) USING BTREE\n" +
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
