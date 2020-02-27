package ch1.step4;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
public interface ConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException;
}
