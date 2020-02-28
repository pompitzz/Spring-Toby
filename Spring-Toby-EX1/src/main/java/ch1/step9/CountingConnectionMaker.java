package ch1.step9;

import ch1.step4.ConnectionMaker;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
public class CountingConnectionMaker implements ConnectionMaker {
    private int count = 0;
    private ConnectionMaker connectionMaker;

    public CountingConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    @Override
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
        count++;
        return connectionMaker.makeNewConnection();
    }

    public int getCount() {
        return count;
    }
}
