package gaya.pe.kr.velocity.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBTransaction {

    void task(Connection connection) throws SQLException;


}
