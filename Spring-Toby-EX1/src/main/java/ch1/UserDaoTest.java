package ch1;

import ch1.step1.UserDao;
import ch1.step4.ConnectionMaker;
import ch1.step4.MysqlConnectionMaker;
import ch1.step5.DaoFactory;

import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // fromStep1To3();
        // step4(new MysqlConnectionMaker());
        step5("Step5");

    }

    public static void fromStep1To3(String name) throws SQLException, ClassNotFoundException {
        System.out.println("================ fromStep1To3 ===================");
        addAndSelect(new UserDao(), name);
    }

    public static void step4(ConnectionMaker connectionMaker, String name) throws SQLException, ClassNotFoundException {
        System.out.println("=================== fromStep4 ===================");
        addAndSelect(new ch1.step4.UserDao(connectionMaker), name);
    }

    public static void step5(String name) throws SQLException, ClassNotFoundException {
        System.out.println("=================== fromStep5 ===================");
        addAndSelect(new DaoFactory().userDao(), name);
    }

    private static void addAndSelect(UserDaoInterface userDao, String name) throws ClassNotFoundException, SQLException {
        final User user = new User();
        user.setId("sunlee22");
        user.setName(name);
        user.setPassword("pass");

        userDao.add(user);
        System.out.println("등록 : " + user.getId());


        final User findUser = userDao.get(user.getId());
        System.out.println("findUser = " + findUser);
    }
}
