package ch1.step5;

import ch1.step4.ConnectionMaker;
import ch1.step4.MysqlConnectionMaker;
import ch1.step4.UserDao;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
public class DaoFactory {
    public UserDao userDao(){
        final ConnectionMaker connectionMaker = new MysqlConnectionMaker();
        return new UserDao(connectionMaker);
    }
}
